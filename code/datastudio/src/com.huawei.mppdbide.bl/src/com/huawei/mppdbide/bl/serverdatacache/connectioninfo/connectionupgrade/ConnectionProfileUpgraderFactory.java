/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade;

/**
 * Title: ConnectionProfileUpgraderFactory
 * 
 * Description: A factory for creating ConnectionProfileUpgrader objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public final class ConnectionProfileUpgraderFactory {

    private ConnectionProfileUpgraderFactory() {

    }

    /**
     * Gets the connection upgrader.
     *
     * @param version the version
     * @return the connection upgrader
     */
    public static IConnectionProfileUpgrader getConnectionUpgrader(String version) {

        if ("1.00".equals(version)) {
            return new UpgradeFromVersion1ToVersion2();
        }

        return null;
    }
}
