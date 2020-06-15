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

        ListMyBuckets(amazonS3);

        //browsing objects in bucket//error
//        S3Objects.inBucket(amazonS3, bucketName).forEach((S3ObjectSummary objectSummary) -> {
//            log.info("Object summary in bucket {}: {}", bucketName, objectSummary.getKey());
//        });

        try {
            // Upload a text string as a new object.
            amazonS3.putObject(bucketName, "test.txt", "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, "test.jpg", new File("test.jpg"));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpg");
            metadata.addUserMetadata("title", "test file upload");
            request.setMetadata(metadata);
            amazonS3.putObject(request);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        //XferMgrUpload.uploadFile(amazonS3, "test.jpg", bucketName, "tumbnail_", false);//error

//        XferMgrUrlCopy.copyFileFromUrl(amazonS3, "https://s3.amazonaws.com/mlsgrid/images/742be4ba-76e6-4073-a935-5d6b1f3ec1c2.jpg", bucketName, "tumbnail_MRD10611226.jpg");//error

        //browsing objects in bucket//error
//        S3Objects.inBucket(amazonS3, bucketName).forEach((S3ObjectSummary objectSummary) -> {
//            log.info("Object summary in bucket {}: {}", bucketName, objectSummary.getKey());
//        });

        /*
        1) Если использовать собственные credentials, то мы получим подсказку : Downloading to file: thumbnail_0.jpg
The bucket is in this region: us-east-1. Please use this region to retry the request 2) Если после это в credentials мы поставим вместо нашего региона us-west-2, то мы получаем сообщение Forbidden. То есть  закачка по имени бакета из чужого бакета запрещена все равно.
         */
//        XferMgrDownload.downloadFile(amazonS3, "mlsgrid", "images/742be4ba-76e6-4073-a935-5d6b1f3ec1c2.jpg", "thumbnail_" + "0" + ".jpg", false);

    }

    private static void ListMyBuckets(AmazonS3 amazonS3) {
        List<Bucket> buckets = amazonS3.listBuckets();
        log.info("My buckets now are:");
        for (Bucket b : buckets) {
            log.info(b.getName());
        }
    }
}

