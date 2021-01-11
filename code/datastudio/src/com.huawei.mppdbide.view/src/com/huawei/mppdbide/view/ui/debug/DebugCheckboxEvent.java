/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

/**
 * Title: DebugCheckboxEvent for use
 * Description: the event enum
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-01-04]
 * @since 2021-01-04
 */
public enum DebugCheckboxEvent {
    ENABLE(0x02),
    DISABLE(0x03),
    DELETE(0x08),
    DELETE_ALL(0x0C),
    SELECT_ALL(0x40),
    DE_SELECT_ALL(0x60),
    DOUBLE_CLICK(0x10),
    ALL(0xFF);

    /**
     * Code
     */
    private final int code;

    DebugCheckboxEvent(int code) {
        this.code = code;
    }

    /**
     * Gets the code
     *
     *
     * @return int the code
     */
    public int getCode() {
        return this.code;
    }
}