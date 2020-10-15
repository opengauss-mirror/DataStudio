/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandCut.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
