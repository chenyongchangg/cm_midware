package upc.demo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import upc.demo.Utils;
import upc.demo.util.CacheLoader;
import upc.demo.util.HttpClient;
import upc.demo.util.QueryList;


/**
 * Created by cheny on 2020/08/26
 * 处理单表查询 控制码为59的返回数据
 */
public class FiveNineHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        if(bytes[9] == (byte) 0x59){
            //不是要查的 可能过期了
            if(!QueryList.queryDeviceList.containsKey(CacheLoader.channelMapCS.get(ctx.channel().id()))){
                //丢弃
                System.out.println("收到过期数据");
            }
            else {
                //处理
                System.out.println("----------------------------");
                Utils.printHexString(bytes);
                //http
                byte[] bytes1 = new byte[4];
                System.arraycopy(bytes, 11, bytes1, 0,  4);
                bytes1 = Utils.bytesReverse(bytes1);
                Utils.printHexString(bytes1);
                String url = "http://127.0.0.1:10001/integrationTest/singleReceive?meterNumber="
                        + QueryList.queryDeviceList.get(CacheLoader.channelMapCS.get(ctx.channel().id()))
                        + "&reading=" + Utils.bcd2Str(bytes1);
                HttpClient.sendGetRequest(url);
                //在查询表中删除此项
                QueryList.queryDeviceList.remove(CacheLoader.channelMapCS.get(ctx.channel().id()));
                System.out.println("clean up");
            }

        }
        else{
            ByteBuf heapBuf = Unpooled.buffer();
            heapBuf.writeBytes(bytes);
            ctx.fireChannelRead(heapBuf);
        }

    }
}
