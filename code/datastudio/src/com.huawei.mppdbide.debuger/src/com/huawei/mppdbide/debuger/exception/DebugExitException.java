/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.exception;

/**
 * Title: the DebugExitException class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class DebugExitException extends Exception {
    /**
     *  description of exit exception
     */
    public static final String DEBUG_EXIT = "debug_exit";

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6693123333489198245L;

    public DebugExitException() {
        super(DEBUG_EXIT);
    }
}