package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.innedhub.aws.TransferMgrUrlCopy;
import com.innedhub.enums.MLSResource;
import com.innedhub.exceptions.NotValidKeyForServiceModeException;
import com.innedhub.odata.Client;
import com.innedhub.results.PropertyTO;
import com.innedhub.results.SearchResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
    public SearchResult searchResult(MLSResource resource, String request) {
        SearchResult searchResult = null;
        try {
            if (checkApiKey(apiKey)) {
                Client client = new Client(apiUri, apiServiceKey);
                searchResult = client.doRequestWithFilter(resource, request);
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }

        return searchResult;
    }

    @Override
    public SearchResult searchResult(MLSResource resource, String request, int top) {
        SearchResult searchResult = null;
        try {
            if (checkApiKey(apiKey)) {
                Client client = new Client(apiUri, apiServiceKey);
                searchResult = client.doRequestWithFilter(resource, request, top);
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }

        return searchResult;
    }

    @Override
    public SearchResult searchResult(URI nextPage) {
        SearchResult searchResult = null;
        try {
            if (checkApiKey(apiKey)) {
                Client client = new Client(apiUri, apiServiceKey);
                searchResult = client.doRequestFromUri(nextPage);
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }

        return searchResult;
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
        try {
            if (checkApiKey(apiKey)) {
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
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }
    }

    @Override
    public Map<String, String> getAndSaveAllImagesAndReturnMap(String mlsNumber) {
        return null;
    }

    @Override
    public Map<String, List<String>> getMLSLinksFromMLSGrid(List<PropertyTO> propertyTOList) {
        return null;
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {
        try {
            if (checkApiKey(apiKey)) {
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
            } else {
                throw new NotValidKeyForServiceModeException();
            }

        } catch (NotValidKeyForServiceModeException e) {
            log.error("Customers key not valid, contact technical support service", e);
        }
    }

    private boolean checkApiKey(String apiKey) {
        //TODO
        //realize method checkApiKey for checking apiKey of client in SERVICE mode
        return false;
    }
}
