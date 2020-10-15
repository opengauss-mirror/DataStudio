/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.contentassist.ContentAssistKeywords;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ConnectionUtils {

    /**
     * Checks if is aleast one db connected.
     *
     * @param dbGroup the db group
     * @return true, if is aleast one db connected
     */
    public static boolean isAleastOneDbConnected(ArrayList<Database> dbGroup) {
        Iterator<Database> dbItr = dbGroup.iterator();
        Database db = null;
        boolean hasNext = dbItr.hasNext();

        while (hasNext) {
            db = dbItr.next();
            if (db.isConnected()) {
                return true;
            }
            hasNext = dbItr.hasNext();
        }

        return false;
    }

    /**
     * Gets the another connection.
     *
     * @param oid the oid
     * @param arrayList the array list
     * @return the another connection
     * @throws DatabaseOperationException the database operation exception
     */
    public static DBConnection getAnotherConnection(long oid, ArrayList<Database> arrayList)
            throws DatabaseOperationException {
        Iterator<Database> dbItr = arrayList.iterator();
        boolean hasNext = dbItr.hasNext();

        Database db = null;
        while (hasNext) {
            db = dbItr.next();
            if (db.isConnected() && db.getOid() != oid) {
                return db.getConnectionManager().getObjBrowserConn();
            }

            hasNext = dbItr.hasNext();
        }
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_MSG_NO_OTHER_CONNECTION));
        throw new DatabaseOperationException(IMessagesConstants.ERR_MSG_NO_OTHER_CONNECTION);

    }

}
