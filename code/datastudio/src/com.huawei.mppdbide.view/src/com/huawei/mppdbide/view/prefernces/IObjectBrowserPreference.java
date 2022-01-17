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

/** 
 * Title: IObjectBrowserPreference
 *
 * @since 3.0.0
 */
public interface IObjectBrowserPreference {
    /**
     * The ob filter timeout preference key.
     */
    String OB_FILTER_TIMEOUT_PREFERENCE_KEY = "com.huawei.mppdbide.general.objectbrowser.preferences.filtertimeout";

    /**
     * default ob filter timeout.
     */
    int OB_FILTER_TIMEOUT_DEFAULT = 2;

    /**
     * max ob filter timeout.
     */
    int OB_FILTER_TIMEOUT_MAX = 10;
    
    /**
     * min ob filter timeout.
     */
    int OB_FILTER_TIMEOUT_MIN = 1;
}
