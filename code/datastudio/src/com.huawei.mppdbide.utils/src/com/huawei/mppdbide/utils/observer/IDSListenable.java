/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.observer;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSListenable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDSListenable {

    /**
     * Adds the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    default void addListener(int type, IDSListener listener) {

    }

    /**
     * Removes the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    default void removeListener(int type, IDSListener listener) {

    }
}
