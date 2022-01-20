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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Properties;

import com.huawei.mppdbide.adapter.AbstractConnectionDriver;
import com.huawei.mppdbide.adapter.IDSSupportToolNames;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsFactoryProvider;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class Gauss200V1R6Driver.
 *
 * @since 3.0.0
 */
public class Gauss200V1R6Driver extends AbstractConnectionDriver {

    private static volatile Gauss200V1R6Driver v1r6Driver;

    /**
     * Instantiates a new gauss 200 V 1 R 6 driver.
     *
     * @param dsInstallPath the ds install path
     */
    public Gauss200V1R6Driver(String dsInstallPath) {
        configureDriverDetails(dsInstallPath);

    }

    @Override
    protected void configureDriverDetails(String dsInstallPath) {
        setDriver(MPPDBIDEConstants.GAUSS200V1R6DRIVER);
        HashMap<String, String> hashMap = new HashMap<String, String>(5);

        setToolPathMap(hashMap);

        org.postgresql.Driver driver = new org.postgresql.Driver();
        setJDBCDriver((Driver) driver);

    }

    @Override
    public Properties getDriverSpecificProperties() {
        Properties prop = new Properties();
        prop.setProperty("protocolVersion", "3");
        return prop;
    }

    /**
     * Gets the single instance of Gauss200V1R6Driver.
     *
     * @param dsInstallPath the ds install path
     * @return single instance of Gauss200V1R6Driver
     */
    public static AbstractConnectionDriver getInstance(String dsInstallPath) {
        if (null == v1r6Driver) {
            v1r6Driver = new Gauss200V1R6Driver(dsInstallPath);
        }
        return v1r6Driver;
    }

    @Override
    public String getProtocolMismatchErrorString() {
        return MessageConfigLoader.getProperty(MPPDBIDEConstants.PROTOCOL_VERSION_ERROR);
    }

    /**
     * Gets the keyword list.
     *
     * @return the keyword list
     */
    public Keywords getKeywordList() {
        return KeywordsFactoryProvider.getKeywordsFactory().getOLAPKeywords();
    }
}
