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

package org.opengauss.mppdbide.bl.export;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: PrivilegeParameters
 * 
 */

public class PrivilegeParameters {
    private boolean allWithoutGo = true;
    private boolean allWithGo = true;
    private String privString;
    private StringBuffer priviledgeBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
    private StringBuffer previlegeWithGrantBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

    public void setPriviledgeBuff(StringBuffer priviledgeBuff) {
        this.priviledgeBuff = priviledgeBuff;
    }

    public void setPrevilegeWithGrantBuff(StringBuffer previlegeWithGrantBuff) {
        this.previlegeWithGrantBuff = previlegeWithGrantBuff;
    }

    public StringBuffer getPriviledgeBuff() {
        return priviledgeBuff;
    }

    public StringBuffer getPrevilegeWithGrantBuff() {
        return previlegeWithGrantBuff;
    }

    public boolean isAllWithoutGo() {
        return allWithoutGo;
    }

    public void setAllWithoutGo(boolean allWithoutGo) {
        this.allWithoutGo = allWithoutGo;
    }

    public boolean isAllWithGo() {
        return allWithGo;
    }

    public void setAllWithGo(boolean allWithGo) {
        this.allWithGo = allWithGo;
    }

    public String getPrivString() {
        return privString;
    }

    public void setPrivString(String privString) {
        this.privString = privString;
    }

}