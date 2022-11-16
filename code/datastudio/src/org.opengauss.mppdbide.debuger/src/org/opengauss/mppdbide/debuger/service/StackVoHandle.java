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

package org.opengauss.mppdbide.debuger.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.dbe.BackTraceVo;
import org.opengauss.mppdbide.utils.VariableRunLine;

/**
 * Description: StackVoHandle
 *
 * @since 3.0.0
 */
public class StackVoHandle implements IQueryResConvertService {
    /**
     * covertList
     *
     * @param rs  the rs
     * @param <T> the type
     * @return the return value
     * @throws SQLException the SQL exception
     */
    @Override
    public <T> List<T> covertList(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<T>();
        List<BackTraceVo> infos = ParseVo.parseList(rs, BackTraceVo.class);
        infos.forEach(item -> {
            /**
             * If the step in function is later adapted, 
             * the parameter type obtained here may not be obtained from the inner function parameter type
             */
            List<String> paramTypes = DbeDebugService.paramType.get(VariableRunLine.currentOid);
            StackVo vo = new StackVo();
            vo.func = item.funcoid;
            vo.linenumber = item.lineno;
            vo.level = item.frameno;
            vo.targetname = String.format(Locale.ENGLISH, "%s(%s)", item.funcname, StringUtils.join(paramTypes,','));
            list.add((T) vo);
        });
        return list;
    }
}
