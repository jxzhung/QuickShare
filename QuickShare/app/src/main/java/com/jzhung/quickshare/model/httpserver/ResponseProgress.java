package com.jzhung.quickshare.model.httpserver;

/**
 * 响应实体数据对象
 * @author i@jzhung.com
 */
public class ResponseProgress {
    private String user;
    private String host;
    private long totalData;
    private long sendData;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;

    public long getTotalData() {
        return totalData;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setTotalData(long totalData) {
        this.totalData = totalData;
    }

    public long getSendData() {
        return sendData;
    }

    public void setSendData(long sendData) {
        this.sendData = sendData;
    }
}
