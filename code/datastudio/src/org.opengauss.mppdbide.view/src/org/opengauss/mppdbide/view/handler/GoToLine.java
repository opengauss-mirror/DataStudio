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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.view.ui.GoToLineDialog;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class GoToLine.
 *
 * @since 3.0.0
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
            } else {
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
