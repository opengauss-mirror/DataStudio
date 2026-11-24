/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.presentation;

import java.util.List;

import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

public interface IDSGridDataProvider
{
    void init() throws DatabaseOperationException, DatabaseCriticalException;

    void close() throws DatabaseOperationException, DatabaseCriticalException;

    List<IDSGridDataRow> getNextBatch()
            throws DatabaseOperationException, DatabaseCriticalException;

    List<IDSGridDataRow> getAllFetchedRows();

    boolean isEndOfRecords();

    int getRecordCount();

    IDSGridColumnProvider getColumnDataProvider();

    void preDestroy();
}

