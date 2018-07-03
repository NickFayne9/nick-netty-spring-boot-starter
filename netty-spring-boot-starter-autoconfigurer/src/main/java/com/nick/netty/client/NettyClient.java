package com.nick.netty.client;

import com.nick.netty.client.handlerAdapter.ConnectionWatchdog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.HashedWheelTimer;

import java.util.ArrayList;
import java.util.List;

public class NettyClient {
    
    private final HashedWheelTimer timer = new HashedWheelTimer();

    private Bootstrap boot;
    private EventLoopGroup group;
    private ConnectionWatchdog connectionWatchdog;

    private Channel channel;

    private List<ChannelHandler> channelHandlerList = new ArrayList<>();

    public NettyClient(String host, int port, boolean ssl) {
        group = new NioEventLoopGroup();
        boot = new Bootstrap();
        boot.group(group).
                channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO));

        connectionWatchdog = new ConnectionWatchdog(boot, timer, port, host, true, ssl);
    }

    public void connect(String host, int port) throws Exception {
        ChannelFuture future;
        try {
            synchronized (boot) {
                boot.handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(connectionWatchdog.handlers());
                        for(ChannelHandler channelHandler : channelHandlerList){
                            ch.pipeline().addLast(channelHandler);
                        }
                    }
                });

                future = boot.connect(host, port);
                channel = future.channel();
            }
            future.sync();
        } catch (Throwable t) {
            throw new Exception("Connecting is failed cause by :", t);
        }
    }

    void addClientHandler(ChannelHandler channelHandler){
        channelHandlerList.add(channelHandler);
    }

    boolean sendMsg(String msg){
        if (channel != null && channel.isActive()){
            channel.writeAndFlush(msg);
            return true;
        }else{
            return false;
        }
    }
}
