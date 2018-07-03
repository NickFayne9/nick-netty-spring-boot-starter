package com.nick.netty.client;

import com.nick.netty.client.handlerAdapter.ConnectionWatchdog;
import com.nick.netty.channelInitializer.CustomChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.HashedWheelTimer;

public class NettyClient {
    
    private final HashedWheelTimer timer = new HashedWheelTimer();

    private CustomChannelInitializer customChannelInitializer;

    public void setCustomChannelInitializer(CustomChannelInitializer customChannelInitializer) {
        this.customChannelInitializer = customChannelInitializer;
    }

    private Bootstrap boot;
    
    public void connect(String host, int port, boolean ssl) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        boot = new Bootstrap();
        boot.group(group).channel(NioSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO));
            
        ConnectionWatchdog watchdog = new ConnectionWatchdog(boot, timer, port, host, true, ssl);
            
        ChannelFuture future;

        try {
            synchronized (boot) {
                if(customChannelInitializer == null){
                    customChannelInitializer = new CustomChannelInitializer();
                }

                boot.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(watchdog.handlers());
                    }
                });//.handler(customChannelInitializer);

                future = boot.connect(host,port);
            }

            future.sync();
        } catch (Throwable t) {
            throw new Exception("Connecting is failed cause by :", t);
        }
    }

}
