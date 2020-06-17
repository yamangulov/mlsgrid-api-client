package com.innedhub.results;

import java.net.URI;
import java.util.List;

public interface SearchResult {
    List<PropertyTO> getPropertyTOList();
    URI getNextPage();
    boolean isHasNextPage();
}
