/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class DbAssistantSupportedVersions.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DbAssistantSupportedVersions {
    private static final List<String> DBASSISTANT_SUPPORTEDLIST = Arrays.asList("openGauss 1.0.0");

    /**
     * Gets the db assistant supported versions.
     *
     * @return the db assistant supported versions
     */
    public static List<String> getDbAssistantSupportedVersions() {
        return DBASSISTANT_SUPPORTEDLIST;
    }

    /**
     * Checks if is db assistant supported.
     *
     * @param docRealVersion the doc real version
     * @return true, if is db assistant supported
     */
    public static boolean isDbAssistantSupported(String docRealVersion) {
        if (DBASSISTANT_SUPPORTEDLIST.contains(docRealVersion)) {
            return true;
        } else if (DBAssistantWindow.isDBAssistVersionExist(null, docRealVersion)) {
            return true;
        } else {
            return false;
        }
    }
}
