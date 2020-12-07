/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.vo;

import com.huawei.mppdbide.debuger.annotation.DumpFiled;
import org.postgresql.jdbc.PgArray;
import org.postgresql.util.PGobject;

import java.util.List;

/**
 * Title: the FunctionVo class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/16]
 * @since 2020/11/16
 */
public class FunctionVo {
    @DumpFiled
    public Long oid;
    @DumpFiled
    public String proname;
    @DumpFiled
    public Long prorettype;
    @DumpFiled
    public Boolean proretset;
    @DumpFiled
    public Integer pronargs;
    @DumpFiled
    public Integer pronargdefaults;
    @DumpFiled
    public PGobject proargtypes;
    @DumpFiled
    public PgArray proallargtypes;
    @DumpFiled
    public PgArray proargmodes;
    @DumpFiled
    public PgArray proargnames;
    @DumpFiled
    public PGobject proargdefaults;
    @DumpFiled
    public PGobject prodefaultargpos;
    @DumpFiled
    public String prosrc;

    // all param desc
    public Boolean allInputParam;
    public List<ParamVo> allParam;
}
