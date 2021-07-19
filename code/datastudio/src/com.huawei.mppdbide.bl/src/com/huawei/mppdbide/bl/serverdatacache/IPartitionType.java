/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
*
* Title: Interface
*
* Description: The interface IPartitionType
*
* Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
*
* @version [DataStudio 6.5.1, 17 May, 2019]
* @since 17 May, 2019
*/
public interface IPartitionType<F, T> {
    /**
     * Converts partition type enum value to different method
     *
     * @param <F> from the input param
     * @return <T> the return value
     */
    T convert(F from);
}
