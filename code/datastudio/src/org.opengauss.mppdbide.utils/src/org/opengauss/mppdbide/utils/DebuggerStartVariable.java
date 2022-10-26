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

package org.opengauss.mppdbide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opengauss.mppdbide.utils.vo.DebuggerEndInfoVo;
import org.opengauss.mppdbide.utils.vo.DebuggerStartInfoVo;

/**
 * Title: class
 * Description: The Class DebuggerStartVariable.
 *
 * @since 3.0.0
 */
public class DebuggerStartVariable {
    /**
     * beginInfoMap of function
     */
    public static Map<Long, DebuggerStartInfoVo> beginInfoMap = new HashMap();

    /**
     * endInfoMap of function
     */
    public static Map<Long, List<DebuggerEndInfoVo>> endInfoMap = new HashMap();

    /**
     * getHistoryList of function
     *
     * @param oid the oid
     * @return the value of function
     */
    public static List<DebuggerEndInfoVo> getHistoryList(Long oid) {
        List<DebuggerEndInfoVo> list = endInfoMap.get(oid);
        if (list == null) {
            list = new ArrayList();
            setHistoryList(oid, list);
        }
        return list;
    }

    /**
     * setHistoryList of function
     *
     * @param oid  the oid
     * @param list the list
     */
    public static void setHistoryList(Long oid, List<DebuggerEndInfoVo> list) {
        endInfoMap.put(oid, list);
    }

    /**
     * getStartInfo of function
     *
     * @param oid the oid
     * @return the value of the function
     */
    public static DebuggerStartInfoVo getStartInfo(Long oid) {
        DebuggerStartInfoVo info = beginInfoMap.get(oid);
        if (info == null) {
            info = new DebuggerStartInfoVo();
            setStartInfo(oid, info);
        }
        return info;
    }

    /**
     * setStartInfo of function
     *
     * @param oid  the oid
     * @param info the info
     */
    public static void setStartInfo(Long oid, DebuggerStartInfoVo info) {
        beginInfoMap.put(oid, info);
    }
}
