/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class FileOperationException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class FileOperationException extends MPPDBIDEException {

    private static final long serialVersionUID = 1026999581597045273L;

    /**
     * Instantiates a new file operation exception.
     *
     * @param fileErrorMessageCode the file error message code
     */
    public FileOperationException(String fileErrorMessageCode) {
        super(fileErrorMessageCode);

    }

}
