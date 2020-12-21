/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.dao;

import com.huawei.mppdbide.debuger.annotation.ParseVo;
import com.huawei.mppdbide.debuger.vo.FunctionVo;

import java.sql.ResultSet;
import java.util.Locale;

/**
 * Title: the FunctionDao class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/17]
 * @since 2020/11/17
 */
public class FunctionDao {
    /**
     * query function detail by proname
     *
     * @param proname name of function
     * @return String query sql
     */
    public String getSql(String proname) {
        String sql = "select oid, proname, proretset, prorettype, "
                    + "pronargs, pronargdefaults, proargtypes, proallargtypes,"
                    + "proargmodes, proargnames, proargdefaults, "
                    + "prodefaultargpos, prosrc from pg_proc where proname = ";
        return String.format(Locale.ENGLISH, "%s \'%s\'", sql, proname);
    }

    /**
     * parse ResultSet to FunctionVo object
     *
     * @param rs the sql query result
     * @return FunctionVo the result
     */
    public FunctionVo parse(ResultSet rs) {
        return ParseVo.parse(rs, FunctionVo.class);
    }
}