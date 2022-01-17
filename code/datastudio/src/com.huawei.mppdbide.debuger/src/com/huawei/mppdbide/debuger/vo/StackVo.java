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

import java.util.Locale;

/**
 * Title: the StackVo class
 *
 * @since 3.0.0
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
