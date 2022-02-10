package org.opengauss.mppdbide.test.presentation.table;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.QueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.QueryExecutionSummary;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.ContextExecutionOperationType;
import org.opengauss.mppdbide.presentation.EditTableDataCore;
import org.opengauss.mppdbide.presentation.ExecutionFailureActionOptions;
import org.opengauss.mppdbide.presentation.IEditTableDataCore;
import org.opengauss.mppdbide.presentation.IExecutionContext;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.edittabledata.DSEditTableDataGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IRowEffectedConfirmation;
import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import static org.junit.Assert.*;

public class EditTableDataHelper
{

    private Database    database;

    private QueryResult result;

    public EditTableDataHelper(Database database)
    {
        this.database = database;
    }

    public QueryResult getQueryResult()
    {
        return this.result;
    }

    public EditTableDataCore getCoreObject()
    {
        IEditTableDataCore core = null;
        try
        {
            Database database = this.database;
            TableMetaData editTableMetadata = new TableMetaData(1, "EditTable",
                    database.getNameSpaceById(1), "tablespace");
            // add datatype to columns of a table
            TypeMetaData type1 = new TypeMetaData(1, "bigint",
                    database.getNameSpaceById(1));
            TypeMetaData type2 = new TypeMetaData(2, "varchar",
                    database.getNameSpaceById(1));
            TypeMetaData type3 = new TypeMetaData(3, "integer",
                    database.getNameSpaceById(1));
            // add columns to the table
            ColumnMetaData empId = new ColumnMetaData(editTableMetadata, 1,
                    "Emp_ID", type1);
            empId.setDefaultValue("1");
            empId.setAttDefString("");
            ColumnMetaData empName = new ColumnMetaData(editTableMetadata, 2,
                    "Emp_NAME", type2);
            empName.setDefaultValue("empname");
            empName.setAttDefString("");
            ColumnMetaData empAge = new ColumnMetaData(editTableMetadata, 3,
                    "EMP_AGE", type3);
            empAge.setDefaultValue("21");
            empAge.setAttDefString("{CONST");
            empAge.setHasDefVal(true);
            editTableMetadata.addColumn(empId);
            editTableMetadata.addColumn(empName);
            editTableMetadata.addColumn(empAge);

            // add constraint to the table
            ConstraintMetaData primaryCons = new ConstraintMetaData(1, "Cons1",
                    ConstraintType.PRIMARY_KEY_CONSTRSINT);
            editTableMetadata.addConstraint(primaryCons);
            ConstraintMetaData uniqueCons = new ConstraintMetaData(2, "Cons2",
                    ConstraintType.UNIQUE_KEY_CONSTRSINT);
            editTableMetadata.addConstraint(uniqueCons);
            
            // create edit table data core
            core = new EditTableDataCore();
            core.init(editTableMetadata);
            core.getTermConnection()
                    .setConnection(database.getConnectionManager().getFreeConnection());
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("database operation occurred");
        }
        catch (MPPDBIDEException e)
        {
            System.out.println("MPPDBIDEException occurred");
        }
        return (EditTableDataCore) core;

    }

    public IDSEditGridDataProvider getDataProvider(
            PreparedStatementResultSetHandler preparedstatementHandler)
            throws DatabaseCriticalException, DatabaseOperationException,
            MPPDBIDEException
    {
        return prepareObjectsForProvider(preparedstatementHandler);
    }

    public DSEditTableDataGridDataProvider prepareObjectsForProvider(
            PreparedStatementResultSetHandler preparedstatementHandler)
            throws DatabaseCriticalException, DatabaseOperationException,
            MPPDBIDEException
    {
        CommonLLTUtils.prepareEditTableDataResultSet(preparedstatementHandler);
        Database database = this.database;
        DSEditTableDataGridDataProvider dataProvider = null;
        EditTableDataCore coreObject = getCoreObject();
        result = DatabaseUtils.executeOnSqlTerminal(coreObject.getQuery(), 10,
                database.getConnectionManager().getFreeConnection(), new MessageQueue());
        IResultConfig rsConfig = getResultConfig();
        IQueryExecutionSummary summary = getQueryExecutionSummary();
        IExecutionContext context = getExecutionContext();
        dataProvider = new DSEditTableDataGridDataProvider(result, rsConfig,
                summary, context, false);

        return dataProvider;
    }

    public IQueryExecutionSummary getQueryExecutionSummary()
    {
        QueryExecutionSummary queryExcSummary = null;
        String input = "2016-12-16 18:29:09";

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf1.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date;
        try
        {
            date = sdf1.parse(input);
            String dt = sdf1.format(date);
            queryExcSummary = new QueryExecutionSummary("Gauss", "ds",
                    CommonLLTUtils.EDIT_TABLE_DATA_SELECT_QUERY, true, dt, 109,
                    0);
        }
        catch (ParseException e)
        {
            System.out.println("Parse exception occurred");
        }

        return queryExcSummary;
    }

    public List<String> getUniqueKeys()
    {
        List<String> uniqueKeyList = new ArrayList<String>();
        uniqueKeyList.add("Emp_ID");
        uniqueKeyList.add("Emp_NAME");
        return uniqueKeyList;

    }

    public IRowEffectedConfirmation getRowEffectedConfirm(boolean success)
    {
        if (success)
        {
            return new IRowEffectedConfirmation()
            {

                @Override
                public void promptUerConfirmation()
                {
                    
                }

            };
        }
        return new IRowEffectedConfirmation()
        {

            @Override
            public void promptUerConfirmation()
            {
                
            }
         
        };
    }

    public IResultConfig getResultConfig()
    {
        return new IResultConfig()
        {

            @Override
            public int getFetchCount()
            {
                
                return 100;
            }

            @Override
            public ActionAfterResultFetch getActionAfterFetch()
            {
                
                return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
            }
        };
    }

    public IExecutionContext getExecutionContext()
    {
        return new IExecutionContext()
        {

            @Override
            public void setWorkingJobContext(Object jobContext)
            {
                

            }

            @Override
            public void setJobDone()
            {
                

            }

            @Override
            public void setCurrentExecution(
                    ContextExecutionOperationType contextOperationTypeNewPlSqlCreation)
            {
                

            }

            @Override
            public void setCriticalErrorThrown(boolean b)
            {
                

            }

            @Override
            public boolean needQueryParseAndSplit()
            {
                
                return false;
            }

            @Override
            public String jobType()
            {
                
                return null;
            }

            @Override
            public void handleSuccessfullCompletion()
            {
                

            }

            @Override
            public void handleExecutionException(Exception e)
            {
                

            }

            @Override
            public Object getWorkingJobContext()
            {
                
                return null;
            }

            @Override
            public TerminalExecutionConnectionInfra getTermConnection()
            {
                
                return getCoreObject().getTermConnection();
            }

            @Override
            public IResultDisplayUIManager getResultDisplayUIManager()
            {
                
                return null;
            }

            @Override
            public IResultConfig getResultConfig()
            {
                
                return getResultConfig();
            }

            @Override
            public String getQuery()
            {
                
                return null;
            }

            @Override
            public MessageQueue getNoticeMessageQueue()
            {
                
                return null;
            }

            @Override
            public ServerObject getCurrentServerObject()
            {
                
                return getCoreObject().getTable();
            }

            @Override
            public ContextExecutionOperationType getCurrentExecution()
            {
                
                return null;
            }

            @Override
            public String getContextName()
            {
                
                return null;
            }

            @Override
            public String getConnectionProfileID()
            {
                
                return null;
            }

            @Override
            public ExecutionFailureActionOptions getActionOnQueryFailure()
            {
                
                return null;
            }

            @Override
            public boolean canFreeConnectionAfterUse()
            {
                
                return false;
            }

            @Override
            public ArrayList<DefaultParameter> getInputValues() {
                return null;
            }
        };
    }

}
