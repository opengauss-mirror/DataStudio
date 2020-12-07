/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;

import java.util.Optional;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.common.DBConnectionAdapter;
import com.huawei.mppdbide.common.IConnection;
import com.huawei.mppdbide.common.IConnectionProvider;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class DBConnectionProvider implements IConnectionProvider {
    private Database db;
    public DBConnectionProvider(Database db) {
        this.db = db;
    }

    /**
     * get free connection from database
     * */
    @Override
    public Optional<IConnection> getFreeConnection() {
        try {
            return Optional.of(
                    new DBConnectionAdapter(
                            db.getConnectionManager().getFreeConnection()
                        )
                    );
        } catch (MPPDBIDEException e) {
            return Optional.empty();
        }
    }
}
