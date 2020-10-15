/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.export;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: PrivilegeParameters
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 09-Sep-2020]
 * @since 09-Sep-2020
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