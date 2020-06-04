package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.odata.Client;
import com.innedhub.requests.MLSRequest;
import com.innedhub.requests.SyncGetMLSRequest;
import com.innedhub.results.PropertyTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;


import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class SingleModeMLSGridClient implements MLSGridClient {
    private String apiUri;
    private String apiKey;

    @Override
    public List<PropertyTO> searchResult(MLSResource resource, String request) {
        Client client = new Client(apiUri, apiKey);
        return client.doRequestWithFilter(resource, request);
    }

    //old realization with okhttp
    @Override
    public List<PropertyTO> searchResultOkHttp(MLSResource resource, String... params) {
        //TODO
        //invoke doRequest in SyncGetMLSRequest, get json string response, handle it to List<PropertyTO> - for it invoke special handler. Handler can differ for different MLSResource. So handler should be invoked by interface.
        MLSRequest request = new SyncGetMLSRequest();
        String responseString = request.doRequest(resource, apiUri, apiKey, params);
        log.info("json response from server: {}", responseString);
        return null;
    }
}
