package com.innedhub.results;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JsonMLSResult implements MLSResult {
    private List<PropertyTO> result;
}
