/*
 * Copyright: Huawei Technologies Co., Ltd. Copyright 2012-2019, All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.exception;

/**
 * Title: GaussDBSQLParserException
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 02-Dec-2019]
 * @since 02-Dec-2019
 */

public class GaussDBSQLParserException extends RuntimeException {

    private static final long serialVersionUID = -2820181785972339647L;

    public GaussDBSQLParserException() {
        super();
    }

    public GaussDBSQLParserException(String message) {
        super(message);
    }

}
