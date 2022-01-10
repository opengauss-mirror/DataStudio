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


package com.huawei.mppdbide.adapter.driver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.huawei.mppdbide.adapter.AbstractConnectionDriver;
import com.huawei.mppdbide.adapter.factory.ConnectionDriverFactory;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussDriverWrapper;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBMSDriverManager.
 * 
 */
public final class DBMSDriverManager {

    private static volatile DBMSDriverManager driverManager;
    private static volatile ArrayList<AbstractConnectionDriver> knownOLAPDrivers;
    private String dsInstallPath;
    private static final Object LOCK = new Object();

    private DBMSDriverManager(String dsInstallPath) {
        this.dsInstallPath = dsInstallPath;

    }

    /**
     * Gets the connection.
     *
     * @param props the props
     * @param url the url
     * @param driverType the driver type
     * @return the connection
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public DBConnection getConnection(Properties props, String url, String driverType)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        switch (driverType) {
            case MPPDBIDEConstants.OPENGAUSS: {
                getOLAPDriverInstance(this.dsInstallPath);
                for (AbstractConnectionDriver driver : knownOLAPDrivers) {

                    DBConnection conn = establishOLAPConnection(driver, props, url);
                    if (null != conn) {
                        GaussDriverWrapper driverWrapper = setDriverDetails(driver);
                        conn.setDriver(driverWrapper);
                        return conn;
                    }
                }
                break;
            }

            default: {
                break;
            }
        }
        return null;
    }

    private GaussDriverWrapper setDriverDetails(AbstractConnectionDriver driver) {
        GaussDriverWrapper driverWrapper = new GaussDriverWrapper(driver);
        ConnectionDriverFactory.getInstance().addDriver(driverWrapper.getDriverName(), driverWrapper);
        return driverWrapper;
    }

    private static DBConnection establishOLAPConnection(AbstractConnectionDriver driver, Properties props, String url)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        DBConnection conn = new DBConnection(driver);
        if (GaussUtils.isProtocolVersionNeeded(driver)) {
            props.setProperty("protocolVersion", "3");
        }

        try {
            conn.connectViaDriver(props, url);

        } catch (DatabaseOperationException exp) {
            // if error deals with protocol error, then return null
            if (exp.getMessage().contains(driver.getProtocolMismatchErrorString()) || exp.getServerMessage() != null
                    && (exp.getServerMessage().contains(driver.getProtocolMismatchErrorString())
                            || exp.getServerMessage().contains("Protocol error."))) {
                conn = null;
            } else {
                throw exp;
            }
        }
        return conn;
    }

    /**
     * Gets the single instance of DBMSDriverManager.
     *
     * @param dsInstallPath the ds install path
     * @return single instance of DBMSDriverManager
     */
    public static DBMSDriverManager getInstance(String dsInstallPath) {
        if (driverManager == null) {
            synchronized (LOCK) {
                if (driverManager == null) {
                    driverManager = new DBMSDriverManager(dsInstallPath);
                }

            }

        }

        return driverManager;
    }

    /**
     * Gets the OLAP driver instance.
     *
     * @param dsInstallPath the ds install path
     * @return the OLAP driver instance
     */
    public static ArrayList<AbstractConnectionDriver> getOLAPDriverInstance(String dsInstallPath) {
        if (knownOLAPDrivers == null) {
            synchronized (LOCK) {
                if (knownOLAPDrivers == null) {
                    knownOLAPDrivers = (ArrayList<AbstractConnectionDriver>) loadOLAPDrivers(dsInstallPath);
                }
            }
        }
        return knownOLAPDrivers;

    }

    private static List<AbstractConnectionDriver> loadOLAPDrivers(String dsInstallPath) {
        knownOLAPDrivers = new ArrayList<AbstractConnectionDriver>(2);
        knownOLAPDrivers.add(Gauss200V1R7Driver.getInstance(dsInstallPath));
        knownOLAPDrivers.add(Gauss200V1R6Driver.getInstance(dsInstallPath));
        return knownOLAPDrivers;
    }

}
