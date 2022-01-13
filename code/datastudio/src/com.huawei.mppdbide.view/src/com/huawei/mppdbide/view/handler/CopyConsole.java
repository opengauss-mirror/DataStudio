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
 * @since 3.0.0
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

                if (!str.isEmpty()) {

                    cb.setContents(new Object[] {str}, new Transfer[] {TextTransfer.getInstance()});
                }
            }
            // removing dead code
        } else {
            String str = ConsoleCoreWindow.getInstance().getTextViewer().getDocument().get();

            if (!str.isEmpty()) {
                cb.setContents(new Object[] {str}, new Transfer[] {TextTransfer.getInstance()});
            }
        }
    }

}
