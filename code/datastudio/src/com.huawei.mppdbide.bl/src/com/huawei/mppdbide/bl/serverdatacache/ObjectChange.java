/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ObjectChange.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ObjectChange {

    /**
     * Checks if is changed.
     *
     * @param obj the obj
     * @return true, if is changed
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    boolean isChanged(String obj) throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Gets the latest info.
     *
     * @return the latest info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    Object getLatestInfo() throws DatabaseCriticalException, DatabaseOperationException;

    /**
     * Handle change.
     *
     * @param obj the obj
     */
    void handleChange(String obj);

}
