package org.opengauss.mppdbide.mock.bl;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExceptionConnectionHelper extends MockConnection {
	boolean throwExceptioForPrepareStmt = false;
	boolean throwExceptioForStmt = false;
	boolean needExceptioStatement = false;
	boolean throwExceptionClose = false;
	boolean throwExceptionMetaData = false;
	boolean throwExceptionSetLong = false;
	boolean throwExceptionCloseResultSet = false;
	boolean throwExceptionGetInt = false;
	boolean throwExceptionSetInt = false;
	boolean throwExceptionGetString = false;
	boolean throwExceptionSetString = false;
	boolean throwExceptionGetBoolean = false;
	boolean throwExceptionRollback = false;
	boolean throwExceptionCommit=false;

	EXCEPTIONENUM thrownResultSetNext = EXCEPTIONENUM.NO;
	boolean needExceptionResultset = false;
	boolean throwoutofmemerrorinrs = false;
	private boolean throwExceptionNext = false;
	String sqlState = "";
	private boolean throwExceptionGetLong = false;
	private boolean throwIndexoutOfBondException = false;
	private boolean stmtCloseSQLExceptionNeede = false;

	public void setThrowExceptionNext(boolean throwExceptionNext) {
		this.throwExceptionNext = throwExceptionNext;
	}

	public void setThrowIndexoutOfBondException(
			boolean throwIndexoutOfBondException) {
		this.throwIndexoutOfBondException = throwIndexoutOfBondException;
	}

	public void setStmtCloseSQLExceptionNeede(boolean stmtCloseSQLExceptionNeede) {
		this.stmtCloseSQLExceptionNeede = stmtCloseSQLExceptionNeede;
	}

	public void setThrowoutofmemerrorinrs(boolean throwoutofmemerrorinrs) {
		this.throwoutofmemerrorinrs = throwoutofmemerrorinrs;
	}

	public void setNeedExceptionResultset(boolean needExceptionResultset) {
		this.needExceptionResultset = needExceptionResultset;
	}

	public void setThrowExceptioForPrepareStmt(
			boolean throwExceptioForPrepareStmt) {
		this.throwExceptioForPrepareStmt = throwExceptioForPrepareStmt;
	}

	public void setNeedExceptioStatement(boolean needExceptioStatement) {
		this.needExceptioStatement = needExceptioStatement;
	}

	public void setThrowExceptionGetInt(boolean throwExceptionGetInt) {
		this.throwExceptionGetInt = throwExceptionGetInt;
	}

	public void setThrowExceptionSetInt(boolean throwExceptionSetInt) {
		this.throwExceptionSetInt = throwExceptionSetInt;
	}

	public void setThrowExceptionGetString(boolean throwExceptionGetString) {
		this.throwExceptionGetString = throwExceptionGetString;
	}

	public void setThrowExceptionGetLong(boolean throwExceptionGetLong) {
		this.throwExceptionGetLong = throwExceptionGetLong;
	}

	public void setThrowExceptionSetString(boolean throwExceptionSetString) {
		this.throwExceptionSetString = throwExceptionSetString;
	}

	public void setThrowExceptionGetBoolean(boolean throwExceptionGetBoolean) {
		this.throwExceptionGetBoolean = throwExceptionGetBoolean;
	}

	public void setThrowExceptionClose(boolean throwExceptionClose) {
		this.throwExceptionClose = throwExceptionClose;
	}

	public void setThrowExceptionMetaData(boolean throwExceptionMetaData) {
		this.throwExceptionMetaData = throwExceptionMetaData;
	}

	public void setThrowExceptioForStmt(boolean throwExceptioForStmt) {
		this.throwExceptioForStmt = throwExceptioForStmt;
	}

	public void setThrowExceptionSetLong(boolean throwExceptionSetLong) {
		this.throwExceptionSetLong = throwExceptionSetLong;
	}

	public void setThrowExceptionCloseResultSet(
			boolean throwExceptionCloseResultSet) {
		this.throwExceptionCloseResultSet = throwExceptionCloseResultSet;
	}

	public void setSqlState(String sqlState) {
		this.sqlState = sqlState;
	}

	public void setThrownResultSetNext(EXCEPTIONENUM thrownResultSetNext) {
		this.thrownResultSetNext = thrownResultSetNext;
	}

	public boolean isThrowExceptionRollback() {
		return throwExceptionRollback;
	}

	public void setThrowExceptionRollback(boolean throwExceptionRollback) {
		this.throwExceptionRollback = throwExceptionRollback;
	}

	public boolean isThrowExceptionCommit() {
		return throwExceptionCommit;
	}

	public void setThrowExceptionCommit(boolean throwExceptionCommit) {
		this.throwExceptionCommit = throwExceptionCommit;
	}


	@Override
	public PreparedStatement prepareStatement(String arg0) throws SQLException,
			IndexOutOfBoundsException {
		if (throwExceptioForPrepareStmt) {
			throwExceptioForPrepareStmt = false;
			if (null != sqlState && sqlState.length() > 0) {
				throw new SQLException(sqlState, sqlState);
			} else {
				throw new SQLException();
			}

		} else if (needExceptioStatement) {
			ExceptionPreparedStatementHelper exceptionPreparedStatement = new ExceptionPreparedStatementHelper();
			if (throwExceptionSetLong) {
				exceptionPreparedStatement.setLongExceptionRequired(true);
			}

			if (throwExceptionGetInt) {
				exceptionPreparedStatement.setThrowExceptionGetInt(true);
			}

			if (throwExceptionGetLong) {
				exceptionPreparedStatement.setThrowExceptionGetLong(true);
			}

			if (throwExceptionSetInt) {
				exceptionPreparedStatement.setThrowExceptionSetInt(true);
			}

			if (throwExceptionGetString) {
				exceptionPreparedStatement.setThrowExceptionGetString(true);
			}

			if (throwExceptionSetString) {
				exceptionPreparedStatement.setThrowExceptionSetString(true);
			}

			if (throwExceptionGetBoolean) {
				exceptionPreparedStatement.setThrowExceptionGetBoolean(true);
			}

			if (!thrownResultSetNext.equals(EXCEPTIONENUM.NO)) {
				exceptionPreparedStatement
						.setThrownResultSetNext(thrownResultSetNext);
			}

			if (throwExceptionCloseResultSet) {
				exceptionPreparedStatement
						.setThrowExceptionCloseResultSet(true);
			}

			if (throwIndexoutOfBondException) {
				exceptionPreparedStatement
						.setThrowIndexoutOfBondException(true);
			}
			if (stmtCloseSQLExceptionNeede) {
				exceptionPreparedStatement.setStmtCloseSQLExceptionNeede(true);
			}
			exceptionPreparedStatement.setExceptionResultSetRequired(true);
			return exceptionPreparedStatement;
		}

		return super.prepareStatement(arg0);
	}

	@Override
	public Statement createStatement() throws SQLException {
		if (throwExceptioForStmt) {
			throwExceptioForStmt = false;
			throw new SQLException();
		} else if (needExceptioStatement) {
			ExceptionStatementHelper exceptionStatement = new ExceptionStatementHelper();
			if (needExceptionResultset) {
				exceptionStatement.setNeedExceptionResultset(true);
			}
			if (throwoutofmemerrorinrs) {
				exceptionStatement
						.setThrowoutofmemerror(throwoutofmemerrorinrs);
			}
			if (throwIndexoutOfBondException)
				exceptionStatement.setThrowIndexoutOfBondException(true);
			if (throwExceptionNext) {
				exceptionStatement.setThrowExceptionNext(true);
			}
			if (stmtCloseSQLExceptionNeede) {
				exceptionStatement.setStmtCloseSQLExceptionNeede(true);
			}
			return exceptionStatement;
		}
		return super.createStatement();
	}

	@Override
	public void close() throws SQLException {
		if (throwExceptionClose) {
			throw new SQLException();
		}
		super.close();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (throwExceptionMetaData) {
			throw new SQLException();
		}
		return super.getMetaData();
	}

	@Override
	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		if (throwExceptionRollback) {
			throw new SQLException();
		}
		super.rollback();
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
