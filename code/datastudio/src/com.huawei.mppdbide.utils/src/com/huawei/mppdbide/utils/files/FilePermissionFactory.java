/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.files;


/**
 * Title: FilePermissionFactory
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 12-Jul-2019]
 * @since 12-Jul-2019
 */

public class FilePermissionFactory {
    private static volatile ISetFilePermission INSTANCE = null;
    private static final Object MUTEX = new Object();

    /**
     * Gets the file permission instance.
     *
     * @return the file permission instance
     */
    public static ISetFilePermission getFilePermissionInstance() {
        if (INSTANCE == null) {
            synchronized (MUTEX) {
                if (INSTANCE == null) {
                    INSTANCE = new SetFilePermission();
                }
            }
        }
        return INSTANCE;
    }
}
