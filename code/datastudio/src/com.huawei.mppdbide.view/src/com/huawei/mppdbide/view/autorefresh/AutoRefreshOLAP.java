/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.autorefresh;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.presentation.IExecutionContext;
import com.huawei.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import com.huawei.mppdbide.view.utils.common.UICommonUtil;

/**
 * Title: AutoRefreshOLAP
 * 
 * Description:AutoRefreshOLAP
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 30-Aug-2019]
 * @since 30-Aug-2019
 */

public class AutoRefreshOLAP extends AutoRefreshObject {

    /**
     * Instantiates a new auto refresh OLAP.
     *
     * @param listOfObjects the list of objects
     * @param executionContext the execution context
     */
    public AutoRefreshOLAP(HashSet<Object> listOfObjects, IExecutionContext executionContext) {
        super(listOfObjects, executionContext);
    }

    /**
     * Gets the view objects.
     *
     * @param database the database
     * @param namespace the namespace
     * @param listOfObjects the list of objects
     * @param tableName the table name
     * @return the view objects
     */
    @Override
    public void getViewObjects(Database database, INamespace namespace, RefreshObjectDetails refObject2,
            String tableName) {
        refObject2.setParent(((Namespace) namespace).getViewGroup());
    }

    /**
     * Gets the table objects.
     *
     * @param db the db
     * @param namespace the namespace
     * @param listOfObjects the list of objects
     * @return the table objects
     */
    @Override
    public void getTableObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2) {
        refObject2.setParent(((Namespace) namespace).getTablesGroup());

    }

    /**
     * Gets the alter table object.
     *
     * @param tableName the table name
     * @param namespace the namespace
     * @param listOfObjects the list of objects
     * @return the alter table object
     */
    @Override
    public void getAlterTableObject(String tableName, INamespace namespace, RefreshObjectDetails refObject2) {
        tableName = getUnQuotedIdentifier(tableName);
        refObject2.setParent(((Namespace) namespace).getTables().get(tableName));
    }

    /**
     * Gets the alter view object.
     *
     * @param tableName the table name
     * @param namespace the namespace
     * @param listOfObjects the list of objects
     * @return the alter view object
     */
    @Override
    public void getAlterViewObject(String tableName, INamespace namespace, RefreshObjectDetails refObject2) {
        tableName = getUnQuotedIdentifier(tableName);
        refObject2.setParent(((Namespace) namespace).getViewGroup().get(tableName));
    }

    /**
     * Gets the un quoted identifier.
     *
     * @param queryString the query string
     * @return the un quoted identifier
     */
    public String getUnQuotedIdentifier(String queryString) {
        return UICommonUtil.getUnQuotedIdentifierOLAP(queryString);
    }

    /**
     * Gets the trigger objects.
     *
     * @param Database the database
     * @param INamespace the namespace
     * @param RefreshObjectDetails the refresh object detail
     * @return the trigger objects
     */
    @Override
    public void getTriggerObjects(Database database,
            INamespace namespace,
            RefreshObjectDetails refObject2) {
        refObject2.setParent(((Namespace) namespace).getTriggerObjectGroup());
    }

    /**
     * Gets the table name for index.
     *
     * @param namespace the namespace
     * @param tableName the table name
     * @return the table name for index
     */
    @Override
    public String getTableNameForIndex(INamespace namespace, String tableName) {
        TableObjectGroup tables = ((Namespace) namespace).getTables();
        tableName = getUnQuotedIdentifier(tableName);
        for (TableMetaData table : tables) {
            List<IndexMetaData> indexMetaData = table.getIndexArrayList();
            for (IndexMetaData index : indexMetaData) {
                if (index.getName().equals(tableName)) {
                    tableName = index.getParent().getName();
                    return tableName;
                }
            }
        }
        return tableName;
    }

    @Override
    protected void getDropViewObjects(Database db, INamespace namespace, RefreshObjectDetails refObject2) {
        refObject2.setParent(((Namespace) namespace).getViewGroup());
    }

    @Override
    protected String getQualifiedTableName(String tableName2) {
        return tableName2.toLowerCase(Locale.ENGLISH);
    }

}
