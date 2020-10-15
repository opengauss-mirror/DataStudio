/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.functionchange;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;

/**
 * 
 * Title: ExecuteWrapper
 * 
 * Description:ExecuteWrapper
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 15 Oct, 2019]
 * @since 15 Oct, 2019
 */
public interface ExecuteWrapper {

    /**
     * Handle execute.
     *
     * @param event the event
     * @throws DatabaseCriticalException the database critical exception
     */
    void handleExecute(ObjectChangeEvent event) throws DatabaseCriticalException;

    /**
     * Handle exception.
     *
     * @param e the e
     * @param event the event
     */
    void handleException(Throwable e, ObjectChangeEvent event);

}
