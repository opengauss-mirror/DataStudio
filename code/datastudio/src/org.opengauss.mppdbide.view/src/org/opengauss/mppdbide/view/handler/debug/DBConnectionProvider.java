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

package org.opengauss.mppdbide.view.handler.debug;

import java.util.Optional;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.common.DBConnectionAdapter;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionDisconnect;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * Title: class
 * Description: The ClassD BConnectionProvider
 *
 * @since 3.0.0
 */
public class DBConnectionProvider implements IConnectionProvider,
                                IConnectionDisconnect<DBConnection> {
    private Database db;

    public DBConnectionProvider(Database db) {
        this.db = db;
    }

    /**
     * desrciption: get free connection from database
     *
     * @return Optional<IConnection> the connection
     */
    @Override
    public Optional<IConnection> getFreeConnection() {
        try {
            return Optional.of(
                    new DBConnectionAdapter(
                            db.getConnectionManager().getFreeConnection(),
                            this
                        )
                    );
        } catch (MPPDBIDEException e) {
            return Optional.empty();
        }
    }

    @Override
    public void releaseConnection(DBConnection connection) {
        db.getConnectionManager().releaseAndDisconnection(connection);
    }
}
