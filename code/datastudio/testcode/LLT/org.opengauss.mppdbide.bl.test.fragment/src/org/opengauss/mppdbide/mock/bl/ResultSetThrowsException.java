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

import java.sql.SQLException;

import com.mockrunner.mock.jdbc.MockResultSet;

public class ResultSetThrowsException extends MockResultSet
{

    public ResultSetThrowsException(String id)
    {
        super(id);
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        throw new SQLException("Some error occurred");
    }
    
    @Override
    public boolean next() throws SQLException
    {
        throw new SQLException("Some error occurred");
    }    
}
