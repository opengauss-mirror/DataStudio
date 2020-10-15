package com.huawei.mppdbide.mock.bl.windows;

import com.huawei.mppdbide.bl.preferences.IBLPreference;

public class MockBLPreferenceImpl implements IBLPreference
{

    private static String DsEncoding;
    private static String fileEncoding;
    private static int    sqlHistorySize;
    private static int    sqlQueryLength;
    public MockBLPreferenceImpl()
    {
       
    }
    @Override
    public int getSQLHistorySize()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getSQLQueryLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public  String getDSEncoding()
    {
        return DsEncoding;
    }

    @Override
    public  String getFileEncoding()
    {
       
        return fileEncoding;
    }
    
    public static void setDsEncoding(String DsEncoding)
    {
        MockBLPreferenceImpl.DsEncoding = DsEncoding;

    }

    public static void setFileEncoding(String fileEncoding)
    {
        MockBLPreferenceImpl.fileEncoding = fileEncoding;

    }
    
    
    
    public static void setSQLHistorySize(int size)
    {
        MockBLPreferenceImpl.sqlHistorySize = size;

    }
    
    public static void setSQLQueryLength(int size)
    {
        MockBLPreferenceImpl.sqlQueryLength = size;

    }
    @Override
    public boolean isIncludeEncoding()
    {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public String getDateFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public String getTimeFormat() {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    

}
