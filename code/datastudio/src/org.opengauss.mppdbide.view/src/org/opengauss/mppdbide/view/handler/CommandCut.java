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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandCut.
 *
 * @since 3.0.0
 */
public class CommandCut {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        if (Display.getDefault().getFocusControl() instanceof StyledText) {

            Object partObject = UIElement.getInstance().getActivePartObject();
            PLSourceEditorCore core = null;
            if (partObject instanceof PLSourceEditor) {
                core = ((PLSourceEditor) partObject).getSourceEditorCore();
                core.cutSelectedDocText();

            } else if (partObject instanceof SQLTerminal) {
                core = ((SQLTerminal) partObject).getTerminalCore();
                core.cutSelectedDocText();
            } else {
                String key = (String) ((StyledText) Display.getDefault().getFocusControl())
                        .getData(MPPDBIDEConstants.SWTBOT_KEY);
                if (key != null && ("ID_TXT_SQLTERMINAL_TEXT_001".equalsIgnoreCase(key)
                        || "ID_TXT_SRCEDITOR_TEXT_001".equalsIgnoreCase(key))) {
                    ((StyledText) Display.getDefault().getFocusControl()).cut();
                }
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
        StyledText styledText = (StyledText) Display.getDefault().getFocusControl();
        String key = null;
        if (null != styledText) {
            key = (String) styledText.getData(MPPDBIDEConstants.SWTBOT_KEY);
        }
        if (key != null && ("ID_TXT_SQLTERMINAL_TEXT_001".equalsIgnoreCase(key)
                || "ID_TXT_SRCEDITOR_TEXT_001".equalsIgnoreCase(key))) {
            return styledText.getSelectionCount() > 0;
        }
        return false;

    }

}
