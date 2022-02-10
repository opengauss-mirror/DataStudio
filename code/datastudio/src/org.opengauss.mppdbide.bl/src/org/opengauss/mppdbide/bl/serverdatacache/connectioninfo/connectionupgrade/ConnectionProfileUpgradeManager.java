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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionProfileUpgradeManager.
 * 
 */

public final class ConnectionProfileUpgradeManager {

    private static final Object lock = new Object();
    private static volatile ConnectionProfileUpgradeManager upgrader;
    /*
     * The versions array should be always kept in the order of the versions
     * introduced
     */
    private final List<String> versionList;

    private ConnectionProfileUpgradeManager() {
        versionList = new ArrayList<String>();
        versionList.add(IConnectionProfileVersions.CONNECTION_PROFILE_FIRST_VERSION);
        versionList.add(IConnectionProfileVersions.CONNECTION_PROFILE_SECOND_VERSION);
    }

    /**
     * Checks if is version available.
     *
     * @param versionNumber the version number
     * @return true, if is version available
     */
    public boolean isVersionAvailable(String versionNumber) {
        return versionList.contains(versionNumber);
    }

    /**
     * Gets the version index.
     *
     * @param version the version
     * @return the version index
     */
    public int getVersionIndex(String version) {
        if (versionList.contains(version)) {
            return versionList.indexOf(version);
        }
        return -1;
    }

    /**
     * Gets the single instance of ConnectionProfileUpgradeManager.
     *
     * @return single instance of ConnectionProfileUpgradeManager
     */
    public static ConnectionProfileUpgradeManager getInstance() {
        if (upgrader == null) {
            synchronized (lock) {

                if (upgrader == null) {
                    upgrader = new ConnectionProfileUpgradeManager();
                }
            }
        }
        return upgrader;
    }

    /**
     * Gets the upgraded connection profiles.
     *
     * @param jsonString the json string
     * @param type the type
     * @param sourceProfileVersion the source profile version
     * @return the upgraded connection profiles
     */
    public List<IServerConnectionInfo> getUpgradedConnectionProfiles(String jsonString, Type type,
            String sourceProfileVersion) {
        int sourceVersionIndex = versionList.indexOf(sourceProfileVersion);
        int currentDsVersionIndex = versionList.indexOf(IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION);

        String upgradeString = jsonString;
        
        IConnectionProfileUpgrader connectionUpgrader;
        for (int versionIndex = sourceVersionIndex; versionIndex < currentDsVersionIndex; versionIndex++) {
            connectionUpgrader = ConnectionProfileUpgraderFactory.getConnectionUpgrader(versionList.get(versionIndex));
            if (upgradeString != null && connectionUpgrader != null) {
                upgradeString = connectionUpgrader.upgrade(upgradeString);
            }

        }
        Gson gson = new Gson();
        IServerConnectionInfo fromJson = gson.fromJson(upgradeString, ServerConnectionInfo.class);
        List<IServerConnectionInfo> infoList = new ArrayList<>();
        infoList.add(fromJson);
        return infoList;

    }

}
