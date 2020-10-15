/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.connectionprofileversion;

/**
 * Title: IConnectionProfileVersions
 * 
 * Description: The Interface IConnectionProfileVersions
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-May-2019]
 * @since 21-May-2019
 */

public interface IConnectionProfileVersions {

    /**
     * The connection profile first version.
     */
    String CONNECTION_PROFILE_FIRST_VERSION = "1.00";

    /**
     * The connection profile second version. Restructuring of the json
     * structure of all the profiles that was used in version 1.00.Each tab
     * information is now under the tab root element
     */
    String CONNECTION_PROFILE_SECOND_VERSION = "2.00";

    /**
     * The connection profile current version.
     */
    String CONNECTION_PROFILE_CURRENT_VERSION = CONNECTION_PROFILE_SECOND_VERSION;
}
