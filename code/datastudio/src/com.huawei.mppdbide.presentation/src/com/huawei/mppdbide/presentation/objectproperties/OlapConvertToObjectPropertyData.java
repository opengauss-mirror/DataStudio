/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class OlapConvertToObjectPropertyData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class OlapConvertToObjectPropertyData extends ConvertToObjectPropertyData {

    @Override
    public List<IObjectPropertyData> getObjectPropertyData(List<String> tabName, List<List<String[]>> properties,
            ServerObject table, IServerObjectProperties objectPropertyObject)
            throws DatabaseOperationException, DatabaseCriticalException {

        return super.getObjectPropertyData(tabName, properties, table, objectPropertyObject);
    }
}
