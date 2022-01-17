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

import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableOptionProviderForPreferences.
 *
 * @since 3.0.0
 */
public class EditTableOptionProviderForPreferences {

    /**
     * The Constant EDITTABLE_COMMIT_ON_FAILURE.
     */
    public static final String EDITTABLE_COMMIT_ON_FAILURE = "resultmanagement.edittable.commitvalid";

    /**
     * Sets the default edit table preferences.
     *
     * @param ps the new default edit table preferences
     */
    public static void setDefaultEditTablePreferences(PreferenceStore ps) {
        ps.setDefault(EDITTABLE_COMMIT_ON_FAILURE, true);
    }

    /**
     * Update edit table preference option.
     *
     * @param ps the ps
     */
    public static void updateEditTablePreferenceOption(PreferenceStore ps) {
        boolean commitValidOption = ps.getBoolean(EDITTABLE_COMMIT_ON_FAILURE);

        ((UserPreference) UserPreference.getInstance()).setCommitValidRows(commitValidOption);

    }

}
