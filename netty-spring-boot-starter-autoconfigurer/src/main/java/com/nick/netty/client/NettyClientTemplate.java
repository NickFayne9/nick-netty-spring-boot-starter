package com.nick.netty.client;

import com.nick.NettyProperties;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Netty client template class
 * @author NickFayne 2018-07-03 5:37
 * @since 1.0.0
 */
public class NettyClientTemplate implements InitializingBean, DisposableBean {

    //# ==========properties==========
    private String serverHost;
    private Integer serverPort;
    private boolean ssl;

    //# ==========fields==========
    private NettyClient nettyClient;

    //# ==========properties==========
    private NettyProperties nettyProperties;

    public NettyClientTemplate(NettyProperties nettyProperties) {
        this.nettyProperties = nettyProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serverHost = nettyProperties.getServerHost();
        serverPort = nettyProperties.getServerPort();
        ssl = nettyProperties.getSsl();

        nettyClient = new NettyClient(serverHost, serverPort, ssl);
    }

    public void addClientCustomHandler(ChannelHandler channelHandler){
        nettyClient.addClientHandler(channelHandler);
    }

    public void connect(){
        try {
            nettyClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String msg){
        return nettyClient.sendMsg(msg);
    }

    @Override
    public void destroy() throws Exception {

    }

}
