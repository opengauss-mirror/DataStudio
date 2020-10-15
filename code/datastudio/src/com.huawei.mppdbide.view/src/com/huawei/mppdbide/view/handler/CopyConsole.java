/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CopyConsole.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CopyConsole {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        Clipboard cb = new Clipboard(Display.getDefault());
        cb.clearContents();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;

            ConsoleMessageWindow messageWindow = terminal.getTerminalResultManager().getConsoleWindow();
            if (null != messageWindow) {
                String str = messageWindow.getTextViewer().getDocument().get();

                // DTS fix DTS2017071504367

                if (!str.isEmpty()) {

                    cb.setContents(new Object[] {str}, new Transfer[] {TextTransfer.getInstance()});
                }
            }
            // removing dead code
        } else {
            String str = ConsoleCoreWindow.getInstance().getTextViewer().getDocument().get();

            // DTS fix DTS2017071504367
            if (!str.isEmpty()) {
                cb.setContents(new Object[] {str}, new Transfer[] {TextTransfer.getInstance()});
            }
        }
    }

}
