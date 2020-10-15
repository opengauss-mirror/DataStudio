/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * Title: FileCompressException
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author xWX634836
 * @version [DataStudio 6.5.1, Jul 4, 2019]
 * @since Jul 4, 2019
 */

public class FileCompressException extends MPPDBIDEException {
    private static final long serialVersionUID = 1026899581597045271L;

    public FileCompressException(String dbErrorMessageCode, String parsedErrorMessage, Exception exception) {
        super(dbErrorMessageCode, parsedErrorMessage, exception);
    }

}
