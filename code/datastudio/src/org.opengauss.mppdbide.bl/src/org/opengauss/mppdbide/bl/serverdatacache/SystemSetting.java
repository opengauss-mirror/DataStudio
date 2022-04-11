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

package org.opengauss.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class SystemSetting.
 * 
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
     * org.opengauss.mppdbide.bl.serverdatacache.ISystemSetting#
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
