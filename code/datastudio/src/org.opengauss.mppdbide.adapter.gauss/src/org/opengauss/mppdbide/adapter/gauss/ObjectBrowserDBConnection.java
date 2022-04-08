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

package org.opengauss.mppdbide.adapter.gauss;

import java.sql.ResultSet;

import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserDBConnection.
 * 
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
