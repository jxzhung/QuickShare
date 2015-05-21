package com.jzhung.quickshare.model.httpserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
/**
 * 封装请求对象
 * @author i@jzhung.com
 *
 */
public class Request {
    private static final String TAG = "webserver";
	private BufferedReader br;
	private String[] Headers;
	private String requestPath;
	private String requestMethod;
	private String session;
    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }


	public Request(InputStream in){
		this.br = new BufferedReader(new InputStreamReader(in));
		try {
			parseRequest(br);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解析请求
	 * @param br
	 * @throws java.io.IOException
	 */
	private void parseRequest(BufferedReader br) throws IOException{
		Log.i(TAG, "线程："+Thread.currentThread().getName());
        if(br == null){
            Log.i(TAG, "输入流为空");
            return;
        }
		String line = null;
		//构造对象属性
		while ((line = br.readLine()) != null) {
            Log.i(TAG, "线程："+Thread.currentThread().getName()+" 内容:"+ line);
			//到达请求数据末尾跳出
			if (line.equals("rn") || line.equals("")){
				break;
			}
			StringTokenizer st = new StringTokenizer(line);
			//get请求
			if (line.startsWith("GET")) {
				setRequestMethod(st.nextToken());
				String path = st.nextToken();
				setRequestPath(path);
			}
			//post请求
			if (line.startsWith("POST")) {
				setRequestMethod(st.nextToken());
				setRequestPath(st.nextToken());
			}
			
			//session
			if (line.startsWith("Cookie")) {
				//String session = headerLine.substring(headerLine.lastIndexOf("JSESSIONID="), endIndex)
				setSession(line);
			}

            if (line.startsWith("Host")) {
                //String session = headerLine.substring(headerLine.lastIndexOf("JSESSIONID="), endIndex)
                st.nextToken(":");
                setHost(st.nextToken(":").trim());
            }
		}
	}
	

	public String[] getHeaders() {
		return Headers;
	}

	private void setHeaders(String[] headers) {
		Headers = headers;
	}

	public String getRequestPath() {
		return requestPath;
	}

	private void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getSession() {
		return session;
	}

	private void setSession(String session) {
		this.session = session;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	private void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

}
