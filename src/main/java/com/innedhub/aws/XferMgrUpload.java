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
public class XferMgrUpload {
    public static void uploadDir(AmazonS3 amazonS3, String dir_path, String bucket_name,
                                 String key_prefix, boolean recursive, boolean pause) {
        log.info("directory: " + dir_path + (recursive ?
                " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadDirectory(bucket_name,
                    key_prefix, new File(dir_path), recursive);
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

    public static void uploadFileList(AmazonS3 amazonS3, String[] file_paths, String bucket_name,
                                      String key_prefix, boolean pause) {
        log.info("file list: " + Arrays.toString(file_paths) +
                (pause ? " (pause)" : ""));
        // convert the file paths to a list of File objects (required by the
        // uploadFileList method)
        ArrayList<File> files = new ArrayList<File>();
        for (String path : file_paths) {
            files.add(new File(path));
        }

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            MultipleFileUpload xfer = xfer_mgr.uploadFileList(bucket_name,
                    key_prefix, new File("."), files);
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

    public static void uploadFile(AmazonS3 amazonS3, String file_path, String bucket_name,
                                  String key_prefix, boolean pause) {
        log.info("file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }

        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Upload xfer = xfer_mgr.upload(bucket_name, key_name, f);
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
