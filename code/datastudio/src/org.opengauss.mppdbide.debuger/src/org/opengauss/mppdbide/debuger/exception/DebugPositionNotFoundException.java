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

package org.opengauss.mppdbide.debuger.exception;

/**
 * Title: the DebugPositionNotFoundException class
 *
 * @since 3.0.0
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
