/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.huawei.mppdbide.bl.serverdatacache.ProfileMetaData.ProfileInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfoJsonValidator;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade.ConnectionProfileUpgradeManager;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFolderDeleteUtility;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class ProfileDiskUtility.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ProfileDiskUtility {

    private static final int CONNECTION_PROPERTIES_SIZE = 2 * 1024 * 1024;

    private static final String PROFILE_FOLDERNAME_PREFIX = "PROFILE";

    private static List<String> serverList = new ArrayList<String>(2);

    private static List<String> pswdDecFailList = new ArrayList<String>(2);

    private static List<String> exceptionList = new ArrayList<String>(2);

    private String osCurrentUserFolderPath;
    private Path profileFolderPath;
    private ProfileMetaData metaData;

    /**
     * Instantiates a new profile disk utility.
     */
    public ProfileDiskUtility() {
        this.metaData = new ProfileMetaData();
    }

    /**
     * Gets the meta data.
     *
     * @return the meta data
     */
    public ProfileMetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the meta data.
     *
     * @param metaData the new meta data
     */
    public void setMetaData(ProfileMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * Gets the user profile folder name.
     *
     * @param profileId the profile id
     * @return the user profile folder name
     */
    public String getUserProfileFolderName(String profileId) {
        return PROFILE_FOLDERNAME_PREFIX + profileId;
    }

    /**
     * Write profile to disk.
     *
     * @param serverInfo the server info
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public void writeProfileToDisk(IServerConnectionInfo serverInfo)
            throws DatabaseOperationException, DataStudioSecurityException {
        Path parentFolder = null;
        Path file = null;
        try {

            parentFolder = createFolderStructure(getUserProfileFolderName(serverInfo.getProfileId()));
            Path propFile = Paths.get(parentFolder.toString(), MPPDBIDEConstants.CONNECTION_PROFILE_NAME);
            file = createConnectionPropFileIfNotExists(propFile);
            writeProfileToFile(file, serverInfo);
            getMetaData().addProfile(serverInfo.getConectionName(), serverInfo.getProfileId(), propFile.toString(),
                    serverInfo.getVersion());

            writeProfileMetaFile();

        } catch (IOException | FileOperationException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_WRITE_DISK, exception);
        }
        MPPDBIDELoggerUtility.operationInfo("Created/Updated user profile :" + serverInfo.getProfileId());
    }

    /**
     * Rename profile on disk.
     *
     * @param serverConnInfo the server info
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public void renameProfileOnDisk(IServerConnectionInfo serverConnInfo)
            throws DatabaseOperationException, DataStudioSecurityException {
        Path parentFolder = null;
        Path file = null;
        try {
            parentFolder = getConnectionProfilepath(getUserProfileFolderName(serverConnInfo.getProfileId()));
            Path propFilePath = Paths.get(parentFolder.toString(), MPPDBIDEConstants.CONNECTION_PROFILE_NAME);
            file = createConnectionPropFileIfNotExists(propFilePath);
            writeProfileToFile(file, serverConnInfo);
            getMetaData().addProfile(serverConnInfo.getConectionName(), serverConnInfo.getProfileId(),
                    propFilePath.toString(), serverConnInfo.getVersion());

            writeProfileMetaFile();
        } catch (IOException | FileOperationException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_WRITE_DISK, exception);
        }
        MPPDBIDELoggerUtility.operationInfo("Renamed user profile :" + serverConnInfo.getProfileId());
    }

    /**
     * Write profile to file.
     *
     * @param file the file
     * @param serverInfo the server info
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void writeProfileToFile(Path file, IServerConnectionInfo serverInfo) throws IOException {
        List<IServerConnectionInfo> lstServerInfo = new ArrayList<IServerConnectionInfo>(1);

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type type = getType();
        lstServerInfo.add(serverInfo);
        String json = gson.toJson(lstServerInfo, type);
        Files.write(file, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    private Type getType() {
        return new ProfileTypeToken().getType();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileTypeToken.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class ProfileTypeToken extends TypeToken<List<ServerConnectionInfo>> {
    }

    /**
     * Read profile from file.
     *
     * @param profileInfo the profile info
     * @return the server connection info
     */
    public IServerConnectionInfo readProfileFromFile(ProfileInfo profileInfo) {
        Path file = Paths.get(profileInfo.getFilePath());
        IServerConnectionInfo serverInfo = null;
        try {
            double fileSizeInBytes = FileUtils.sizeOf(file.toFile());
            if (fileSizeInBytes > CONNECTION_PROPERTIES_SIZE) {
                MPPDBIDELoggerUtility.error("Error while reading profile file. File size exceeded 2MB");
                throw new DatabaseOperationException(IMessagesConstants.ERR_READING_PROFILE_DATA);
            }

            if (Files.isReadable(file)) {
                byte[] bytes = Files.readAllBytes(file);
                if (bytes.length == 0) {
                    serverList.add(getServerName(file));
                    return null;
                }
                String json = new String(bytes, StandardCharsets.UTF_8);
                if (!ServerConnectionInfoJsonValidator.validateJson(json)) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE));
                    throw new DatabaseOperationException(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE);
                }
                Type type = getType();
                List<IServerConnectionInfo> lstServerInfo = null;
                if (!profileInfo.getVersionNo().equals(IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION)) {
                    lstServerInfo = getServerInfoObject(json, type, profileInfo.getVersionNo());
                    // update the version in metafile
                    metaData.updateProfileVersion(profileInfo);
                    writeProfileToFile(Paths.get(profileInfo.getFilePath()), lstServerInfo.get(0));
                    writeProfileMetaFile();
                } else {
                    Gson gson = new Gson();
                    lstServerInfo = gson.fromJson(json, type);
                }
                file.getParent();

                if (null != lstServerInfo) {
                    serverInfo = lstServerInfo.size() > 0 ? lstServerInfo.get(0) : null;
                }

            }
        } catch (IOException e) {
            serverList.add(getServerName(file));
        } catch (JsonSyntaxException e) {
            serverList.add(getServerName(file));
        } catch (DatabaseOperationException e) {
            serverList.add(getServerName(file));
        }
        return serverInfo;
    }

    /**
     * Gets the server info object.
     *
     * @param jsonString the json string
     * @param type the type
     * @param profileVersion the profile version
     * @return the server info object
     */
    private List<IServerConnectionInfo> getServerInfoObject(String jsonString, Type type, String profileVersion) {

        List<IServerConnectionInfo> upgradedConnectionProfiles = ConnectionProfileUpgradeManager.getInstance()
                .getUpgradedConnectionProfiles(jsonString, type, profileVersion);

        return upgradedConnectionProfiles;
    }

    /**
     * Gets the decrypted prd.
     *
     * @param serverInfo the server info
     * @return the decrypted prd
     */
    public IServerConnectionInfo getDecryptedPrd(IServerConnectionInfo serverInfo) {
        SecureUtil sec = new SecureUtil();
        String path = ConnectionProfileManagerImpl.getInstance().getProfilePath(serverInfo);
        sec.setPackagePath(path);
        try {
            String prd = new String(serverInfo.getPrd());
            serverInfo.setPrd(sec.decryptPrd(prd));
            prd = null;
        } catch (DataStudioSecurityException e) {
            serverInfo.clearPasrd();
        } catch (NumberFormatException e) {
            serverInfo.clearPasrd();
        }
        return serverInfo;
    }

    /**
     * Gets the server name.
     *
     * @param file the file
     * @return the server name
     */
    public String getServerName(Path file) {
        Path path = file.getParent();
        String dbName = "";
        if (path != null) {
            StringTokenizer st2 = new StringTokenizer(path.toString(), "\\");
            while (st2.hasMoreElements()) {
                dbName = (String) st2.nextElement();
            }
        }
        return dbName;
    }

    /**
     * Gets the profiles.
     *
     * @return the profiles
     * @throws DatabaseOperationException the database operation exception
     * @throws DataStudioSecurityException the data studio security exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public List<IServerConnectionInfo> getProfiles()
            throws DatabaseOperationException, DataStudioSecurityException, IOException {
        List<IServerConnectionInfo> list = new ArrayList<IServerConnectionInfo>(1);
        Map<String, ProfileMetaData.ProfileInfo> maps = getProfilePaths();
        ServerConnectionInfoComparator comparator = new ServerConnectionInfoComparator();
        final int nonExistingIndex = 0;

        Iterator<Entry<String, ProfileInfo>> entrySet = maps.entrySet().iterator();
        Entry<String, ProfileInfo> data;
        boolean isMetadataChanged = false;
        boolean hasNext = entrySet.hasNext();

        while (hasNext) {
            data = entrySet.next();
            IServerConnectionInfo serv = readProfileFromFile(data.getValue());
            if (null != serv) {
                int insertionPoint = Collections.binarySearch(list, serv, comparator);

                if (insertionPoint < nonExistingIndex) {
                    insertionPoint = -(insertionPoint + 1);
                }

                if (DBTYPE.OPENGAUSS.equals(serv.getServerDBType())) {
                    list.add(insertionPoint, serv);
                }
            } else {
                getMetaData().deleteProfile(data.getKey());
                String filePath = data.getValue().getFilePath();
                int lastIndex = filePath.lastIndexOf('\\');
                String profilePath = filePath.substring(0, lastIndex);
                deleteFolder(Paths.get(profilePath));
                MPPDBIDELoggerUtility.info("Profile deleted successfully");
                isMetadataChanged = true;
            }

            hasNext = entrySet.hasNext();
        }

        if (isMetadataChanged) {
            writeProfileMetaFile();
        }

        return list;
    }

    /**
     * Gets the profile paths.
     *
     * @return the profile paths
     * @throws DatabaseOperationException the database operation exception
     */
    private Map<String, ProfileMetaData.ProfileInfo> getProfilePaths() throws DatabaseOperationException {
        Path basePath = Paths.get(getConnctionProfileBasePath().toString(),
                MPPDBIDEConstants.CONNECTION_PROFILE_META_FILE);
        getMetaData().readFromDisk(basePath.toString());
        return getMetaData().getAllProfiles();
    }

    /**
     * Write profile meta file.
     *
     * @return the path
     * @throws DatabaseOperationException the database operation exception
     */
    public Path writeProfileMetaFile() throws DatabaseOperationException {
        Path basePath = getConnctionProfileBasePath();
        Path metaPath = Paths.get(basePath.toString(), MPPDBIDEConstants.CONNECTION_PROFILE_META_FILE);
        if (!Files.exists(metaPath, LinkOption.NOFOLLOW_LINKS)) {
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(metaPath.toString(), false, null,
                    true);
        }

        getMetaData().writeToDisk(metaPath.toString());
        return metaPath;
    }

    /**
     * Creates the connection prop file if not exists.
     *
     * @param propFile the prop file
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws FileOperationException the file operation exception
     */
    private Path createConnectionPropFileIfNotExists(Path propFile)
            throws IOException, FileOperationException, DatabaseOperationException {
        Path propFileAbsolute = propFile.toAbsolutePath().normalize();

        if (!Files.exists(propFileAbsolute)) {
            return FilePermissionFactory.getFilePermissionInstance()
                    .createFileWithPermission(propFileAbsolute.toString(), false, null, true);
        }

        return propFileAbsolute;
    }

    /**
     * Creates the folder structure.
     *
     * @param profileFolderName the profile folder name
     * @return the path
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws FileOperationException the file operation exception
     */
    public Path createFolderStructure(String profileFolderName)
            throws IOException, FileOperationException, DatabaseOperationException {
        if (!Files.exists(getConnctionProfileBasePath())) {
            FilePermissionFactory.getFilePermissionInstance()
                    .createFileWithPermission(getConnctionProfileBasePath().toString(), true, null, true);
        }
        Path folderPath = getConnectionProfilepath(profileFolderName);
        FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(folderPath.toString(), true, null,
                true);
        return folderPath;
    }

    /**
     * Gets the connection profilepath.
     *
     * @param profileFolderName the profile folder name
     * @return the connection profilepath
     */
    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(osCurrentUserFolderPath, MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName);
        return parentPath.normalize();
    }

    /**
     * Gets the history profilepath.
     *
     * @param profileFolderName the profile folder name
     * @return the history profilepath
     */
    public Path getHistoryProfilepath(String profileFolderName) {
        Path historyPath = Paths.get(osCurrentUserFolderPath, MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName,
                MPPDBIDEConstants.HISTORY_BASE_PATH);
        return historyPath.toAbsolutePath().normalize();
    }

    /**
     * Gets the connction profile base path.
     *
     * @return the connction profile base path
     */
    public Path getConnctionProfileBasePath() {
        return Paths.get(osCurrentUserFolderPath, MPPDBIDEConstants.PROFILE_BASE_PATH);
    }

    /**
     * Drop profile folder.
     *
     * @param profileFolderName the profile folder name
     * @param profileName the profile name
     * @throws DatabaseOperationException the database operation exception
     */
    public void dropProfileFolder(String profileFolderName, String profileName) throws DatabaseOperationException {
        // Update metadata file first so, any corruption or failure below will
        // have least impact on further executions
        getMetaData().deleteProfile(profileName);
        writeProfileMetaFile();

        try {
            deleteFolder(getConnectionProfilepath(profileFolderName));
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DROP_CON_PROF_FILE),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DROP_CON_PROF_FILE, exception);
        }
    }

    /**
     * Delete folder.
     *
     * @param path the path
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void deleteFolder(Path path) throws IOException {
        Files.walkFileTree(path, new DSFolderDeleteUtility());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ServerConnectionInfoComparator.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class ServerConnectionInfoComparator implements Comparator<IServerConnectionInfo>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(IServerConnectionInfo object1, IServerConnectionInfo object2) {
            return object1.getConectionName().compareTo(object2.getConectionName());
        }
    }

    /**
     * Gets the exception list.
     *
     * @return the exception list
     */
    public List<String> getExceptionList() {
        StringBuilder server = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if (!serverList.isEmpty()) {
            for (String serverName : serverList) {
                server.append(serverName);
                server.append(",");
            }
            server.deleteCharAt(server.length() - 1);

            exceptionList
                    .add(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROFILE_ERROR_MSG, server.toString()));
            serverList.clear();
        }

        if (!pswdDecFailList.isEmpty()) {
            server = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

            for (String serverName : pswdDecFailList) {
                server.append(serverName);
                server.append(",");
            }
            server.deleteCharAt(server.length() - 1);
            exceptionList
                    .add(MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_DECRYPT_ERROR_MSG, server.toString()));
            pswdDecFailList.clear();
        }

        return exceptionList;
    }

    /**
     * Write exported profile to disk.
     *
     * @param exportFileList the export file list
     * @param filePath the file path
     * @throws DatabaseOperationException the database operation exception
     */
    public void writeExportedProfileToDisk(List<IServerConnectionInfo> exportFileList, String filePath)
            throws DatabaseOperationException {

        Path path = Paths.get(filePath).toAbsolutePath().normalize();
        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Type type = getType();
            String json = gson.toJson(exportFileList, type);
            try {
                FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(path.toString(), false, null,
                        true);
                Files.write(path, json.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Error while writing exported connection profile to the disk", exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_WRITE_DISK, exception);
            }
            MPPDBIDELoggerUtility.operationInfo("Exported user profiles");
        }
    }

    /**
     * Read imported file.
     *
     * @param path the path
     * @param fileSizeLimit the file data size limit
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     */
    public List<IServerConnectionInfo> readImportedFile(String path, double fileSizeLimit)
            throws DatabaseOperationException {
        try {
            Path filePath = Paths.get(path);
            File file = new File(path);
            double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
            if (fileSizeLimit != 0 && fileSizeInMB > fileSizeLimit) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
                throw new MPPDBIDEException(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE);
            }
            List<IServerConnectionInfo> serverInfoList = null;
            if (Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
                byte[] readAllBytes = Files.readAllBytes(filePath);
                String json = new String(readAllBytes, StandardCharsets.UTF_8);
                if (!ServerConnectionInfoJsonValidator.validateJson(json)) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE));
                    throw new FileOperationException(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE);
                }
                Gson gson = new Gson();
                serverInfoList = gson.fromJson(json, getType());
                if (serverInfoList != null) {
                    removeNonOlapProfiles(serverInfoList); 
                }
                
                return serverInfoList;
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("IO Exception occurred while importing the connection profile", exception);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_IMPORTING_CONNECTION_PROFILES), exception);
        } catch (JsonSyntaxException exception) {
            MPPDBIDELoggerUtility.error("Json exception occurred while reading the json file", exception);
            throw new DatabaseOperationException(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE, exception);
        } catch (FileOperationException mppdbException) {
            MPPDBIDELoggerUtility.error("Invalid file format.", mppdbException);
            throw new DatabaseOperationException(IMessagesConstants.IMPORT_PROFILE_INCORRECT_FILE, mppdbException);
        } catch (MPPDBIDEException mppdbException) {
            MPPDBIDELoggerUtility.error("File Size is more than specified.", mppdbException);
            throw new DatabaseOperationException(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE, mppdbException);
        }
        MPPDBIDELoggerUtility.operationInfo("Imported user profiles");
        return new ArrayList<IServerConnectionInfo>();
    }

    private void removeNonOlapProfiles(List<IServerConnectionInfo> serverInfoList) {
        List<IServerConnectionInfo> deleteList = new ArrayList<IServerConnectionInfo>(1);
        for (IServerConnectionInfo info : serverInfoList) {
            if (!DBTYPE.OPENGAUSS.equals(info.getServerDBType())) {
                deleteList.add(info);
            }
        }
        serverInfoList.removeAll(deleteList);
    }

    /**
     * Sets the os current user folder path.
     *
     * @param osCurrentUserFolderPath the new os current user folder path
     */
    public void setOsCurrentUserFolderPath(String osCurrentUserFolderPath) {
        this.osCurrentUserFolderPath = osCurrentUserFolderPath;
    }

    /**
     * Gets the os current user folder path.
     *
     * @return the os current user folder path
     */
    public String getOsCurrentUserFolderPath() {
        return osCurrentUserFolderPath;
    }

    /**
     * Gets the profile folder path.
     *
     * @return the profile folder path
     */
    public Path getProfileFolderPath() {
        profileFolderPath = getConnctionProfileBasePath();
        return profileFolderPath;
    }

    /**
     * Clear exception list.
     */
    public void clearExceptionList() {
        exceptionList.clear();
    }
}
