package upc.demo.handler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;;
import org.springframework.web.bind.annotation.RestController;
import upc.demo.Utils;
import upc.demo.test.TestData;
import upc.demo.util.CacheLoader;
import upc.demo.util.HttpClient;
import upc.demo.util.QueryList;

import java.io.IOException;

/**
 * Created by cheny on 2020/08/26
 * 处理Axxxxxxxx 上线设备登录和退出
 */
@Slf4j
@RestController
@ChannelHandler.Sharable
public class RegistrationHandler extends ChannelInboundHandlerAdapter{
    /**
     * 客户端连接会触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("Channel active......");
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved (ChannelHandlerContext ctx) throws IOException {
        CacheLoader.removeChannel(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException {
        System.out.println("收到一条新数据：");
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        Utils.printHexString(bytes);
//         如果A开头 认为是注册消息
        if(bytes[0] == (byte)0x41){
            System.out.println("----------------------------");
            System.out.println("registration msg:");
            Utils.printHexString(bytes);
            String deviceNumber = new String(bytes).substring(1, 12);
            System.out.println(deviceNumber);
            String url = "http://127.0.0.1:10001/integrationTest/addOnlineDevice?deviceNumber="
                    + deviceNumber;
            HttpClient.sendGetRequest(url);
            String equipmentId = new String(bytes).substring(1,12);
            CacheLoader.addChannel(ctx.channel(), equipmentId);
        }
        else{
            ByteBuf heapBuf = Unpooled.buffer();
            heapBuf.writeBytes(bytes);
            ctx.fireChannelRead(heapBuf);
        }
        ReferenceCountUtil.release(msg);
    }
}


