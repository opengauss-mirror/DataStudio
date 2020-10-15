/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
