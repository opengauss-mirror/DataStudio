/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSIndexPropertiesGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
