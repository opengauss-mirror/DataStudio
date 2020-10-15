/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.lock;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.data.DSViewDataManager;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSInstanceLock.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class DSInstanceLock {
    private static volatile DSInstanceLock instance;
    private static final Object LOCK = new Object();
    private FileLock fileLock = null;
    private FileChannel fileChannel = null;

    /**
     * Gets the single instance of DSInstanceLock.
     *
     * @return single instance of DSInstanceLock
     */
    public static DSInstanceLock getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DSInstanceLock();
                }
            }
        }
        return instance;
    }

    private DSInstanceLock() {

    }

    /**
     * Creates the lock.
     *
     * @throws DataStudioSecurityException the data studio security exception
     */
    public void createLock() throws DataStudioSecurityException {
        boolean isSuccess = false;
        String folderPath = DSViewDataManager.getInstance().getCurrentOsUserPath();

        // Convert string to nio path object
        Path newPath = Paths.get(folderPath, MPPDBIDEConstants.LOCK_FILE);

        RandomAccessFile file = null;

        try {
            if (Files.exists(newPath)) {
                try {
                    if (!Files.deleteIfExists(newPath)) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR));
                        throw new DataStudioSecurityException(IMessagesConstants.FILE_PERMISSION_ERROR);
                    }

                } catch (IOException ex) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR), ex);
                    throw new DataStudioSecurityException(IMessagesConstants.FILE_PERMISSION_ERROR);
                }
            }
            file = new RandomAccessFile(newPath.toString(), "rw");

            fileChannel = file.getChannel();
            fileLock = fileChannel.lock();
            isSuccess = true;
        } catch (FileNotFoundException exception) {
            MPPDBIDELoggerUtility.error("Lock file not found.", exception);
            throw new DataStudioSecurityException("");
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Failed to lock the file", exception);
            throw new DataStudioSecurityException(IMessagesConstants.FAILED_TO_LOCK_FILE);
        }

        finally {
            if (!isSuccess) {
                releaseLock();
            }
            if (null != file) {
                try {
                    file.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("exception while closing resources", exception);
                }
            }
        }
    }

    /**
     * Release lock.
     */
    public void releaseLock() {
        String folderPath = DSViewDataManager.getInstance().getCurrentOsUserPath();
        // Convert string to nio path object
        Path newPath = Paths.get(folderPath, MPPDBIDEConstants.LOCK_FILE);
        try {
            if (fileLock != null) {
                fileLock.release();
                fileLock.close();
            }
            if (fileChannel != null) {
                fileChannel.close();
            }
            Path lockFilePath = Paths.get(newPath.toString());
            Files.deleteIfExists(lockFilePath);
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Release DSInstance lock failed.", exception);
        }
    }
}