/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.importexporttabledata;

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
 * @since 29-August-2019
 */
public class ExportTableDataUiFactory {

    /**
     * Gets the export table data UI initializer.
     *
     * @param object the object
     * @return the export table data UI initializer
     */
    public static void getExportTableDataUIInitializer(Object object) {
        if (null != object) {
            if (object instanceof TableMetaData) {
                ExportTableData exporttabledata = new ExportTableData();
                exporttabledata.excuteExport((TableMetaData) object);
            }
        }
    }
}
