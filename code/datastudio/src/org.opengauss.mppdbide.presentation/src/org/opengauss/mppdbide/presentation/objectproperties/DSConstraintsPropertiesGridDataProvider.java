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

import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSConstraintsPropertiesGridDataProvider.
 * 
 * @since 3.0.0
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
