package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

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

        //Next three lines doesn't work. There aren't object key and bucket name of source photo.
//        AmazonS3 amazonS3 = gridClient.getAmazonS3();
//        S3Object s3Object = amazonS3.getObject("294A46D26B8F2797E040010A340171D1", "5aaa0569b61ff020b918d06a");
//        log.info("S3Object is: {}", s3Object);



    }
}

