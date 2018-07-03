package com.nick.netty.server;

import com.nick.NettyProperties;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Netty server template class
 * @author NickFayne 2018-07-03 5:43
 * @since 1.0.0
 */
public class NettyServerTemplate implements InitializingBean, DisposableBean {

    private NettyServer nettyServer;

    private NettyProperties nettyProperties;

    public NettyServerTemplate(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Integer serverPort = nettyProperties.getServerPort();
        Boolean ssl = nettyProperties.getSsl();

        nettyServer = new NettyServer(serverPort, ssl);
    }

    public void addHandlers(ChannelHandler channelHandler){

    }

    public void start(){
        nettyServer.start();
    }

    @Override
    public void destroy() throws Exception {

    }

}
