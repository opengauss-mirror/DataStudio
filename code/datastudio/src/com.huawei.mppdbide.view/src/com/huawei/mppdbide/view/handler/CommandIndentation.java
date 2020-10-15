/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;

import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandIndentation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandIndentation {

    /**
     * Execute.
     *
     * @param activePart the active part
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return;
        }

        Object partObject = activePart.getObject();
        if (partObject instanceof PLSourceEditor) {
            ((PLSourceEditor) partObject).getSourceEditorCore()
                    .performIndentUnindentOperation(PLSourceEditorCore.INDENT_OPERATION, null);
        } else if (partObject instanceof SQLTerminal) {
            ((SQLTerminal) partObject).getTerminalCore()
                    .performIndentUnindentOperation(PLSourceEditorCore.INDENT_OPERATION, null);
        }
    }

    /**
     * Can execute.
     *
     * @param activePart the active part
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return false;
        }

        Object partObj = activePart.getObject();
        if (partObj instanceof SQLTerminal) {
            SQLTerminal terminal1 = (SQLTerminal) partObj;
            return terminal1.getTerminalCore().getSelectedLineCount()
                    && terminal1.getTerminalCore().getSourceViewer().isEditable();
        } else if (partObj instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObj;
            return sourceEditor.getSourceEditorCore() != null
                    && sourceEditor.getSourceEditorCore().getSelectedLineCount()
                    && sourceEditor.getSourceEditorCore().getSourceViewer().isEditable();
        }
        return false;
    }
}
