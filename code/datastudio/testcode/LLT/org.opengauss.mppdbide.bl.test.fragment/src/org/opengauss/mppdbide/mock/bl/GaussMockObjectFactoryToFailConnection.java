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

package org.opengauss.mppdbide.mock.bl;

import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockDriver;

public class GaussMockObjectFactoryToFailConnection extends
        JDBCMockObjectFactory
{

    private GaussMockDriverFailConnection failConnection = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.mock.jdbc.JDBCMockObjectFactory#createMockDriver()
     */
    @Override
    public MockDriver createMockDriver()
    {
        if (null == failConnection)
        {
            failConnection = new GaussMockDriverFailConnection();
            failConnection.resetConnectionCounters();
        }

        return failConnection;
    }

    public boolean isConnectionParams(String url, String username,
            String password)
    {
        return failConnection.isConnectionParams(url, username, password);
    }

    public int getConnectionCount()
    {
        return failConnection.getConnectionCount();
    }
    
    public void setActiveConnection(int active)
    {
        failConnection.setActiveConnection(active);
    }
    
    @Override
    public MockConnection getMockConnection()
    {
        if (null != failConnection)
        {
            MockConnection conn = failConnection.getActiveConnection();
            if (null != conn)
            {
                return conn;
            }
        }
        
        return super.getMockConnection();        
    }
}
