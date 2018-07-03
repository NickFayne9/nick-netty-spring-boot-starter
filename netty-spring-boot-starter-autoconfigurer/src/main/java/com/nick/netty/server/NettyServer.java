package com.nick.netty.server;

import com.nick.netty.channelInitializer.CustomChannelInitializer;
import com.nick.netty.channelInitializer.DefaultChannelInitializer;
import com.nick.netty.server.handlerAdapter.AcceptorIdleStateTrigger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final AcceptorIdleStateTrigger idleStateTrigger = new AcceptorIdleStateTrigger();
    
    private int port;

    private boolean ssl;

    private CustomChannelInitializer customChannelInitializer;

    public NettyServer(int port, boolean ssl) {
        this.port = port;
        this.ssl = ssl;
    }

    public void setCustomChannelInitializer(CustomChannelInitializer customChannelInitializer) {
        this.customChannelInitializer = customChannelInitializer;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            if(customChannelInitializer == null){
                customChannelInitializer = new CustomChannelInitializer();
            }

            ServerBootstrap sbs = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new DefaultChannelInitializer(ssl))
                    //.childHandler(customChannelInitializer)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //bind port and receive requests
            ChannelFuture future = sbs.bind(port).sync();

            logger.info("Server listening port is {}", port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
