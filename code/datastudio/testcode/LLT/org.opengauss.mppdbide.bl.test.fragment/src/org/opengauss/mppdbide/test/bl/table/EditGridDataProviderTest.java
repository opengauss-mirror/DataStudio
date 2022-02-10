package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.edittabledata.CommitStatus;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.grid.IRowEffectedConfirmation;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

public class EditGridDataProviderTest
{

    @Test
    public void test_IDSEditGridDataProvider_allmethods()
    {
        IDSEditGridDataProvider dsEditGridDataProvider = new IDSEditGridDataProvider()
        {

            @Override
            public void init() throws DatabaseOperationException, DatabaseCriticalException
            {

            }

            @Override
            public List<IDSGridDataRow> getAllFetchedRows()
            {
                return null;
            }

            @Override
            public boolean isEndOfRecords()
            {
                return false;
            }

            @Override
            public int getRecordCount()
            {
                return 0;
            }

            @Override
            public IDSGridColumnProvider getColumnDataProvider()
            {
                return null;
            }

            @Override
            public ServerObject getTable()
            {
                return null;
            }

            @Override
            public boolean getResultTabDirtyFlag()
            {
                return false;
            }

            @Override
            public void setResultTabDirtyFlag(boolean flag)
            {

            }

            @Override
            public boolean isEditSupported()
            {
                return false;
            }

            @Override
            public CommitStatus commit(List<String> uniqueKeys, boolean isAtomic,
                    IRowEffectedConfirmation rowEffectedConfirm, DBConnection termConnection) throws MPPDBIDEException
            {
                return null;
            }

            @Override
            public void rollBackProvider()
            {

            }

            @Override
            public IDSGridEditDataRow getEmptyRowForInsert(int index)
            {
                return null;
            }

            @Override
            public void deleteRecord(IDSGridEditDataRow row, boolean isInserted)
            {

            }

            @Override
            public boolean isGridDataEdited()
            {
                return false;
            }

            @Override
            public List<IDSGridDataRow> getConsolidatedRows()
            {
                return null;
            }

            @Override
            public int getUpdatedRowCount()
            {
                return 0;
            }

            @Override
            public int getInsertedRowCount()
            {
                return 0;
            }

            @Override
            public int getDeletedRowCount()
            {
                return 0;
            }

            @Override
            public void cancelCommit() throws DatabaseCriticalException, DatabaseOperationException
            {

            }

            @Override
            public Database getDatabse() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
                    throws DatabaseOperationException, DatabaseCriticalException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
                    throws DatabaseOperationException, DatabaseCriticalException, SQLException {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void setFuncProcExport(boolean b) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public boolean isFuncProcExport() {
                // TODO Auto-generated method stub
                return false;
            }

        };

        assertEquals(null, dsEditGridDataProvider.getAllFetchedRows());
        assertEquals(false, dsEditGridDataProvider.isEndOfRecords());
        assertEquals(0, dsEditGridDataProvider.getRecordCount());
        assertEquals(null, dsEditGridDataProvider.getColumnDataProvider());
        assertEquals(null, dsEditGridDataProvider.getTable());
        assertEquals(null, dsEditGridDataProvider.getDatabse());
        assertEquals(false, dsEditGridDataProvider.getResultTabDirtyFlag());
        try
        {
            assertEquals(null, dsEditGridDataProvider.commit(null, false, null, null));
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
        assertEquals(null, dsEditGridDataProvider.getEmptyRowForInsert(0));
        assertEquals(false, dsEditGridDataProvider.isGridDataEdited());
        assertEquals(null, dsEditGridDataProvider.getConsolidatedRows());
        assertEquals(0, dsEditGridDataProvider.getUpdatedRowCount());
        assertEquals(0, dsEditGridDataProvider.getInsertedRowCount());
        assertEquals(0, dsEditGridDataProvider.getDeletedRowCount());
        assertEquals(false, dsEditGridDataProvider.isDistributionColumnsRequired());
        assertEquals(null, dsEditGridDataProvider.getDistributedColumnList());
        assertEquals(false, dsEditGridDataProvider.isCancelled());
        assertEquals(false, dsEditGridDataProvider.isDistributionColumn(0));
        assertEquals("", dsEditGridDataProvider.getTableName());
        assertEquals(null, dsEditGridDataProvider.getColumnNames());
        assertEquals(null, dsEditGridDataProvider.getColumnDataTypeNames());
        assertEquals(0, dsEditGridDataProvider.getColumnCount());
        assertEquals(false, dsEditGridDataProvider.isUniqueKeyPresent());
        assertEquals(false, dsEditGridDataProvider.isEditSupported());
    }

    @Test
    public void test_IDSGridDataProvider_allmethods()
    {
        IDSGridDataProvider dsGridDataProvider = new IDSGridDataProvider()
        {

            @Override
            public void init() throws DatabaseOperationException, DatabaseCriticalException
            {
            }

            @Override
            public List<IDSGridDataRow> getAllFetchedRows()
            {
                return null;
            }

            @Override
            public boolean isEndOfRecords()
            {
                return false;
            }

            @Override
            public int getRecordCount()
            {
                return 0;
            }

            @Override
            public IDSGridColumnProvider getColumnDataProvider()
            {
                return null;
            }

            @Override
            public ServerObject getTable()
            {
                return null;
            }

            @Override
            public boolean getResultTabDirtyFlag()
            {
                return false;
            }

            @Override
            public void setResultTabDirtyFlag(boolean flag)
            {

            }

            @Override
            public Database getDatabse() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
                    throws DatabaseOperationException, DatabaseCriticalException {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
                    throws DatabaseOperationException, DatabaseCriticalException, SQLException {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void setFuncProcExport(boolean isFuncProcExport) {
                
            }

            @Override
            public boolean isFuncProcExport() {
                return false;
            }

        };
        try
        {
            assertEquals(null, dsGridDataProvider.getNextBatch());
        }
        catch (DatabaseOperationException | DatabaseCriticalException e)
        {
            e.printStackTrace();
        }
        assertEquals(null, dsGridDataProvider.getColumnGroupProvider());
        assertEquals(null, dsGridDataProvider.getAllFetchedRows());
        assertEquals(false, dsGridDataProvider.isEndOfRecords());
        assertEquals(0, dsGridDataProvider.getRecordCount());
        assertEquals(null, dsGridDataProvider.getColumnDataProvider());
        assertEquals(null, dsGridDataProvider.getTable());
        assertEquals(null, dsGridDataProvider.getDatabse());
        assertEquals(false, dsGridDataProvider.getResultTabDirtyFlag());
    }
}
