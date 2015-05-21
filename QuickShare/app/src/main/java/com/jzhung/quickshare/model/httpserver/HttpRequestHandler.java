package com.jzhung.quickshare.model.httpserver;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URLEncoder;

/**
 * 请求处理器
 * @author i@jzhung.com
 *
 */
public class HttpRequestHandler implements Runnable {
    private static final String TAG = "webserver";
	private Socket socket;
	private InputStream inputStream;
	private PrintStream printStream;
    private URIHandler uriHandler;
    private ResponseProgress responseProgress;
    private Handler handler;

	/**
	 * 构造方法
	 * @param socket 客户端Socket
	 * @throws java.io.IOException 读取Socket的输入流或输出流错误
	 */
	public HttpRequestHandler(Socket socket) throws IOException{
		this.socket = socket;
		this.inputStream = socket.getInputStream();
		this.printStream= new PrintStream(socket.getOutputStream());
        responseProgress = new ResponseProgress();
        responseProgress.setHost(socket.getInetAddress().getHostAddress());
    }

    /**
     * 传递进度的构造方法
     * @param socket 客户端Socket
     * @throws java.io.IOException 读取Socket的输入流或输出流错误
     */
    public HttpRequestHandler(Socket socket, Handler handler) throws IOException{
        Log.i(TAG,"启动并开启进度更新");
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.printStream= new PrintStream(socket.getOutputStream());
        this.handler = handler;
        responseProgress = new ResponseProgress();
        responseProgress.setHost(socket.getInetAddress().getHostAddress());
    }


    /**
	 * 处理器
	 */
	public void run() {
        Log.i(TAG, "处理新请求");
		processRequest();
	}

	/**
	 * 处理请求
	 * @throws Exception
	 */
	private void processRequest(){

        Request request = new Request(inputStream);
        //请求路径
		String path = request.getRequestPath();
        String host  = request.getHost();

        //HttpServer.getRequestQueue().getResponseProgressList().add(responseProgress);

        uriHandler = new URIHandler(host + path);
        //请求用户
        String user = uriHandler.getParameter("user");
        responseProgress.setUser(user);
        Log.i(TAG, "请求路径："+path);
        Log.i(TAG, "请求文件："+uriHandler.getFile());
        Log.i(TAG, "请求参数："+uriHandler.getParemeterMap());
		String file = uriHandler.getFile();

		// 打开所请求的文件
		FileInputStream fileInputStream = null;

		// 发送到服务器信息
		try {
            if(file.equals("/down")){
                String fileName = uriHandler.getParameter("file");
                responseProgress.setFileName(fileName);
                String filepath = HttpServer.getServerConfig().getHomePath()+fileName;
                File dfile = new File(filepath);
                Log.i(TAG, "文件："+filepath+" 存在情况："+dfile.exists());
                if (dfile.exists()) {
                    fileInputStream = new FileInputStream(dfile);
                    //如果是二进制文件
                    Log.i(TAG, "二进制文件");
                    writeFile(printStream, fileInputStream, fileName);
                } else {
                    write404(printStream);
                }
            }else{
                writeString(printStream, "<html><head><title>Server is Running!</title></head><body>" +
                        "<h1>Server is Running!</h1><p>"+HttpServer.getServerDescription()+"</p></body></html>");
            }
            printStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			release(printStream, fileInputStream, fileInputStream, socket);
		}
	}

	/**
	 * 释放所有资源
	 * @param printStream
	 * @param inputStream
	 * @param fileInputStream
	 * @param socket
	 */
	public void release(PrintStream printStream,
			InputStream inputStream, FileInputStream fileInputStream, Socket socket){
		try {
			if (printStream != null) {
				printStream.close();
			}
			if (inputStream != null) {
				inputStream.close();//后面socket已经关闭了。
			}
			if (fileInputStream != null) {
				fileInputStream.close();
			}
			if (socket != null && socket.isClosed() == false) {
                //socket.shutdownInput();
                //socket.shutdownOutput();
                //socket.getInputStream().close();
               // socket.getOutputStream().close();
                socket.close();
			}
            Log.i(TAG, "释放资源");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 向客户端写响应头部
	 * @param ps
	 * @param status
	 * @param contentType
	 * @param contentLen
	 */
	private void writeHeader(PrintStream ps, String status, String contentType, int contentLen){
		ps.println("HTTP/1.1 "+status);
		ps.println("Server: "+HttpServer.getServerName());
		ps.println("Content-Type:"+contentType); //返回的类型
		ps.println("Content-length:"+contentLen);
		ps.println("Connection: close");// 请求完毕立即断开连接
		ps.println("Allow: GET");
		ps.println("Expires: 0");
	}
	
	/**
	 * 向客户端直接返回字符串
	 * @param ps
	 * @param body
	 */
	private void writeString(PrintStream ps, String body){
		Log.i(TAG, "返回文本类型");
		writeHeader(ps, "200 OK", "Content-Type: text/html", body.length());
		ps.println();//响应头与主体内容之间必需空一行，否则浏览器无法读取内容
		ps.println(body);
	}
	
	/**
	 * 向客户端返回404错误
	 * @param ps
	 */
	private void write404(PrintStream ps){
		Log.i(TAG, "返回404");
		String html="<html><head><title>404 not fount</title></head><body><h1>404 not found.</h1><p>"+HttpServer.getServerDescription()+"</p></body></html>";
		writeHeader(ps, "404 Not Found", "Content-Type: text/html", html.length());
		ps.println();//响应头与主体内容之间必需空一行，否则浏览器无法读取内容
		ps.println(html);
	}
	
	/**
	 * 向客户端写文件
	 * @param ps
	 * @param fis
	 */
	private void writeFile(PrintStream ps, FileInputStream fis, String fileName){
		try {
			Log.i(TAG, "返回文件"+fileName);
			writeHeader(ps, "200 OK", "application/octet-stream", fis.available());
            //响应对象总大小
            responseProgress.setTotalData(fis.available());
            fileName = URLEncoder.encode(fileName,"utf-8");
            ps.println("Content-Disposition: attachment; filename=\""+fileName+"\"; " +" filename*=utf-8''"+fileName); //为了兼容IE6文件名需要带扩展名
			ps.println();//响应头与主体内容之间必需空一行，否则浏览器无法读取内容

            if(handler != null){
                Log.i(TAG, "运行更新进度");
                sendBytesAndUpdateProgress(ps, fis);//发送的时候更新进度
            }else{
                sendBytes(ps, fis);
            }

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向客户端写文件
	 * @param fis
	 * @param ps
	 * @throws Exception
	 */
	private void sendBytes(PrintStream ps, FileInputStream fis) throws Exception {
		// 创建一个 1K buffer
		byte[] buffer = new byte[1024];
		int bytes = 0;
        long send = 0L;
		// 将文件输出到套接字输出流中
		while ((bytes = fis.read(buffer)) != -1) {
			ps.write(buffer, 0, bytes);
            responseProgress.setSendData(send+buffer.length);
		}
	}

    /**
     * 向客户端写文件并更新进度
     * @param fis
     * @param ps
     * @throws Exception
     */
    private void sendBytesAndUpdateProgress(PrintStream ps, FileInputStream fis) throws Exception {
        // 创建一个 1K buffer
        byte[] buffer = new byte[1024*16];
        int bytes = 0;
        long send = 0L;
        // 间隔
        long jiange = 100;
        long last  = System.currentTimeMillis();
        // 将文件输出到套接字输出流中
        while ((bytes = fis.read(buffer)) != -1) {
            ps.write(buffer, 0, bytes);
            //实时更新传输状态
            send = send+bytes;
            responseProgress.setSendData(send);
            long now = System.currentTimeMillis();
            Message msg = handler.obtainMessage();

            if(send == responseProgress.getTotalData()){
                msg.what=1;
                msg.obj = responseProgress; //可以是基本类型，可以是对象，可以是List、map等；
                Log.i(TAG,"用户："+responseProgress.getUser()+" 主机："+responseProgress.getHost()+" 总大小："+responseProgress.getTotalData()+" 已传输:"+ responseProgress.getSendData());
                handler.sendMessage(msg);
            }else{
                msg.what=0;
                if(now-last > jiange){
                    last = now;
                    //更新界面线程。。
                    msg.obj = responseProgress; //可以是基本类型，可以是对象，可以是List、map等；
                    Log.i(TAG,"用户："+responseProgress.getUser()+" 主机："+responseProgress.getHost()+" 总大小："+responseProgress.getTotalData()+" 已传输:"+ responseProgress.getSendData());
                    handler.sendMessage(msg);
                }
            }
        }
        Log.i(TAG,"文件发送完毕...");
    }

	/**
	 * 文件类型
	 * @param fileName
	 * @return
	 */
	private String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html") || fileName.endsWith(".jsp")|| fileName.endsWith(".txt")) {
			return "text/HTML";
		}
		return "application/octet-stream";
	}
}
