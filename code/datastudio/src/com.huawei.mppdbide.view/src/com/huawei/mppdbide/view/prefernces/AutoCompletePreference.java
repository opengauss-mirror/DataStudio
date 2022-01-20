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

/**
 * 
 * Title: class AutoCompletePreference
 * 
 * Description: The Class AutoCompletePreference.
 *
 * @since 3.0.0
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
