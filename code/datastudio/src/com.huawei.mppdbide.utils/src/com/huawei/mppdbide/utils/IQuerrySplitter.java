/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

import java.util.ArrayList;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IQuerrySplitter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IQuerrySplitter {

    /**
     * 
     * splitQuerries.
     *
     * @param queryArray the query array
     * @param query the query
     * @param isOLAP the is OLAP
     * @throws DatabaseOperationException the database operation exception
     */
    void splitQuerries(ArrayList<String> queryArray, String query, boolean isOLAP) throws DatabaseOperationException;

}
