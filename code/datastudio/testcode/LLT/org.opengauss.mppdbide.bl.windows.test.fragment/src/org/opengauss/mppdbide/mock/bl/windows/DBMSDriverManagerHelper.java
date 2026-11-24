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

package org.opengauss.mppdbide.mock.bl.windows;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.keywordssyntax.Keywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.OLAPKeywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;

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
