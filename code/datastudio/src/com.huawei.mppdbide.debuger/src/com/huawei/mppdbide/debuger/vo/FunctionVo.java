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
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

/**
 * Title: the FunctionVo class
 *
 * @since 3.0.0
 */
public class FunctionVo {
    /**
     *  oid of function
     */
    @DumpFiled
    public Long oid;

    /**
     *  proname of function
     */
    @DumpFiled
    public String proname;

    /**
     *  prorettype of function
     */
    @DumpFiled
    public Long prorettype;

    /**
     *  proretset of function
     */
    @DumpFiled
    public Boolean proretset;

    /**
     *  pronargs of function
     */
    @DumpFiled
    public Integer pronargs;

    /**
     *  pronargdefaults of function
     */
    @DumpFiled
    public Integer pronargdefaults;

    /**
     *  proargtypes of function
     */
    @DumpFiled
    public PGobject proargtypes;

    /**
     *  proallargtypes of function
     */
    @DumpFiled
    public PgArray proallargtypes;

    /**
     *  proargmodes of function
     */
    @DumpFiled
    public PgArray proargmodes;

    /**
     *  proargnames of function
     */
    @DumpFiled
    public PgArray proargnames;

    /**
     *  proargdefaults of function
     */
    @DumpFiled
    public PGobject proargdefaults;

    /**
     *  prodefaultargpos of function
     */
    @DumpFiled
    public PGobject prodefaultargpos;

    /**
     *  prosrc of function
     */
    @DumpFiled
    public String prosrc;
}
