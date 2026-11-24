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

package org.opengauss.mppdbide.bl.test.debug;

import org.opengauss.mppdbide.bl.preferences.IBLPreference;

public class MockDebugBLPreferenceImpl implements IBLPreference
{
    public MockDebugBLPreferenceImpl()
    {
        
    }

    private static String DsEncoding;
    private static String fileEncoding;
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
        MockDebugBLPreferenceImpl.DsEncoding = DsEncoding;

    }

    public static void setFileEncoding(String fileEncoding)
    {
        MockDebugBLPreferenceImpl.fileEncoding = fileEncoding;

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
