package com.innedhub;

import com.innedhub.enums.MLSResource;
import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
class SingleModeMLSGridClient implements MLSGridClient {
    String apiUri;
    String apiKey;

    @Override
    public Map<String, String> searchResult(MLSResource resource, String... params) {
        return null;
    }
}
