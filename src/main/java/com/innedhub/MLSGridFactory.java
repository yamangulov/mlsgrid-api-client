package com.innedhub;

import com.innedhub.client.MLSGridClient;
import com.innedhub.keystore.stores.KeyStore;

public class MLSGridFactory {
    //overloaded method for SINGLE factory mode
    static MLSGridClient createClient(String apiUri, String apiKey) {
        try {
            throw new NoSuchMethodException("Method is not implemented");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    //overloaded method for SERVICE factory mode
    static MLSGridClient createClient(String apiUri, String apiKey, String apiServiceKey) {
        try {
            throw new NoSuchMethodException("Method is not implemented");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    //method for fulfill storeState in memento storage for using in SERVICE factory mode
    static void initKeyStore(KeyStore keyStore) {
        try {
            throw new NoSuchMethodException("Method is not implemented");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
