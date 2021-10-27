package RPCDemo.rpc.transport;

import RPCDemo.rpc.UnpackMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
        UnpackMessage byteBuf = (UnpackMessage) msg;
        RequestCallback.runCallback(byteBuf);
    }
}
