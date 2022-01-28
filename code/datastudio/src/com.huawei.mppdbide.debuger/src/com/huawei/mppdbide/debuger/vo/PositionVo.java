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
 * Title: the PositionVo class
 *
 * @since 3.0.0
 */
public class PositionVo {
    /**
     *  the format of this vo
     */
    public static final String FORMAT = "%8s %20s";

    /**
     *  func of positionvo
     */
    @DumpFiled
    public Long func;

    /**
     *  linenumber of positionvo
     */
    @DumpFiled
    public Integer linenumber;

    /**
     *  targetname of positionvo
     */
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

    /**
     * format this vo title
     *
     * @return String the title
     */
    public static String title() {
        return String.format(Locale.ENGLISH, FORMAT, "linenum", "funcoid");
    }

    /**
     * format self
     *
     * @return String the self
     */
    public String formatSelf() {
        return String.format(Locale.ENGLISH, FORMAT, linenumber, func);
    }
}
