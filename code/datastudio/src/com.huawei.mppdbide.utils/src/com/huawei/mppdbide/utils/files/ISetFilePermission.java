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
 * @since 3.0.0
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
