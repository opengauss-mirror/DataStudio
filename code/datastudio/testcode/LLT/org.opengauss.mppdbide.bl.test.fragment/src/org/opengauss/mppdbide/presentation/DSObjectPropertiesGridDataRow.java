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

package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.presentation.IDSGridDataRow;

public class DSObjectPropertiesGridDataRow implements IDSGridDataRow
{
    private Object[] rows;

    public DSObjectPropertiesGridDataRow(Object[] row)
    {
        this.rows = row;
    }

    @Override
    public Object[] getValues()
    {

        return rows;
    }

    @Override
    public Object getValue(int columnIndex)
    {
        
        return rows[columnIndex];
    }

}
