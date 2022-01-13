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

package com.huawei.mppdbide.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.huawei.mppdbide.debuger.debug.DebugConstants;
import org.postgresql.core.NoticeListener;

/**
 * Title: interface
 * Description: IConnection
 *
 * @since 3.0.0
 */
public interface IConnection {
    /**
     * description: get PreparedStatement instance
     *
     * @param sql the sql query string
     * @return PreparedStatement the instance
     * @throws SQLException then sql exception
     */
    PreparedStatement getStatement(String sql) throws SQLException;

    /**
     * description: get PreparedStatement instance
     *
     * @param debugOpt which debug operation
     * @param params input params
     * @return PreparedStatement the instance
     * @throws SQLException then sql exception
     */
    PreparedStatement getDebugOptPrepareStatement(DebugConstants.DebugOpt debugOpt,
            List<Object> params) throws SQLException;

    /**
     * description: set listener
     *
     * @param listener then listener
     * @return void
     */
    void setNoticeListener(NoticeListener listener);

    /**
     * description: close connection
     *
     * @return void
     * @throws SQLException
     */
    void close() throws SQLException;
}
