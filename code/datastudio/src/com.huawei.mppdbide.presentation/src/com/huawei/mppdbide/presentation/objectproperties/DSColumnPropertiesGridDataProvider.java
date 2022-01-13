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

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSColumnPropertiesGridDataProvider.
 * 
 * @since 3.0.0
 */
public class DSColumnPropertiesGridDataProvider {

    /**
     * Perform column delete.
     *
     * @param columnMetaDataList the column meta data list
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void performColumnDelete(List<ColumnMetaData> columnMetaDataList, DBConnection freeConnection,
            IDSGridEditDataRow row) {
        boolean isColDropped = false;
        for (ColumnMetaData col : columnMetaDataList) {
            if (col.getName().equals(row.getValue(0))) {
                // need to check the solution if column has been dropped
                // from the OB and delete oprtn is performed.
                isColDropped = false;
                PropertiesInfoExecuteQueryUtility.deleteColumnQuery((ColumnMetaData) col, freeConnection, row);
                break;

            } else {
                isColDropped = true;
            }

        }
        if (isColDropped) {
            row.setCommitStatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE,
                    row.getOriginalValue(0)));
            row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
        }
    }

    /**
     * Perform column update.
     *
     * @param columnMetaDataList the column meta data list
     * @param freeConnection the free connection
     * @param updatedRows the updated rows
     */
    public static void performColumnUpdate(List<ColumnMetaData> columnMetaDataList, DBConnection freeConnection,
            List<IDSGridEditDataRow> updatedRows) {
        boolean isColumnDropped = false;
        for (IDSGridEditDataRow row : updatedRows) {
            for (ColumnMetaData column : columnMetaDataList) {
                if (row.getOriginalValue(0).equals(column.getName())) {
                    isColumnDropped = false;
                    PropertiesInfoExecuteQueryUtility.updateColumnInfoQuery(freeConnection, row, column);
                    break;

                } else {
                    isColumnDropped = true;
                }

            }
            if (isColumnDropped) {
                row.setCommitStatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE,
                        row.getOriginalValue(0)));
                row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            }
        }
    }

}
