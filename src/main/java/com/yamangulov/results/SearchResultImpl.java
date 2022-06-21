package com.yamangulov.results;

import lombok.AllArgsConstructor;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
public class SearchResultImpl implements SearchResult {
    private final List<PropertyTO> propertyTOList;
    private final URI nextPage;
    private final boolean hasNextPage;

    public List<PropertyTO> getPropertyTOList() {
        return this.propertyTOList;
    }

    @Override
    public URI nextPage() {
        return this.nextPage;
    }

    @Override
    public boolean hasNextPage() {
        return this.hasNextPage;
    }
}
