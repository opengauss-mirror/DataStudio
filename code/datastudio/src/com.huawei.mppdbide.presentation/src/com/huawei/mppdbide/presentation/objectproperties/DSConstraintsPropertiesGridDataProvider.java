/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSConstraintsPropertiesGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DSConstraintsPropertiesGridDataProvider {

    /**
     * Perform constraint delete.
     *
     * @param constraintList the constraint list
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void performConstraintDelete(List<ConstraintMetaData> constraintList, DBConnection freeConnection,
            IDSGridEditDataRow row) {
        boolean isConsDropped = false;
        for (ConstraintMetaData cons : constraintList) {

            if (cons.getName().equals(row.getValue(0))) {
                isConsDropped = false;
                PropertiesInfoExecuteQueryUtility.deleteConstraintQuery((ConstraintMetaData) cons, freeConnection, row);
                break;
            } else {
                isConsDropped = true;
            }
        }
        if (isConsDropped) {

            if (row.getOriginalValue(0) != null && !row.getOriginalValue(0).toString().isEmpty()) {

                row.setCommitStatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE,
                        row.getOriginalValue(0)));
            } else if (row.getOriginalValue(0) != null) {
                row.setCommitStatusMessage(MessageConfigLoader.getProperty(
                        IMessagesConstants.PROPERTIES_ERROR_MESSAGE_FOR_CONSTRAINT, row.getOriginalValue(0)));
            }

            row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
        }
    }

    /**
     * Perform constraint update.
     *
     * @param constraintList the constraint list
     * @param freeConnection the free connection
     * @param updatedRows the updated rows
     */
    public static void performConstraintUpdate(List<ConstraintMetaData> constraintList, DBConnection freeConnection,
            List<IDSGridEditDataRow> updatedRows) {
        boolean isConsDropped = false;
        for (IDSGridEditDataRow row : updatedRows) {
            for (ConstraintMetaData cons : constraintList) {
                if (row.getOriginalValue(0).equals(cons.getName())) {
                    isConsDropped = false;
                    PropertiesInfoExecuteQueryUtility.updateConstraintInfoQuery(freeConnection, row, cons);
                    break;

                } else {
                    isConsDropped = true;
                }

            }
            if (isConsDropped) {
                if (row.getOriginalValue(0) != null && !row.getOriginalValue(0).toString().isEmpty()) {

                    row.setCommitStatusMessage(MessageConfigLoader
                            .getProperty(IMessagesConstants.PROPERTIES_ERROR_MESSAGE, row.getOriginalValue(0)));
                } else if (row.getOriginalValue(0) != null) {
                    row.setCommitStatusMessage(MessageConfigLoader.getProperty(
                            IMessagesConstants.PROPERTIES_ERROR_MESSAGE_FOR_CONSTRAINT, row.getOriginalValue(0)));
                }
                row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
            }
        }
    }

}
