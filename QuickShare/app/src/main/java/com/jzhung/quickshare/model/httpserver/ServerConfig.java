package com.jzhung.quickshare.model.httpserver;

/**
 * 服务器配置
 * @author i@jzhung.com
 */
public class ServerConfig {
    private int port;//服务器端口
    private String homePath;//服务器主目录
    private String localIp; //本机IP

    /**
     * 服务器配置对象
     * @param localIp 本机IP
     * @param port web服务端口
     * @param homePath 服务器根路径
     */
    public ServerConfig(String localIp, int port, String homePath){
        this.localIp = localIp;
        this.port = port;
        this.homePath = homePath;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

}
