/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.mock.bl;

import java.util.Comparator;

import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;



/**
 * Compare 2 debug objects are equal.
 *
 * @since 3.0.0
 */
public class DebugObjectsCompare implements Comparator<DebugObjects>
{
    @Override
    public int compare(DebugObjects o1, DebugObjects o2)
    {
        int retVal = -1;
        if (o1.getOid()== o2.getOid()
                && o1.getDisplayName(false).equalsIgnoreCase(o2.getDisplayName(false)))
        {
            if (o1.getSourceCode().getCode()
                    .equalsIgnoreCase(o2.getSourceCode().getCode()))
            {
                if (o1.getObjectType().ordinal() == o2.getObjectType()
                        .ordinal())
                {
                    retVal = 0;
                }
            }
        }
        return retVal;
    }
}
