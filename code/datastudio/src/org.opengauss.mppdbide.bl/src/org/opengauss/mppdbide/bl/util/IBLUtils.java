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

package org.opengauss.mppdbide.bl.util;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IBLUtils.
 * 
 */

public interface IBLUtils {

    /**
     * Sets the platform args.
     *
     * @param arguments the new platform args
     */
    void setPlatformArgs(String[] arguments);

    /**
     * Gets the platform args.
     *
     * @return the platform args
     */
    String[] getPlatformArgs();

    /**
     * Gets the installation location.
     *
     * @return the installation location
     */
    String getInstallationLocation();

}
