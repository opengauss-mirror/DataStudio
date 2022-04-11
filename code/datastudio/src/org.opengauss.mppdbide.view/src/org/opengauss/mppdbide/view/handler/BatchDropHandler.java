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

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.view.ui.terminal.BatchDropDisplayUIManager;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropHandler.
 *
 * @since 3.0.0
 */
public class BatchDropHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }

        final List<?> selectedObjects = getSelectedItems();
        if (null == selectedObjects) {
            return;
        }

        BatchDropDisplayUIManager batchDropWorker = new BatchDropDisplayUIManager(selectedObjects);
        batchDropWorker.startUIWork();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return true;
    }

    /**
     * Gets the selected items.
     *
     * @return the selected items
     */
    private List<?> getSelectedItems() {
        return IHandlerUtilities.getObjectBrowserSelectedObjects();
    }
}
