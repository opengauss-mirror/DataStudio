/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractConnectionDriver.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractConnectionDriver implements IConnectionDriver {
    private String driverName;
    private HashMap<String, String> toolpathMap;
    private Driver jdbcDriver;
    private volatile SQLSyntax sqlSyntax;
    private final Object lock = new Object();
    private Keywords keywords;

    /**
     * Instantiates a new abstract connection driver.
     *
     * @param dsInstallPath the ds install path
     * @param driverType the driver type
     */
    public AbstractConnectionDriver() {
    }

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    @Override
    public String getDriverName() {
        return this.driverName;
    }

    /**
     * Gets the tool path.
     *
     * @param toolName the tool name
     * @return the tool path
     */
    @Override
    public String getToolPath(String toolName) {
        return this.toolpathMap.get(toolName);
    }

    /**
     * Gets the JDBC driver.
     *
     * @return the JDBC driver
     */
    @Override
    public Driver getJDBCDriver() {
        return this.jdbcDriver;
    }

    /**
     * Gets the driver specific properties.
     *
     * @return the driver specific properties
     */
    @Override
    public Properties getDriverSpecificProperties() {
        return new Properties();
    }


    /**
     * Sets the driver.
     *
     * @param dname the new driver
     */
    protected void setDriver(String dname) {
        this.driverName = dname;
    }

    /**
     * Sets the tool path map.
     *
     * @param map the map
     */
    protected void setToolPathMap(HashMap<String, String> map) {
        this.toolpathMap = map;
    }

    /**
     * Sets the JDBC driver.
     *
     * @param driver the new JDBC driver
     */
    public void setJDBCDriver(Driver driver) {
        this.jdbcDriver = driver;
    }

    /**
     * Configure driver details.
     *
     * @param dsInstallPath the ds install path
     * @param driverType the driver type
     */
    protected abstract void configureDriverDetails(String dsInstallPath);

    /**
     * Gets the protocol mismatch error string.
     *
     * @return the protocol mismatch error string
     */
    public abstract String getProtocolMismatchErrorString();

    /**
     * Extract error code and error msg from server error.
     *
     * @param exe the e
     * @return the string
     */
    public String extractErrCodeAdErrMsgFrmServErr(SQLException exe) {
        StringBuilder errorBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        errorBuilder.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDIT_TABLE_COMMIT_FAIL))
                .append(" = ");
        errorBuilder.append(exe.getSQLState());
        errorBuilder.append(MPPDBIDEConstants.NEW_LINE_SIGN);
        errorBuilder.append(exe.getLocalizedMessage());
        errorBuilder.append(MPPDBIDEConstants.NEW_LINE_SIGN);
        return errorBuilder.toString();
    }

    /**
     * Load SQL syntax.
     *
     * @return SQLSyntax sqlSyntax
     * 
     * while Overriding the following method in subclass, method should be made
     * thread-safe similar to super class implementation since sql syntax needs
     * to be loaded sequentially to not cause mismatch of values between objects
     */
    public SQLSyntax loadSQLSyntax() {
        keywords = loadKeyWords();
        if (sqlSyntax == null) {
            synchronized (lock) {
                if (sqlSyntax == null) {
                    MPPDBIDELoggerUtility.debug("sql syntax convert trie starts");
                    sqlSyntax = new SQLSyntax();
                    sqlSyntax = KeywordsToTrieConverter.convertKeywordstoTrie(sqlSyntax, keywords);
                    MPPDBIDELoggerUtility.debug("sql syntax convert trie ends");
                }
            }
        }
        return sqlSyntax;
    }

    /**
     * Load key words.
     *
     * @return the keywords
     */
    public Keywords loadKeyWords() {
        return getKeywordList();
    }

}
