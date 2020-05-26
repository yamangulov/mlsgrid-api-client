package com.innedhub.requests;

import com.innedhub.PropsLoader;
import com.innedhub.enums.MLSResource;

import java.util.Properties;

public class SyncGetMLSRequest implements MLSRequest {
    //overloaded method for SINGLE factory mode
    public static String doRequest(MLSResource resource, String apiUri, String apiKey, String... params) {
        Properties properties = new Properties();
        PropsLoader.load(properties);
        //TODO
        //build and make request to mls api, that returns json string response
        return null;
    }
    //overloaded method for SERVICE factory mode
    public static String doRequest(MLSResource resource, String apiUri, String apiKey, String apiServiceKey, String... params) {
        Properties properties = new Properties();
        PropsLoader.load(properties);
        //TODO
        //build and make request to mls api, that returns json string response
        return null;
    }
}
