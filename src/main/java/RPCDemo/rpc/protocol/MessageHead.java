package RPCDemo.rpc.protocol;

import java.io.Serializable;
import java.util.UUID;

public class MessageHead implements Serializable {
    private int flag;
    private long requestId;
    private long dataLength;

    public int getFlag() {
        return flag;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getDataLength() {
        return dataLength;
    }

    public static MessageHead createHeader(byte[] body) {
        long uuid = Math.abs(UUID.randomUUID().getLeastSignificantBits());
        MessageHead header = new MessageHead(0X14141414, uuid, body.length);
        return header;
    }

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
