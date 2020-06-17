package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class TransferMgrUpload {
    public static void uploadDir(AmazonS3 amazonS3, String dirPath, String bucketName,
                                 String keyPrefix, boolean recursive, boolean pause) {
        log.info("directory: " + dirPath + (recursive ?
                " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            MultipleFileUpload transfer = transferManager.uploadDirectory(bucketName,
                    keyPrefix, new File(dirPath), recursive);
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

    public static void uploadFileList(AmazonS3 amazonS3, String[] filePaths, String bucketName,
                                      String keyPrefix, boolean pause) {
        log.info("file list: " + Arrays.toString(filePaths) +
                (pause ? " (pause)" : ""));
        // convert the file paths to a list of File objects (required by the
        // uploadFileList method)
        ArrayList<File> files = new ArrayList<>();
        for (String path : filePaths) {
            files.add(new File(path));
        }

        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            MultipleFileUpload transfer = transferManager.uploadFileList(bucketName,
                    keyPrefix, new File("."), files);
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

    public static void uploadFile(AmazonS3 amazonS3, String filePath, String bucketName,
                                  String keyPrefix, boolean pause) {
        log.info("file: " + filePath +
                (pause ? " (pause)" : ""));

        String keyName;
        if (keyPrefix != null) {
            keyName = keyPrefix + '/' + filePath;
        } else {
            keyName = filePath;
        }

        File file = new File(filePath);
        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Upload transfer = transferManager.upload(bucketName, keyName, file);
            // loop with Transfer.isDone()
            TransferMgrProgress.showTransferProgress(transfer);
            //  or block with Transfer.waitForCompletion()
            TransferMgrProgress.waitForCompletion(transfer);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }
}
