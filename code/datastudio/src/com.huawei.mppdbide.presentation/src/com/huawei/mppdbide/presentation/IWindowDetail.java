/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IWindowDetail.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IWindowDetail {

    /**
     * Gets the title.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Gets the short title.
     *
     * @return the short title
     */
    String getShortTitle();

    /**
     * Gets the unique ID.
     *
     * @return the unique ID
     */
    String getUniqueID();

    /**
     * Gets the icon.
     *
     * @return the icon
     */
    default String getIcon() {
        return null;
    }

    /**
     * Checks if is closeable.
     *
     * @return true, if is closeable
     */
    default boolean isCloseable() {
        return true;
    }
}