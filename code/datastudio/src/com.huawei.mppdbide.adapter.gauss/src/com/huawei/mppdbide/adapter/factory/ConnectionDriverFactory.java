/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.factory;

import java.util.HashMap;

import com.huawei.mppdbide.adapter.IConnectionDriver;

/**
 * Title: ConnectionDriverFactory
 * 
 * Description:A factory for creating ConnectionDriver objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public final class ConnectionDriverFactory {

    private static volatile ConnectionDriverFactory obj;
    private HashMap<String, IConnectionDriver> driverlookup;
    private static final Object LOCK = new Object();

    /**
     * Gets the driver.
     *
     * @param driverName the driver name
     * @return the driver
     */
    public IConnectionDriver getDriver(String driverName) {
        return driverlookup.get(driverName);
    }

    /**
     * Adds the driver.
     *
     * @param driverName the driver name
     * @param driver the driver
     */
    public void addDriver(String driverName, IConnectionDriver driver) {
        driverlookup.put(driverName, driver);
    }

    private ConnectionDriverFactory() {
        this.driverlookup = new HashMap<String, IConnectionDriver>(5);
    }

    /**
     * Gets the single instance of ConnectionDriverFactory.
     *
     * @return single instance of ConnectionDriverFactory
     */
    public static ConnectionDriverFactory getInstance() {
        if (obj == null) {
            synchronized (LOCK) {
                if (obj == null) {
                    obj = new ConnectionDriverFactory();
                }

            }

        }

        return obj;
    }

}
