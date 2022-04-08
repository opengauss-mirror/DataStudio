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

package org.opengauss.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

import org.opengauss.mppdbide.bl.serverdatacache.SystemSetting;
import org.opengauss.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecurityOptionProviderForPreferences.
 *
 * @since 3.0.0
 */
public class SecurityOptionProviderForPreferences {

    /**
     * The Constant SAVE_PD_PERMANENTLY.
     */
    public static final String SAVE_PD_PERMANENTLY = "security.password.savepwdpermanently";

    /**
     * The Constant ENABLE_SECURITY_WARNING.
     */
    public static final String ENABLE_SECURITY_WARNING = "security.securitydisclaimer.enablesecuritywarning";

    /**
     * The Constant YES_PD_EXPIRY_PERMANENTLY.
     */
    public static final String YES_PD_EXPIRY_PERMANENTLY = "security.password.expirypwdpermanently";

    /**
     * Sets the default security preferences.
     *
     * @param ps the new default security preferences
     */
    public static void setDefaultSecurityPreferences(PreferenceStore ps) {
        ps.setDefault(SAVE_PD_PERMANENTLY, false);
        ps.setDefault(ENABLE_SECURITY_WARNING, true);
        ps.setDefault(YES_PD_EXPIRY_PERMANENTLY, true);
    }

    /**
     * Update security preference option.
     *
     * @param ps the ps
     */
    public static void updateSecurityPreferenceOption(PreferenceStore ps) {
        boolean savePwdOpt = ps.getBoolean(SAVE_PD_PERMANENTLY);
        boolean enableSecurityWarn = ps.getBoolean(ENABLE_SECURITY_WARNING);
        boolean passwordExpiOption = ps.getBoolean(YES_PD_EXPIRY_PERMANENTLY);
        ((UserPreference) UserPreference.getInstance()).setEnablePermanentPasswordSaveOption(savePwdOpt);
        ((UserPreference) UserPreference.getInstance()).setEnableSecurityWarningOption(enableSecurityWarn);
        ((UserPreference) UserPreference.getInstance()).setLoginAllowedOnPasswordExpiry(passwordExpiOption);

        ((SystemSetting) SystemSetting.getInstance()).setLoginAllowedOnPasswordExpiry(passwordExpiOption);
    }

}
