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

package com.huawei.mppdbide.view.functionchange;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.view.ui.PLSourceEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectChangeEvent.
 *
 * @since 3.0.0
 */
public class ObjectChangeEvent {
    private IDebugObject dbgObj;
    private PLSourceEditor editor;
    private ButtonPressed status;

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum ButtonPressed.
     */
    public enum ButtonPressed {

        /**
         * The refresh.
         */
        REFRESH,
        /**
         * The overwrite.
         */
        OVERWRITE,
        /**
         * The nochange.
         */
        NOCHANGE
    }

    /**
     * Gets the dbg obj.
     *
     * @return the dbg obj
     */
    public IDebugObject getDbgObj() {
        return dbgObj;
    }

    /**
     * Sets the dbg obj.
     *
     * @param dbgObj the new dbg obj
     */
    public void setDbgObj(IDebugObject dbgObj) {
        this.dbgObj = dbgObj;
    }

    /**
     * Gets the editor.
     *
     * @return the editor
     */
    public PLSourceEditor getEditor() {
        return editor;
    }

    /**
     * Sets the editor.
     *
     * @param editor the new editor
     */
    public void setEditor(PLSourceEditor editor) {
        this.editor = editor;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public ButtonPressed getStatus() {
        return status;
    }

    /**
     * Update status.
     *
     * @param status1 the status 1
     */
    public void updateStatus(ButtonPressed status1) {
        this.status = status1;
    }

}
