/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.ui;

import java.util.Locale;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.resulttab.ResultTabManager;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 14,12,2020]
 * @since 14,12,2020
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
