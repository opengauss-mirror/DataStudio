package org.opengauss.mppdbide.mock.bl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ExceptionRSMetadata implements ResultSetMetaData
{

    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getCatalogName(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnClassName(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getColumnCount() throws SQLException
    {
        // TODO Auto-generated method stub
        return 1;
    }

    @Override
    public int getColumnDisplaySize(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getColumnLabel(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnName(int arg0) throws SQLException
    {
        throw new SQLException();
    }

    @Override
    public int getColumnType(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getColumnTypeName(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPrecision(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getScale(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getSchemaName(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAutoIncrement(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCaseSensitive(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCurrency(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDefinitelyWritable(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int isNullable(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isReadOnly(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSearchable(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSigned(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isWritable(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

}
