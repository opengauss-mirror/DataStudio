/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IConnectionDriver.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IConnectionDriver {

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    String getDriverName();

    /**
     * Gets the tool path.
     *
     * @param toolName the tool name
     * @return the tool path
     */
    String getToolPath(String toolName);

    /**
     * Gets the JDBC driver.
     *
     * @return the JDBC driver
     */
    Driver getJDBCDriver();

    /**
     * Gets the driver specific properties.
     *
     * @return the driver specific properties
     */
    Properties getDriverSpecificProperties();

    /**
     * Extract error code and error msg from server error.
     *
     * @param excep the excep
     * @return the string
     */
    String extractErrCodeAdErrMsgFrmServErr(SQLException excep);

    /**
     * Gets the keyword list.
     *
     * @return the keyword list
     */
    Keywords getKeywordList();

    /**
     * Gets the SQL syntax.
     *
     * @return the SQL syntax
     */
    SQLSyntax loadSQLSyntax();

    /**
     * check if server DDL support check is already called before. If yes, no
     * need to check further.
     * 
     * @return the show DDL support check
     */
    default boolean getShowDDLSupportCheck() {
        return false;
    }

    /**
     * Gets the show DDL support.
     *
     * @return the show DDL support
     */
    default boolean getShowDDLSupport() {
        return false;
    }

    /**
     * Gets the show DDL support.
     *
     * @param conn the conn
     * @return the show DDL support
     */
    default boolean getShowDDLSupport(DBConnection conn) {
        return false;
    }
}
