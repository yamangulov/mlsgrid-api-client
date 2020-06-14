//snippet-sourcedescription:[XferMgrCopy.java demonstrates how to copy an object from one Amazon S3 bucket to another using S3 TransferManager.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[TransferManager copy]
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
// snippet-start:[s3.java1.s3_xfer_mgr_copy.import]

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
// snippet-end:[s3.java1.s3_xfer_mgr_copy.import]

// snippet-start:[s3.java1.s3_xfer_mgr_copy.complete]

/**
 * Copy an object from one Amazon S3 bucket to another using S3 TransferManager.
 * 
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
 */
public class XferMgrCopy {
    public static void copyObjectSimple(AmazonS3 amazonS3, String from_bucket, String from_key,
                                        String to_bucket, String to_key) {
        // snippet-start:[s3.java1.s3_xfer_mgr_copy.copy_object]
        System.out.println("Copying s3 object: " + from_key);
        System.out.println("      from bucket: " + from_bucket);
        System.out.println("     to s3 object: " + to_key);
        System.out.println("        in bucket: " + to_bucket);

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Copy xfer = xfer_mgr.copy(from_bucket, from_key, to_bucket, to_key);
            // loop with Transfer.isDone()
            XferMgrProgress.showTransferProgress(xfer);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(xfer);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        // snippet-end:[s3.java1.s3_xfer_mgr_copy.copy_object]
    }

}
// snippet-end:[s3.java1.s3_xfer_mgr_copy.complete]