package RPCDemo.rpc.transport;

import RPCDemo.rpc.protocol.MessageBody;
import RPCDemo.rpc.protocol.MessageHead;
import RPC.MessageSer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.InetSocketAddress;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public enum ClientChannelFactory {
    instance;

    ClientChannel clientChannel = null;
    ConcurrentHashMap<InetSocketAddress, ClientChannel> outboxs = new ConcurrentHashMap<>();
    Random random = new Random();
    int poolSize = 5;

    public NioSocketChannel getClient(InetSocketAddress address){
        clientChannel = outboxs.get(address);
        /**注意
         * ClientChannel clientChannel = outboxs.get(address);不可以如此使用。
         * 因为新创建的指针对象clientChannel属于局部变量，不唯一，在多线程中一个线程有一个。
         * 所以clientChannel == null，进行判断时阻塞不了其他线程。换言之每个线程都会执行clientChannel = outboxs.get(address)方法。
         * 或可以使用(不赋值给局部变量)outboxs.get(address) == null。因为是线程安全的。
         */
        if (clientChannel == null){
            synchronized (this) {
                if (clientChannel == null) {
                    outboxs.put(address, new ClientChannel(poolSize));
                    clientChannel = outboxs.get(address);
                    System.out.println("HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
                }
            }
        }
        int i = random.nextInt(poolSize);

        if (clientChannel.socketchanns[i] != null && clientChannel.socketchanns[i].isActive()){
            return clientChannel.socketchanns[i];
        }else {
            synchronized (ClientChannelFactory.class) {
                if (clientChannel.socketchanns[i] == null || !clientChannel.socketchanns[i].isActive()) {
                    clientChannel.socketchanns[i] = createNeety(address);
                }
            }
            return clientChannel.socketchanns[i];
        }
    }

    public CompletableFuture<Object> transport(MessageBody cont){
        byte[] body = MessageSer.messageEncode(cont);
        MessageHead header = MessageHead.createHeader(body);
        byte[] head = MessageSer.messageEncode(header);
//        System.out.println("head length.." + head.length);

        NioSocketChannel ch = getClient(new InetSocketAddress("localhost", 9090));

        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.directBuffer(body.length + head.length);
        byteBuf.writeBytes(head);
        byteBuf.writeBytes(body);

        try {
            ch.writeAndFlush(byteBuf).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CompletableFuture<Object> cf = new CompletableFuture<>();
        RequestCallback.addCallback(header.getRequestId(), cf);
        return cf;
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
        System.out.println("建立连接:" + address);
        try {
            return (NioSocketChannel) bind.sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
