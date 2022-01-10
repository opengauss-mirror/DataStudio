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
package com.huawei.mppdbide.adapter.factory;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;

/**
 * Title: DBConnectionFactory
 * 
 * Description: A factory for creating DBConnection objects.
 * 
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
