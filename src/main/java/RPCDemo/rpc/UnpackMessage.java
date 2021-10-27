package RPCDemo.rpc;

import RPCDemo.rpc.protocol.MessageBody;
import RPCDemo.rpc.protocol.MessageHead;

public class UnpackMessage {
    private MessageHead head;
    private MessageBody body;

    public MessageHead getHead() {
        return head;
    }

    public MessageBody getBody() {
        return body;
    }

    public UnpackMessage(MessageHead head, MessageBody body) {
        this.head = head;
        this.body = body;
    }
}