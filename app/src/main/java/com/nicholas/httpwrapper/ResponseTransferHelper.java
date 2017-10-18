package com.nicholas.httpwrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Drew Relyea on 10/17/2017.
 */

public class ResponseTransferHelper {
    private static final ResponseTransferHelper ourInstance = new ResponseTransferHelper();
    private static Map<String, String> myMap = new HashMap<>();

    public static ResponseTransferHelper getInstance() {
        return ourInstance;
    }

    private ResponseTransferHelper() {
    }

    public void addPair(String key, String response) {
        myMap.put(key, response);
    }

    public String getValue(String key) {
        String temp = myMap.get(key);
        myMap.remove(key);
        return temp;
    }
}
