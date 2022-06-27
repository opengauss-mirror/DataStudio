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

package org.opengauss.mppdbide.view.dto.version;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIVersionDO.
 *
 * @since 3.0.0
 */
public class UIVersionDO {
    private static final String RELEASE_NAME = "Data Studio ";
    private static final int VERSION = 3;
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
