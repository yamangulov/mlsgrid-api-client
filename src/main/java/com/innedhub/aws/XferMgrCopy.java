package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XferMgrCopy {
    public static void copyObjectSimple(AmazonS3 amazonS3, String from_bucket, String from_key,
                                        String to_bucket, String to_key) {
        log.info("Copying s3 object: " + from_key);
        log.info("      from bucket: " + from_bucket);
        log.info("     to s3 object: " + to_key);
        log.info("        in bucket: " + to_bucket);

        TransferManager xfer_mgr = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Copy xfer = xfer_mgr.copy(from_bucket, from_key, to_bucket, to_key);
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
