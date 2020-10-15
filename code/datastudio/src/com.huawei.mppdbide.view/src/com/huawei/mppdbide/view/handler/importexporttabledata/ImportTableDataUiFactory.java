/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.importexporttabledata;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * Title: ExportTableDataUiFactory
 * 
 * Description:A factory for export table data Ui.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author gWX773294
 * @version
 * @since 30-August-2019
 */
public class ImportTableDataUiFactory {

    /**
     * Gets the import table data UI initializer.
     *
     * @param object the object
     * @param shell the shell
     * @return the import table data UI initializer
     */
    public void getImportTableDataUIInitializer(Object object, Shell shell) {
        if (null != object) {
            if (object instanceof TableMetaData) {
                ImportTableData importtabledata = new ImportTableData();
                importtabledata.excuteImportTableData((TableMetaData) object, shell);
            }
        }

    }
}
