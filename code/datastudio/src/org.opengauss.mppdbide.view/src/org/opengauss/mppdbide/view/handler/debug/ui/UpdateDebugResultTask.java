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

package org.opengauss.mppdbide.view.handler.debug.ui;

import java.util.Locale;

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.terminal.resulttab.ResultTabManager;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class UpdateDebugResultTask implements Runnable {
    private Event event;

    public UpdateDebugResultTask(Event event) {
        this.event = event;
    }

    @Override
    public void run() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (plSourceEditor == null) {
            return;
        }
        plSourceEditor.setEditable(true);
        plSourceEditor.setExecuteInProgress(false);

        ResultTabManager resultTabManager = plSourceEditor.getResultManager();
        resultTabManager.createConsole();
        String resultMsg = "";
        if (event.hasException()) {
            resultMsg = String.format(Locale.ENGLISH,
                    "debug result with error:  %s",
                    event.getException().getMessage());
        } else {
            resultMsg = String.format(Locale.ENGLISH,
                    "debug result:  %s",
                    event.getAddition().orElse("NULL"));
        }
        resultTabManager.getConsoleWindow().logInfo(resultMsg);
    }
}
