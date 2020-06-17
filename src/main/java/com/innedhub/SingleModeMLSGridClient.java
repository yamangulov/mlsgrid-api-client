package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.innedhub.aws.TransferMgrUrlCopy;
import com.innedhub.enums.MLSResource;
import com.innedhub.odata.Client;
import com.innedhub.results.PropertyTO;
import com.innedhub.results.SearchResult;
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
    public SearchResult searchResult(MLSResource resource, String request) {
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
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "'");
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        for (PropertyTO media : searchResult.getPropertyTOList()) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            if (order == 0) {
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg");
            } else {
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg");
            }
        }
        transferManager.shutdownNow();
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "'");
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        if (limit > searchResult.getPropertyTOList().size()) {
            log.info("List Media files has less than {} photos. It'll be downloaded all {} files presented in list", limit, searchResult.getPropertyTOList().size());
            limit = searchResult.getPropertyTOList().size();
        }
        for (PropertyTO media : searchResult.getPropertyTOList()) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            if (order == 0) {
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg");
            } else if (order < limit){
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg");
            }
        }
        transferManager.shutdownNow();
    }
}
