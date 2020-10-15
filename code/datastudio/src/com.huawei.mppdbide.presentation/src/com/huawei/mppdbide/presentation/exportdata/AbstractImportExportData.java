/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.util.ArrayList;
import java.util.Observable;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataFactory.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 03 September, 2019
 */
public abstract class AbstractImportExportData extends Observable {

    /**
     * The import export server obj.
     */
    protected ServerObject importExportServerObj;

    /**
     * Instantiates a new abstract import export data.
     *
     * @param obj the obj
     */
    public AbstractImportExportData(ServerObject obj) {
        this.importExportServerObj = obj;
    }

    /**
     * Compose excel query.
     *
     * @param queryBuff the query buff
     * @param importExportoptions the import exportoptions
     * @return the string
     */
    protected String composeExcelQuery(StringBuffer queryBuff, ImportExportOption importExportoptions) {
        queryBuff.append("SELECT");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        if (!importExportoptions.isAllColunms()) {
            queryBuff.append(getSelectedColumn(importExportoptions.getTablecolumns()));
        } else {
            queryBuff.append("*");
        }
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        queryBuff.append("FROM");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        queryBuff.append(this.importExportServerObj.getDisplayName());
        return queryBuff.toString();
    }

    /**
     * Append query format for csv format.
     *
     * @param queryBuff the query buff
     * @return the string
     */
    protected String appendQueryFormatForCsvFormat(StringBuffer queryBuff) {
        queryBuff.append("DELIMITERS");
        return queryBuff.toString();
    }

    /**
     * Appaend tbl name or executed query.
     *
     * @param queryBuff the query buff
     * @param importExportoptions the import exportoptions
     */
    protected void appaendTblNameOrExecutedQuery(StringBuffer queryBuff, ImportExportOption importExportoptions) {
        queryBuff.append(this.importExportServerObj.getDisplayName());

        if (!importExportoptions.isAllColunms()) {
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR).append('(')
                    .append(getSelectedColumn(importExportoptions.getTablecolumns())).append(')');
        }
    }

    private String getSelectedColumn(ArrayList<String> selectedColsList) {
        StringBuilder strBuild = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (String clm : selectedColsList) {
            strBuild.append(ServerObject.getQualifiedObjectName(clm));
            strBuild.append(",");
        }
        if (selectedColsList.size() > 0) {
            strBuild.deleteCharAt(strBuild.length() - 1);
        }
        return strBuild.toString();

    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    protected String getFileName() {
        return importExportServerObj.getName();
    }

    /**
     * Gets the display table name.
     *
     * @return the display table name
     */
    public abstract String getDisplayTableName();

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public abstract String getDisplayName();

    /**
     * Gets the progress label name.
     *
     * @return the progress label name
     */
    public abstract String getProgressLabelName();

    /**
     * Gets the database.
     *
     * @return the database
     */
    public abstract Database getDatabase();
}
