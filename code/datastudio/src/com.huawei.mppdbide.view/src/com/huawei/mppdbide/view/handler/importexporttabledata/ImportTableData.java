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
 * @since 3.0.0
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
