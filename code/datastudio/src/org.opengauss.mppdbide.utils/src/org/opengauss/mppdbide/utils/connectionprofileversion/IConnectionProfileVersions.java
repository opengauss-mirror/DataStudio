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

package org.opengauss.mppdbide.utils.connectionprofileversion;

/**
 * Title: IConnectionProfileVersions
 * 
 * Description: The Interface IConnectionProfileVersions
 * 
 * @since 3.0.0
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
