/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

/** 
 * Title: IObjectBrowserPreference
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 27-May-2020]
 * @since 27-May-2020
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
