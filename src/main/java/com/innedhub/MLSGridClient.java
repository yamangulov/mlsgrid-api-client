package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import com.innedhub.results.PropertyTO;

import java.util.List;

public interface MLSGridClient {
    List<PropertyTO> searchResultOkHttp(MLSResource resource, String... params);
    List<PropertyTO> searchResult(MLSResource resource, String request);
}
