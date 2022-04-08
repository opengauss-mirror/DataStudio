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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistKeywords;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionUtils.
 * 
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
