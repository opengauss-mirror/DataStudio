/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.bl.mock.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.opengauss.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

public class ProfileDiskUtilityHelper extends ProfileDiskUtility {
    public String getUserProfileFolderName(String profileId) {
        return "PROFILE" + profileId;
    }
    
    public Path getConnectionProfilepath(String profileFolderName) {
        Path parentPath = Paths.get(".", MPPDBIDEConstants.PROFILE_BASE_PATH, profileFolderName);
        return parentPath;
    }


}
