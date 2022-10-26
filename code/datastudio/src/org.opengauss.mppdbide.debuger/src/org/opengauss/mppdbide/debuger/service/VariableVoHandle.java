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
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;
import org.opengauss.mppdbide.debuger.vo.dbe.VariablesVo;

/**
 * Description: VariableVoHandle
 *
 * @since 3.0.0
 */
public class VariableVoHandle implements IQueryResConvertService {
    /**
     * covertList
     *
     * @param rs  the rs
     * @param <T>
     * @return the return value
     */
    @Override
    public <T> List<T> covertList(ResultSet rs) {
        List<T> list = new ArrayList<T>();
        VariablesVo item = ParseVo.parse(rs, VariablesVo.class);
        VariableVo vo = new VariableVo();
        vo.name = item.getVarname();
        vo.isconst = item.getIsConst();
        vo.value = item.getValue();
        vo.dtype = item.getDtype();
        list.add((T) vo);
        return list;
    }
}
