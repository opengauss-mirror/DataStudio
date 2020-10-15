/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
