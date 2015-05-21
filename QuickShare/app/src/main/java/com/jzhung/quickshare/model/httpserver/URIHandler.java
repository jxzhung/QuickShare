package com.jzhung.quickshare.model.httpserver;

import android.text.TextUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 参数解析器
 * @author i@jzhung.com
 *         content://com.example.project:200/folder/subfolder/etc
 *         \-------/\------------------/\---/\------------------/ scheme host
 *         port path \-------authority-------/
 */
public class URIHandler {
	private String URI;
	private String scheme = "";
	private String host = "";
	private int port = -1;
	private String path = "";
	private String file = "";
	private Map<String, String> paremeterMap = new HashMap<String, String>();

	@Override
	public String toString() {
		return "URIHandler{" + "URI='" + URI + '\'' + ", scheme='" + scheme
				+ '\'' + ", host='" + host + '\'' + ", port=" + port
				+ ", path='" + path + '\'' + ", file='" + file + '\''
				+ ", paremeterMap=" + paremeterMap + '}';
	}

	public URIHandler(String url) {
        java.net.URI uri = java.net.URI.create(url);
		setURI(url);
        setScheme(uri.getScheme());
		setHost(uri.getHost());
        setPort(uri.getPort()==-1?80:uri.getPort());
        String path = uri.getPath();
        setPath(path);
        setFile(path.substring(path.lastIndexOf("/"), path.length()));
        String query = uri.getQuery();
        if(query!=null){
            StringTokenizer paramst = new StringTokenizer(query, "&");
            String part = null;
            String key = null;
            String value = null;
            while (paramst.hasMoreElements()) {
                part = paramst.nextToken();
                key = part.substring(0, part.indexOf("="));
                value = part.substring(part.indexOf("=") + 1);
                paremeterMap.put(key, URLDecoder.decode(value));
            }
        }

	}


	public String getParameter(String key) {
		return paremeterMap.get(key);
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getURI() {
		return URI;
	}

	public void setURI(String URI) {
		this.URI = URI;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public Map<String, String> getParemeterMap() {
		return paremeterMap;
	}

	public void setParemeterMap(Map<String, String> paremeterMap) {
		this.paremeterMap = paremeterMap;
	}

}
