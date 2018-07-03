package com.nick.netty.client.handlerAdapter;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Sharable
public class HeartbeatClientHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(HeartbeatClientHandler.class);
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Active time is ："+ new Date());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Inactive time is ：" + new Date());
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String) msg;
        if (message.equals("Heartbeat")) {
            logger.debug(ctx.channel().remoteAddress() + "-> Client :" + msg);
            ctx.write("Client has read \"Heartbeat\" from server");
            ctx.flush();
            ReferenceCountUtil.release(msg);
        }else{
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
