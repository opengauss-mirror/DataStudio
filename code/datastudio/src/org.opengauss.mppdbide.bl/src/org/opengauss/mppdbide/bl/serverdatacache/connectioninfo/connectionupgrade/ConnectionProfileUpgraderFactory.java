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

package org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.connectionupgrade;

/**
 * Title: ConnectionProfileUpgraderFactory
 * 
 * Description: A factory for creating ConnectionProfileUpgrader objects.
 * 
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
