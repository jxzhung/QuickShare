package com.jzhung.quickshare.model.httpserver;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Android Web服务器
 * 
 * @author i@jzhung.com
 */
public class HttpServer {
    private static HttpServer httpServer;
    private static ServerConfig serverConfig;
    private static final String TAG = "webserver";
    private ServerSocket server = null;
    private static RequestQueue requestQueue = new RequestQueue();
    private boolean shutdown = false;

    /**
     * 获取服务器实例
     * @param serverConfig
     * @return
     */
    public static synchronized  HttpServer getInstance(ServerConfig serverConfig){
        if(httpServer ==null){
            httpServer = new HttpServer(serverConfig);
        }
        return httpServer;
    }

    /**
     * 私有构造器
     * @param serverConfig
     */
    private HttpServer(ServerConfig serverConfig){
        this.serverConfig = serverConfig;
    }

    public static ServerConfig getServerConfig() {
        return serverConfig;
    }

    /**
     * 开启服务器
     * @return
     */
	public boolean start() {
        return startServer(null);
	}

    /**
     * 开启服务器,返回进度主界面
     * @param handler
     * @return
     */
    public boolean startWithHandler(Handler handler) {
        return startServer(handler);
    }

    private boolean startServer(Handler handler){
        setShutdown(false);//重置服务器状态
        Log.i(TAG, "开启服务器");
        boolean result = false;
        SocketAddress socketAddr = new InetSocketAddress(serverConfig.getPort());
        try {
            // 监听服务器端口，等待连接请求
            server = new ServerSocket();
            server.setReuseAddress(true);
            server.bind(socketAddr);

            Log.i(TAG, "web服务器运行在: " + server.getLocalPort());
            result = true;

            // 显示启动信息
            while (!shutdown) {
                Socket socket = server.accept();

                //socket.setSoTimeout(1000); //设置超时时间
                Log.i(TAG, "收到新请求 " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                // 创建子线程处理
                try {
                    if(handler != null){

                        Thread thread = new Thread(new HttpRequestHandler(socket, handler));
                        thread.start();
                    }else{
                        Thread thread = new Thread(new HttpRequestHandler(socket));
                        thread.start();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (IOException e) {
            Log.i(TAG, "异常....");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 关闭服务器
     */
    public void shutdown() {
        shutdown = true;
        if(server != null){
            try {
				Log.i(TAG, "关闭服务器");
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
    public static String getServerName(){
        return "JWebServer";
    }

    public static String getServerVersion(){
        return "v1.0.2";
    }

    public static String getServerDescription(){
        return "JWebServer is a simple web server for android, support:http get, code by jzhung i@jzhung.com";
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static void setRequestQueue(RequestQueue requestQueue) {
        HttpServer.requestQueue = requestQueue;
    }
}