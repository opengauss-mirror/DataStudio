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

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ObjectChange.
 * 
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
