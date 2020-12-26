/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.common;

import java.util.Optional;

/**
 * Title: IConnectionProvider
 * Description: IConnectionProvider to provider connection of database
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public interface IConnectionProvider {
    /**
     * desrciption: get free connection from database
     *
     * @return Optional<IConnection> the connection
     */
    Optional<IConnection> getFreeConnection();
}
