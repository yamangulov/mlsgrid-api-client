package com.innedhub;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.innedhub.aws.XferMgrDownload;
import com.innedhub.aws.XferMgrUpload;
import com.innedhub.aws.XferMgrUrlCopy;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

//class had used for testing in process of development and will be deleted after
@Slf4j
public class Main {
    private static String bucketName = "mls-grid-test-property-images-bucket";
    private static String region = "us-west-2";
    private static String awsAccessKey = "AKIAJB3RTOCO4PBLPN7A";
    private static String awsSecretKey = "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";

    public static void main(String[] args) {

        MLSGridFactory factory = new MLSGridFactory();
        MLSGridClient gridClient = factory.createClient("https://api.mlsgrid.com/", "9559104ea30324a4cbe8b0b25b9b0ec6be948ca8");
        gridClient.initAmazonConnection(bucketName, region, awsAccessKey, awsSecretKey, gridClient);
        AmazonS3 amazonS3 = gridClient.getAmazonS3();

//        deleting extra objects
//        amazonS3.deleteObject(bucketName, "thumbnail_MRD10611226.jpg");

//        copying file from url to bucket
//        XferMgrUrlCopy.copyFileFromUrl(amazonS3, "https://s3.amazonaws.com/mlsgrid/images/742be4ba-76e6-4073-a935-5d6b1f3ec1c2.jpg", bucketName, "thumbnail_MRD10611226.jpg");//error


//        browsing objects in bucket - don't work simultaneously with classes from package aws because method xfer_mgr.shutdownNow() in the ends of methods of these classes - it kills connection pool !!! Run only single, i.e. without XferMgrUrlCopy.copyFileFromUrl !!!
//        S3Objects.inBucket(amazonS3, bucketName).forEach((S3ObjectSummary objectSummary) -> {
//            log.info("Object summary in bucket {}: {}", bucketName, objectSummary.getKey());
//        });

    }
}

