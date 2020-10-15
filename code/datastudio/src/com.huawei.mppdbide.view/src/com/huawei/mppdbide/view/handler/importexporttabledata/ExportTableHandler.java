/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.importexporttabledata;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ITableMetaData;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExportTableHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        // Get the user selected table
        Object object = IHandlerUtilities.getObjectBrowserSelectedObject();
        ExportTableDataUiFactory.getExportTableDataUIInitializer(object);
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ITableMetaData table = IHandlerUtilities.getSelectedITableMetaData();
        if (null == table) {
            return false;
        }

        if (!IHandlerUtilities.getExportDataSelectionOptions()) {
            return false;
        }

        return !IHandlerUtilities.isSelectedTableForignPartition();
    }
}
