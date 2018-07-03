package com.nick;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Netty properties class
 * @author NickFayne 2018-07-02 19:05
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "spring.netty")
public class NettyProperties {
    private String serverHost;
    private Integer serverPort;
    private Boolean ssl;

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }
}

