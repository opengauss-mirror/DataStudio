/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

import java.util.Locale;

/**
 * Title: the PositionVo class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/20]
 * @since 2020/11/20
 */
public class PositionVo {
    @DumpFiled
    public Long func;

    @DumpFiled
    public Integer linenumber;

    @DumpFiled
    public String targetname;

    public PositionVo() {
        this(null, null, null);
    }
    public PositionVo(Long func, Integer linenumber, String targetname) {
        this.func = func;
        this.linenumber = linenumber;
        this.targetname = targetname;
    }

    public static final String FORMAT = "%8s %20s";
    public static String title() {
        return String.format(Locale.ENGLISH, FORMAT, "linenum", "funcoid");
    }

    public String formatSelf() {
        return String.format(Locale.ENGLISH, FORMAT, linenumber, func);
    }
}
