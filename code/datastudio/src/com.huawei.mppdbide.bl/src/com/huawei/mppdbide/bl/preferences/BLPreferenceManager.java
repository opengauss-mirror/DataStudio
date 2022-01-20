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

package com.huawei.mppdbide.bl.preferences;

/**
 * 
 * Title: class
 * 
 * Description: The Class BLPreferenceManager.
 * 
 */

public class BLPreferenceManager {

    private static volatile BLPreferenceManager instance = null;
    private static final Object LOCK = new Object();
    private IBLPreference systemPrefs = null;

    private BLPreferenceManager() {
    }

    /**
     * Gets the single instance of BLPreferenceManager.
     *
     * @return single instance of BLPreferenceManager
     */
    public static BLPreferenceManager getInstance() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new BLPreferenceManager();
                }
            }
        }
        return instance;

    }

    /**
     * Gets the BL preference.
     *
     * @return the BL preference
     */
    public IBLPreference getBLPreference() {
        return systemPrefs;
    }

    /**
     * Sets the BL preference.
     *
     * @param prefs the new BL preference
     */
    public void setBLPreference(IBLPreference prefs) {
        systemPrefs = prefs;
    }

}
