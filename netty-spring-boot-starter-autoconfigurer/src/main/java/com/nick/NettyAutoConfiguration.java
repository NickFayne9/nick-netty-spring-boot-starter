package com.nick;

import com.nick.netty.client.NettyClientTemplate;
import com.nick.netty.server.NettyServerTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring auto-configuration for netty4
 * @author NickFayne 2018-07-02 19:02
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(NettyProperties.class)
public class NettyAutoConfiguration {

    @Bean
    public NettyClientTemplate nettyClientTemplate(NettyProperties nettyProperties){
        return new NettyClientTemplate(nettyProperties);
    }

    @Bean
    public NettyServerTemplate nettyServerTemplate(NettyProperties nettyProperties){
        return new NettyServerTemplate(nettyProperties);
    }
}
