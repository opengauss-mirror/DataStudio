/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.factory;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;

/**
 * Title: DBConnectionFactory
 * 
 * Description: A factory for creating DBConnection objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class DBConnectionFactory {

    /**
     * Gets the connection obj.
     *
     * @param connectionDriver the connection driver
     * @return the connection obj
     */
    public static DBConnection getConnectionObj(IConnectionDriver connectionDriver) {
        return new DBConnection(connectionDriver);
    }

}
