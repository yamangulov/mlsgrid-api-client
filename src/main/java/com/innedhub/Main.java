package com.innedhub;

import com.innedhub.enums.MLSResource;
import lombok.extern.slf4j.Slf4j;

//class had used for testing in process of development and will be deleted after
public class Main {
    public static void main(String[] args) {
        MLSGridFactory factory = new MLSGridFactory();
        MLSGridClient gridClient = factory.createClient("api.mlsgrid.com", "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8");
        //not used searchable fields should be null or "" (empty string)
        gridClient.searchResult(MLSResource.PROPERTY_RESI, null, null, null, null, "true");
    }
}
