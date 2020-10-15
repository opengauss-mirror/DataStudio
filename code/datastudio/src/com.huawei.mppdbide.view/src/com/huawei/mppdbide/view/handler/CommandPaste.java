/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CommandPaste.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
