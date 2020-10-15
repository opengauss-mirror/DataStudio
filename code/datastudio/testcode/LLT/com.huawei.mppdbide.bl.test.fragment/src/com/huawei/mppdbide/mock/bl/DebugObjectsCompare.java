package com.huawei.mppdbide.mock.bl;

import java.util.Comparator;

import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;



/**
 * Compare 2 debug objects are equal.
 * 
 * @author S00902246
 * 
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
