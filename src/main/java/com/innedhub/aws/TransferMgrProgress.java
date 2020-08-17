package com.innedhub.aws;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.*;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
public class TransferMgrProgress {
    // waits for the transfer to complete, catching any exceptions that occur.
    public static void waitForCompletion(Transfer transfer) {

        try {
            transfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            log.error("Amazon service error: " + e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            log.error("Amazon client error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            log.error("Transfer interrupted: " + e.getMessage());
            System.exit(1);
        }

    }

    // Prints progress while waiting for the transfer to finish.
    public static void showTransferProgress(Transfer transfer) {

        // print the transfer's human-readable description
        log.info(transfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the transfer is ongoing.
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            // Note: soFar and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = transfer.getProgress();
            long soFar = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (transfer.isDone() == false);
        // print the final state of the transfer.
        TransferState transferState = transfer.getState();
        log.info(": " + transferState);

    }

    // Prints progress of a multiple file upload while waiting for it to finish.
    public static void showMultiUploadProgress(MultipleFileUpload multiUpload) {
        // print the upload's human-readable description
        log.info(multiUpload.getDescription());

        Collection<? extends Upload> subTransfers = new ArrayList<Upload>();
        subTransfers = multiUpload.getSubTransfers();

        do {
            log.info("\nSubtransfer progress:\n");
            for (Upload subTransfer : subTransfers) {
                log.info("  " + subTransfer.getDescription());
                if (subTransfer.isDone()) {
                    TransferState transferState = subTransfer.getState();
                    log.info("  " + transferState);
                } else {
                    TransferProgress progress = subTransfer.getProgress();
                    double pct = progress.getPercentTransferred();
                    printProgressBar(pct);
                    log.info("\n");
                }
            }

            // wait a bit before the next update.
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return;
            }
        } while (multiUpload.isDone() == false);
        // print the final state of the transfer.
        TransferState transferState = multiUpload.getState();
        log.info("\nMultipleFileUpload " + transferState);

    }

    // prints a simple text progressbar: [#####     ]
    public static void printProgressBar(double pct) {
        // if barSize changes, then change eraseBar (in eraseProgressBar) to
        // match.
        final int barSize = 40;
        final String emptyBar = "                                        ";
        final String filledBar = "########################################";
        int amtFull = (int) (barSize * (pct / 100.0));
        System.out.format("  [%s%s]", filledBar.substring(0, amtFull),
                emptyBar.substring(0, barSize - amtFull));
    }

    // erases the progress bar.
    public static void eraseProgressBar() {
        // eraseBar is barSize (from printProgressBar) + 4 chars.
        final String eraseBar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(eraseBar);
    }

    public static void uploadFileWithListener(String filePath,
                                              String bucketName, String keyPrefix, boolean pause) {
        log.info("file: " + filePath +
                (pause ? " (pause)" : ""));

        String keyName;
        if (keyPrefix != null) {
            keyName = keyPrefix + '/' + filePath;
        } else {
            keyName = filePath;
        }

        File file = new File(filePath);
        TransferManager transferManager = TransferManagerBuilder.standard().build();
        try {
            Upload transfer = transferManager.upload(bucketName, keyName, file);
            // print an empty progress bar...
            printProgressBar(0.0);
            transfer.addProgressListener(new ProgressListener() {
                public void progressChanged(ProgressEvent e) {
                    double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                    eraseProgressBar();
                    printProgressBar(pct);
                }
            });
            // block with Transfer.waitForCompletion()
            TransferMgrProgress.waitForCompletion(transfer);
            // print the final state of the transfer.
            TransferState transferState = transfer.getState();
            log.info(": " + transferState);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();

    }

    public static void uploadDirWithSubprogress(String dirPath,
                                                String bucketName, String keyPrefix, boolean recursive,
                                                boolean pause) {
        log.info("directory: " + dirPath + (recursive ?
                " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager transferManager = new TransferManager();
        try {
            MultipleFileUpload multiUpload = transferManager.uploadDirectory(
                    bucketName, keyPrefix, new File(dirPath), recursive);
            // loop with Transfer.isDone()
            TransferMgrProgress.showMultiUploadProgress(multiUpload);
            // or block with Transfer.waitForCompletion()
            TransferMgrProgress.waitForCompletion(multiUpload);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        transferManager.shutdownNow();
    }
}
