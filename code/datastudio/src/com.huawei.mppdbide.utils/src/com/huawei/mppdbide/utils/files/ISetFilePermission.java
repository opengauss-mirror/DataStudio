/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.files;

import java.nio.file.Path;
import java.nio.file.attribute.AclEntryPermission;
import java.util.EnumSet;
import java.util.Set;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ISetFilePermission.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface ISetFilePermission {
    /**
     * Creates the file with permission.
     *
     * @param path the path
     * @param isDir the is dir
     * @param userChosenPermissions the user chosen permissions
     * @param setDefaultOnNull the set default on null
     * @return the path
     * @throws DatabaseOperationException the database operation exception
     */
    Path createFileWithPermission(String path, boolean isDir, Set<AclEntryPermission> userChosenPermissions,
            boolean setDefaultOnNull) throws DatabaseOperationException;
}
