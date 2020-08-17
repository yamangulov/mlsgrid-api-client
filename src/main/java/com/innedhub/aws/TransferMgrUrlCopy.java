package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Slf4j
public class TransferMgrUrlCopy {
    public static void copyFileFromUrl(AmazonS3 amazonS3, TransferManager transferManager, String fromUrl, String toBucket, String toKey) {

        log.info("Amazon client before: {}", amazonS3);
        log.info("Copying file from: " + fromUrl);
        log.info("     to s3 object: " + toKey);
        log.info("        in bucket: " + toBucket);

        try (InputStream inputStream = new URL(fromUrl).openStream()){
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpeg");
            objectMetadata.addUserMetadata("title", toBucket + "/" + toKey);
            try {
                Upload upload = transferManager.upload(toBucket, toKey, inputStream, objectMetadata);
                TransferMgrProgress.showTransferProgress(upload);
                TransferMgrProgress.waitForCompletion(upload);
            } catch (AmazonServiceException e) {
                log.error(e.getErrorMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
