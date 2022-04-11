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

package org.opengauss.mppdbide.view.ui;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class DbAssistantSupportedVersions.
 *
 * @since 3.0.0
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
