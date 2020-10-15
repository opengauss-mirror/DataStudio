/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.view.ui.terminal.BatchDropDisplayUIManager;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchDropHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
