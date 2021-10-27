package RPCDemo.rpc.transport;

import RPCDemo.rpc.Dispatcher;
import RPCDemo.rpc.UnpackMessage;
import RPCDemo.rpc.protocol.MessageBody;
import RPCDemo.rpc.protocol.MessageHead;
import RPCTest.MessageSer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.lang.reflect.Method;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnpackMessage data = (UnpackMessage) msg;

        //异步处理逻辑
        ctx.executor().parent().next().execute(()->{
            Object object = Dispatcher.instance.getMapping(data.getBody().getName());
            try {
                Method method = object.getClass().getMethod(data.getBody().getMethodname(), data.getBody().getMethodType());
                Object invoke = method.invoke(object, data.getBody().getArgs());

                MessageBody content = new MessageBody((String) invoke);
                byte[] tail = MessageSer.messageEncode(content);
                MessageHead header = new MessageHead(0x14141424, data.getHead().getRequestId(), tail.length);
                byte[] head = MessageSer.messageEncode(header);

                ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.directBuffer(tail.length + head.length);
                byteBuf.writeBytes(head);
                byteBuf.writeBytes(tail);

                ctx.writeAndFlush(byteBuf);
            } catch (Exception e) {
                e.printStackTrace();
             }
        });
    }
}
