package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import com.innedhub.requests.MLSRequest;
import com.innedhub.requests.SyncGetMLSRequest;
import com.innedhub.results.PropertyTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
class ServiceModeMLSGridClient implements MLSGridClient {
    private String apiUri;
    private String apiKey;
    private String apiServiceKey;

    @Override
    public List<PropertyTO> searchResult(MLSResource resource, String... params) {
        //TODO
        //invoke doRequest in SyncGetMLSRequest, get json string response, handle it to List<PropertyTO> - for it invoke special handler. Handler can differ for different MLSResource. So handler should be invoked by interface.
        MLSRequest request = new SyncGetMLSRequest();
        String responseString = request.doRequest(resource, apiUri, apiKey, apiServiceKey, params);
        log.info("json response from server: {}", responseString);
        return null;
    }
}
