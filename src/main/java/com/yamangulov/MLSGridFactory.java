package com.yamangulov;

import com.yamangulov.keystore.stores.KeyStore;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MLSGridFactory {

    //overloaded method for SINGLE factory mode
    public MLSGridClient createClient(String apiUri, String apiKey) {
       return new SingleModeMLSGridClient(apiUri, apiKey);
    }
    //overloaded method for SERVICE factory mode
    public MLSGridClient createClient(String apiUri, String apiKey, String apiServiceKey) {
        return new ServiceModeMLSGridClient(apiUri, apiKey, apiServiceKey);
    }

    //method for fulfill storeState in memento storage for using in SERVICE factory mode
    public void initKeyStore(KeyStore keyStore) {
        try {
            throw new NoSuchMethodException("Method is not implemented");
        } catch (NoSuchMethodException e) {
            log.info(e.getMessage());
        }
    }
}
