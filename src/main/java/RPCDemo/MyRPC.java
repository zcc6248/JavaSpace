package RPCDemo;

import RPCDemo.proxy.ProxyFactory;
import RPCDemo.rpc.Dispatcher;
import RPCDemo.rpc.transport.*;
import RPCDemo.service.Icar;
import RPCDemo.service.Ifly;
import RPCDemo.service.MyCar;
import RPCDemo.service.MyFly;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

public class MyRPC {

    private static final int CLIENT_NUM = 50;
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
                Icar ca = ProxyFactory.proxyGet(Icar.class);
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
        System.out.println("requestid重复数量：" + RequestCallback.getRepeatNum());
    }

    private static void serverStart(){
        MyCar myCar = new MyCar();
        MyFly myFly = new MyFly();
        Dispatcher.instance.addMapping(Icar.class.getName(), myCar);
        Dispatcher.instance.addMapping(Ifly.class.getName(), myFly);

        NioEventLoopGroup eventExecutors = new NioEventLoopGroup(LOOPGROUP_NUM);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ChannelFuture bind = serverBootstrap.group(eventExecutors, eventExecutors)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        System.out.println("server accept port:"  + ch.remoteAddress().getPort());
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
}



