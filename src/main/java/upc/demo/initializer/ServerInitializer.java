package upc.demo.initializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import upc.demo.handler.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by cheny on 2020/08/26
 * 初始化类
 */
public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    byte[] b1= {0x16};
    ByteBuf delimiter = Unpooled.copiedBuffer(b1);
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new IdleStateHandler(60*10, 0, 0, TimeUnit.SECONDS));
        socketChannel.pipeline().addLast(new IdleStateTrigger());
        socketChannel.pipeline().addLast(new RegistrationHandler());
        socketChannel.pipeline().addLast(new IdleHandler());
        socketChannel.pipeline().addLast(new ThreeNineHandler());
        socketChannel.pipeline().addLast(new FourNineHandler());
        socketChannel.pipeline().addLast(new FiveNineHandler());
        // socketChannel.pipeline().addLast(new RegistrationHandler());




    }
}
