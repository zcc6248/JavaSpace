package MyNeety;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class selectorRunable implements Runnable {

    //多路复用器
    Selector selector = null;
    nioGroup group = null;
    LinkedBlockingQueue<Channel> queue = new LinkedBlockingQueue();

    selectorRunable(nioGroup group){
        try {
            this.group = group;
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true){
            try {
                //select
                int num = selector.select();    //一直阻塞，等待客户端消息或者selector.wakeup()

                //selectkey
                if(num > 0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()){
                            acceptHandler(key);
                        }else if(key.isReadable()){
                            readHandler(key);
                        }else if(key.isWritable()){
                            writHandler(key);
                        }
                    }
                }

                //task
                while (!queue.isEmpty()){
                    Channel take = queue.take();
                    group.registerSelector(this.selector, take);
                }
                System.out.println(Thread.currentThread().getName() + " 当前数量:"  + selector.keys().size());
                for (SelectionKey key : selector.keys()) {
                    System.out.println(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writHandler(SelectionKey key) {

    }

    private void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
        byteBuffer.clear();
        while (true){
            try {
                int num = client.read(byteBuffer);
                if (num > 0){
                    byteBuffer.flip();
                    while (byteBuffer.hasRemaining()){
                        client.write(byteBuffer);
                    }
                    byteBuffer.clear();
                }else if(num == 0){
                    break;
                }else {
                    System.out.println("客户端:" + client.getRemoteAddress() + " 断开链接......");
                    key.cancel();
                    client.close();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                key.cancel();
                try {
                    client.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    private void acceptHandler(SelectionKey key) {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = channel.accept();
            System.out.println("客户端链接:" + client.getRemoteAddress());
            client.configureBlocking(false);

            group.registerSelector(group.addQueue(client), client);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
