package org.opengauss.mppdbide.mock.bl;

import java.sql.SQLException;

import org.postgresql.copy.CopyIn;
import org.postgresql.copy.CopyOut;

public class CopyOperationHelper implements CopyOut, CopyIn
{

    byte[] bytearray = new String("\u7528\u6237\u53EF\u5B9A\u5236\u754C\u9762\u5E03\u5C40").getBytes();
    @Override
    public int getFieldCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFormat()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFieldFormat(int field)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isActive()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancelCopy() throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public long getHandledRowCount()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] readFromCopy() throws SQLException
    {
        
        byte[] dummy = bytearray;
        bytearray = null;
        return dummy;
    }

    @Override
    public void writeToCopy(byte[] buf, int off, int siz) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void flushCopy() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public long endCopy() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] readFromCopy(boolean arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

}
