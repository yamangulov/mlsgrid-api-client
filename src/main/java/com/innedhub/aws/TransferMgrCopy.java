package com.innedhub.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.Copy;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferMgrCopy {
    public static void copyObjectSimple(AmazonS3 amazonS3, String fromBucket, String fromKey,
                                        String toBucket, String toKey) {
        log.info("Copying s3 object: " + fromKey);
        log.info("      from bucket: " + fromBucket);
        log.info("     to s3 object: " + toKey);
        log.info("        in bucket: " + toBucket);

        TransferManager transferManager = TransferManagerBuilder.standard().withS3Client(amazonS3).build();
        try {
            Copy transfer = transferManager.copy(fromBucket, fromKey, toBucket, toKey);
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
