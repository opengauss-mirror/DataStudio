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

/**
 * Title: VersionVo for use
 *
 * @since 3.0.0
 */
public class VersionVo {
    /**
     * this is version str
     */
    @DumpFiled
    public String serverversionstr;

    /**
     * this is version number
     */
    @DumpFiled
    public Integer serverversionnum;

    /**
     * this is proxy api version
     */
    @DumpFiled
    public Integer proxyapiver;

    /**
     * this is server process id
     */
    @DumpFiled
    public Long serverprocessid;
}
