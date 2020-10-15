package com.huawei.mppdbide.test.presentation.table;

import com.huawei.mppdbide.bl.preferences.IBLPreference;
import static org.junit.Assert.*;

public class MockPresentationBLPreferenceImpl implements IBLPreference
{
    public MockPresentationBLPreferenceImpl()
    {
        
        
    }

    private static String DsEncoding;
    private static String fileEncoding;
    private static String dateFormat;
    private static String timeFormat;
    @Override
    public int getSQLHistorySize()
    {
        
        return 0;
    }

    @Override
    public int getSQLQueryLength()
    {
        
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
        MockPresentationBLPreferenceImpl.DsEncoding = DsEncoding;

    }

    public static void setFileEncoding(String fileEncoding)
    {
        MockPresentationBLPreferenceImpl.fileEncoding = fileEncoding;

    }
    
    public static void setDateFormat(String dateFormat)
    {
        MockPresentationBLPreferenceImpl.dateFormat = dateFormat;

    }

    public static void setTimeFormat(String timeFormat)
    {
        MockPresentationBLPreferenceImpl.timeFormat = timeFormat;

    }

    @Override
    public boolean isIncludeEncoding()
    {
        
        return false;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public String getTimeFormat() {
        return timeFormat;
    }
    
    
    
    
    

}
