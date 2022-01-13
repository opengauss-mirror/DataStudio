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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandChangeLowerCase.
 *
 * @since 3.0.0
 */
public class CommandChangeLowerCase {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        if (partObject instanceof PLSourceEditor) {
            ((PLSourceEditor) partObject).getSourceEditorCore().changeCase(IMessagesConstants.LOWER_CASE);
        } else if (partObject instanceof SQLTerminal) {
            ((SQLTerminal) partObject).getTerminalCore().changeCase(IMessagesConstants.LOWER_CASE);
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
            SQLTerminal terminalObject = (SQLTerminal) partObject;
            return terminalObject.getTerminalCore().getSelectionCount() > 0
                    && terminalObject.getTerminalCore().getSourceViewer().isEditable();
        } else if (partObject instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObject;
            return sourceEditor.getSourceEditorCore() != null
                    && sourceEditor.getSourceEditorCore().getSelectionCount() > 0
                    && sourceEditor.getSourceEditorCore().getSourceViewer().isEditable();
        }
        return false;
    }
}
