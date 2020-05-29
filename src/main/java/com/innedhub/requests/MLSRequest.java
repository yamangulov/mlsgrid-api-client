package com.innedhub.requests;

import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;

public interface MLSRequest {
    //overloaded method for SINGLE factory mode
    String doRequest(MLSResource resource, String apiUri, String apiKey, String... params);

    //overloaded method for SERVICE factory mode
    String doRequest(MLSResource resource, String apiUri, String apiKey, String apiServiceKey, String... params) throws NotValidKeyForServiceModeException;
}
