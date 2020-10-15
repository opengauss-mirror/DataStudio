/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils.exceptions;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableImporExportException.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class TableImporExportException extends MPPDBIDEException {

    private static final long serialVersionUID = 6331637509114059067L;

    /**
     * Instantiates a new table impor export exception.
     *
     * @param dbErrorMessageCode the db error message code
     */
    public TableImporExportException(String dbErrorMessageCode) {
        super(dbErrorMessageCode);
    }

}
