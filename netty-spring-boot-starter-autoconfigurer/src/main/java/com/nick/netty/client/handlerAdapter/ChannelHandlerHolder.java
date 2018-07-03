package com.nick.netty.client.handlerAdapter;

import io.netty.channel.ChannelHandler;

import java.net.URISyntaxException;

/**
 * A collection of client handlers
 *
 * @author NickFayne
 * @since 1.0.0
 */
public interface ChannelHandlerHolder {

    ChannelHandler[] handlers() throws Exception;
}
