/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser;

/**
 * Title: SQLDDLEnum Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 23-Aug-2019]
 * @since 23-Aug-2019
 */
public enum SQLDDLTypeEnum {

    /**
     * The procedure.
     */
    PROCEDURE,

    /**
     * The function.
     */
    FUNCTION,

    PACKAGE,

    /**
     * The table.
     */
    TABLE,

    /**
     * The view.
     */
    VIEW,

    /**
     * The trigger.
     */
    TRIGGER,

    /**
     * The unknown.
     */
    UNKNOWN
}
