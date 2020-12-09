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
    // name linenumber value dtype isnotnull
    public static final String FORMAT = "%40s %6s %20s %6s %7s";

    @DumpFiled
    public String name;

    @DumpFiled
    public String varclass;

    @DumpFiled
    public Integer linenumber;

    @DumpFiled
    public Boolean isunique;

    @DumpFiled
    public Boolean isconst;

    @DumpFiled
    public Boolean isnotnull;

    @DumpFiled
    public Long dtype;

    @DumpFiled
    public Object value;

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "VariableVo(name %s : value: %s)", name, value);
    }

    public static String title() {
        return String.format(Locale.ENGLISH, FORMAT, "name", "line", "value", "type", "isNull");
    }

    public String formatSelf() {
        String newName = name.length() > 40 ? name.substring(0, 40) : name;
        return String.format(Locale.ENGLISH, FORMAT, newName, linenumber, value, dtype, isnotnull ? "f" : "t");
    }

}
