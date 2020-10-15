/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemSetting.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class SystemSetting implements ISystemSetting {

    private boolean loginAllowedOnPasswordExpiry;
    private static volatile ISystemSetting instance = null;
    private static final Object LOCK = new Object();

    /**
     * Gets the single instance of SystemSetting.
     *
     * @return single instance of SystemSetting
     */
    public static ISystemSetting getInstance() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new SystemSetting();
                }
            }
        }
        return instance;
    }

    /**
     * Checks if is login allowed on password expiry.
     *
     * @return true, if is login allowed on password expiry
     * com.huawei.mppdbide.bl.serverdatacache.ISystemSetting#
     * isLogginaAllowedOnPasswordExpiry()
     */
    @Override
    public boolean isLoginAllowedOnPasswordExpiry() {

        return loginAllowedOnPasswordExpiry;
    }

    /**
     * Sets the login allowed on password expiry.
     *
     * @param isAllowedOnPasswordExpiry the new login allowed on password expiry
     */
    public void setLoginAllowedOnPasswordExpiry(boolean isAllowedOnPasswordExpiry) {
        this.loginAllowedOnPasswordExpiry = isAllowedOnPasswordExpiry;
    }

}
