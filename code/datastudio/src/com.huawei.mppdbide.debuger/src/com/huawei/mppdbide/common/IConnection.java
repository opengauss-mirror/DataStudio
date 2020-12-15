/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
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
