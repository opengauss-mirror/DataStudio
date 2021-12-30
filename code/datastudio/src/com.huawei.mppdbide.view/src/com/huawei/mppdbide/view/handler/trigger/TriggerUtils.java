/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.trigger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.trigger.CreateTriggerParam;

/**
 * Title: class
 * Description: The Class TriggerUtils.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 30,04,2021]
 * @since 30,04,2021
 */
public class TriggerUtils {
    /**
     * description: get select trigger from ObjectBrowser
     *
     * @return TriggerMetaData the trigger obj or null
     */
    public static TriggerMetaData getTrigger() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj != null && obj instanceof TriggerMetaData) {
            return (TriggerMetaData) obj;
        }
        return null;
    }

    /**
     * Get function names
     *
     * @param Database the database
     * @return List<String> the function name list
     */
    public static List<String> getFunctionNames(Database db) {
        String sql = "select DISTINCT p.oid, p.proname from pg_proc p, pg_type t "
                + "where p.prorettype = t.oid and t.typname='trigger'";
        List<String> functionNameList = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(sql);
            while (rs.next()) {
                functionNameList.add(rs.getString(2));
            }
        } catch (SQLException | DatabaseCriticalException | DatabaseOperationException ex) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    ex);
        } finally {
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
        return functionNameList;
    }

    /**
     * Get table names
     *
     * @param TableObjectGroup the table group
     * @return List<String> the table name list
     */
    public static List<String> getTableNames(TableObjectGroup tableGroup) {
        ArrayList<TableMetaData> tableList = tableGroup.getSortedServerObjectList();
        ArrayList<String> tableNameList = new ArrayList<String>();
        for (TableMetaData tableData : tableList) {
            tableNameList.add(tableData.getName());
        }
        return tableNameList;
    }
    
    /**
     * Get view names
     *
     * @param ViewObjectGroup the view group
     * @return List<String> the view name list
     */
    public static List<String> getViewNames(ViewObjectGroup  viewGroup) {        
        ArrayList<ViewMetaData> viewList = viewGroup.getSortedServerObjectList();
        ArrayList<String> viewNameList = new ArrayList<String>();
        for (ViewMetaData viewData : viewList) {
            viewNameList.add(viewData.getName());
        }
        return viewNameList;
    }

    /**
     * Get table columns
     *
     * @param TableObjectGroup the table group
     * @param String the table name
     * @return List<CreateTriggerParam> the trigger param list
     */
    public static List<CreateTriggerParam> getTableColumns(TableObjectGroup tableGroup, String tableName) {
        ArrayList<TableMetaData> tableList  = tableGroup.getSortedServerObjectList();
        List<ColumnMetaData> columnList = new ArrayList<>();
        for (TableMetaData tableData : tableList) {
            if (tableName.equals(tableData.getName())) {
                columnList = tableData.getColumnMetaDataList();
                break;
            }
        }
        List<CreateTriggerParam> paramList = new ArrayList<>();
        for (ColumnMetaData columnData : columnList) {
            paramList.add(new CreateTriggerParam(columnData.getName(), columnData.getDataType().getName()));
        }
        return paramList;
    }
}
