package com.nick.netty.channelInitializer;

import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NickFayne 2018-07-03 13:56
 * @since 1.0.0
 */
public class CustomChannelInitializer extends AbstractChannelInitializer{

    private List<ChannelHandler> customChannelHandlerList = new ArrayList<>();

    @Override
    protected void initChannelHandlers() throws Exception {
        for(ChannelHandler channelHandler : customChannelHandlerList){
            addHandler(channelHandler);
        }
    }

    public void addCustomHandler(ChannelHandler channelHandler){
        customChannelHandlerList.add(channelHandler);
    }

}
