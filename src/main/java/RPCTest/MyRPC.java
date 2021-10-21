package RPCTest;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.w3c.dom.css.Counter;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class MyRPC {

    public static void main(String[] args) {
        car ca = proxyGet(car.class);
        ca.hello("你好");
    }

    private static <T>T proxyGet(Class<T> inter) {
        return (T) Proxy.newProxyInstance(inter.getClassLoader(), new Class<?>[]{inter}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                content cont = new content(inter.getName(), method.getName(), method.getParameterTypes(), args);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
                outputStream.writeObject(cont);
                byte[] body = byteArrayOutputStream.toByteArray();

                header header = createHeader(body);
                byteArrayOutputStream.reset();
                outputStream = new ObjectOutputStream(byteArrayOutputStream);
                outputStream.writeObject(header);
                byte[] head = byteArrayOutputStream.toByteArray();

//                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(head));
//                header o = (header)objectInputStream.readObject();
//                System.out.println(o.toString());

                NioSocketChannel ch = clientFactory.instance.getClient(new InetSocketAddress("192.168.1.147", 9090));

                ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(body.length + head.length);
                byteBuf.writeBytes(head);
                byteBuf.writeBytes(body);

                ch.writeAndFlush(byteBuf).sync();
                CountDownLatch countDownLatch = new CountDownLatch(1);
                staticUUID.add(header.requestId, ()->{
                    countDownLatch.countDown();
                });
                countDownLatch.await();
                return null;
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
    private static ConcurrentHashMap<Long, Runnable> map = new ConcurrentHashMap<Long, Runnable>();

    public static void add(Long requestid, Runnable r){
        map.putIfAbsent(requestid, r);
    }
    public static void run(Long requestid){
        map.get(requestid).run();
        map.remove(requestid);
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

    ConcurrentHashMap<InetSocketAddress, clientPool> outboxs = new ConcurrentHashMap<>();

    Random random = new Random();
    int poolSize = 1;

    public synchronized NioSocketChannel getClient(InetSocketAddress address){
        if (outboxs.get(address) == null){
            outboxs.put(address, new clientPool(poolSize));
        }
        clientPool clientPool = outboxs.get(address);
        NioSocketChannel cha = clientPool.clients[random.nextInt(poolSize)];
        if (cha == null){
            return createNeety(address);
        }else {
            return cha;
        }
    }

    private NioSocketChannel createNeety(InetSocketAddress address) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();
        ChannelFuture bind = bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new readHander())
                .bind(address);
        try {
            return (NioSocketChannel) bind.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

class readHander extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.readableBytes() >= 110){
            byte[] b = new byte[110];
            byteBuf.readBytes(b);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(b));
                header o = (header)objectInputStream.readObject();
                System.out.println(o.toString());
        }
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

    public content(String name, String methodname, Class<?>[] methodType, Object[] args) {
        this.name = name;
        this.methodname = methodname;
        this.methodType = methodType;
        this.args = args;
    }

    @Override
    public String toString() {
        return "content{" +
                "name='" + name + '\'' +
                ", methodname='" + methodname + '\'' +
                ", methodType=" + Arrays.toString(methodType) +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}

interface car{
    void hello(String string);
}

interface fly{
    void f(String str);
}