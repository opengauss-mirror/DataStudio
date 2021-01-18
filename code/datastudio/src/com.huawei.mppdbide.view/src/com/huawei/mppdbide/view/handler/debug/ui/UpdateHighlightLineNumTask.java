/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.ui;

import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class UpdateHighlightLineNumTask.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class UpdateHighlightLineNumTask implements Runnable {
    @Override
    public void run() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
        int lineNum = sourceEditor.getHighlightLineNum();
        if (lineNum != -1) {
            plSourceEditor.deHighlightLine(lineNum);
            sourceEditor.setHighlightLineNum(-1);
        }
    }
}