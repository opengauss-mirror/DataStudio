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

package org.opengauss.mppdbide.debuger.vo.dbe;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title: the InfoCodeVo class
 *
 * @since 3.0.0
 */
public class ExportParamVo {
    /**
     * oid of function
     */
    public Long oid;

    /**
     * index of function
     */
    public String index;

    /**
     * executeSql of function
     */
    public Map<Integer, String> executeSql;

    /**
     * remarkLines of function
     */
    public Set<String> remarkLines;

    /**
     * coveragePassLines of function
     */
    public Set<String> coveragePassLines;

    /**
     * list of function
     */
    public List<String> list;

    /**
     * html of function
     */
    public String html;

    /**
     * canBreakLine of function
     */
    public String canBreakLine;
}
