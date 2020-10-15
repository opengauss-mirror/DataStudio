/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.utils.Preferencekeys;

/**
 * 
 * Title: DsDataLimitPreferencePage
 * 
 * Description: The Class DsDataLimitPreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DsDataLimitPreferencePage {

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {

        preferenceStore.setDefault(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA, 1536);
        preferenceStore.setDefault(Preferencekeys.FILE_LIMIT_FOR_SQL, 100);

        preferenceStore.setDefault(Preferencekeys.OBECT_COUNT_FOR_LAZY_RENDERING,
                MPPDBIDEConstants.DEFAULT_TREE_NODE_COUNT);
    }

}
