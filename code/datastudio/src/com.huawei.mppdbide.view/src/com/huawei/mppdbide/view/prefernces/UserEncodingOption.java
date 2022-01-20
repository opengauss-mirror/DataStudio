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

import com.huawei.mppdbide.utils.DsEncodingEnum;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserEncodingOption.
 *
 * @since 3.0.0
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
