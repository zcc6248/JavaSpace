package MyNeety;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.atomic.AtomicInteger;

public class nioGroup {
    selectorRunable[] que = null;
    ServerSocketChannel server = null;
    AtomicInteger index = new AtomicInteger();

    public nioGroup(int num) {
        que = new selectorRunable[num];
        for (int i = 0; i < num; i++) {
            que[i] = new selectorRunable(this);
            new Thread(que[i]).start();
        }
    }

    public void bind(int port) {
        try {
            System.out.println("服务器启动............");
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress(port));

            registerSelector(addQueue(server), server);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Selector addQueue(Channel channel){
        int in = index.get() % que.length;
        index.getAndIncrement();
        que[in].queue.offer(channel);
        que[in].selector.wakeup();
        return que[in].selector;
    }

    public void registerSelector(Selector selector, Channel channel){
        try {
            if (channel instanceof ServerSocketChannel){
                ((ServerSocketChannel) channel).register(selector, SelectionKey.OP_ACCEPT);
            }else if (channel instanceof SocketChannel){
                ByteBuffer by = ByteBuffer.allocateDirect(1024);
                ((SocketChannel) channel).register(selector, SelectionKey.OP_READ, by);
            }
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }
}
