/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.util.BoundedInputStream;

import com.google.gson.JsonSyntaxException;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryCorePersistence.
 * 
 */

public class SQLHistoryCorePersistence {

    // holds the file names for each valid history item.
    private ArrayList<String> validHistoryFiles;

    // holds the file names for each pinned history item.
    private ArrayList<String> pinnedHistoryFiles;

    // holds the file names for each history item to be deleted.
    private ArrayList<String> tobeDeletedHistoryFiles;

    private final Object deleteListLock = new Object();

    // if true, indicates that the purge task in the progress.
    private boolean isPurgeInProgress = false;

    // if true, will make the purge task stop the operation at the earliest.
    private boolean needCancelPurge = false;

    // file name into which validHistoryFiles object is serialized into.
    private static final String VALID_HISTORY_DETAILS_METADATA_FILENAME = "v_he.meta";

    // file name into which pinnedHistoryFiles object is serialized into.
    private static final String PINNED_HISTORY_DETAILS_METADATA_FILENAME = "p_he.meta";

    // file name into which tobeDeletedHistoryFiles object is serialized into.
    private static final String TOBE_DELETED_HISTORY_DETAILS_METADATA_FILENAME = "tbd_he.meta";
    
    // meta file size max
    private static final double META_FILE_MAX_SIZE_IN_KB = 30;

    // history file size max
    private static final double HISTORY_FILE_MAX_SIZE_IN_MB = 1;
    
    // indicates the path into which the history files can be created.
    private String path;

    /**
     * Instantiates a new SQL history core persistence.
     *
     * @param persistencepath the persistencepath
     * @param historySize the history size
     */
    public SQLHistoryCorePersistence(String persistencepath, int historySize) {
        validHistoryFiles = new ArrayList<String>(10);
        tobeDeletedHistoryFiles = new ArrayList<String>(10);
        pinnedHistoryFiles = new ArrayList<String>(10);

        createHistroryFolder(persistencepath);
        path = persistencepath;
    }

    private void createHistroryFolder(String persistencepath) {
        String standardizedPath = null;
        try {
            standardizedPath = new File(persistencepath).getCanonicalPath();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Invalid File Path", exception);
        }
        if (standardizedPath != null) {
            Path filePath = Paths.get(standardizedPath);
            if (!Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                ISetFilePermission file = FilePermissionFactory.getFilePermissionInstance();
                try {
                    file.createFileWithPermission(standardizedPath, true, null, true);
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("folder creation error", exception);
                }
            }
        }
    }

    /**
     * Addto delete list.
     *
     * @param historyItem the history item
     */
    public void addtoDeleteList(SQLHistoryItemDetail historyItem) {
        synchronized (deleteListLock) {
            tobeDeletedHistoryFiles.add(historyItem.getFileName());
        }
    }

    /**
     * Load valid queries.
     *
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     */
    public List<SQLHistoryItemDetail> loadValidQueries() throws DatabaseOperationException {
        return loadHistroyItemsFromFileList(validHistoryFiles, path
                + EnvirnmentVariableValidator.validateAndGetFileSeperator() + VALID_HISTORY_DETAILS_METADATA_FILENAME);
    }

    /**
     * Load pinned queries.
     *
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     */
    public List<SQLHistoryItemDetail> loadPinnedQueries() throws DatabaseOperationException {
        return loadHistroyItemsFromFileList(pinnedHistoryFiles, path
                + EnvirnmentVariableValidator.validateAndGetFileSeperator() + PINNED_HISTORY_DETAILS_METADATA_FILENAME);
    }

    private List<SQLHistoryItemDetail> loadHistroyItemsFromFileList(ArrayList<String> fileListParam, String fileName)
            throws DatabaseOperationException {
        ArrayList<String> fileList = fileListParam;
        List<SQLHistoryItemDetail> list = new ArrayList<SQLHistoryItemDetail>(fileList.size());
        String standardizedPath = null;
        try {
            standardizedPath = new File(fileName).getCanonicalPath();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Invalid File Path", exception);
        }
        if (standardizedPath != null) {
            Path filePath = Paths.get(standardizedPath);
            if (!Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                ISetFilePermission file = FilePermissionFactory.getFilePermissionInstance();
                file.createFileWithPermission(standardizedPath, false, null, true);
                return list;
            } else {
                double fileSizeInKB = FileUtils.sizeOf(filePath.toFile()) / (double) (1024) ;
                if (fileSizeInKB > META_FILE_MAX_SIZE_IN_KB) {
                    MPPDBIDELoggerUtility.error("Error while reading the SQL History file. File size exceeded 30KB");
                    return list;
                }
            }

            fileList = desearlizeHistoryFileMetaData(fileName);

            if (null != fileList) {
                list = loadHistoryitems(fileList);
            }
        }
        return list;
    }

    private List<SQLHistoryItemDetail> loadHistoryitems(ArrayList<String> fileList) {
        List<SQLHistoryItemDetail> historyItems = new ArrayList<SQLHistoryItemDetail>(fileList.size());
        SQLHistoryItemDetail historyItem = null;
        int fileSize = fileList.size();
        for (int index = 0; index < fileSize; index++) {
            historyItem = readDetailFromFile(
                    path + EnvirnmentVariableValidator.validateAndGetFileSeperator() + fileList.get(index));
            if (historyItem != null) {
                historyItems.add(historyItem);
            } else {
                deleteInvalidHistFile(fileList, index);
            }
        }
        return historyItems;
    }

    private void deleteInvalidHistFile(ArrayList<String> fileList, int indx) {
        String stdizedPath = null;
        String pathToDelete = path + EnvirnmentVariableValidator.validateAndGetFileSeperator() + fileList.get(indx);
        try {
            stdizedPath = new File(pathToDelete).getCanonicalPath();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Invalid File Path", exception);
        }
        try {
            Files.deleteIfExists(Paths.get(stdizedPath));
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Invalid History deleted from file", exception);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SecuredObjectInputStream.
     * 
     */
    private static class SecuredObjectInputStream extends ObjectInputStream {

        /**
         * Instantiates a new secured object input stream.
         *
         * @param inputStream the input stream
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public SecuredObjectInputStream(InputStream inputStream) throws IOException {
            super(inputStream);
        }

        /**
         * Resolve class.
         *
         * @param desc the desc
         * @return the class
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException the class not found exception
         */
        @Override
        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            if (desc.getName().equals(SQLHistoryFileMetadata.class.getName())
                    || desc.getName().equals(ArrayList.class.getName())) {
                return super.resolveClass(desc);
            }
            throw new InvalidClassException("Unauthorized deserialization attempt", desc.getName());
        }
    }

    private void searlizeHistoryFileMetaData(ArrayList<String> topersist, String filename) {
        SQLHistoryFileMetadata lSQLHistoryFileMetadata = new SQLHistoryFileMetadata();
        lSQLHistoryFileMetadata.setHisFileMetadata(topersist);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(filename);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(lSQLHistoryFileMetadata);
        } catch (IOException ioe) {
            MPPDBIDELoggerUtility.error("io exception caught", ioe);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException exception) {
                    oos = null;
                    MPPDBIDELoggerUtility.error("IO operation failed.", exception);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException exception) {
                    fos = null;
                    MPPDBIDELoggerUtility.error("IO operation failed.", exception);
                }
            }

        }
    }

    private ArrayList<String> desearlizeHistoryFileMetaData(String filename) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        BoundedInputStream bis = null;

        try {
            fis = new FileInputStream(filename);
            bis = new BoundedInputStream(fis, 2097152);
            ois = new SecuredObjectInputStream(bis);
            Object object = ois.readObject();
            SQLHistoryFileMetadata hisFileMetaData = null;
            if (object instanceof SQLHistoryFileMetadata) {
                hisFileMetaData = (SQLHistoryFileMetadata) object;
            } else {
                return new ArrayList<String>();
            }
            return hisFileMetaData.getHisFileMetadata();
        } catch (IOException ioe) {
            return null;
        } catch (ClassNotFoundException exception) {
            return null;
        }

        finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("IO operation failed.", exception);
                    ois = null;
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("IO operation failed.", exception);
                    fis = null;
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("IO operation failed.", exception);
                    bis = null;
                }
            }
        }
    }

    /**
     * Read detail from file.
     *
     * @param fileName the file name
     * @return the SQL history item detail
     */
    public static SQLHistoryItemDetail readDetailFromFile(String fileName) {
        FileInputStream fis = null;
        BoundedInputStream bis = null;
        SQLHistoryItemDetail historyItem = null;
        try {
            File file = new File(fileName);
            if (file.exists()) {
                double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
                if (fileSizeInMB > HISTORY_FILE_MAX_SIZE_IN_MB) {
                    MPPDBIDELoggerUtility.error("SQL Histrory file size exceeds file limit 1MB .");
                    return null;
                }
                fis = new FileInputStream(fileName);
                bis = new BoundedInputStream(fis, 20971520);
                byte[] bytes = IOUtils.toByteArray(bis);
                if (bytes.length == 0) {
                    return null;
                }

                String fileContent = new String(bytes, StandardCharsets.UTF_8);
                historyItem = SQLHistoryItemDetail.getDeserializedContent(fileContent);
            }
            return historyItem;
        } catch (IOException exception) {
            return null;
        } catch (JsonSyntaxException exception) {
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException ex) {
                MPPDBIDELoggerUtility.error("IO exception occured while closing files");
            }
        }
    }

    /**
     * Persist history.
     *
     * @param queryHistory the query history
     * @param pinnedQueryHistory the pinned query history
     */
    public void persistHistory(List<SQLHistoryItemDetail> queryHistory, List<SQLHistoryItemDetail> pinnedQueryHistory) {

        isPurgeInProgress = true;

        persistHistoryList(pinnedQueryHistory, path + EnvirnmentVariableValidator.validateAndGetFileSeperator(),
                PINNED_HISTORY_DETAILS_METADATA_FILENAME);

        persistHistoryList(queryHistory, path + EnvirnmentVariableValidator.validateAndGetFileSeperator(),
                VALID_HISTORY_DETAILS_METADATA_FILENAME);

        deleteOlderUnwantedFiles();

        isPurgeInProgress = false;

    }

    /**
     * Delete older unwanted files.
     */
    public void deleteOlderUnwantedFiles() {
        ArrayList<String> deleteFileNameList = new ArrayList<String>(0);

        synchronized (deleteListLock) {
            if (tobeDeletedHistoryFiles.size() > 0) {
                deleteFileNameList.addAll(tobeDeletedHistoryFiles);
                tobeDeletedHistoryFiles.clear();
            } else {
                return;
            }
        }

        purgeDeletedHistoryItems(deleteFileNameList);
        synchronized (deleteListLock) {
            tobeDeletedHistoryFiles.addAll(deleteFileNameList);
        }

    }

    private void purgeDeletedHistoryItems(ArrayList<String> todeleteListToPersist) {
        int size = todeleteListToPersist.size();
        searlizeHistoryFileMetaData(todeleteListToPersist,
                path + EnvirnmentVariableValidator.validateAndGetFileSeperator()
                        + TOBE_DELETED_HISTORY_DETAILS_METADATA_FILENAME);

        ArrayList<Integer> itemsToremove = new ArrayList<Integer>(size);
        for (int cnt = 0; cnt < size; cnt++) {
            if (needCancelPurge) {
                return;
            }
            try {
                Files.deleteIfExists(Paths.get(path + EnvirnmentVariableValidator.validateAndGetFileSeperator()
                        + todeleteListToPersist.get(cnt)));
                itemsToremove.add(cnt, 1);
            } catch (IOException exception) {
                itemsToremove.add(cnt, 0);
            }
        }

        for (int index = itemsToremove.size() - 1; index >= 0; index--) {
            if (needCancelPurge) {
                return;
            }
            if (1 == itemsToremove.get(index)) {
                todeleteListToPersist.remove(index);
            }

        }

        searlizeHistoryFileMetaData(todeleteListToPersist,
                path + EnvirnmentVariableValidator.validateAndGetFileSeperator()
                        + TOBE_DELETED_HISTORY_DETAILS_METADATA_FILENAME);
    }

    private void persistHistoryList(List<SQLHistoryItemDetail> queryHistoryToPersist, String filePath,
            String filename) {
        ArrayList<String> historyItemFileNames = new ArrayList<String>(queryHistoryToPersist.size());
        int persistSize = queryHistoryToPersist.size();

        for (int index = 0; index < persistSize; index++) {
            if (needCancelPurge) {
                return;
            }
            historyItemFileNames.add(index, queryHistoryToPersist.get(index).getFileName());

        }

        searlizeHistoryFileMetaData(historyItemFileNames, filePath + filename);
        persistEachHistorytItem(queryHistoryToPersist, filePath);
    }

    private void persistEachHistorytItem(List<SQLHistoryItemDetail> queryHistoryToPersist, String workingpath) {
        ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
        int queryPersistSize = queryHistoryToPersist.size();

        for (int indx = 0; indx < queryPersistSize; indx++) {
            if (needCancelPurge) {
                return;
            }
            persistToFile(withPermission, workingpath + queryHistoryToPersist.get(indx).getFileName(),
                    queryHistoryToPersist.get(indx).getSerializedContent());
        }

    }

    private boolean persistToFile(ISetFilePermission withPermission, String fileName, String serializedContent) {

        Path pathToSave = Paths.get(fileName);
        try {
            if (!Files.exists(Paths.get(fileName), LinkOption.NOFOLLOW_LINKS)) {
                pathToSave = withPermission.createFileWithPermission(fileName, false, null, true);
            }

            Files.write(pathToSave, serializedContent.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING);
            return true;

        } catch (IOException exception) {
            return false;
        } catch (DatabaseOperationException exception) {
            return false;
        }
    }

    /**
     * Cancel persistence operation.
     */
    public void cancelPersistenceOperation() {
        needCancelPurge = true;
    }

    /**
     * Checks if is purge in progress.
     *
     * @return true, if is purge in progress
     */
    public boolean isPurgeInProgress() {
        return isPurgeInProgress;
    }
}
