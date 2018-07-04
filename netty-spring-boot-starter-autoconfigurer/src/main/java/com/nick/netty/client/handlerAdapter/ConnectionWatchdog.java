package com.nick.netty.client.handlerAdapter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Re-connection processor
 *
 * @author NickFayne
 * @since 1.0.0
 */
@Sharable
public class ConnectionWatchdog extends ChannelInboundHandlerAdapter implements TimerTask, ChannelHandlerHolder {

    private Logger logger = LoggerFactory.getLogger(ConnectionWatchdog.class);

    private final Bootstrap bootstrap;
    private final Timer timer;
    private final int port;
    private final String host;
    private boolean ssl;

    private volatile boolean reconnect = true;
    private int attempts;

    private List<ChannelHandler> channelHandlerList = new ArrayList<>();
    private List<ChannelHandler> sharableCustomChannelHandlerList = new ArrayList<>();
    
    public ConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port,String host, boolean reconnect, boolean ssl) {
        this.bootstrap = bootstrap;
        this.timer = timer;
        this.port = port;
        this.host = host;
        this.reconnect = reconnect;
        this.ssl = ssl;
    }

    /**
     * Add sharable custom handlers after method handlers
     * @param channelHandler
     */
    public void addSharableCustomHandler(ChannelHandler channelHandler){
        sharableCustomChannelHandlerList.add(channelHandler);
    }

    /**
     * channel链路每次active的时候，将其连接的次数重新☞ 0
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("The current link has been activated and the number of reconnection attempts is reset to 0.");
        attempts = 0;
        ctx.fireChannelActive();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connection closed");
        if(reconnect){
            logger.debug("The Connection is closed and will be reconnected");
            if (attempts < 50) {
                attempts++;
                //重连的间隔时间会越来越长
                int timeout = 2 << attempts;
                timer.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
        }
        ctx.fireChannelInactive();
    }
    

    public void run(Timeout timeout) throws Exception {
        
        ChannelFuture future;
        synchronized (bootstrap) {
            bootstrap.handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    
                    ch.pipeline().addLast(handlers());
                }
            });
            future = bootstrap.connect(host,port);
        }
        future.addListener((ChannelFutureListener) f -> {
            boolean succeed = f.isSuccess();

            //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
            if (!succeed) {
                logger.info("Reconnected failed.");
                f.channel().pipeline().fireChannelInactive();
            }else{
                logger.info("Reconnected success.");
            }
        });
        
    }

    /**
     * Manage Netty handlers
     * @return
     */
    @Override
    public ChannelHandler[] handlers() throws Exception {

        //add SSL handler
        if(ssl){
            URL certChainUrl = this.getClass().getClassLoader().getResource("cert/client/client.crt");
            File certChainFile = new File(certChainUrl.toURI());

            URL keyUrl = this.getClass().getClassLoader().getResource("cert/client/pkcs8_client.key");
            File keyFile = new File(keyUrl.toURI());

            URL rootCaUrl = this.getClass().getClassLoader().getResource("cert/client/ca.crt");
            File rootCaFile = new File(rootCaUrl.toURI());
            SslContext sslCtx = SslContextBuilder.forClient()
                        .keyManager(certChainFile, keyFile)
                        .trustManager(rootCaFile)
                        .build();

            channelHandlerList.add(sslCtx.newHandler(ByteBufAllocator.DEFAULT));
        }

        //add decoder handlers
        channelHandlerList.add(new StringDecoder());

        //add encoder handlers
        channelHandlerList.add(new StringEncoder());

        //add idle handlers
        channelHandlerList.add(new IdleStateHandler(0, 4, 0, TimeUnit.SECONDS));

        //add connector idle trigger
        channelHandlerList.add(new ConnectorIdleStateTrigger());

        //add heartbeat handlers
        channelHandlerList.add(new HeartbeatClientHandler());

        //add sharable custom channel handlers
        for(ChannelHandler channelHandler : sharableCustomChannelHandlerList){
            channelHandlerList.add(channelHandler);
        }

        //Return: list to array
        ChannelHandler[] channelHandlers = new ChannelHandler[channelHandlerList.size()];
        return channelHandlerList.toArray(channelHandlers);
    }
}
