package com.innedhub.keystore.stores;

import java.util.Map;

public interface KeyStore {
    Map<String, String> getKeyStore();
    void setKeyStore(Map<String, String> keyStore);
}
