//snippet-sourcedescription:[XferMgrDownload.java demonstrates how to download objects from an Amazon S3 bucket using S3 TransferManager.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[TransferManager downloadDirectory]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
package com.innedhub.aws;
// snippet-start:[s3.java1.s3_xfer_mgr_download.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

import java.io.File;
// snippet-end:[s3.java1.s3_xfer_mgr_download.import]

// snippet-start:[s3.java1.s3_xfer_mgr_download.complete]

/**
 * Download objects from an Amazon S3 bucket using S3 TransferManager.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrDownload {
    public static void downloadDir(AmazonS3 amazonS3, String bucket_name, String key_prefix,
                                   String dir_path, boolean pause) {
        System.out.println("downloading to directory: " + dir_path +
                (pause ? " (pause)" : ""));

        // snippet-start:[s3.java1.s3_xfer_mgr_download.directory]
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();

        try {
            MultipleFileDownload xfer = xfer_mgr.downloadDirectory(
                    bucket_name, key_prefix, new File(dir_path));
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        // snippet-end:[s3.java1.s3_xfer_mgr_download.directory]
    }

    public static void downloadFile(AmazonS3 amazonS3, String bucket_name, String key_name,
                                    String file_path, boolean pause) {
        System.out.println("Downloading to file: " + file_path +
                (pause ? " (pause)" : ""));

        // snippet-start:[s3.java1.s3_xfer_mgr_download.single]
        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Download xfer = xfer_mgr.download(bucket_name, key_name, f);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        // snippet-end:[s3.java1.s3_xfer_mgr_download.single]
    }
}
// snippet-end:[s3.java1.s3_xfer_mgr_download.complete]