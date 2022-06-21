package com.yamangulov.keystore.memento;

import java.util.Map;

public interface Memento {
    Map<String, String> getStoreState();
    void setStoreState(Map<String, String> serviceClientsMap);
}
