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
public class XferMgrDownload {
    public static void downloadDir(AmazonS3 amazonS3, String bucket_name, String key_prefix,
                                   String dir_path, boolean pause) {
        log.info("downloading to directory: " + dir_path +
                (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();

        try {
            MultipleFileDownload xfer = xfer_mgr.downloadDirectory(
                    bucket_name, key_prefix, new File(dir_path));
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }

    public static void downloadFile(AmazonS3 amazonS3, String bucket_name, String key_name,
                                    String file_path, boolean pause) {
        log.info("Downloading to file: " + file_path +
                (pause ? " (pause)" : ""));

        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }
}
