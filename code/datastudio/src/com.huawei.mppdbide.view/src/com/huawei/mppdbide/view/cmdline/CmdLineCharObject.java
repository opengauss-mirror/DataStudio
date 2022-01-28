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

package com.huawei.mppdbide.view.cmdline;

import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * Title: CmdLineObject
 *
 * @since 3.0.0
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
