package RPCDemo.rpc.transport;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Arrays;

public class ClientChannel {
    NioSocketChannel[] socketchanns;
    Object[] lock;

    ClientChannel(int size) {
        socketchanns = new NioSocketChannel[size];
        lock = new Object[size];
        Arrays.fill(lock, new Object());
    }
}
