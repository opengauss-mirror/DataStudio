/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.dto.version;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIVersionDO.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UIVersionDO {
    private static final String RELEASE_NAME = "Data Studio ";
    private static final int VERSION = 2;
    private static final String DOT = ".";
    private static final int SUB_VERSION = 0;
    private static final int SUB_SUB_VERSION = 0;
    private static final String MINOR_VERSION = "";

    /**
     * Gets the UI version.
     *
     * @return the UI version
     */
    public static String getUIVersion() {
        StringBuilder sb = new StringBuilder(RELEASE_NAME);
        sb.append(VERSION).append(DOT).append(SUB_VERSION).append(DOT).append(SUB_SUB_VERSION)
                .append(MPPDBIDEConstants.SPACE_CHAR).append(MINOR_VERSION);
        return sb.toString();
    }
}
