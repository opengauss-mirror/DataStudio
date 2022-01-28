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

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class ViewBody.
 *
 * @since 3.0.0
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
