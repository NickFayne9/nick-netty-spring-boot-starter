package com.nick.netty.server.channelInitializer;

import com.nick.netty.server.handlerAdapter.AcceptorIdleStateTrigger;
import com.nick.netty.server.handlerAdapter.HeartBeatServerHandler;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * System default ChannelInitializer
 * @author NickFayne 2018-07-03 12:32
 * @since 1.0.0
 */
public class DefaultChannelInitializer extends AbstractChannelInitializer {

    private Boolean ssl;

    private List<ChannelHandler> customChannelHandlerList;

    public DefaultChannelInitializer(boolean ssl, List<ChannelHandler> customChannelHandlerList) {
        this.ssl = ssl;
        this.customChannelHandlerList = customChannelHandlerList;

        try {
            initChannelHandlers();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initChannelHandlers() throws Exception {
        if(ssl){
            URL certChainUrl = this.getClass().getClassLoader().getResource("cert/server/server.crt");
            File certChainFile = new File(certChainUrl.toURI());

            URL keyUrl = this.getClass().getClassLoader().getResource("cert/server/pkcs8_server.key");
            File keyFile = new File(keyUrl.toURI());

            URL rootCaUrl = this.getClass().getClassLoader().getResource("cert/server/ca.crt");
            File rootCaFile = new File(rootCaUrl.toURI());
            SslContext sslCtx = SslContextBuilder.forServer(certChainFile, keyFile)
                    .trustManager(rootCaFile)
                    .clientAuth(ClientAuth.REQUIRE)
                    .build();


            addHandler(sslCtx.newHandler(ByteBufAllocator.DEFAULT));
        }

        addHandler(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));

        addHandler(new AcceptorIdleStateTrigger());

        addHandler(new StringDecoder());

        addHandler(new StringEncoder());

        addHandler(new HeartBeatServerHandler());

        for(ChannelHandler channelHandler : customChannelHandlerList){
            addHandler(channelHandler);
        }
    }

}
