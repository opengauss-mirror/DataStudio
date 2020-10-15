/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.grid;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.observer.IDSListenable;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDSGridDataAccessListenable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDSGridDataAccessListenable extends IDSListenable {
    int LISTEN_TYPE_POST_FETCH = 1;
    int LISTEN_TYPE_ENDOF_RS = 2;
    int LISTEN_TYPE_ABOUTTO_REEXECUTE_QUERY = 3;

    /** 
     * visits the InputValues
     * 
     * @param dp the dp
     * @param index the index
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    void visitInputValues(DefaultParameter dp, int index) throws DatabaseOperationException, DatabaseCriticalException;

}
