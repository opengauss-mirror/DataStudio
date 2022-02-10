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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSIndexPropertiesGridDataProvider.
 * 
 * @since 3.0.0
 */
public class DSIndexPropertiesGridDataProvider {

    /**
     * Perform index delete.
     *
     * @param indexList the index list
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void performIndexDelete(List<IndexMetaData> indexList, DBConnection freeConnection,
            IDSGridEditDataRow row) {
        List<IndexMetaData> indexList2 = new ArrayList<>(indexList);
        boolean isIndexDropped = false;
        for (IndexMetaData index : indexList2) {

            if (index.getName().equals(row.getValue(0))) {
                isIndexDropped = false;
                PropertiesInfoExecuteQueryUtility.deleteIndexQuery((IndexMetaData) index, freeConnection, row);
                break;
            } else {
                isIndexDropped = true;
            }
        }

        if (isIndexDropped || indexList2.size() == 0) {
            row.setCommitStatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE,
                    row.getOriginalValue(0)));
            row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
        }

    }

    /**
     * Perform index update.
     *
     * @param indexList the index list
     * @param freeConnection the free connection
     * @param updatedRows the updated rows
     */
    public static void performIndexUpdate(List<IndexMetaData> indexList, DBConnection freeConnection,
            List<IDSGridEditDataRow> updatedRows) {
        boolean isIndexDropped = false;
        for (IDSGridEditDataRow row : updatedRows) {
            for (IndexMetaData index : indexList) {
                if (row.getOriginalValue(0).equals(index.getName())) {
                    isIndexDropped = false;
                    PropertiesInfoExecuteQueryUtility.updateIndexInfoQuery(freeConnection, row, index);
                    break;

                } else {
                    isIndexDropped = true;
                }

            }

            if (isIndexDropped || indexList.size() == 0) {
                row.setCommitStatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE,
                        row.getOriginalValue(0)));
                row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            }
        }
    }

}
