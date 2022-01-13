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

package com.huawei.mppdbide.view.utils;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * Title: ImportExportFileCounter
 *
 * @since 3.0.0
 */
public class ImportExportFileCounter {
    private static final Object INSTANCE_LOCK = new Object();
    private static volatile ImportExportFileCounter instance;

    private int importExportCounter = 0;

    /**
     * ImportExportFileCounter class getInstance
     * 
     * @return ImportExportFileCounter return instance
     */
    public static ImportExportFileCounter getInstance() {
        if (null == instance) {
            synchronized (INSTANCE_LOCK) {
                if (null == instance) {
                    instance = new ImportExportFileCounter();
                }
            }
        }
            
        return instance;
    }

    /**
     * registerCounter
     */
    public void registerCounter() {
        synchronized (INSTANCE_LOCK) {
            importExportCounter++;
        }
    }

    /**
     * deRegisterCounter
     */
    public void deRegisterCounter() {
        synchronized (INSTANCE_LOCK) {
            importExportCounter--;
        }
    }

    /**
     * canExportOrImport
     * 
     * @return boolean canExportOrImport
     */
    public boolean canExportOrImport() {
        int counter = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF);
        synchronized (INSTANCE_LOCK) {
            if (importExportCounter < counter || counter == 0) {
                return true;
            } 
        }
        
        return false;
    }
}
