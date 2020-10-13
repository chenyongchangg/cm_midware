package upc.demo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import upc.demo.Utils;
import upc.demo.test.TestData;

/**
 * Created by cheny on 2020/08/26
 * 回复心跳
 */
public class IdleHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        ByteBuf heapBuf = Unpooled.buffer();
        if(bytes.length == 11){
            System.out.println("----------------------------");
            System.out.println("idle msg:");
            Utils.printHexString(bytes);
            heapBuf.writeBytes("ok".getBytes());
            ctx.channel().writeAndFlush(heapBuf);
        }
        else{
            heapBuf.writeBytes(bytes);
            ctx.fireChannelRead(heapBuf);
        }
        ReferenceCountUtil.release(msg);
    }
}
