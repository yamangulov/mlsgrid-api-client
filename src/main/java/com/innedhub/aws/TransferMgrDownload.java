package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class TransferMgrDownload {
    public static void downloadDir(AmazonS3 amazonS3, String bucketName, String keyPrefix,
                                   String dirPath, boolean pause) {
        log.info("downloading to directory: " + dirPath +
                (pause ? " (pause)" : ""));

        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();

        try {
            MultipleFileDownload transfer = transferManager.downloadDirectory(
                    bucketName, keyPrefix, new File(dirPath));
            // loop with Transfer.isDone()
            TransferMgrProgress.showTransferProgress(transfer);
            // or block with Transfer.waitForCompletion()
            TransferMgrProgress.waitForCompletion(transfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }

    public static void downloadFile(AmazonS3 amazonS3, String bucketName, String keyName,
                                    String filePath, boolean pause) {
        log.info("Downloading to file: " + filePath +
                (pause ? " (pause)" : ""));

        File file = new File(filePath);
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Download transfer = transferManager.download(bucketName, keyName, file);
            // loop with Transfer.isDone()
            TransferMgrProgress.showTransferProgress(transfer);
            // or block with Transfer.waitForCompletion()
            TransferMgrProgress.waitForCompletion(transfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }
}
