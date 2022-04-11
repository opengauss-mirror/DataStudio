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

package org.opengauss.mppdbide.adapter;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.keywordssyntax.Keywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IConnectionDriver.
 * 
 * @since 3.0.0
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
