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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade.ConnectionProfileUpgradeManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ProfileMetaData.
 * 
 */

public class ProfileMetaData {

    private static final int NON_EXISTING_INDEX = 0;
    private static final int PROFILE_INDEX_SIZE = 12 * 1024 * 1024;
    private List<ProfileInfo> profileInfos;
    private Gson profileGson;
    private ProfileInfoComparator comparator;
    private boolean isMetaModified;

    /**
     * Instantiates a new profile meta data.
     */
    public ProfileMetaData() {
        profileInfos = new ArrayList<ProfileInfo>(1);
        profileGson = new Gson();
        comparator = new ProfileInfoComparator();
        isMetaModified = false;
    }

    /**
     * Gets the all profiles.
     *
     * @return the all profiles
     */
    public Map<String, ProfileInfo> getAllProfiles() {
        Map<String, ProfileInfo> map = new HashMap<String, ProfileInfo>(1);
        ProfileInfo info = null;
        int index = 0;
        int size = profileInfos.size();
        for (; index < size; index++) {
            info = profileInfos.get(index);
            map.put(info.getProfileName(), info);
        }

        return map;
    }

    /**
     * Gets the profile id.
     *
     * @param profileName the profile name
     * @return the profile id
     */
    public int getProfileId(String profileName) {
        for (ProfileInfo info : profileInfos) {
            if (profileName.equals(info.getProfileName())) {
                return Integer.parseInt(info.profileId);
            }
        }
        return -1;
    }

    /**
     * Gets the all profile ids.
     *
     * @return the all profile ids
     */
    public List<Integer> getAllProfileIds() {
        List<Integer> profileIds = new ArrayList<Integer>(1);
        for (ProfileInfo info : profileInfos) {
            profileIds.add(Integer.parseInt(info.profileId));
        }
        return profileIds;
    }

    /**
     * Adds the profile.
     *
     * @param name the name
     * @param id the id
     * @param path the path
     * @param versionNumber the version number
     */
    public void addProfile(String name, String id, String path, String versionNumber) {
        ProfileInfo profile = getProfileByName(name);
        if (profile == null) {
            profile = getProfileById(id);
        }
        if (null != profile) {
            if (!path.equals(profile.getFilePath())) {
                profile.setFilePath(path);
                isMetaModified = true;
            }

            if (!name.equals(profile.getProfileName())) {
                profile.setProfileName(name);
                profile.setVersionNo(versionNumber);
                isMetaModified = true;
            }

        } else {
            ProfileInfo newProfile = new ProfileInfo(name, id, path, versionNumber);
            int insertionPoint = Collections.binarySearch(profileInfos, newProfile, comparator);

            if (insertionPoint < NON_EXISTING_INDEX) {
                insertionPoint = -(insertionPoint + 1);
            }

            profileInfos.add(insertionPoint, newProfile);
            isMetaModified = true;
        }
    }

    /**
     * Gets the profile by name.
     *
     * @param name the name
     * @return the profile by name
     */
    private ProfileInfo getProfileByName(String name) {
        for (ProfileInfo profile : profileInfos) {
            if (name.equals(profile.getProfileName())) {
                return profile;
            }
        }

        return null;
    }

    /**
     * Gets the profile by id.
     *
     * @param id the id
     * @return the profile by id
     */
    private ProfileInfo getProfileById(String id) {
        for (ProfileInfo profile : profileInfos) {
            if (id.equals(profile.getProfileId())) {
                return profile;
            }
        }

        return null;
    }

    /**
     * Delete profile.
     *
     * @param name the name
     */
    public void deleteProfile(String name) {
        ProfileInfo deleteInfo = null;
        for (ProfileInfo info : profileInfos) {
            if (name.equals(info.getProfileName())) {
                deleteInfo = info;
                break;
            }
        }

        if (null != deleteInfo) {
            profileInfos.remove(deleteInfo);
            isMetaModified = true;
        }
    }

    /**
     * Write to disk.
     *
     * @param path the path
     * @throws DatabaseOperationException the database operation exception
     */
    public final void writeToDisk(String path) throws DatabaseOperationException {
        String jsonStr = getJsonString();
        ISetFilePermission file = FilePermissionFactory.getFilePermissionInstance();
        Path filePath = Paths.get(path);
        if (!Files.exists(filePath, LinkOption.NOFOLLOW_LINKS)) {
            filePath = file.createFileWithPermission(path, false, null, true);
        } else if (!Files.isWritable(filePath)) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_WRITE_DISK));
            throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_WRITE_DISK);
        }

        if (!isMetaModified) {
            return;
        }

        try {
            Files.write(filePath, jsonStr.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            deleteFileNoThrowError(filePath);
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WRITING_PROFILE_TO_DISK),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_WRITING_PROFILE_TO_DISK, exception);
        }
    }

    /**
     * Update profile version.
     *
     * @param info the info
     */
    public void updateProfileVersion(ProfileInfo info) {
        info.setVersionNo(IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);
        isMetaModified = true;
    }

    /**
     * Read from disk.
     *
     * @param pathStr the path str
     * @throws DatabaseOperationException the database operation exception
     */
    public final void readFromDisk(String pathStr) throws DatabaseOperationException {
        String stdizedPath = null;
        try {
            stdizedPath = new File(pathStr).getCanonicalPath();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_FILE_REACHABLE),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_FILE_REACHABLE, exception);
        }
        if (null != stdizedPath) {
            Path path = Paths.get(stdizedPath);
            if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
                if (!Files.isReadable(path)) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_PROFILE_FILE_REACHABLE));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_PROFILE_FILE_REACHABLE);
                }

                double fileSizeInBytes = FileUtils.sizeOf(path.toFile());
                if (fileSizeInBytes > PROFILE_INDEX_SIZE) {
                    MPPDBIDELoggerUtility.error("Error while reading profile file. File size exceeded 12MB");
                    throw new DatabaseOperationException(IMessagesConstants.ERR_READING_PROFILE_DATA);
                }

                try {
                    byte[] bytes = Files.readAllBytes(path);
                    if (bytes.length == 0) {
                        return;
                    }
                    String jsonStr = new String(bytes, StandardCharsets.UTF_8);
                    parseJsonString(jsonStr);
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error(
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_READING_PROFILE_DATA), exception);
                    throw new DatabaseOperationException(IMessagesConstants.ERR_READING_PROFILE_DATA, exception);
                }
            }
        }

    }

    /**
     * Gets the json string.
     *
     * @return the json string
     */
    private String getJsonString() {
        return profileGson.toJson(profileInfos, getType());
    }

    /**
     * Parses the json string.
     *
     * @param json the json
     * @throws DatabaseOperationException the database operation exception
     */
    private void parseJsonString(String json) throws DatabaseOperationException {
        profileInfos = profileGson.fromJson(json, getType());
        if (profileInfos != null) {
            checkVersionCompatibility();
        }
    }

    /**
     * Check version compatibility.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    private void checkVersionCompatibility() throws DatabaseOperationException {
        ConnectionProfileUpgradeManager instance = ConnectionProfileUpgradeManager.getInstance();
        for (ProfileInfo profileInfo : profileInfos) {

            String versionNo = profileInfo.getVersionNo();

            if (!instance.isVersionAvailable(versionNo) || instance.getVersionIndex(versionNo) > instance
                    .getVersionIndex(IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION)) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.VERSION_NO_INCOMPATIBLE));
                throw new DatabaseOperationException(IMessagesConstants.VERSION_NO_INCOMPATIBLE);
            }

        }
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
     */
    private static final class ProfileTypeToken extends TypeToken<List<ProfileInfo>> {
    }

    /**
     * Delete file no throw error.
     *
     * @param filePath the file path
     */
    private void deleteFileNoThrowError(Path filePath) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("ProfileMetaData: delete file failed", exception);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileInfo.
     * 
     */
    public static final class ProfileInfo {

        private String profileName;
        private String profileId;
        private String filePath;
        private String versionNo;

        private ProfileInfo(String profileName, String profileId, String filePath, String versionNo) {
            this.setProfileName(profileName);
            this.filePath = filePath;
            this.profileId = profileId;
            this.versionNo = versionNo;
        }

        /**
         * Gets the version no.
         *
         * @return the version no
         */
        public String getVersionNo() {
            return versionNo;
        }

        /**
         * Gets the profile name.
         *
         * @return the profile name
         */
        public String getProfileName() {
            return profileName;
        }

        /**
         * Gets the file path.
         *
         * @return the file path
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * Sets the file path.
         *
         * @param filePath the new file path
         */
        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Gets the profile id.
         *
         * @return the profile id
         */
        public String getProfileId() {
            return profileId;
        }

        /**
         * Sets the profile name.
         *
         * @param profileName the new profile name
         */
        public void setProfileName(String profileName) {
            this.profileName = profileName;
        }

        /**
         * Sets the version no.
         *
         * @param versionNo the new version no
         */
        public void setVersionNo(String versionNo) {
            this.versionNo = versionNo;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ProfileInfoComparator.
     * 
     */
    private static class ProfileInfoComparator implements Comparator<ProfileInfo>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(ProfileInfo object1, ProfileInfo object2) {
            return object1.getProfileName().compareTo(object2.getProfileName());
        }
    }
}
