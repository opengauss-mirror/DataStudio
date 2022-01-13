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

package com.huawei.mppdbide.utils.files;


/**
 * Title: FilePermissionFactory
 * 
 * @since 3.0.0
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
