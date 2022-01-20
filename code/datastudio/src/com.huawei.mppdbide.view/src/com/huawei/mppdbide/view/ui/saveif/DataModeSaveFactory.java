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

package com.huawei.mppdbide.view.ui.saveif;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionManager;

/**
 * Title: class DataModeSaveFactory
 * Description: The Class DataModeSaveFactory.
 *
 * @since 3.0.0
 */
public class DataModeSaveFactory {
    private static DataModeSaveFactory factory = new DataModeSaveFactory();
    private AtomicInteger autoSaveId = new AtomicInteger(0);
    private Map<String, DbDataModeSave> mapModeSave = new ConcurrentHashMap<>();

    /**
     * The data model save factory instance
     *
     * @return DataModeSaveFactory the data model save factory instance
     */
    public static DataModeSaveFactory instance() {
        return factory;
    }

    /**
     * Initial data model save
     *
     * @param ConnectionManager the connection manager
     * @param int the save type
     * @return DataModeSave the data mode to save
     */
    public DataModeSave init(ConnectionManager manager, int saveType) {
        String id = getNextId(saveType);
        DbDataModeSave modeSave = new DbDataModeSave(manager, id, saveType);
        mapModeSave.put(id, modeSave);
        return modeSave;
    }

    /**
     * Clear data model save
     *
     * @param DataModeSave the data mode to clear
     */
    public void clear(DataModeSave modeSave) {
        if (! (modeSave instanceof DbDataModeSave)) {
            return;
        }
        DbDataModeSave dbDataModeSave = (DbDataModeSave) modeSave;
        dbDataModeSave.clear();
        mapModeSave.remove(dbDataModeSave.getId());
    }

    private String getNextId(int saveType) {
        return String.format(Locale.ENGLISH,
                "save-%d-%d",
                autoSaveId.incrementAndGet(),
                saveType);
    }
}
