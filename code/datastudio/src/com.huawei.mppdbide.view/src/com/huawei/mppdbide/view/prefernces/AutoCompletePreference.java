/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * 
 * Title: class AutoCompletePreference
 * 
 * Description: The Class AutoCompletePreference.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author swx316469
 * @version [DataStudio 8.0.0, 28 Aug, 2019]
 * @since 28 Aug, 2019
 */
public class AutoCompletePreference implements IAutoCompletePreference {

    /**
     * Sets the default for auto complete preferences.
     *
     * @param ps the new default for auto complete preferences
     */
    public static void setDefaultForAutoCompletePreferences(PreferenceStore ps) {
        ps.setDefault(AUTO_COMPLETE_PREFERENCE_KEY, AUTO_COMPLETE_DEFAULT_WORD_SIZE);

    }

}
