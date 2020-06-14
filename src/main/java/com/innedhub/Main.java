package com.innedhub;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.innedhub.aws.XferMgrCopy;
import com.innedhub.aws.XferMgrDownload;
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
        AmazonS3 amazonS3 = gridClient.getAmazonS3();

        /*
        1) Если использовать собственные credentials, то мы получим подсказку : Downloading to file: thumbnail_0.jpg
The bucket is in this region: us-east-1. Please use this region to retry the request 2) Если после это в credentials мы поставим вместо нашего региона us-west-2, то мы получаем сообщение Forbidden. То есть  закачка по имени бакета из чужого бакета запрещена все равно.
         */
        XferMgrDownload.downloadFile(amazonS3, "mlsgrid", "images/742be4ba-76e6-4073-a935-5d6b1f3ec1c2.jpg", "thumbnail_" + "0" + ".jpg", false);

    }
}

