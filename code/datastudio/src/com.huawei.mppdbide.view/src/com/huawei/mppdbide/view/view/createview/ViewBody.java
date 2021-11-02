/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class ViewBody.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class ViewBody {
    private String schemaName;
    private String tableName;
    private String columnName;
    private String columnAlias;

    public ViewBody (String schemaName, String tableName, String columnName, String columnAlias) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.columnName = columnName;
        this.columnAlias = columnAlias;
    }

    /**
     * Gets schema name
     *
     * @return String the schema name
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Gets table name
     *
     * @return String the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets column name
     *
     * @return String the column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Gets column alias
     *
     * @return String the column alias
     */
    public String getColumnAlias() {
        return columnAlias;
    }
}
