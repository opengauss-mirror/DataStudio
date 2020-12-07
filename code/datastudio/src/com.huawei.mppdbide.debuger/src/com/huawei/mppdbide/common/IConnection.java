/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.huawei.mppdbide.debuger.debug.DebugConstants;
import org.postgresql.core.NoticeListener;

/**
 * 
 * Title: interface
 * 
 * Description: IConnection
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public interface IConnection {
    PreparedStatement getStatement(String sql) throws SQLException;
    PreparedStatement getDebugOptPrepareStatement(DebugConstants.DebugOpt debugOpt, Object[] params) throws SQLException;
    void setNoticeListener(NoticeListener listener);
    void close() throws SQLException;
}
