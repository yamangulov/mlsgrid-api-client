package com.yamangulov.keystore.stores;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class MockKeyStore implements KeyStore {
    Map<String, String> keystore;

    @Override
    public Map<String, String> getKeyStore() {
        return keystore;
    }

    @Override
    public void setKeyStore(Map<String, String> keyStore) {
        this.keystore = keyStore;
    }
}
