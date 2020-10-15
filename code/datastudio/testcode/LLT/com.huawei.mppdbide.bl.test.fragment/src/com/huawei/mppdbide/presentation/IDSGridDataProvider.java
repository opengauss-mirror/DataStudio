package com.huawei.mppdbide.presentation;

import java.util.List;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

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

