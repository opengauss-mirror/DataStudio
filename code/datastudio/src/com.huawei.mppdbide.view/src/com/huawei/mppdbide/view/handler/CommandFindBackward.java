/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.FindAndReplaceOptions;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandFindBackward.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandFindBackward {

    /* Elements */

    private FindAndReplaceOptions findandreplace;

    private PLSourceEditorCore core;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        Object partObject = UIElement.getInstance().getActivePartObject();

        if (partObject instanceof PLSourceEditor) {
            core = ((PLSourceEditor) partObject).getSourceEditorCore();

        } else if (partObject instanceof SQLTerminal) {
            core = ((SQLTerminal) partObject).getTerminalCore();

        }

        else {
            return;
        }
        findandreplace = core.getFindAndReplaceoptions();

        if (null != findandreplace) {
            findandreplace.setBackwardSearch(true);
            core.findText(false);
        }
    }
}
