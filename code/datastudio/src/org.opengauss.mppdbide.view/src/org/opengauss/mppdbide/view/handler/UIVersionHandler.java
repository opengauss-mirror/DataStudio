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

package org.opengauss.mppdbide.view.handler;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIVersionHandler.
 *
 * @since 3.0.0
 */
public class UIVersionHandler {
    private static final List<String> LST_BL_VERSIONS = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private static final List<String> LST_UTIL_VERSIONS = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    private static boolean isVersionCompatible = true;

    /**
     * Add all supported versions in the list.
     */
    static {
        LST_BL_VERSIONS.add("Data Studio 18.0.T003");
        LST_UTIL_VERSIONS.add("Data Studio 18.0.T003");
    }

    /**
     * Sets the version compatible.
     *
     * @param versionCompatible the new version compatible
     */
    public static void setVersionCompatible(boolean versionCompatible) {
        isVersionCompatible = versionCompatible;
    }

    /**
     * Checks if is version compatible.
     *
     * @return true, if is version compatible
     */
    public static boolean isVersionCompatible() {
        return isVersionCompatible;
    }
}
