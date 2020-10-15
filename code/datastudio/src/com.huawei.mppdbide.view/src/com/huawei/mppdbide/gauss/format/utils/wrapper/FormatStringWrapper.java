/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.utils.wrapper;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: FormatStringWrapper
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 05-Dec-2019]
 * @since 05-Dec-2019
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
