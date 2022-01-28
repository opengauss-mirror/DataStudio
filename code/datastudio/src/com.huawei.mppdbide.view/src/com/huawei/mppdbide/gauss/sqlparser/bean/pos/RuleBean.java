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

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * Title: RuleBean
 *
 * @since 3.0.0
 */
public class RuleBean {

    private ScriptBlockInfo scriptBlockInfo = null;

    private ScriptBlockInfo previousScriptBlock = null;

    private boolean continueLoop = false;

    /**
     * Gets the script block info.
     *
     * @return the script block info
     */
    public ScriptBlockInfo getScriptBlockInfo() {
        return scriptBlockInfo;
    }

    /**
     * Sets the script block info.
     *
     * @param scriptBlockInfo the new script block info
     */
    public void setScriptBlockInfo(ScriptBlockInfo scriptBlockInfo) {
        this.scriptBlockInfo = scriptBlockInfo;
    }

    /**
     * Checks if is continue loop.
     *
     * @return true, if is continue loop
     */
    public boolean isContinueLoop() {
        return continueLoop;
    }

    /**
     * Sets the continue loop.
     *
     * @param continueLoop the new continue loop
     */
    public void setContinueLoop(boolean continueLoop) {
        this.continueLoop = continueLoop;
    }

    /**
     * return the previous script block
     * 
     * @return return the previous script block
     */
    public ScriptBlockInfo getPreviousScriptBlock() {
        return previousScriptBlock;
    }

    public void setPreviousScriptBlock(ScriptBlockInfo previousScriptBlock) {
        this.previousScriptBlock = previousScriptBlock;
    }

}
