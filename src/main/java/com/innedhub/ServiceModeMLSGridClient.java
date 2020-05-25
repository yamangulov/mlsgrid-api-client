package com.innedhub;

import com.innedhub.enums.MLSResource;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
class ServiceModeMLSGridClient implements MLSGridClient {
    String apiUri;
    String apiKey;
    String apiServiceKey;

    @Override
    public Map<String, String> searchResult(MLSResource resource, String... params) {
        return null;
    }
}
