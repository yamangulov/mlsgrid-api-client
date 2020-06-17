package com.innedhub.results;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.URI;
import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultImpl implements SearchResult {
    private final List<PropertyTO> propertyTOList;
    private final URI nextPage;
    private final boolean hasNextPage;
}
