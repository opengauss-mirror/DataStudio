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

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * the class VariableRunLine
 *
 * @since 3.0.0
 */
public class VariableRunLine {
    /**
     * runList of function
     */
    public static volatile List<String> runList = new CopyOnWriteArrayList<>();

    /**
     * passLine of function
     */
    public static volatile Set<Integer> passLine = new CopyOnWriteArraySet<Integer>();

    /**
     * isTerminate of function
     */
    public static volatile boolean isTerminate = true;

    /**
     * isContinue of function
     */
    public static volatile Boolean isContinue;

    /**
     * isPldebugger of function
     */
    public static volatile Boolean isPldebugger;
}
