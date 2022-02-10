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

import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class Undo.
 *
 * @since 3.0.0
 */
public class Undo {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Object partObject = UIElement.getInstance().getActivePartObject();

        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            terminal.getTerminalCore().undo();
            terminal.resetSQLTerminalButton();
            terminal.resetAutoCommitButton();
        } else if (partObject instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObject;
            sourceEditor.getSourceEditorCore().undo();
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

        if (partObject instanceof SQLTerminal) {
            return ((SQLTerminal) partObject).getTerminalCore().canUndo();
        } else if (partObject instanceof PLSourceEditor) {
            return ((PLSourceEditor) partObject).getSourceEditorCore().canUndo();
        }

        return false;
    }
}
