package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;

import java.util.List;

public interface MLSGridClient {
    List<PropertyTO> searchResult(MLSResource resource, String request);
}
