/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.ui.GoToLineDialog;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class GoToLine.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GoToLine {

    private SourceViewer viewer;
    private Boolean flag = true;
    private int lineNumber;
    private static final int OK_ID = 0;

    /**
     * Execute.
     *
     * @param parentShell the parent shell
     */
    @Execute
    public void execute(final Shell parentShell) {

        Object partObject = UIElement.getInstance().getActivePartObject();

        if (partObject instanceof PLSourceEditor) {
            viewer = ((PLSourceEditor) partObject).getSourceEditorCore().getSourceViewer();
            flag = true;
        } else if (partObject instanceof SQLTerminal) {
            viewer = ((SQLTerminal) partObject).getTerminalCore().getSourceViewer();
            flag = false;
        }

        else {
            flag = null;
            return;
        }

        GoToLineDialog dialog = new GoToLineDialog(parentShell, viewer);

        int buttonvalue = dialog.open();
        if (buttonvalue == OK_ID) {
            lineNumber = dialog.getViewerLinenumber();
            if (flag) {
                ((PLSourceEditor) partObject).getSourceEditorCore().goToLineNumber(lineNumber);
            } else if (!flag) {
                ((SQLTerminal) partObject).getTerminalCore().goToLineNumber(lineNumber);
            }
        }
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
        }

        if (null != txtViewer) {
            if (!txtViewer.getTextWidget().isFocusControl()) {
                return false;
            }
            return true;
        }

        return false;
    }

}
