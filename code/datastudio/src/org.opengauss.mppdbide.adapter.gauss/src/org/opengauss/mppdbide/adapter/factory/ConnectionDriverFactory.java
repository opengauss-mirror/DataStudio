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

package org.opengauss.mppdbide.adapter.factory;

import java.util.HashMap;

import org.opengauss.mppdbide.adapter.IConnectionDriver;

/**
 * Title: ConnectionDriverFactory
 * 
 * Description:A factory for creating ConnectionDriver objects.
 * 
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
