/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIVersionHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
