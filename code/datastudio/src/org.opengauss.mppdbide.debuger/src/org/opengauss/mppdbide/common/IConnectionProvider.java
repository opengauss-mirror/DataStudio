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

package org.opengauss.mppdbide.common;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Title: IConnectionProvider
 * Description: IConnectionProvider to provider connection of database
 *
 * @since 3.0.0
 */
public interface IConnectionProvider {
    /**
     * desrciption: get free connection from database
     *
     * @return Optional<IConnection> the connection
     */
    Optional<IConnection> getFreeConnection();

    /**
     * desrciption: get free valid connection from database
     *
     * @return IConnection the connection
     * @throws SQLException the null connection exception
     */
    default IConnection getValidFreeConnection() throws SQLException {
        Optional<IConnection> conn = getFreeConnection();
        if (conn.isPresent()) {
            return conn.get();
        }
        throw new SQLException("get free connection failed!");
    }
}
