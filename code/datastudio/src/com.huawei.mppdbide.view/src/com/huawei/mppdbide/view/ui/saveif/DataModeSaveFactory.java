/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 02,06,2021]
 * @since 02,06,2021
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
