package com.innedhub.keystore.memento.classes;

import java.util.Map;

public class Memento implements com.innedhub.keystore.memento.interfaces.Memento {
    //state of store, key - name of service client, value - apiKey of service client
    private Map<String, String> storeState;

    public Memento(Map<String, String> storeState) {
        this.storeState = storeState;
    }


    @Override
    public Map<String, String> getStoreState() {
        return storeState;
    }

    @Override
    public void setStoreState(Map<String, String> serviceClientsMap) {
        this.storeState = serviceClientsMap;
    }
}
