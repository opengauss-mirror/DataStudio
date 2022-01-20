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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * Title: class
 * Description: The Class CreateViewRelyInfo.
 *
 * @since 3.0.0
 */
public class CreateViewRelyInfo implements ICreateViewRelyInfo {
    private String schemaName;
    private String tableName;
    private String ddlSentence;
    private String fixedSchemaName;
    private boolean isEditView;
    private String fixedViewName;
    private Database db;

    public CreateViewRelyInfo(Database db) {
        this.db = db;
    }

    @Override
    public String getFixedSchemaName() {
        return fixedSchemaName;
    }

    @Override
    public List<String> getAllSchemas() {
        List<String> schemas = new ArrayList<String>();
        Iterator<Namespace> objectIter = null;
        boolean hasMore = false;
        objectIter = db.getAllNameSpaces().iterator();
        hasMore = objectIter.hasNext();
        String name = null;
        ServerObject namespace = null;
        while (hasMore) {
            namespace = objectIter.next();
            name = namespace.getName();
            schemas.add(name);
            hasMore = objectIter.hasNext();
        }
        return schemas;
    }

    @Override
    public List<String> getAllTablesBySchema(String schema) {
        List<String> tableList = new ArrayList<String>();
        Iterator<Namespace> objectIter = null;
        boolean hasMore = false;
        objectIter = db.getAllNameSpaces().iterator();
        hasMore = objectIter.hasNext();
        String name = null;
        Namespace namespace = null;
        while (hasMore) {
            namespace = objectIter.next();
            name = namespace.getName();
            if (name.equals(schema)) {
                ArrayList<TableMetaData> tables = namespace.getAllTablesForNamespace();
                for (TableMetaData table : tables) {
                    tableList.add(table.getName());
                }
            }
            hasMore = objectIter.hasNext();
        }
        return tableList;
    }

    @Override
    public List<String> getAllColumnsByTable(String schema, String table) {
        List<String> columnList = new ArrayList<String>();
        Iterator<Namespace> objectIter = null;
        boolean hasMore = false;
        objectIter = db.getAllNameSpaces().iterator();
        hasMore = objectIter.hasNext();
        String name = null;
        Namespace namespace = null;
        while (hasMore) {
            namespace = objectIter.next();
            name = namespace.getName();
            if (name.equals(schema)) {
                ArrayList<TableMetaData> tables = namespace.getAllTablesForNamespace();
                for (TableMetaData tableObject : tables) {
                    if (tableObject.getName().equals(table)) {
                        Iterator<ColumnMetaData> columnObjectIter = null;
                        columnObjectIter = tableObject.getColumns().getList().iterator();
                        boolean hasMoreColumn = false;
                        hasMoreColumn = columnObjectIter.hasNext();
                        ColumnMetaData column = null;
                        columnList.add("*");
                        if ("pg_catalog".equals(schema)) {
                            columnList.add("oid");
                        }
                        while (hasMoreColumn) {
                            column = columnObjectIter.next();
                            columnList.add(column.getName());
                            hasMoreColumn = columnObjectIter.hasNext();
                        }
                        break;
                    }
                }
            }
            hasMore = objectIter.hasNext();
        }
        return columnList;
    }

    @Override
    public void setSchemaName(String schema) {
        this.schemaName = schema;
    }

    @Override
    public void setTableName(String table) {
        this.tableName = table;
    }

    @Override
    public void setDdlSentence(String ddlSentence) {
        this.ddlSentence = ddlSentence;
    }

    @Override
    public String getDdlSentence() {
        return ddlSentence;
    }

    @Override
    public void setFixedSchemaName(String schema) {
        this.fixedSchemaName = schema;
    }

    @Override
    public void setIsEditView(boolean isEditView) {
        this.isEditView = isEditView;
    }

    @Override
    public boolean getIsEditView() {
        return isEditView;
    }

    @Override
    public void setFixedViewName(String view) {
        this.fixedViewName = view;
    }

    @Override
    public String getFixedViewName() {
        return fixedViewName;
    }
}
