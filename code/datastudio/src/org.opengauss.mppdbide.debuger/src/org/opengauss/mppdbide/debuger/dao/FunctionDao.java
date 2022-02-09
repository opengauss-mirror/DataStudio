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

package org.opengauss.mppdbide.debuger.dao;

import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;

import java.sql.ResultSet;
import java.util.Locale;

/**
 * Title: the FunctionDao class
 *
 * @since 3.0.0
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
