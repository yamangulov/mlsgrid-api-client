package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
class ServiceModeMLSGridClient implements MLSGridClient {
    String apiUri;
    String apiKey;
    String apiServiceKey;

    @Override
    public List<PropertyTO> searchResult(MLSResource resource, String... params) {
        //TODO
        //invoke doRequest in SyncGetMLSRequest, get json string response, handle it to List<PropertyTO> - for it invoke special handler. Handler can differ for different MLSResource. So handler should be invoked by interface.
        return null;
    }
}
