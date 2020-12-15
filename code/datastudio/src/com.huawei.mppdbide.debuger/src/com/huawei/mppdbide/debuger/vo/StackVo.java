/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;

import java.util.Locale;

/**
 * Title: the StackVo class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/19]
 * @since 2020/11/19
 */
public class StackVo {
    /**
     *  level of stack
     */
    @DumpFiled
    public Integer level;

    /**
     *  targetname of stack
     */
    @DumpFiled
    public String targetname;

    /**
     *  func of stack
     */
    @DumpFiled
    public Long func;

    /**
     *  linenumbe of stack
     */
    @DumpFiled
    public Integer linenumber;

    /**
     *  args of stack
     */
    @DumpFiled
    public Object args;

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                "StackVo(level %s : targetname %s)",
                level,
                targetname);
    }
}
