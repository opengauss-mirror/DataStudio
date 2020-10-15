/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.preferences;

/**
 * 
 * Title: class
 * 
 * Description: The Class BLPreferenceManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
