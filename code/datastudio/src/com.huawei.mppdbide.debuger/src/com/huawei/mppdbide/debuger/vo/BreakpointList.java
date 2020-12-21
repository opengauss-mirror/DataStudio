/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Title: class
 * Description: The Class BreakpointList.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
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