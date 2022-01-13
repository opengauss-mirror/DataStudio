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
 * @since 3.0.0
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
