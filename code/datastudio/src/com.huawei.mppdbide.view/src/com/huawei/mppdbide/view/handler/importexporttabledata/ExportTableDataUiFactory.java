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

import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * Title: ExportTableDataUiFactory
 * 
 * Description:A factory for export table data Ui.
 *
 * @since 3.0.0
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
