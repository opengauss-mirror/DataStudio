/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.util;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IBLUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
