/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

/**
 * Title: the FunctionVo class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/16]
 * @since 2020/11/16
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

    /**
     *  all input param desc
     */
    public Boolean allInputParam;
}
