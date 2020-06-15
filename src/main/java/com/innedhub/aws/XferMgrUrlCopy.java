package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class XferMgrUrlCopy {
    public static void copyFileFromUrl(AmazonS3 amazonS3, String from_url, String to_bucket, String to_key) {
        log.info("Copying file from: " + from_url);
        log.info("     to s3 object: " + to_key);
        log.info("        in bucket: " + to_bucket);

        InputStream inputStream = null;
        try {
            inputStream = new URL(from_url).openStream();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/jpeg");
        objectMetadata.addUserMetadata("title", to_bucket + "/" + to_key);
        //method don't set objectMetadata.setContentLength, because mlsgrid aws bucket don't give it, also it can be the same for other buckets where the photos from api.mlsgrid.com store, but method works well despite warning in log.
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Upload xfer = xfer_mgr.upload(to_bucket, to_key, inputStream, objectMetadata);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            //  or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }
}
