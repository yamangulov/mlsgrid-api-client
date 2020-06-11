package com.innedhub;

import com.amazonaws.services.s3.AmazonS3;
import com.innedhub.enums.MLSResource;
import com.innedhub.results.PropertyTO;
import lombok.Getter;

import java.util.List;
public interface MLSGridClient {

    List<PropertyTO> searchResult(MLSResource resource, String request);

    void getAndSaveAllImages(String mlsNumber);

    void getAndSaveAllImages(String mlsNumber, int limit);

    void initAmazonConnection(String bucketName, String region, String awsAccessKey, String awsSecretKey, MLSGridClient currentGridClient);

    AmazonS3 getAmazonS3();
}
