package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.odata.Client;
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
}
