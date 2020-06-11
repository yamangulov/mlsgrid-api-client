package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import com.innedhub.odata.Client;
import com.innedhub.results.PropertyTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter
@Setter
class ServiceModeMLSGridClient implements MLSGridClient {
    private String apiUri;
    private String apiKey;
    private String apiServiceKey;
    private String bucketName;
    private AmazonS3 amazonS3;
    private MLSGridClient currentGridClient;

    public ServiceModeMLSGridClient(String apiUri, String apiKey, String apiServiceKey) {
        this.apiUri = apiUri;
        this.apiKey = apiKey;
        this.apiServiceKey = apiServiceKey;
    }

    @Override
    public List<PropertyTO> searchResult(MLSResource resource, String request) {
        List<PropertyTO> listProperties = null;
        try {
            if (checkApiKey(apiKey)) {
                Client client = new Client(apiUri, apiServiceKey);
                listProperties = client.doRequestWithFilter(resource, request);
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }

        return listProperties;
    }

    @Override
    public void initAmazonConnection(String bucketName, String region, String awsAccessKey, String awsSecretKey, MLSGridClient currentGridClient) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        this.bucketName = bucketName;
        this.amazonS3 = amazonS3;
        this.currentGridClient = currentGridClient;
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber) {

    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {

    }

    private boolean checkApiKey(String apiKey) {
        //TODO
        //realize method checkApiKey for checking apiKey of client in SERVICE mode
        return false;
    }
}
