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

package org.opengauss.mppdbide.presentation.exportdata;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataCore.
 * 
 * @since 3.0.0
 */
public class ImportExportData extends AbstractImportExportData {

    private String displayTableName;

    /**
     * Instantiates a new import export data core.
     *
     * @param obj the obj
     */
    public ImportExportData(ServerObject obj) {
        super(obj);
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    @Override
    public String getDisplayName() {
        displayTableName = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_TABLE_SUCESS,
                ((TableMetaData) importExportServerObj).getDisplayName());
        return displayTableName;
    }

    /**
     * Gets the display table name.
     *
     * @return the display table name
     */
    public String getDisplayTableName() {
        displayTableName = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_TABLE_SUCESS,
                ((TableMetaData) importExportServerObj).getDisplayName());
        return displayTableName;
    }

    /**
     * Gets the progress label name.
     *
     * @return the progress label name
     */
    public String getProgressLabelName() {
        TableMetaData table = (TableMetaData) importExportServerObj;
        displayTableName = ProgressBarLabelFormatter.getProgressLabelForTableWithoutMsg(table.getName(),
                table.getNamespace().getName(), table.getDatabase().getDbName(),
                table.getDatabase().getServer().getName());
        return displayTableName;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return ((TableMetaData) importExportServerObj).getDatabase();
    }

}
