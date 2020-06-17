package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
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
            //inputStream = new URL(fromUrl).openStream();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType("image/jpeg");
            objectMetadata.addUserMetadata("title", toBucket + "/" + toKey);
            //method don't set objectMetadata.setContentLength, because mlsgrid aws bucket don't give it, also it can be the same for other buckets where the photos from api.mlsgrid.com store, but method works well despite warning in log.
            try {
                transferManager.upload(toBucket, toKey, inputStream, objectMetadata);
            } catch (AmazonServiceException e) {
                log.error(e.getErrorMessage());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
