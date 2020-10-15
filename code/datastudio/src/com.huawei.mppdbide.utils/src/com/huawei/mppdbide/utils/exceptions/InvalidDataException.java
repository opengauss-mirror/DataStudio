/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * Title: InvalidDataException
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 18-Apr-2020]
 * @since 18-Apr-2020
 */

public class InvalidDataException extends MPPDBIDEException {
    private static final long serialVersionUID = 1L;

    /** 
     * InvalidDataException exception
     * 
     * @param dbErrorMessageCode error message
     */
    public InvalidDataException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
    }

}
