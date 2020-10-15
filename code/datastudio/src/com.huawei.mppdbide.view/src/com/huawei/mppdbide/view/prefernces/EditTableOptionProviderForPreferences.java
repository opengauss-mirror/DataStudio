/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
