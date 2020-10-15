/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.cmdline;

import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * Title: CmdLineObject Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 26-Dec-2019]
 * @since 26-Dec-2019
 */
public class CmdLineCharObject {

    private char[] prd = new char[0];

    /**
     * Gets the prd.
     *
     * @return the prd
     */
    public char[] getPrd() {
        if (this.prd == null) {
            return new char[0];
        }
        return prd.clone();
    }

    /**
     * Sets the prd.
     *
     * @param prd the new prd
     */
    public void setPrd(char[] prd) {
        this.prd = prd.clone();
    }

    /**
     * Clear pssrd.
     */
    public void clearPssrd() {
        SecureUtil.clearPassword(this.prd);
    }

}
