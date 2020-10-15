/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.gauss;

import java.sql.ResultSet;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserDBConnection.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ObjectBrowserDBConnection extends DBConnection {
    private final Object lock = new Object();

    @Override
    public String execSelectAndGetFirstVal(String query) throws DatabaseCriticalException, DatabaseOperationException {
        synchronized (lock) {
            return super.execSelectAndGetFirstVal(query);
        }
    }

    @Override
    public ResultSet execSelectAndReturnRs(String query) throws DatabaseCriticalException, DatabaseOperationException {
        synchronized (lock) {
            return super.execSelectAndReturnRs(query);
        }
    }

    @Override
    public ResultSet execSelectToExportCSV(String query, int preferenceCount)
            throws DatabaseCriticalException, DatabaseOperationException {
        synchronized (lock) {
            return super.execSelectToExportCSV(query, preferenceCount);
        }
    }

    @Override
    public void execNonSelect(String query) throws DatabaseOperationException, DatabaseCriticalException {
        synchronized (lock) {
            super.execNonSelect(query);
        }
    }

    @Override
    public void execNonSelectForTimeout(String query) throws DatabaseOperationException, DatabaseCriticalException {
        synchronized (lock) {
            super.execNonSelectForTimeout(query);
        }
    }

}
