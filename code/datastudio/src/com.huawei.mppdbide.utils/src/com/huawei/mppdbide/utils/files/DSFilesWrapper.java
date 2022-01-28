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

package com.huawei.mppdbide.utils.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSFilesWrapper.
 *
 * @since 3.0.0
 */
public class DSFilesWrapper {
    /**
     * Creates the directory.
     *
     * @param folderPath the folder path
     * @param fileAttributes the file attributes
     * @return the path
     * @throws FileOperationException the file operation exception
     */
    public static Path createDirectory(Path folderPath, FileAttribute<List<AclEntry>> fileAttributes)
            throws FileOperationException {
        Path directoryName = folderPath.getFileName();
        Path parent = folderPath.getParent();
        if (null != parent && parent.toAbsolutePath() != null && parent.toAbsolutePath().normalize() != null) {
            String workingDir = null;
            try {
                workingDir = new File(parent.toAbsolutePath().normalize().toString()).getCanonicalPath();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Exception when trying to access the file", exception);
                throw new FileOperationException(IMessagesConstants.CREATE_FOLDER_FAIL_ERR);
            }
            if (null != workingDir && directoryName != null) {
                Path folder = Paths.get(workingDir, directoryName.normalize().toString());

                if (!Files.exists(folder)) {
                    Set<String> supportedAttr = folder.getFileSystem().supportedFileAttributeViews();
                    try {
                        Files.createDirectory(folderPath);
                        if (supportedAttr.contains("acl")) {
                            setWindowsPermissions(folderPath);
                        }
                    } catch (IOException e) {
                        throw new FileOperationException(IMessagesConstants.CREATE_FOLDER_FAIL_ERR);
                    }
                }
            }
        }
        return folderPath;
    }

    /**
     * Sets the windows permissions.
     *
     * @param path the new windows permissions
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void setWindowsPermissions(Path path) throws IOException {
        AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class);
        UserPrincipal owner = view.getOwner(); 
        List<AclEntry> acl = view.getAcl();
        ListIterator<AclEntry> it = acl.listIterator();
        while (it.hasNext()) {
            AclEntry entry = it.next();
            if ("BUILTIN\\Administrators".equals(entry.principal().getName())
                    || "NT AUTHORITY\\SYSTEM".equals(entry.principal().getName())) {
                continue;
            }
            it.remove();
        }
        AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(owner)
                .setPermissions(AclEntryPermission.READ_DATA, AclEntryPermission.WRITE_DATA,
                        AclEntryPermission.APPEND_DATA, AclEntryPermission.READ_NAMED_ATTRS,
                        AclEntryPermission.WRITE_NAMED_ATTRS, AclEntryPermission.EXECUTE,
                        AclEntryPermission.READ_ATTRIBUTES, AclEntryPermission.WRITE_ATTRIBUTES,
                        AclEntryPermission.DELETE, AclEntryPermission.READ_ACL, AclEntryPermission.SYNCHRONIZE)
                .build();
        acl.add(entry);
        view.setAcl(acl);
    }

    /**
     * Creates the file.
     *
     * @param propFileAbsolute the prop file absolute
     * @param fileAttributes the file attributes
     * @return the path
     * @throws FileOperationException the file operation exception
     */
    public static Path createFile(Path propFileAbsolute, FileAttribute<List<AclEntry>> fileAttributes)
            throws FileOperationException {
        Path fileName = propFileAbsolute.getFileName();
        Path parent = propFileAbsolute.getParent();
        if (null != parent && parent.toAbsolutePath() != null && parent.toAbsolutePath().normalize() != null) {
            File workingDir = new File(parent.toAbsolutePath().normalize().toString());
            if (fileName != null) {
                Path file = Paths.get(workingDir.toString(), fileName.toString());

                if (!Files.exists(file)) {
                    Set<String> supportedAttr = file.getFileSystem().supportedFileAttributeViews();
                    try {
                        Files.createFile(file);
                        if (supportedAttr.contains("acl")) {
                            setWindowsPermissions(file);
                        }
                    } catch (IOException exp) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FOLDER_FAIL_ERR), exp);
                        throw new FileOperationException(IMessagesConstants.CREATE_FOLDER_FAIL_ERR);
                    }
                }
            }
        }
        return propFileAbsolute;
    }

    /**
     * Checks if is existing directory.
     *
     * @param directoryPath the directory path
     * @return return true if the path is a existing directory; otherwise return
     * false.
     * @Title: isExistingDirectory
     * @Description: validate the directory if is existing
     */
    public static boolean isExistingDirectory(String directoryPath) {
        if (directoryPath == null) {
            return false;
        }
        File filePath = null;
        if (FileValidationUtils.validateFilePathName(directoryPath)) {
            filePath = new File(directoryPath);
        }
        return filePath != null && filePath.exists() && filePath.isDirectory();
    }
}
