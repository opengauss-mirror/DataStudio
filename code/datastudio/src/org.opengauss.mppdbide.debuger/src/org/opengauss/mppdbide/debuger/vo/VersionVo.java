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

package org.opengauss.mppdbide.debuger.vo;

import org.opengauss.mppdbide.debuger.annotation.DumpFiled;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;

/**
 * Title: VersionVo for use
 *
 * @since 3.0.0
 */
public class VersionVo {
    /**
     * this is version str
     */
    @DumpFiled
    public String version;

    /**
     * this is debuggerVersion
     */
    public String debuggerVersion;

    /**
     * this is version str
     */
    public String serverversionstr;

    /**
     * this is version number
     */
    public Integer serverversionnum;

    /**
     * this is proxy api version
     */
    public Integer proxyapiver;

    /**
     * this is server process id
     */
    public Long serverprocessid;

    /**
     * get version
     * 
     * @return the version string
     */
    public String getDebuggerVersion() {
        String versionStr = this.version;
        Integer start = versionStr.indexOf("openGauss") + 9;
        Integer end = versionStr.indexOf("build");
        versionStr = versionStr.substring(start, end);
        versionStr = versionStr.replaceAll("\\.", "").trim();
        Integer ver = Integer.valueOf(versionStr);
        if (ver < DebugConstants.DBE_DEBUGGER_MIN_VERSION) {
            return DebugConstants.PL_DEBUGGER;
        } else {
            return DebugConstants.DBE_DEBUGGER;
        }
    }

    /**
     * check version
     *
     * @return check version
     */
    public boolean isPldebugger() {
        this.debuggerVersion = getDebuggerVersion();
        return debuggerVersion.equalsIgnoreCase(DebugConstants.PL_DEBUGGER);
    }
}
