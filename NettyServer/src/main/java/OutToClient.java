import commands.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.charset.StandardCharsets;
//
//public class OutToClient extends ChannelOutboundHandlerAdapter {
//    @Override
//    public void write(ChannelHandlerContext ctx, Object command, ChannelPromise promise) throws Exception {
//        Command commandFromServer = (Command) command;
//        ctx.writeAndFlush(commandFromServer);
//    }
//
//}
