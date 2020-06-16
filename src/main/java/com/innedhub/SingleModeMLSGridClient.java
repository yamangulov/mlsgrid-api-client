package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.innedhub.aws.XferMgrUrlCopy;
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
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        for (PropertyTO media : listMedia) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            if (order == 0) {
                XferMgrUrlCopy.copyFileFromUrl(amazonS3, xfer_mgr, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg");
            } else {
                XferMgrUrlCopy.copyFileFromUrl(amazonS3, xfer_mgr, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg");
            }
        }
        xfer_mgr.shutdownNow();
    }

    @Override
    public void getAndSaveAllImages(String mlsNumber, int limit) {
        List<PropertyTO> listMedia = currentGridClient.searchResult(MLSResource.MEDIA, "ResourceRecordID eq '" + mlsNumber + "'");
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        if (limit > listMedia.size()) {
            log.info("List Media files has less than {} photos. It'll be downloaded all {} files presented in list", limit, listMedia.size());
            limit = listMedia.size();
        }
        for (PropertyTO media : listMedia) {
            int order = Integer.parseInt(media.getSingleOption("Order"));
            if (order == 0) {
                XferMgrUrlCopy.copyFileFromUrl(amazonS3, xfer_mgr, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + ".jpg");
            } else if (order < limit){
                XferMgrUrlCopy.copyFileFromUrl(amazonS3, xfer_mgr, media.getSingleOption("MediaURL"), bucketName, "thumbnail_" + media.getSingleOption("ResourceRecordID") + "_" + media.getSingleOption("Order") +  ".jpg");
            }
        }
        xfer_mgr.shutdownNow();
    }
}
