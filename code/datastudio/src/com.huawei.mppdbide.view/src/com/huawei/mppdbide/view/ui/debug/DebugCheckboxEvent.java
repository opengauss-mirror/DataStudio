/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.huawei.mppdbide.view.ui.debug;

/**
 * Title: DebugCheckboxEvent for use
 * Description: the event enum
 *
 * @since 3.0.0
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