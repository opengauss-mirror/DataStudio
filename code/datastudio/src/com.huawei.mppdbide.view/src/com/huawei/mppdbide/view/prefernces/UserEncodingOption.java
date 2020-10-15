/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.utils.DsEncodingEnum;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserEncodingOption.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UserEncodingOption {

    /**
     * The Constant DATA_STUDIO_ENCODING.
     */
    public static final String DATA_STUDIO_ENCODING = "environment.sessionsetting.datastudioencoding";

    /**
     * The Constant FILE_ENCODING.
     */
    public static final String FILE_ENCODING = "environment.sessionsetting.fileencoding";

    /**
     * Sets the default security preferences.
     *
     * @param ps the new default security preferences
     */
    public static void setDefaultSecurityPreferences(PreferenceStore ps) {
        ps.setDefault(DATA_STUDIO_ENCODING, DsEncodingEnum.UTF_8.getEncoding());
        ps.setDefault(FILE_ENCODING, "UTF-8");

    }

    /**
     * Update preference.
     *
     * @param ps the ps
     */
    public static void updatePreference(PreferenceStore ps) {
        if (!ps.getString(DATA_STUDIO_ENCODING).equals(DsEncodingEnum.GBK.getEncoding())) {
            ps.setValue(DATA_STUDIO_ENCODING, DsEncodingEnum.UTF_8.getEncoding());
        }
    }

}
