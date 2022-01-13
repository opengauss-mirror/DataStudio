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

package com.huawei.mppdbide.debuger.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: class
 * Description: The Class BreakpointList.
 *
 * @since 3.0.0
 */
public class BreakpointList {
    private static Map<Integer, BreakpointVo> breakpointList;

    private BreakpointList() {
    }

    /**
     * Gets the instance of BreakpointList.
     *
     * @return Map<Integer, BreakpointVo> the instance of BreakpointList
     */
    public static Map<Integer, BreakpointVo> getInstance() {
        return breakpointList;
    }

    /**
     * Initials the instance of BreakpointList.
     */
    public static void initialInstance() {
        breakpointList = new HashMap<Integer, BreakpointVo>();
    }

    /**
     * Resets the instance of BreakpointList.
     */
    public static void resetInstance() {
        breakpointList = null;
    }

    /**
     * Sets the instance of BreakpointList.
     *
     * @param breakpoint the breakpoint
     */
    public static void setBreakpointList(Map<Integer, BreakpointVo> breakpoint) {
        breakpointList = breakpoint;
    }
}