/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.test.presentation.table;

import org.opengauss.mppdbide.bl.preferences.IBLPreference;
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
