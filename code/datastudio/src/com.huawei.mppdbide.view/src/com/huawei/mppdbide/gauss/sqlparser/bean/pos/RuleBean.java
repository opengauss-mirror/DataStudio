/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.bean.pos;

import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;

/**
 * Title: RuleBean Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 19-Aug-2019]
 * @since 19-Aug-2019
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
