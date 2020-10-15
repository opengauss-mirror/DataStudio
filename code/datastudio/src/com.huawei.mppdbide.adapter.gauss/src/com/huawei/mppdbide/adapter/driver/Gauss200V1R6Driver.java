/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class Gauss200V1R6Driver extends AbstractConnectionDriver {

    private static final String GSDUMP_WINDOWS_RELATIVE_PATH = "tools\\win\\dbms1\\gs_dump.exe";
    private static final String GS_ZENITH_DUMP_RELATIVE_PATH = "tools\\win\\dbms3\\gs_dump.exe";
    private static volatile Gauss200V1R6Driver v1r6Driver;

    /**
     * Instantiates a new gauss 200 V 1 R 6 driver.
     *
     * @param dsInstallPath the ds install path
     */
    public Gauss200V1R6Driver(String dsInstallPath) {
        configureDriverDetails(dsInstallPath);

    }

    private String getToolAbsolutePath(String installPath, String toolRelativePath) {
        Path dumpLoc = Paths.get(installPath, toolRelativePath);
        String toolpath = null;
        if (dumpLoc.toAbsolutePath() != null && dumpLoc.toAbsolutePath().normalize() != null) {
            toolpath = dumpLoc.toAbsolutePath().normalize().toString();
        }
        return toolpath;
    }

    @Override
    protected void configureDriverDetails(String dsInstallPath) {
        setDriver(MPPDBIDEConstants.GAUSS200V1R6DRIVER);
        HashMap<String, String> hashMap = new HashMap<String, String>(5);

        hashMap.put(IDSSupportToolNames.GS_DUMP, getToolAbsolutePath(dsInstallPath, GSDUMP_WINDOWS_RELATIVE_PATH));

        hashMap.put(IDSSupportToolNames.GS_ZENITH_DUMP,
                getToolAbsolutePath(dsInstallPath, GS_ZENITH_DUMP_RELATIVE_PATH));
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
