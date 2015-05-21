package com.jzhung.quickshare.model.httpserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求列表
 * @author i@jzhung.com
 */
public class RequestQueue {
    public void setResponseProgressList(List<ResponseProgress> responseProgressList) {
        this.responseProgressList = responseProgressList;
    }

    private List<ResponseProgress> responseProgressList = new ArrayList<>();
    //响应
    private Map<String, HttpRequestHandler> requestMap = new HashMap<String, HttpRequestHandler>();

    public List<ResponseProgress> getResponseProgressList(){
        return responseProgressList;
    }
}
