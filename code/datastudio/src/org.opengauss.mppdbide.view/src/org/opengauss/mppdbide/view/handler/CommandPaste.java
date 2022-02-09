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
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandPaste.
 *
 * @since 3.0.0
 */
public class CommandPaste {

    /**
     * Execute.
     */

    @Execute
    public void execute() {
        String elapsedTime = null;
        IExecTimer exc = new ExecTimer("CommandPaste#execute");

        if (Display.getDefault().getFocusControl() instanceof StyledText) {
            String key = (String) ((StyledText) Display.getDefault().getFocusControl())
                    .getData(MPPDBIDEConstants.SWTBOT_KEY);
            if (key != null && ("ID_TXT_SQLTERMINAL_TEXT_001".equalsIgnoreCase(key)
                    || "ID_TXT_SRCEDITOR_TEXT_001".equalsIgnoreCase(key))) {
                try {
                    exc.start();
                    ((StyledText) Display.getDefault().getFocusControl()).paste();
                } catch (OutOfMemoryError e) {
                    try {
                        exc.stop();
                        elapsedTime = exc.getElapsedTime();
                    } catch (DatabaseOperationException e1) {
                        MPPDBIDELoggerUtility.info("Execute timer operation failed.");
                    }
                    UIElement.getInstance().outOfMemoryCatch(elapsedTime, e.getMessage());
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
        Clipboard clipboard = new Clipboard(Display.getDefault());
        TextTransfer textTransfer = TextTransfer.getInstance();
        String textData = (String) clipboard.getContents(textTransfer);

        StyledText styledText = (StyledText) Display.getDefault().getFocusControl();
        String key = null;
        if (null != styledText) {
            key = (String) styledText.getData(MPPDBIDEConstants.SWTBOT_KEY);
        }
        return key != null
                && ("ID_TXT_SQLTERMINAL_TEXT_001".equalsIgnoreCase(key)
                        || "ID_TXT_SRCEDITOR_TEXT_001".equalsIgnoreCase(key))
                && null != textData && !"".equals(textData);
    }
}
