/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.autosave;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import org.apache.commons.io.FileUtils;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.files.DSFolderDeleteUtility;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutosaveFileUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class AutosaveFileUtility {

    private String osCurrentUserFolderPath;
    private static final String AUTOSAVE_FOLDER = "Autosave";

    /**
     * Instantiates a new autosave file utility.
     */
    public AutosaveFileUtility() {
    }

    /**
     * Creates the folder structure.
     *
     * @throws FileOperationException the file operation exception
     */
    public void createFolderStructure()
            throws FileOperationException, DatabaseOperationException {
        FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(getAutosaveFolderPath().toString(),
                true, null, true);
    }

    /**
     * Delete folder structure.
     *
     * @throws FileOperationException the file operation exception
     */
    public void deleteFolderStructure() throws FileOperationException {
        try {
            Files.walkFileTree(getAutosaveFolderPath(), new DSFolderDeleteUtility());
        } catch (IOException exp) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FOLDERCREATEDELETEEXCEPTION), exp);
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_FOLDERCREATEDELETEEXCEPTION);
        }
    }

    /**
     * Gets the autosave folder path.
     *
     * @return the autosave folder path
     */
    public Path getAutosaveFolderPath() {
        Path parentPath = Paths.get(osCurrentUserFolderPath, AUTOSAVE_FOLDER);
        return parentPath.toAbsolutePath().normalize();
    }

    /**
     * Gets the autosave info.
     *
     * @param file the file
     * @param maxTabCnt the max tab cnt
     * @return the autosave info
     * @throws FileOperationException the file operation exception
     */
    public AutoSaveInfo getAutosaveInfo(Path file, int maxTabCnt) throws FileOperationException {
        AutoSaveInfo autosaveInfo = null;
        try {
            if (!Files.exists(file)) {
                return null;
            }

            // validate files and throws exception if validation failes
            validateFile(file);

            byte[] bytes = Files.readAllBytes(file);
            // validate byteslength and throws exception if length is 0
            validateBytesLength(bytes.length);

            Gson gson = new Gson();
            String json = new String(bytes, StandardCharsets.UTF_8);

            JsonReader reader = null;
            autosaveInfo = new AutoSaveInfo();
            try {
                reader = new JsonReader(new StringReader(json));
                reader.beginObject();

                readJsonFile(maxTabCnt, autosaveInfo, gson, reader);
                reader.endObject();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                        reader = null;
                    } catch (IOException exception) {
                        /* No way to recover from close failure */
                        MPPDBIDELoggerUtility.error("Error while closing json reader", exception);
                    }
                }
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("getting Auto save info of file failed", exception);
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION);
        }

        return autosaveInfo;
    }

    private void readJsonFile(int maxTabCnt, AutoSaveInfo autosaveInfo, Gson gson, JsonReader reader)
            throws IOException, FileOperationException {
        while (reader.hasNext()) {
            String name = reader.nextName();
            if ("version".equals(name)) {
                autosaveInfo.setVersion(reader.nextString());
            } else if ("timestamp".equals(name)) {
                autosaveInfo.setTimestamp(reader.nextString());
            } else if ("activeterminal".equals(name)) {
                autosaveInfo.setActiveTerminalName(reader.nextString());
            } else if ("tabinfo".equals(name)) {
                autosaveInfo.setAutosaveMD(new ArrayList<AutoSaveMetadata>());
                reader.beginArray();
                readTabInfo(maxTabCnt, autosaveInfo, gson, reader);

                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
    }

    private void readTabInfo(int maxTabCnt, AutoSaveInfo autosaveInfo, Gson gson, JsonReader reader)
            throws IOException, FileOperationException {
        int count = 0;
        while (reader.hasNext()) {
            // throws Exception when the count is greather than maxTabCnt
            checkFileCount(maxTabCnt, count);

            AutoSaveMetadata md = null;
            try {
                md = gson.fromJson(reader, getTypeForLoad());
            } catch (JsonIOException | JsonSyntaxException exception) {
                MPPDBIDELoggerUtility.error("getting Auto save info of file failed", exception);
                throw new FileOperationException(IMessagesConstants.PRESERVESQL_JSONEXCEPTION);
            }

            autosaveInfo.addAutoSaveMetadata(md);
            count++;
        }
    }

    private void validateBytesLength(int byteslength) throws FileOperationException {
        if (byteslength == 0) {
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION);
        }
    }

    private void checkFileCount(int maxTabCnt, int count) throws FileOperationException {
        if (count > maxTabCnt) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_JSONEXCEPTION));
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_JSONEXCEPTION);
        }
    }

    private void validateFile(Path file) throws IOException, FileOperationException {
        int maxFileSize = 5242880;
        if (Files.isDirectory(file) || Files.isSymbolicLink(file) || !Files.isReadable(file)
                || !Files.isRegularFile(file) || Files.size(file) == 0 || Files.size(file) > maxFileSize) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION));
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION);
        }
    }

    /**
     * Save autosave info.
     *
     * @param autosaveInfo the autosave info
     * @param fileName the file name
     * @param tempFileName the temp file name
     * @throws FileOperationException the file operation exception
     */
    public void saveAutosaveInfo(AutoSaveInfo autosaveInfo, String fileName, String tempFileName)
            throws FileOperationException {
        try {
            createFolderStructure();
            Path temp = createAutosaveTempFile(tempFileName);
            Gson gson = new Gson();
            Type type = getType();
            String json = gson.toJson(autosaveInfo, type);
            Files.write(temp, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException | DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("save auto save info failed", exception);
            deleteFile(tempFileName);
            throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEWRITEEXCEPTION);
        }
        rename(tempFileName, fileName);
    }

    private Path createAutosaveTempFile(String tempFileName)
            throws IOException, FileOperationException, DatabaseOperationException {
        Path backup = Paths.get(getAutosaveFolderPath().toString(), tempFileName);

        if (Files.exists(backup)) {
            Files.delete(backup);
        }
        FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(backup.toString(), false, null,
                true);

        return backup;
    }

    private Type getType() {
        return new AutoSaveTypeToken().getType();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AutoSaveTypeToken.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class AutoSaveTypeToken extends TypeToken<AutoSaveInfo> {

    }

    private Type getTypeForLoad() {
        return new AutoSaveMetaDataTypeToken().getType();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AutoSaveMetaDataTypeToken.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class AutoSaveMetaDataTypeToken extends TypeToken<AutoSaveMetadata> {

    }

    /**
     * Sets the os current user folder path.
     *
     * @param currentOsUserPath the new os current user folder path
     */
    public void setOsCurrentUserFolderPath(String currentOsUserPath) {
        this.osCurrentUserFolderPath = currentOsUserPath;
    }

    /**
     * Gets the autosave info.
     *
     * @param autosaveFileName the autosave file name
     * @param maxTabCnt the max tab cnt
     * @return the autosave info
     * @throws FileOperationException the file operation exception
     */
    public AutoSaveInfo getAutosaveInfo(String autosaveFileName, int maxTabCnt) throws FileOperationException {
        return getAutosaveInfo(Paths.get(getAutosaveFolderPath().toString(), autosaveFileName), maxTabCnt);
    }

    /**
     * Rename.
     *
     * @param srcFileName the src file name
     * @param dstFileName the dst file name
     */
    public void rename(String srcFileName, String dstFileName) {
        try {
            CopyOption[] newOptions = new CopyOption[2];
            newOptions[0] = StandardCopyOption.REPLACE_EXISTING;
            newOptions[1] = StandardCopyOption.ATOMIC_MOVE;
            Files.move(Paths.get(getAutosaveFolderPath().toString(), srcFileName),
                    Paths.get(getAutosaveFolderPath().toString(), dstFileName), newOptions);
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("rename auto save info file failed", exception);
        }

    }

    /**
     * Delete stale files.
     *
     * @param validFileList the valid file list
     * @param autoSaveDataFilePattern the auto save data file pattern
     */
    public void deleteStaleFiles(final ArrayList<String> validFileList, final String autoSaveDataFilePattern) {
        File folder = new File(getAutosaveFolderPath().toString());
        File[] list = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(autoSaveDataFilePattern) && !validFileList.contains(name)) {
                    return true;
                }

                return false;
            }
        });

        if (list != null) {
            for (File item : list) {
                try {
                    Files.walkFileTree(Paths.get(item.getCanonicalPath()), new DSFolderDeleteUtility());
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("Delete auto saved file failed.", exception);
                }
            }
        }
    }

    /**
     * Delete file.
     *
     * @param fileName the file name
     */
    public void deleteFile(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(getAutosaveFolderPath().toString(), fileName));
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Delete auto saved file failed.", exception);
        }
    }

    /**
     * Delete folder structure if empty.
     */
    public void deleteFolderStructureIfEmpty() {
        try {
            Files.delete(getAutosaveFolderPath());
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Delete auto save folder failed", exception);
        }
    }

    /**
     * Checks if is valid file.
     *
     * @param fileName the file name
     * @param fileLimit long value
     * @return true, if is valid file
     */
    public boolean isValidFile(String fileName, double fileLimit) {
        Path filePath = Paths.get(getAutosaveFolderPath().toString(), fileName);
        double fileSizeInMB = FileUtils.sizeOf(filePath.toFile()) / (double) (1024 * 1024);
        try {
            if (Files.exists(filePath) && !Files.isDirectory(filePath) && !Files.isSymbolicLink(filePath)
                    && Files.isReadable(filePath) && Files.isRegularFile(filePath) && Files.size(filePath) != 0
                    && fileSizeInMB < fileLimit) {
                return true;
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Checking if file is valid failed. ", exception);
        }

        return false;
    }

    /**
     * Read.
     *
     * @param fileName the file name
     * @return the byte[]
     * @throws FileOperationException the file operation exception
     */
    public byte[] read(String fileName, double fileLimit) throws FileOperationException {
        Path file = Paths.get(getAutosaveFolderPath().toString(), fileName);
        double fileSizeInMB = FileUtils.sizeOf(file.toFile()) / (double) (1024 * 1024);

        if (Files.exists(file) && !Files.isDirectory(file) && !Files.isSymbolicLink(file) && Files.isReadable(file)
                && Files.isRegularFile(file) && (fileLimit != 0 && fileSizeInMB < fileLimit)) {
            byte[] bytes = null;
            try {
                bytes = Files.readAllBytes(file);
            } catch (IOException exp) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION), exp);
                throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION);
            }

            validateBytesLength(bytes.length);

            return bytes;
        }
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION));
        throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEREADEXCEPTION);
    }

    /**
     * Gets the file output stream.
     *
     * @param metaData the meta data
     * @return the file output stream
     * @throws FileOperationException the file operation exception
     */
    public FileOutputStream getFileOutputStream(AutoSaveMetadata metaData) throws FileOperationException {
        FileOutputStream fileWriter = null;
        Path file = Paths.get(getAutosaveFolderPath().toString(), metaData.getAutoSaveFileName());

        if (!Files.exists(file) || (Files.exists(file) && !Files.isDirectory(file) && !Files.isSymbolicLink(file)
                && Files.isReadable(file) && Files.isRegularFile(file))) {
            try {
                if (Files.exists(file)) {
                    Files.delete(file);
                }

                file = FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(file.toString(),
                        false, null, true);
                fileWriter = new FileOutputStream(file.toFile());
            } catch (IOException | DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Write to file failed.", exception);
                throw new FileOperationException(IMessagesConstants.PRESERVESQL_FILEWRITEEXCEPTION);
            }
        }

        return fileWriter;
    }
}
