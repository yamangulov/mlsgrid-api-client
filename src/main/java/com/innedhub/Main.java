package com.innedhub;

import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//class had used for testing in process of development and will be deleted after
@Slf4j
public class Main {
    public static void main(String[] args) {

        MLSGridFactory factory = new MLSGridFactory();
        MLSGridClient gridClient = factory.createClient("https://api.mlsgrid.com/", "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8");

        List<PropertyTO> searchResult = gridClient.searchResult(MLSResource.PROPERTY_RESI, "ListingId eq 'MRD06341151' or ListingId eq 'MRD06340449' and MlgCanView eq true");
        log.info("Search result:\n" + searchResult);
    }
}
