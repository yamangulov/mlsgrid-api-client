package com.innedhub;

import com.innedhub.keystore.stores.KeyStore;

public class MLSGridFactory {

    //overloaded method for SINGLE factory mode
    public static MLSGridClient createClient(String apiUri, String apiKey) {
       return new SingleModeMLSGridClient(apiUri, apiKey);
    }
    //overloaded method for SERVICE factory mode
    public static MLSGridClient createClient(String apiUri, String apiKey, String apiServiceKey) {
        return new ServiceModeMLSGridClient(apiUri, apiKey, apiServiceKey);
    }

    //method for fulfill storeState in memento storage for using in SERVICE factory mode
    public static void initKeyStore(KeyStore keyStore) {
        try {
            throw new NoSuchMethodException("Method is not implemented");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
