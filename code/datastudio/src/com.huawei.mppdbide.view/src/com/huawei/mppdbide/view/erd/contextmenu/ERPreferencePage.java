/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.contextmenu;

import org.eclipse.jface.preference.PreferenceStore;

/**
 * Title: ERPreferencePage
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
 */

public class ERPreferencePage {

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {

        preferenceStore.setDefault(ERPreferencekeys.PREF_ATTR_STYLES, ERViewStyle.ICONS.name());

        preferenceStore.setDefault(ERPreferencekeys.PREF_ATTR_VISIBILITY, ERAttributeVisibility.ALL.name());
    }
}
