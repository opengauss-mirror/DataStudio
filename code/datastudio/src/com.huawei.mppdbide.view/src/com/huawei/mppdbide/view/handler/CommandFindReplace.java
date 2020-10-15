/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.ui.ConsoleWindow;
import com.huawei.mppdbide.view.ui.FindAndReplaceDialog;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandFindReplace.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CommandFindReplace {

    /**
     * Execute.
     *
     * @param parentShell the parent shell
     */
    @Execute
    public void execute(final Shell parentShell) {
        Object partObject = UIElement.getInstance().getActivePartObject();
        TextViewer txtViewer = null;

        if (partObject instanceof PLSourceEditor) {
            txtViewer = ((PLSourceEditor) partObject).getSourceEditorCore().getSourceViewer();
        } else if (partObject instanceof SQLTerminal) {
            txtViewer = ((SQLTerminal) partObject).getTerminalCore().getSourceViewer();
        }

        if (null == txtViewer) {
            return;
        }

        FindAndReplaceDialog findDialog = new FindAndReplaceDialog(parentShell, txtViewer);
        findDialog.open();
        /* Create a find and replace dialog box */
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        TextViewer txtViewer = null;

        if (partObject instanceof PLSourceEditor) {
            txtViewer = ((PLSourceEditor) partObject).getSourceEditorCore().getSourceViewer();
        } else if (partObject instanceof SQLTerminal) {
            txtViewer = ((SQLTerminal) partObject).getTerminalCore().getSourceViewer();
        } else if (partObject instanceof ConsoleWindow) {
            txtViewer = ((ConsoleWindow) partObject).getConsoleCore().getTextViewer();
        }

        if (null != txtViewer) {
            if (!txtViewer.getTextWidget().isFocusControl()) {
                return false;
            }
            return txtViewer.getFindReplaceTarget().canPerformFind();
        }

        return false;
    }

}
