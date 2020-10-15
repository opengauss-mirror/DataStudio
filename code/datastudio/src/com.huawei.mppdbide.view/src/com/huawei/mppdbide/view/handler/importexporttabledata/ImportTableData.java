/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.importexporttabledata;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.ImportExportDataCore;
import com.huawei.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportTableData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 30-August-2019
 */
public class ImportTableData extends AbstractImportTableData {
    private ImportExportDataCore importExportDataCore;

    /**
     * Excute import table data.
     *
     * @param selectedTable the selected table
     * @param shell the shell
     */
    public void excuteImportTableData(TableMetaData selectedTable, Shell shell) {
        try {
            if (!selectedTable.isLoaded()) {
                ObjectBrowserObjectRefreshPresentation.refreshSeverObject(selectedTable);
                if (null == selectedTable.getNamespace().getTables().getObjectById(selectedTable.getOid())) {
                    generateImportErrorMessageDialog();
                    return;
                }
            }
        } catch (MPPDBIDEException exception) {
            generateImportErrorMessageDialog(exception);
            return;
        }
        importExportDataCore = new ImportExportDataCore(selectedTable,
                ImportExportTableData.getColoumns(selectedTable.getColumns()), null, null, null);
        importExportDataCore.setExport(false);
        boolean result = getInformationForImport(shell, importExportDataCore);
        if (!result) {
            importExportDataCore.importExportCleanUp();
            return;
        }
        scheduleImportDataJob(importExportDataCore);
    }
}
