/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;

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
 * Description: The Class AccessMethod.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class AccessMethod extends ServerObject implements GaussOLAPDBMSObject {

    /**
     * Instantiates a new access method.
     *
     * @param oid the oid
     * @param name the name
     */
    public AccessMethod(long oid, String name) {
        super(oid, name, OBJECTTYPE.ACCESSMETHOD, false);
    }

    /**
     * Convert to access method.
     *
     * @param rs the rs
     * @param server the server
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void convertToAccessMethod(ResultSet rs, Server server)
            throws DatabaseCriticalException, DatabaseOperationException {
        try {
            long oid = rs.getLong("oid");
            String name = rs.getString("amname");
            AccessMethod am = new AccessMethod(oid, name);
            server.addToAccessMethods(am);
        } catch (SQLException eexp) {
            GaussUtils.handleCriticalException(eexp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, eexp);
        }

    }

    /**
     * Fetch all access methods.
     *
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void fetchAllAccessMethods(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        int counter = 0;

        String qry = "SELECT oid, amname from pg_am order by oid;";
        ResultSet rs = null;
        boolean hasNextRecord = false;

        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
            hasNextRecord = rs.next();
            while (hasNextRecord) {
                AccessMethod.convertToAccessMethod(rs, db.getServer());
                counter++;
                hasNextRecord = rs.next();
            }
        } catch (SQLException sqle) {
            try {
                GaussUtils.handleCriticalException(sqle);
            } catch (DatabaseCriticalException dc) {
                throw dc;
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    sqle);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqle);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility
                        .trace("Total Number of access methods loaded for selected server is : " + counter);
            }

            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

}
