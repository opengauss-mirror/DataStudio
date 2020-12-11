/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.exception;

/**
 * Title: the DebugPositionNotFoundException class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/21]
 * @since 2020/11/21
 */
public class DebugPositionNotFoundException extends Exception {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 611011583720487538L;
    private static final String DEBUG_POSITION_NOT_FOUND = "debug_position_not_found";
    public DebugPositionNotFoundException() {
        super(DEBUG_POSITION_NOT_FOUND);
    }
}
