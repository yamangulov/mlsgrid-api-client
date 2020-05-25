package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.requests.MLSRequest;

import java.util.Map;

public interface MLSGridClient {
    Map<String, String> searchResult(MLSResource resource, String... params);
}
