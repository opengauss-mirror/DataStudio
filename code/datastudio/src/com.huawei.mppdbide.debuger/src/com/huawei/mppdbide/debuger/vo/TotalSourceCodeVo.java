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

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

/**
 * Title: the TotalSourceCodeVo class
 *
 * @since 3.0.0
 */
public class TotalSourceCodeVo {
    /**
     * headerlines of total source code
     */
    @DumpFiled
    public Integer headerlines;

    /**
     * definition of total source code
     */
    @DumpFiled
    public String definition;

    /**
     * description: get source code
     *
     * @return String source code
     */
    public String getSourceCode() {
        return definition.replaceAll("\\$function\\$", "\\$\\$") + "/";
    }

    /**
     * description: get head lines
     *
     * @return int head lines
     */
    public int getHeadlines() {
        return headerlines;
    }
}
