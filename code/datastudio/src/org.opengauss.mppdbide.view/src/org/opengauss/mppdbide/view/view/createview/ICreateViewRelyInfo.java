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

package org.opengauss.mppdbide.view.view.createview;

import java.util.List;

/**
 * Title: interface
 * Description: The interface ICreateViewRelyInfo.
 *
 * @since 3.0.0
 */
public interface ICreateViewRelyInfo {
    /**
     * Sets if is edit view
     *
     * @param boolean true if is edit view
     */
    void setIsEditView(boolean isEditView);

    /**
     * Gets if is edit view
     *
     * @return boolean true if is edit view
     */
    boolean getIsEditView();

    /**
     * Sets fixed view name
     *
     * @param String the fixed view name
     */
    void setFixedViewName(String view);

    /**
     * Gets the fixed view name
     *
     * @return String the fixed view name
     */
    String getFixedViewName();

    /**
     * Sets fixed schema name
     *
     * @param String the fixed schema name
     */
    void setFixedSchemaName(String schema);

    /**
     * Gets the fixed schema name
     *
     * @return String the fixed schema name
     */
    String getFixedSchemaName();

    /**
     * Gets all schemas
     *
     * @return List<String> the schema name list
     */
    List<String> getAllSchemas();

    /**
     * Gets all tables by schema
     *
     * @param String the schema name
     * @return List<String> the table name list
     */
    List<String> getAllTablesBySchema(String schema);

    /**
     * Gets all columns by table name
     *
     * @param String the schema name
     * @param String the table name
     * @return List<String> the column name list
     */
    List<String> getAllColumnsByTable(String schema, String table);

    /**
     * Sets the schema name
     *
     * @param String the schema name
     */
    void setSchemaName(String schema);

    /**
     * Sets the table name
     *
     * @param String the table name
     */
    void setTableName(String table);

    /**
     * Sets the ddl sentence
     *
     * @param String the ddlSentence
     */
    void setDdlSentence(String ddlSentence);

    /**
     * Gets the ddl sentence
     *
     * @return String the ddl sentence
     */
    String getDdlSentence();
}