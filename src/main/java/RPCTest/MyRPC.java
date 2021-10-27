package RPCTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRPC {

    private static final int CLIENT_NUM = 500;
    private static final int LOOPGROUP_NUM = 10;
    public static void main(String[] args) {

        new Thread(()->{
            serverStart();
        }).start();
        System.out.println("服务器启动...........");
        Thread[] t = new Thread[CLIENT_NUM];
        AtomicInteger j = new AtomicInteger(0);
        for (int i = 0; i < CLIENT_NUM; i++) {
            t[i] = new Thread(()->{
                Icar ca = proxyGet(Icar.class);
                String arg = "你好官方电话-0014" + j.getAndIncrement();
                String hello = ca.hello(arg);
                System.out.println(hello + "    client: " + arg);
            });
        }
        for (int i = 0; i < t.length; i++) {
            t[i].start();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Enumeration<InetSocketAddress> keys = SocketChannelPoolFactory.instance.outboxs.keys();
        Iterator<InetSocketAddress> iterator = keys.asIterator();
        System.out.println("=======================");
        while (iterator.hasNext()){
            InetSocketAddress next = iterator.next();
            SocketChannelPool clientPool = SocketChannelPoolFactory.instance.outboxs.get(next);
            System.out.println(next + "连接池数量: " + clientPool.socketchanns.length);
        }
        System.out.println("requestid重复数量：" + RequestCallback.getRepeatNum());
    }

    private static void serverStart(){
        MyCar myCar = new MyCar();
        MyFly myFly = new MyFly();
        Dispatcher.addMapping(Icar.class.getName(), myCar);
        Dispatcher.addMapping(Ifly.class.getName(), myFly);

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(LOOPGROUP_NUM);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(eventExecutors, eventExecutors)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new ServerHandler());
                    }
                })
                .bind(new InetSocketAddress(9090));
        try {
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static <T>T proxyGet(Class<T> inter) {
        return (T) Proxy.newProxyInstance(inter.getClassLoader(), new Class<?>[]{inter}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                MessageBody cont = new MessageBody(inter.getName(), method.getName(), method.getParameterTypes(), args);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
//                outputStream.writeObject(cont);
//                byte[] body = byteArrayOutputStream.toByteArray();
                byte[] body = MessageSer.messageEncode(cont);

                MessageHead header = createHeader(body);
//                byteArrayOutputStream.reset();
//                outputStream = new ObjectOutputStream(byteArrayOutputStream);
//                outputStream.writeObject(header);
//                byte[] head = byteArrayOutputStream.toByteArray();
                byte[] head = MessageSer.messageEncode(header);
                System.out.println("hhhhhhhhhhhhhh" + head.length);

//                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(head));
//                header o = (header)objectInputStream.readObject();
//                System.out.println(o.toString());

                NioSocketChannel ch = SocketChannelPoolFactory.instance.getClient(new InetSocketAddress("localhost", 9090));

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(body.length + head.length);
                byteBuf.writeBytes(head);
                byteBuf.writeBytes(body);

                ch.writeAndFlush(byteBuf).sync();
//                runable不能接受返回值，所以使用completable
//                CountDownLatch countDownLatch = new CountDownLatch(1);
//                staticUUID.add(header.requestId, ()->{
//                    countDownLatch.countDown();
//                });
//                countDownLatch.await();
                CompletableFuture<String> cf = new CompletableFuture<>();
                RequestCallback.addCallback(header.requestId, cf);
                return cf.get();
            }

        });
    }
    private static MessageHead createHeader(byte[] body) {
        long uuid = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        MessageHead header = new MessageHead(0X14141414, uuid, body.length);
        return header;
    }
}

class RequestCallback {
    private static AtomicInteger repeatNum = new AtomicInteger(0);
    private static ConcurrentHashMap<Long, CompletableFuture> map = new ConcurrentHashMap<Long, CompletableFuture>();

    public static void addCallback(Long requestid, CompletableFuture r){
        map.putIfAbsent(requestid, r);
    }
    public static void runCallback(UnpackMessage mess){
        CompletableFuture runnable = map.get(mess.head.requestId);
        if(runnable != null){
            runnable.complete(mess.body.result);
            map.remove(mess.head.requestId);
        }else {
            repeatNum.incrementAndGet();
        }
    }
    public static int getRepeatNum(){
        return repeatNum.get();
    }
}

class SocketChannelPool {
    NioSocketChannel[] socketchanns;
    Object[] lock;

    SocketChannelPool(int size) {
        socketchanns = new NioSocketChannel[size];
        lock = new Object[size];
        Arrays.fill(lock, new Object());
    }
}

enum SocketChannelPoolFactory {
    instance;

    volatile ConcurrentHashMap<InetSocketAddress, SocketChannelPool> outboxs = new ConcurrentHashMap<>();

    Random random = new Random();
    int poolSize = 10;

    public synchronized NioSocketChannel getClient(InetSocketAddress address){
        if (outboxs.get(address) == null){
            outboxs.put(address, new SocketChannelPool(poolSize));
        }
        SocketChannelPool clientPool = outboxs.get(address);
        int i = random.nextInt(clientPool.socketchanns.length);
        System.out.println(clientPool.socketchanns.length + " " + Thread.currentThread().getName() + "连接数：" + i);
        NioSocketChannel cha = clientPool.socketchanns[i];
        if (cha != null && cha.isActive()){
            return cha;
        }else {
            NioSocketChannel neety = createNeety(address);
            clientPool.socketchanns[i] = neety;
            return neety;
        }
    }

    private NioSocketChannel createNeety(InetSocketAddress address) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture bind = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new DecodeHandler());
                        pipeline.addLast(new ClientHandler());
                    }
                })
                .connect(address);
        try {
            return (NioSocketChannel) bind.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class DecodeHandler extends ByteToMessageDecoder{

    /**
     * 解决粘包问题
     * 内核接收队列中可能会有很多数据同时到达未来得及处理
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf data, List<Object> list) throws Exception {
        while (data.readableBytes() >= 92){
            byte[] b = new byte[92];
            data.getBytes(data.readerIndex(), b);
//            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(b));
//            header header = (header) objectInputStream.readObject();
            MessageHead header = MessageSer.messageDecode(b);

            if (data.readableBytes() >= (header.dataLength + 92)){
                data.readBytes(92);
                byte[] by  = new byte[(int) header.dataLength];
                data.readBytes(by);
//                ObjectInputStream ob = new ObjectInputStream(new ByteArrayInputStream(by));
//                content body = (content)ob.readObject();
                if (header.flag == 0x14141414){
                    MessageBody body = MessageSer.messageDecode(by);
                    list.add(new UnpackMessage(header, body));
                }else if(header.flag == 0x14141424){
                    MessageBody body = MessageSer.messageDecode(by);
                    list.add(new UnpackMessage(header, body));
                }
            }else {
                break;
            }
        }
    }
}

class UnpackMessage {
    MessageHead head;
    MessageBody body;

    public UnpackMessage(MessageHead head, MessageBody body) {
        this.head = head;
        this.body = body;
    }
}

class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        UnpackMessage byteBuf = (UnpackMessage) msg;
        RequestCallback.runCallback(byteBuf);
    }
}

class ServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnpackMessage data = (UnpackMessage) msg;

        //异步处理逻辑
        ctx.executor().parent().next().execute(()->{
            Object object = Dispatcher.getMapping(data.body.name);
            try {
                Method method = object.getClass().getMethod(data.body.methodname, data.body.methodType);
                Object invoke = method.invoke(object, data.body.args);

                MessageBody content = new MessageBody((String) invoke);
                byte[] tail = MessageSer.messageEncode(content);
                MessageHead header = new MessageHead(0x14141424, data.head.requestId, tail.length);
                byte[] head = MessageSer.messageEncode(header);

                ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer(tail.length + head.length);
                byteBuf.writeBytes(head);
                byteBuf.writeBytes(tail);

                ctx.writeAndFlush(byteBuf);
            } catch (Exception e) {
             e.printStackTrace();
             }
        });
    }
}

class MessageHead implements Serializable {
    int flag;
    long requestId;
    long dataLength;

    public MessageHead(int flag, long requestId, long dataLength) {
        this.flag = flag;
        this.requestId = requestId;
        this.dataLength = dataLength;
    }

    @Override
    public String toString() {
        return "header{" +
                "flag=" + flag +
                ", requestId=" + requestId +
                ", dataLength=" + dataLength +
                '}';
    }
}

class MessageBody implements Serializable {
    String name;
    String methodname;
    Class<?>[] methodType;
    Object[] args;
    String result;

    public MessageBody(String name, String methodname, Class<?>[] methodType, Object[] args) {
        this.name = name;
        this.methodname = methodname;
        this.methodType = methodType;
        this.args = args;
    }

    public MessageBody(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "content{" +
                "name='" + name + '\'' +
                ", methodname='" + methodname + '\'' +
                ", methodType=" + Arrays.toString(methodType) +
                ", args=" + Arrays.toString(args) +
                ", result='" + result + '\'' +
                '}';
    }
}

class MyCar implements Icar {
    @Override
    public String hello(String string) {
        return "ser return: " + string;
    }
}

class MyFly implements Ifly {
    @Override
    public void hello(String str) {
        System.out.println("fly ........................");
    }
}

class Dispatcher {
    private static ConcurrentHashMap<String, Object> mappings = new ConcurrentHashMap<>();

    public static void addMapping(String key, Object obj){
        mappings.putIfAbsent(key, obj);
    }

    public static Object getMapping(String key){
        return mappings.get(key);
    }
}

interface Icar {
    String hello(String string);
}

interface Ifly {
    void hello(String str);
}