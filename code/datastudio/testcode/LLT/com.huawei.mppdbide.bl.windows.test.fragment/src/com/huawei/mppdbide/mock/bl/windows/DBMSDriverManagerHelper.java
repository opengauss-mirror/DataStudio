package com.huawei.mppdbide.mock.bl.windows;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;

public class DBMSDriverManagerHelper implements IConnectionDriver
{

    @Override
    public String getDriverName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getToolPath(String toolName)
    {
        
        return "./tools/pg_dump.bat";
    }

    @Override
    public Driver getJDBCDriver()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Properties getDriverSpecificProperties()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String extractErrCodeAdErrMsgFrmServErr(SQLException e)
    {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public Keywords getKeywordList() {
		// TODO Auto-generated method stub
		return new OLAPKeywords();
	}

    @Override
    public SQLSyntax loadSQLSyntax()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
