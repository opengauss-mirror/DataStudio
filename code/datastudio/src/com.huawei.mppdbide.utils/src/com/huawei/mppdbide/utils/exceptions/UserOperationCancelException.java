/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserOperationCancelException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class UserOperationCancelException extends MPPDBIDEException {

    /**
     * Instantiates a new user operation cancel exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public UserOperationCancelException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
    }

}
