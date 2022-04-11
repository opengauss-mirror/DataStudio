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

package org.opengauss.mppdbide.gauss.format.utils.wrapper;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: FormatStringWrapper
 *
 * @since 3.0.0
 */
public class FormatStringWrapper {
    private StringBuilder formatdata = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
    private String lastStringAdded = null;

    /**
     * returns the StringBuilder
     * 
     * @return returns the StringBuilder which maintain the data
     */
    public StringBuilder getFormatdata() {
        return formatdata;
    }

    /**
     * returns the last string added
     * 
     * @return String which is added latest
     */
    public String getLastStringAdded() {
        return lastStringAdded;
    }

    /**
     * set the lastStringAdded
     * 
     * @param lastStringAdded set the lastStringAdded
     */
    public void setLastStringAdded(String lastStringAdded) {
        this.lastStringAdded = lastStringAdded;
    }

    /**
     * append the give text to the format data
     * 
     * @param nodeText string to be appended
     */
    public void append(String nodeText) {
        lastStringAdded = nodeText;
        formatdata.append(nodeText);
    }

    /**
     * returns the formatted string
     */
    public String toString() {
        return formatdata.toString();
    }
}
