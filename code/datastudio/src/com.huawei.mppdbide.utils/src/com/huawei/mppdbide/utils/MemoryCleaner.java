/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

/**
 * 
 * Title: class
 * 
 * Description: The Class MemoryCleaner.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class MemoryCleaner {

    /**
     * Instantiates a new memory cleaner.
     */
    private MemoryCleaner() {

    }

    /**
     * Clean up memory.
     */
    public static void cleanUpMemory() {
        System.gc();
    }

}
