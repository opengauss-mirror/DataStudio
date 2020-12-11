/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

import java.util.Locale;

/**
 * Title: the VariableVo class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/19]
 * @since 2020/11/19
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
