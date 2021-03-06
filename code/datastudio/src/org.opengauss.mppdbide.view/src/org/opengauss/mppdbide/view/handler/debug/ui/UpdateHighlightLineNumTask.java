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

import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class UpdateHighlightLineNumTask.
 *
 * @since 3.0.0
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