/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.bl.serverdatacache.SystemSetting;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecurityOptionProviderForPreferences.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
