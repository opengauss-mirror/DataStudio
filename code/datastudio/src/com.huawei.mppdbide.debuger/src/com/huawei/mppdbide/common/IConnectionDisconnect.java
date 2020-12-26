/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.common;

/**
 * Title: IConnectionDisconnect for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-26]
 * @since 2020-12-26
 */
public interface IConnectionDisconnect<T> {
    /**
     * description: release generated connection
     *
     * @param connection contact of free connection
     */
    void releaseConnection(T connection);
}
