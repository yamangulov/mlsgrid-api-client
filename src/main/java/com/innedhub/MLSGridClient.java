package com.innedhub;

import com.amazonaws.services.s3.AmazonS3;
import com.innedhub.enums.MLSResource;
import com.innedhub.results.SearchResult;

import java.net.URI;
public interface MLSGridClient {

    SearchResult searchResult(MLSResource resource, String request);

    SearchResult searchResult(MLSResource resource, String request, int top);

    SearchResult searchResult(URI nextPage);

    void getAndSaveAllImages(String mlsNumber);

    void getAndSaveAllImages(String mlsNumber, int limit);

    void initAmazonConnection(String bucketName, String region, String awsAccessKey, String awsSecretKey, MLSGridClient currentGridClient);

    AmazonS3 getAmazonS3();
}
