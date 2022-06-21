package com.yamangulov.results;

import java.util.Map;

public interface PropertyTO {
    void addSingleOption(String key, String value);
    String getSingleOption(String key);
    Map<String, String> getOptions();
}
