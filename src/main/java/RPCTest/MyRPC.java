package RPCTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
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

    private static int client_num = 50;
    private static int LoopGroup_num = 10;
    public static void main(String[] args) {

        new Thread(()->{
            serverStart();
        }).start();
        System.out.println("服务器启动...........");
        Thread[] t = new Thread[client_num];
        AtomicInteger j = new AtomicInteger(0);
        for (int i = 0; i < client_num; i++) {
            t[i] = new Thread(()->{
                car ca = proxyGet(car.class);
                String arg = "你好官方电话发给" + j.getAndIncrement();
                String hello = ca.hello(arg);
                System.out.println(hello + "    client:" + arg);
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

        Enumeration<InetSocketAddress> keys = clientFactory.instance.outboxs.keys();
        Iterator<InetSocketAddress> iterator = keys.asIterator();
        System.out.println("=======================");
        while (iterator.hasNext()){
            InetSocketAddress next = iterator.next();
            clientPool clientPool = clientFactory.instance.outboxs.get(next);
            System.out.println(next + "连接池数量: " + clientPool.clients.length);
        }
        System.out.println("requestid重复数量：" + staticUUID.getRepeatNum());
    }

    private static void serverStart(){
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(LoopGroup_num);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(eventExecutors, eventExecutors)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new requestDecode());
                        pipeline.addLast(new requestHandler());
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
                content cont = new content(inter.getName(), method.getName(), method.getParameterTypes(), args);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
//                outputStream.writeObject(cont);
//                byte[] body = byteArrayOutputStream.toByteArray();
                byte[] body = MessageSer.messageEncode(cont);

                header header = createHeader(body);
//                byteArrayOutputStream.reset();
//                outputStream = new ObjectOutputStream(byteArrayOutputStream);
//                outputStream.writeObject(header);
//                byte[] head = byteArrayOutputStream.toByteArray();
                byte[] head = MessageSer.messageEncode(header);
                System.out.println("hhhhhhhhhhhhhh" + head.length);

//                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(head));
//                header o = (header)objectInputStream.readObject();
//                System.out.println(o.toString());

                NioSocketChannel ch = clientFactory.instance.getClient(new InetSocketAddress("localhost", 9090));

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
                staticUUID.add(header.requestId, cf);
                return cf.get();
            }

            private header createHeader(byte[] body) {
                long uuid = Math.abs(UUID.randomUUID().getLeastSignificantBits());
                header header = new header(0X14141414, uuid, body.length);
                return header;
            }
        });
    }
}

class staticUUID{
    private static AtomicInteger repeatNum = new AtomicInteger(0);
    private static ConcurrentHashMap<Long, CompletableFuture> map = new ConcurrentHashMap<Long, CompletableFuture>();

    public static void add(Long requestid, CompletableFuture r){
        map.putIfAbsent(requestid, r);
    }
    public static void run(requestMQ mess){
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

class clientPool {
    NioSocketChannel[] clients;
    Object[] lock;

    clientPool(int size) {
        clients = new NioSocketChannel[size];
        lock = new Object[size];
        Arrays.fill(lock, new Object());
    }
}

enum  clientFactory{
    instance;

    volatile ConcurrentHashMap<InetSocketAddress, clientPool> outboxs = new ConcurrentHashMap<>();

    Random random = new Random();
    int poolSize = 10;

    public synchronized NioSocketChannel getClient(InetSocketAddress address){
        if (outboxs.get(address) == null){
            outboxs.put(address, new clientPool(poolSize));
        }
        clientPool clientPool = outboxs.get(address);
        int i = random.nextInt(clientPool.clients.length);
        System.out.println(clientPool.clients.length + " " + Thread.currentThread().getName() + "连接数：" + i);
        NioSocketChannel cha = clientPool.clients[i];
        if (cha != null && cha.isActive()){
            return cha;
        }else {
            NioSocketChannel neety = createNeety(address);
            clientPool.clients[i] = neety;
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
                        pipeline.addLast(new requestDecode());
                        pipeline.addLast(new readHandler());
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

class requestDecode extends ByteToMessageDecoder{

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf data, List<Object> list) throws Exception {
        while (data.readableBytes() >= 87){
            byte[] b = new byte[87];
            data.getBytes(data.readerIndex(), b);
//            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(b));
//            header header = (header) objectInputStream.readObject();
            header header = MessageSer.messageDecode(b);

            if (data.readableBytes() >= (header.dataLength + 87)){
                data.readBytes(87);
                byte[] by  = new byte[(int) header.dataLength];
                data.readBytes(by);
//                ObjectInputStream ob = new ObjectInputStream(new ByteArrayInputStream(by));
//                content body = (content)ob.readObject();
                if (header.flag == 0x14141414){
                    content body = MessageSer.messageDecode(by);
                    list.add(new requestMQ(header, body));
                }else if(header.flag == 0x14141424){
                    content body = MessageSer.messageDecode(by);
                    list.add(new requestMQ(header, body));
                }
            }else {
                break;
            }
        }
    }
}

class requestMQ{
    header head;
    content body;

    public requestMQ(header head, content body) {
        this.head = head;
        this.body = body;
    }
}

class readHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        requestMQ byteBuf = (requestMQ) msg;
        staticUUID.run(byteBuf);
    }
}

class requestHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        requestMQ data = (requestMQ) msg;

        String name = Thread.currentThread().getName();
        //异步处理逻辑
        ctx.executor().parent().next().execute(()->{
            content content = new content("io thread:" + name + "   处理逻辑 thread:" + Thread.currentThread().getName() + "      " + data.body.args[0]);
            byte[] tail = MessageSer.messageEncode(content);
            header header = new header(0x14141424, data.head.requestId, tail.length);
            byte[] head = MessageSer.messageEncode(header);

            ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer(tail.length + head.length);
            byteBuf.writeBytes(head);
            byteBuf.writeBytes(tail);

            ctx.writeAndFlush(byteBuf);
        });
    }
}

class header implements Serializable {
    int flag;
    long requestId;
    long dataLength;

    public header(int flag, long requestId, long dataLength) {
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

class content implements Serializable {
    String name;
    String methodname;
    Class<?>[] methodType;
    Object[] args;
    String result;

    public content(String name, String methodname, Class<?>[] methodType, Object[] args) {
        this.name = name;
        this.methodname = methodname;
        this.methodType = methodType;
        this.args = args;
    }

    public content(String result) {
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

interface car{
    String hello(String string);
}

interface fly{
    void f(String str);
}