/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;

/**
 * Title: ImportExportFileCounter
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 21-Apr-2020]
 * @since 21-Apr-2020
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
