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

package org.opengauss.mppdbide.debuger.vo;

import org.opengauss.mppdbide.debuger.annotation.DumpFiled;

import java.util.Locale;

/**
 * Title: the VariableVo class
 *
 * @since 3.0.0
 */
public class VariableVo {
    /**
     * name linenumber value dtype isnotnull
     */
    public static final String FORMAT = "%40s %6s %20s %6s %7s";

    /**
     * name of variable
     */
    @DumpFiled
    public String name;

    /**
     * var class of variable
     */
    @DumpFiled
    public String varclass;

    /**
     * linenumber of variable
     */
    @DumpFiled
    public Integer linenumber;

    /**
     * is unique of variable
     */
    @DumpFiled
    public Boolean isunique;

    /**
     * is const of variable
     */
    @DumpFiled
    public Boolean isconst;

    /**
     * is not null of variable
     */
    @DumpFiled
    public Boolean isnotnull;

    /**
     * dtype of variable
     */
    @DumpFiled
    public Long dtype;

    /**
     * value of variable
     */
    @DumpFiled
    public Object value;

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "VariableVo(name %s : value: %s)", name, value);
    }

    /**
     * description:title of variable
     *
     * @return String title of variable
     */
    public static String title() {
        return String.format(Locale.ENGLISH, FORMAT, "name", "line", "value", "type", "isNull");
    }

    /**
     * description: format variable self
     *
     * @return String formated result
     */
    public String formatSelf() {
        String newName = name.length() > 40 ? name.substring(0, 40) : name;
        return String.format(Locale.ENGLISH, FORMAT, newName, linenumber, value, dtype, isnotnull ? "f" : "t");
    }

}
