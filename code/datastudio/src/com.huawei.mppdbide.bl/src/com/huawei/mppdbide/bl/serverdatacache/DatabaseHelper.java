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

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseHelper.
 * 
 */

public class DatabaseHelper {

    private static final String SELECT_PRIVILEGE_QUERY = "select has_database_privilege(%s, 'CONNECT');";

    /**
     * Fetch tablespace name.
     *
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void fetchTablespaceName(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        String query;
        query = "SELECT tbs.spcname from pg_tablespace tbs, pg_database db "
                + "where tbs.oid = db.dattablespace and db.datname = " + ServerObject.getLiteralName(db.getName());

        String tblSpc = db.getConnectionManager().execSelectAndGetFirstValOnObjBrowserConn(query);
        db.setDBDefaultTblSpc(tblSpc);
    }

    /**
     * Fetch DB oid.
     *
     * @param db the db
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static long fetchDBOid(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        if (0 != db.getOid()) {
            return db.getOid();
        }

        String query;
        query = "SELECT oid from pg_database where datname = " + ServerObject.getLiteralName(db.getName());

        String oidStr = db.getConnectionManager().execSelectAndGetFirstValOnObjBrowserConn(query);
        try {
            db.setOid(Integer.parseInt(oidStr));
        } catch (NumberFormatException numberFormatException) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED),
                    numberFormatException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED);
        }

        return db.getOid();
    }

    /**
     * Creates the new schema.
     *
     * @param schemaName the schema name
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void createNewSchema(String schemaName, Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = String.format(Locale.ENGLISH, "CREATE SCHEMA %s;",
                ServerObject.getQualifiedObjectName(schemaName));
        db.getConnectionManager().execNonSelectOnObjBrowserConn(qry);
        NamespaceUtils.fetchAllUserNamespaces(db);
    }

    /**
     * Can be connected.
     *
     * @param db the db
     * @return true, if successful
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static boolean canBeConnected(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        final String qry = String.format(Locale.ENGLISH, SELECT_PRIVILEGE_QUERY,
                ServerObject.getLiteralName(db.getDbName()));
        ResultSet rs = null;
        rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
        boolean flag = false;
        try {
            boolean hasNext = rs.next();
            if (hasNext) {
                flag = rs.getBoolean(1);
            }
        } catch (SQLException exp) {
            try {
                GaussUtils.handleCriticalException(exp);
            } catch (DatabaseCriticalException dc) {
                throw dc;
            }
            throw new DatabaseOperationException(IMessagesConstants.ERR_FETCH_DATABASE_OPERATION, exp);
        } finally {
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
        return flag;
    }

}
