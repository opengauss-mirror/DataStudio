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

