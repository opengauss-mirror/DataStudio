/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class NoNeedToRefreshException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class NoNeedToRefreshException extends MPPDBIDEException {
    private static final long serialVersionUID = 5816189949561990799L;

    /**
     * Instantiates a new no need to refresh exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public NoNeedToRefreshException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);

    }
}
