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

import org.opengauss.mppdbide.debuger.annotation.DumpFiled;

/**
 * Title: the InfoCodeVo class
 *
 * @since 3.0.0
 */
public class InfoCodeVo {
    /**
     * lineno of function
     */
    @DumpFiled
    public Integer lineno;

    /**
     * query of function
     */
    @DumpFiled
    public String query;

    /**
     * canbreak of function
     */
    @DumpFiled
    public boolean canbreak;

    /**
     * get lineno
     *
     * @return the lineno
     */
    public Integer getLineno() {
        return lineno;
    }

    /**
     * get query
     *
     * @return the sourceCode
     */
    public String getQuery() {
        return query;
    }
}
