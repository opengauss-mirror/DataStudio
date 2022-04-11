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

package org.opengauss.mppdbide.utils.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Set;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetFilePermission.
 *
 * @since 3.0.0
 */
public class SetFilePermission implements ISetFilePermission {

    /**
     * Creates the file with permission.
     *
     * @param path the path
     * @param isDir the is dir
     * @param userChosenPermissions the user chosen permissions
     * @param setDefaultOnNull the set default on null
     * @return the path
     * @throws DatabaseOperationException the database operation exception *
     * org.opengauss.mppdbide.utils.files.ISetFilePermission#
     * createFileWithPermission(java.lang.String, boolean, java.util.Set,
     * boolean)
     */
    @Override
    public final Path createFileWithPermission(String path, boolean isDir,
            final Set<AclEntryPermission> userChosenPermissions, boolean setDefaultOnNull)
            throws DatabaseOperationException {

        // Convert string to nio path object
        Path newPath = Paths.get(path);

        // Check if file already exists
        boolean fileExists = false;
        if (newPath != null) {
            fileExists = Files.exists(newPath);
        }

        // Create file attribute with security permission
        FileAttribute<List<AclEntry>> fileAttributes = new DSFileAttributes(userChosenPermissions);
        try {
            if (isDir) {
                // If create log directory and logs folder does not exist,
                // create it with security permissions.
                if (!fileExists) {
                    if (setDefaultOnNull) {
                        try {
                            newPath = DSFilesWrapper.createDirectory(newPath, fileAttributes);
                        } catch (FileOperationException e) {
                            newPath = Files.createDirectory(newPath);
                        }
                    } else {
                        newPath = Files.createDirectory(newPath);
                    }
                }
            } else {
                // Create file with security permissions.
                if (!fileExists) {
                    if (setDefaultOnNull) {
                        newPath = DSFilesWrapper.createFile(newPath, fileAttributes);
                    } else {
                        newPath = Files.createFile(newPath);
                    }
                }
            }

        } catch (IOException | FileOperationException exception) {
            try {
                Files.deleteIfExists(newPath);
            } catch (IOException e1) {
                MPPDBIDELoggerUtility.error("Error while deleting file in exception.", e1);
                throw new DatabaseOperationException("Error while deleting file in exception.");
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.FILE_PERMISSION_ERROR),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.FILE_PERMISSION_ERROR, exception);
        }
        return newPath;
    }

}
