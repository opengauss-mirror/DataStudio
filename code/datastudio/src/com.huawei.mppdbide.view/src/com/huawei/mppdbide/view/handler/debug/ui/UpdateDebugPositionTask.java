/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug.ui;

import org.eclipse.jface.text.BadLocationException;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class UpdateDebugPositionTask implements Runnable {
    private int showLine = -1;
    public UpdateDebugPositionTask(int showLine) {
        this.showLine = showLine;
    }

    @Override
    public void run() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (plSourceEditor == null) {
            return;
        }

        plSourceEditor.remoteDebugPosition();
        try {
            if (showLine >= 0) {
                plSourceEditor.createDebugPosition(showLine);
            }
        } catch (BadLocationException e) {
            MPPDBIDELoggerUtility.error("set debugPostion at " + showLine + " failed,err=" + e.getMessage());
        }
    }

}
