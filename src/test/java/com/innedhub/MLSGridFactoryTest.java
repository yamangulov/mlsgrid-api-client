package com.innedhub;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class MLSGridFactoryTest {

    private String apiUri = "https://api.mlsgrid.com/";

    private MLSGridFactory factory = new MLSGridFactory();

    @Test
    @DisplayName("Creating odata client for single mode")
    void createClientSingleMode() {
        String apiKey = "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8";
        MLSGridClient client = factory.createClient(apiUri, apiKey);
        assertEquals(SingleModeMLSGridClient.class, client.getClass());
    }

    @Test
    @DisplayName("Creating odata client for service mode")
    void createClientServiceMode() {
        String apiServiceKey = "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8";
        String apiKey = "someKey";
        MLSGridClient client = factory.createClient(apiUri, apiKey, apiServiceKey);
        assertEquals(ServiceModeMLSGridClient.class, client.getClass());
    }

    @Test
    @DisplayName("Initial fulfill local keystore in service mode")
    @Disabled
    void initKeyStore() {
    }
}