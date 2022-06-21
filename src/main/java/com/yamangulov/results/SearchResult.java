package com.yamangulov.results;

import java.net.URI;
import java.util.List;

public interface SearchResult {
    List<PropertyTO> getPropertyTOList();
    URI nextPage();
    boolean hasNextPage();
}
