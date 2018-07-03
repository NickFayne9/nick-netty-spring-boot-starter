package com.nick.netty.channelInitializer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author NickFayne 2018-07-03 13:49
 * @since 1.0.0
 */
public abstract class AbstractChannelInitializer extends ChannelInitializer<SocketChannel> {

    private List<ChannelHandler> channelHandlerList = new ArrayList<>();

    protected abstract void initChannelHandlers() throws Exception;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelHandler[] channelHandlers = new ChannelHandler[channelHandlerList.size()];
        socketChannel.pipeline().addLast(channelHandlerList.toArray(channelHandlers));
    }

    protected void addHandler(ChannelHandler channelHandler){
        channelHandlerList.add(channelHandler);
    }
}
