package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.innedhub.enums.MLSResource;
import com.innedhub.odata.Client;
import com.innedhub.results.PropertyTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@Slf4j
public class SingleModeMLSGridClient implements MLSGridClient {
    private String apiUri;
    private String apiKey;
    private String bucketName;
    private AmazonS3 amazonS3;
    private MLSGridClient currentGridClient;

    public SingleModeMLSGridClient(String apiUri, String apiKey) {
        this.apiUri = apiUri;
        this.apiKey = apiKey;
    }

    @Override
    public List<PropertyTO> searchResult(MLSResource resource, String request) {
        Client client = new Client(apiUri, apiKey);
        return client.doRequestWithFilter(resource, request);
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
        List<PropertyTO> listMedia = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "'");
        for (PropertyTO media : listMedia) {
            //TODO
            //it needs to realize uploading each photo to our AWS by URL or by object key and bucket name in origin AWS bucket
            log.info("Media URL for media {}, {} : {}", media.getSingleOption("ResourceRecordID"), media.getSingleOption("Order"), media.getSingleOption("MediaURL"));
        }
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {

    }
}
