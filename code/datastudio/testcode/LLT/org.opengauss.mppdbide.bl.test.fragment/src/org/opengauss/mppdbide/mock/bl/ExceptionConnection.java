package org.opengauss.mppdbide.mock.bl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.opengauss.mppdbide.mock.bl.CommonLLTUtils.EXCEPTIONENUM;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExceptionConnection extends MockConnection
{
    boolean throwExceptioForPrepareStmt = false;
    boolean throwExceptioForStmt = false;
    boolean needExceptioStatement = false;
    boolean throwExceptionClose = false;
    boolean throwExceptionMetaData = false;
    boolean throwExceptionSetLong = false;
    boolean throwExceptionCloseResultSet = false;
    boolean throwExceptionCloseStmt = false;
    boolean throwExceptionClosePreparedStmt = false;
    boolean throwExceptionGetInt = false;
    boolean throwExceptionSetInt = false;
    boolean throwExceptionGetLong = false;
    boolean throwExceptionGetString = false;
    boolean throwExceptionSetString = false;
    boolean throwExceptionGetBoolean = false;
    EXCEPTIONENUM thrownResultSetNext = EXCEPTIONENUM.NO;
    boolean needExceptionResultset = false;
    boolean throwoutofmemerrorinrs = false;
    private boolean throwExceptionNext = false;
    String sqlState = "";
    boolean throwExceptionExecuteBatch = false;
    SQLException sqlException = null;
    boolean throwExceptionGetStatement = false;   
    boolean throwExceptionAddBatch = false;    
    boolean throwExceptionRollback = false;
	boolean throwExceptionCommit=false;

    public boolean isThrowExceptionCommit() {
		return throwExceptionCommit;
	}

	public void setThrowExceptionCommit(boolean throwExceptionCommit) {
		this.throwExceptionCommit = throwExceptionCommit;
	}

	boolean throwExceptionSetAutoCommitTrue = false;
	boolean throwExceptionGetAutoCommitTrue=false;
    public boolean isThrowExceptionGetAutoCommitTrue()
    {
        return throwExceptionGetAutoCommitTrue;
    }

    public void setThrowExceptionGetAutoCommitTrue(
            boolean throwExceptionGetAutoCommitTrue)
    {
        this.throwExceptionGetAutoCommitTrue = throwExceptionGetAutoCommitTrue;
    }

    private boolean throwIndexoutOfBondException=false;
    String exceptionForNextOn;
    
    String counter;
    
    public void setExceptionForNextOn(String exceptionForNextOn)
    {
        this.exceptionForNextOn = exceptionForNextOn;
    }
    
    public void setCounter(String counter)
    {
        this.counter = counter;
    }
    
    public ExceptionConnection()
    {
        counter = null;
    }
    
    public void setThrowExceptionSetAutoCommitTrue(
            boolean throwExceptionSetAutoCommitTrue)
    {
        this.throwExceptionSetAutoCommitTrue = throwExceptionSetAutoCommitTrue;
    }
    
    public void setThrowExceptionRollback(boolean throwExceptionRollback)
    {
        this.throwExceptionRollback = throwExceptionRollback;
    }
    
    public void setThrowExceptionClosePreparedStmt(
            boolean throwExceptionClosePreparedStmt)
    {
        this.throwExceptionClosePreparedStmt = throwExceptionClosePreparedStmt;
    }
    
    public void setThrowIndexoutOfBondException(boolean throwIndexoutOfBondException) {
		this.throwIndexoutOfBondException = throwIndexoutOfBondException;
	}

	public void setThrowExceptionCloseStmt(boolean throwExceptionCloseStmt)
    {
        this.throwExceptionCloseStmt = throwExceptionCloseStmt;
    }
    
    boolean throwIndexOutOfBoundEx = false;
    
    public void setThrowIndexOutOfBoundEx(boolean throwIndexOutOfBoundEx)
    {
        this.throwIndexOutOfBoundEx = throwIndexOutOfBoundEx;
    }
    
    public void setThrowExceptionAddBatch(boolean throwExceptionAddBatch)
    {
        this.throwExceptionAddBatch = throwExceptionAddBatch;
    }
    
    public void setThrowExceptionGetStatement(boolean throwExceptionGetStatement)
    {
        this.throwExceptionGetStatement = throwExceptionGetStatement;
    }


    public void setSqlException(SQLException sqlException)
    {
        this.sqlException = sqlException;
    }


    public void setThrowExceptionNext(boolean throwExceptionNext)
    {
        this.throwExceptionNext = throwExceptionNext;
    }
     
    
    public void setThrowoutofmemerrorinrs(boolean throwoutofmemerrorinrs)
    {
        this.throwoutofmemerrorinrs = throwoutofmemerrorinrs;
    }

    public void setNeedExceptionResultset(boolean needExceptionResultset)
    {
        this.needExceptionResultset = needExceptionResultset;
    }

    public void setThrowExceptioForPrepareStmt(boolean throwExceptioForPrepareStmt)
    {
        this.throwExceptioForPrepareStmt = throwExceptioForPrepareStmt;
    }

    public void setNeedExceptioStatement(boolean needExceptioStatement)
    {
        this.needExceptioStatement = needExceptioStatement;
    }
    
    public void setThrowExceptionGetInt(boolean throwExceptionGetInt)
    {
        this.throwExceptionGetInt = throwExceptionGetInt;
    }
  	public void setThrowExceptionGetLong(boolean throwExceptionGetLong) {
		this.throwExceptionGetLong = throwExceptionGetLong;
	}

	public void setThrowExceptionSetInt(boolean throwExceptionSetInt)
    {
        this.throwExceptionSetInt = throwExceptionSetInt;
    }
    
    public void setThrowExceptionGetString(boolean throwExceptionGetString)
    {
        this.throwExceptionGetString = throwExceptionGetString;
    }
    
    public void setThrowExceptionSetString(boolean throwExceptionSetString)
    {
        this.throwExceptionSetString = throwExceptionSetString;
    }
    
    public void setThrowExceptionGetBoolean(boolean throwExceptionGetBoolean)
    {
        this.throwExceptionGetBoolean = throwExceptionGetBoolean;
    }
    
    public void setThrowExceptionClose(boolean throwExceptionClose)
    {
        this.throwExceptionClose = throwExceptionClose;
    }

    public void setThrowExceptionMetaData(boolean throwExceptionMetaData)
    {
        this.throwExceptionMetaData = throwExceptionMetaData;
    }

    public void setThrowExceptioForStmt(boolean throwExceptioForStmt)
    {
        this.throwExceptioForStmt = throwExceptioForStmt;
    }
    
    public void setThrowExceptionSetLong(boolean throwExceptionSetLong)
    {
        this.throwExceptionSetLong = throwExceptionSetLong;
    }

    public void setThrowExceptionCloseResultSet(boolean throwExceptionCloseResultSet)
    {
        this.throwExceptionCloseResultSet = throwExceptionCloseResultSet;
    }
    
    public void setSqlState(String sqlState)
    {
        this.sqlState = sqlState;
    }
    
    public void setThrownResultSetNext(EXCEPTIONENUM thrownResultSetNext)
    {
        this.thrownResultSetNext = thrownResultSetNext;
    }
    
    public void setThrowExceptionExecuteBatch(boolean throwExceptionExecuteBatch)
    {
        this.throwExceptionExecuteBatch = throwExceptionExecuteBatch;
    }

    @Override
    public PreparedStatement prepareStatement(String arg0) throws SQLException,IndexOutOfBoundsException
    {
        counter = arg0;
        
        if(throwExceptioForPrepareStmt)
        {
            throwExceptioForPrepareStmt = false;
            if(null != sqlState && sqlState.length() > 0)
            {
                throw new SQLException(sqlState, sqlState);
            }
            else
            {
                throw new SQLException();
            }
            
        }
        else if(needExceptioStatement)
        {
            ExceptionPreparedStatement exceptionPreparedStatement = new ExceptionPreparedStatement();
            
            if(needExceptionResultset)
            {
                exceptionPreparedStatement.setNeedExceptionResultset(true);
            }
            if (throwExceptionSetLong)
            {
                exceptionPreparedStatement.setLongExceptionRequired(true);
            }
            
            if (throwExceptionGetInt)
            {
                exceptionPreparedStatement.setThrowExceptionGetInt(true);
            }
            
            if (throwExceptionGetLong)
            {
                exceptionPreparedStatement.setThrowExceptionGetLong(true);
            }
            
            if (throwExceptionSetInt)
            {
                exceptionPreparedStatement.setThrowExceptionSetInt(true);
            }
            
            if (throwExceptionGetString)
            {
                exceptionPreparedStatement.setThrowExceptionGetString(true);
            }
            
            if (throwExceptionSetString)
            {
                exceptionPreparedStatement.setThrowExceptionSetString(true);
            }
            
            if (throwExceptionGetBoolean)
            {
                exceptionPreparedStatement.setThrowExceptionGetBoolean(true);
            }
            
            if (!thrownResultSetNext.equals(EXCEPTIONENUM.NO))
            {
                exceptionPreparedStatement.setThrownResultSetNext(thrownResultSetNext);
            }
            
            if (throwExceptionCloseResultSet)
            {
                exceptionPreparedStatement.setThrowExceptionCloseResultSet(true);
            }
            
            if(throwExceptionClosePreparedStmt)
            {
                exceptionPreparedStatement.setStmtCloseSQLExceptionNeede(true);
            }
            
            if(throwExceptionGetStatement)
            {
                exceptionPreparedStatement.setThrowExceptionGetStatement(true);
            }
            
            if(throwoutofmemerrorinrs)
            {
                exceptionPreparedStatement.setOutOfMemoryErrorRequired(true);
            }

            if(throwIndexoutOfBondException){
            	exceptionPreparedStatement.setThrowIndexoutOfBondException(true);
            }
            if(sqlState != null)
            {
                exceptionPreparedStatement.setSQLSTate(sqlState);
            }
            if(exceptionForNextOn != null)
            {
                exceptionPreparedStatement.setExceptionForNextOn(exceptionForNextOn);
                exceptionPreparedStatement.setCounter(counter);
            }
            
            exceptionPreparedStatement.setExceptionResultSetRequired(true);
            return exceptionPreparedStatement;
        }
        
        
        return super.prepareStatement(arg0);
    }
    
    @Override
    public Statement createStatement() throws SQLException
    {
        if(throwExceptioForStmt)
        {
            throwExceptioForStmt = false;
            throw new SQLException();
        }
        else if(needExceptioStatement)
        {
            ExceptionStatement exceptionStatement = new ExceptionStatement();
            exceptionStatement.setConnection(this);
            if(needExceptionResultset)
            {
                exceptionStatement.setNeedExceptionResultset(true);
            }
            if(throwoutofmemerrorinrs)
            {
                exceptionStatement.setThrowoutofmemerror(throwoutofmemerrorinrs);   
            }
            if(throwExceptionNext)
            {
                exceptionStatement.setThrowExceptionNext(true);
            }
            if(throwExceptionExecuteBatch)
            {
                exceptionStatement.setSqlException(sqlException);
            }
            
            if(throwExceptionGetStatement)
            {
                exceptionStatement.setThrowExceptionGetStatement(true);
            }
            
            if(throwExceptionAddBatch)
            {
                exceptionStatement.setThrowExceptionAddBatch(true);    
            }
            exceptionStatement.setSqlException(sqlException);
            
            if(throwIndexOutOfBoundEx)
            {
                exceptionStatement.setThrowIndexOutOfBoundEx(true);
            }
            
            if(throwExceptionCloseStmt)
            {
                exceptionStatement.setStmtCloseSQLExceptionNeede(throwExceptionCloseStmt);
            }
            if(throwExceptionCloseResultSet)
            {
                exceptionStatement.setThrowExceptionCloseResultSet(throwExceptionCloseResultSet);
            }
            return exceptionStatement;            
        }
        return super.createStatement();
    }
    
    @Override
    public void close() throws SQLException
    {
        if(throwExceptionClose)
        {
            throw new SQLException();
        }
        super.close();
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException
    {
        if(throwExceptionMetaData)
        {
            throw new SQLException();
        }
        return super.getMetaData();
    }
    
    @Override
    public void rollback() throws SQLException
    {
        if(throwExceptionRollback)
        {
            throw sqlException;
        }
        super.rollback();
    }
    
    @Override
    public void setAutoCommit(boolean arg0) throws SQLException
    {
        if(arg0 && throwExceptionSetAutoCommitTrue)
        {
            throw sqlException;
        }
        super.setAutoCommit(arg0);
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException
    {
        if(throwExceptionGetAutoCommitTrue)
        {
            throw sqlException;
        }
        return super.getAutoCommit();
    }
    
    @Override
	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		if (throwExceptionCommit) {
			throw new SQLException();
		}
		super.commit();
	}
}
