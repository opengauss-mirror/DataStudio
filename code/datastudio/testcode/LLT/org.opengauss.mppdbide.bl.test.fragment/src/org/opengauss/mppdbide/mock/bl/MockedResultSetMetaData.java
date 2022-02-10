package org.opengauss.mppdbide.mock.bl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class MockedResultSetMetaData implements ResultSetMetaData
{
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isWritable(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isSigned(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isSearchable(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public int isNullable(int column) throws SQLException
    {
        
        return 0;
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isCurrency(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException
    {
        
        return false;
    }

    @Override
    public String getTableName(int column) throws SQLException
    {
        
        return null;
    }

    @Override
    public String getSchemaName(int column) throws SQLException
    {
        
        return null;
    }

    @Override
    public int getScale(int column) throws SQLException
    {
        
        return 0;
    }

    @Override
    public int getPrecision(int column) throws SQLException
    {
        
        return 0;
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException
    {
        if(column == 1)
        {
            return "bigint";
        }
        if(column == 2)
        {
            return "float8";
        }
        if(column == 3)
        {
            return "float4";
        }
        if(column == 4)
        {
            return "date";
        }
        if(column == 5)
        {
           return "timestamp";
        }
        if(column == 6)
        {
           return "timestamptz";
        }
        if(column ==7)
        {
           return "bool";
        }
        return null;
    }

    @Override
    public int getColumnType(int column) throws SQLException
    {
        if(column == 1)
        {
            return Types.BIGINT;
        }
        if(column == 2)
        {
            return Types.BOOLEAN;
        }
        if(column == 3)
        {
            return Types.NUMERIC;
        }
        if(column == 4)
        {
            return Types.DATE;
        }
        if(column == 5)
        {
           return Types.TIMESTAMP_WITH_TIMEZONE;
        }
        if(column == 6)
        {
           return Types.CLOB;
        }
        if(column ==7)
        {
           return Types.INTEGER;
        }
        return 0;
    }

    @Override
    public String getColumnName(int column) throws SQLException
    {
        if(column == 1)
        {
            return "Col1";
        }
        if(column == 2)
        {
            return "Col2";
        }
        if(column == 3)
        {
            return "Col3";
        }
        if(column == 4)
        {
            return "Col4";
        }
        if(column == 5)
        {
           return "Col5";
        }
        if(column == 6)
        {
           return "Col6";
        }
        if(column ==7)
        {
           return "Col7";
        }
        return null;
    }

    @Override
    public String getColumnLabel(int column) throws SQLException
    {
        
        return null;
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException
    {
        
        return 0;
    }

    @Override
    public int getColumnCount() throws SQLException
    {
        
        return 7;
    }

    @Override
    public String getColumnClassName(int column) throws SQLException
    {
        
        return null;
    }

    @Override
    public String getCatalogName(int column) throws SQLException
    {
        
        return null;
    }
}