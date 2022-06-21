package com.yamangulov;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.yamangulov.aws.TransferMgrUrlCopy;
import com.yamangulov.enums.MLSResource;
import com.yamangulov.odata.Client;
import com.yamangulov.results.PropertyTO;
import com.yamangulov.results.SearchResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class SingleModeMLSGridClient implements MLSGridClient {
    private String apiUri;
    private String apiKey;
    private String bucketName;
    private AmazonS3 amazonS3;
    private MLSGridClient currentGridClient;
    private TransferManager transferManager;

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
    public SearchResult searchResult(MLSResource resource, String request, int top) {
        Client client = new Client(apiUri, apiKey);
        return client.doRequestWithFilter(resource, request, top);
    }

    @Override
    public SearchResult searchResult(URI nextPage) {
        Client client = new Client(apiUri, apiKey);
        return client.doRequestFromUri(nextPage);
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
        this.transferManager  = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber) {
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "' and MlgCanView eq true");
        for (PropertyTO media : searchResult.getPropertyTOList()) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            if (order == 0) {
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg");
            } else {
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg");
            }
        }
    }

    @Override
    public Map<String, String> getAndSaveAllImagesAndReturnMap(String mlsNumber) {
        Map<String, String> mlsLinkToAwsLinkMap = new LinkedHashMap<>();
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "' and MlgCanView eq true");
        for (PropertyTO media : searchResult.getPropertyTOList()) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            String mediaURL = media.getSingleOption("MediaURL");
            String awsKey;
            if (order == 0) {
                awsKey = "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg";
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, mediaURL, bucketName, awsKey);
            } else {
                awsKey = "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg";
                TransferMgrUrlCopy.copyFileFromUrl(amazonS3, transferManager, mediaURL, bucketName, awsKey);
            }
            mlsLinkToAwsLinkMap.put(mediaURL, awsKey);
        }
        return mlsLinkToAwsLinkMap;
    }

    private List<String> getMLSLinksFromMLSGrid(String mlsNumber) {
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "' and MlgCanView eq true");
        List<PropertyTO> propertyTOList = searchResult.getPropertyTOList();
        List<String> mlsLinks = new ArrayList<>();
        for (PropertyTO propertyTO : propertyTOList) {
            String mlsLink = propertyTO.getSingleOption("MediaURL");
            mlsLinks.add(mlsLink);
        }
        return mlsLinks;
    }

    @Override
    public Map<String, List<String>> getMLSLinksFromMLSGrid(List<PropertyTO> propertyTOList) {
        Map<String, List<String>> mlsLinksMap = new LinkedHashMap<>();
        for (PropertyTO propertyTO : propertyTOList) {
            String mlsNumber = propertyTO.getSingleOption("ListingId");
            List<String> mlsLinksForMLSNumber = getMLSLinksFromMLSGrid(mlsNumber);
            mlsLinksMap.put(mlsNumber, mlsLinksForMLSNumber);
        }
        return mlsLinksMap;
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {
        SearchResult searchResult = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "' and MlgCanView eq true");
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
    }

    @Override
    public void stopTransferManager() {
        transferManager.shutdownNow();
    }
}
