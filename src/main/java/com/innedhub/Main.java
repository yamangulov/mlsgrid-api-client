package com.innedhub;

import lombok.extern.slf4j.Slf4j;

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

        gridClient.getAndSaveAllImages("MRD10611226");
        gridClient.getAndSaveAllImages("MRD10611226");

//        it needs for downloading and browsing and deleting files, see later in code
//        AmazonS3 amazonS3 = gridClient.getAmazonS3();

//        deleting extra objects
//        amazonS3.deleteObject(bucketName, "thumbnail_MRD10611226.jpg");

//        download file to check if it has been downloaded correctly by viewing
//        XferMgrDownload.downloadFile(amazonS3, bucketName, "thumbnail_MRD10611226.jpg", "thumbnail_MRD10611226.jpg", false);


//        browsing objects in bucket - don't work simultaneously with classes from package aws because method xfer_mgr.shutdownNow() in the ends of methods of these classes - it kills connection pool !!! Run only single, i.e. without XferMgrUrlCopy.copyFileFromUrl !!!
//        S3Objects.inBucket(amazonS3, bucketName).forEach((S3ObjectSummary objectSummary) -> {
//            log.info("Object summary in bucket {}: {}", bucketName, objectSummary.getKey());
//        });

    }
}

