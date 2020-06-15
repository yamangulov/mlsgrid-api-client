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
public class XferMgrProgress {
    // waits for the transfer to complete, catching any exceptions that occur.
    public static void waitForCompletion(Transfer xfer) {
        
        try {
            xfer.waitForCompletion();
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
    public static void showTransferProgress(Transfer xfer) {
        
        // print the transfer's human-readable description
        log.info(xfer.getDescription());
        // print an empty progress bar...
        printProgressBar(0.0);
        // update the progress bar while the xfer is ongoing.
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                return;
            }
            // Note: so_far and total aren't used, they're just for
            // documentation purposes.
            TransferProgress progress = xfer.getProgress();
            long so_far = progress.getBytesTransferred();
            long total = progress.getTotalBytesToTransfer();
            double pct = progress.getPercentTransferred();
            eraseProgressBar();
            printProgressBar(pct);
        } while (xfer.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = xfer.getState();
        log.info(": " + xfer_state);
        
    }

    // Prints progress of a multiple file upload while waiting for it to finish.
    public static void showMultiUploadProgress(MultipleFileUpload multi_upload) {
        // print the upload's human-readable description
        log.info(multi_upload.getDescription());
        
        Collection<? extends Upload> sub_xfers = new ArrayList<Upload>();
        sub_xfers = multi_upload.getSubTransfers();

        do {
            log.info("\nSubtransfer progress:\n");
            for (Upload u : sub_xfers) {
                log.info("  " + u.getDescription());
                if (u.isDone()) {
                    TransferState xfer_state = u.getState();
                    log.info("  " + xfer_state);
                } else {
                    TransferProgress progress = u.getProgress();
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
        } while (multi_upload.isDone() == false);
        // print the final state of the transfer.
        TransferState xfer_state = multi_upload.getState();
        log.info("\nMultipleFileUpload " + xfer_state);
        
    }

    // prints a simple text progressbar: [#####     ]
    public static void printProgressBar(double pct) {
        // if bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int) (bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
                empty_bar.substring(0, bar_size - amt_full));
    }

    // erases the progress bar.
    public static void eraseProgressBar() {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }

    public static void uploadFileWithListener(String file_path,
                                              String bucket_name, String key_prefix, boolean pause) {
        log.info("file: " + file_path +
                (pause ? " (pause)" : ""));

        String key_name = null;
        if (key_prefix != null) {
            key_name = key_prefix + '/' + file_path;
        } else {
            key_name = file_path;
        }
        
        File f = new File(file_path);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            Upload u = xfer_mgr.upload(bucket_name, key_name, f);
            // print an empty progress bar...
            printProgressBar(0.0);
            u.addProgressListener(new ProgressListener() {
                public void progressChanged(ProgressEvent e) {
                    double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                    eraseProgressBar();
                    printProgressBar(pct);
                }
            });
            // block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(u);
            // print the final state of the transfer.
            TransferState xfer_state = u.getState();
            log.info(": " + xfer_state);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
        
    }

    public static void uploadDirWithSubprogress(String dir_path,
                                                String bucket_name, String key_prefix, boolean recursive,
                                                boolean pause) {
        log.info("directory: " + dir_path + (recursive ?
                " (recursive)" : "") + (pause ? " (pause)" : ""));

        TransferManager xfer_mgr = new TransferManager();
        try {
            MultipleFileUpload multi_upload = xfer_mgr.uploadDirectory(
                    bucket_name, key_prefix, new File(dir_path), recursive);
            // loop with Transfer.isDone()
            XferMgrProgress.showMultiUploadProgress(multi_upload);
            // or block with Transfer.waitForCompletion()
            XferMgrProgress.waitForCompletion(multi_upload);
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }
}
