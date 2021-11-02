package RPCDemo.rpc.transport;

import RPCDemo.rpc.UnpackMessage;
import RPCDemo.rpc.protocol.MessageBody;
import RPCDemo.rpc.protocol.MessageHead;
import RPC.MessageSer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class DecodeHandler extends ByteToMessageDecoder {

    /**
     * 解决粘包问题
     * 内核接收队列中可能会有很多数据同时到达未来得及处理
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf data, List<Object> list) throws Exception {
        //做了两件事1、解码 2、拆包
        while (data.readableBytes() >= 105){
            byte[] b = new byte[105];
            data.getBytes(data.readerIndex(), b);
            MessageHead header = MessageSer.messageDecode(b);

            if (data.readableBytes() >= (header.getDataLength() + 105)){
                data.readBytes(105);
                byte[] by  = new byte[(int) header.getDataLength()];
                data.readBytes(by);
                if (header.getFlag() == 0x14141414){
                    MessageBody body = MessageSer.messageDecode(by);
                    list.add(new UnpackMessage(header, body));
                }else if(header.getFlag() == 0x14141424){
                    MessageBody body = MessageSer.messageDecode(by);
                    list.add(new UnpackMessage(header, body));
                }
            }else {
                break;
            }
        }
    }
}
