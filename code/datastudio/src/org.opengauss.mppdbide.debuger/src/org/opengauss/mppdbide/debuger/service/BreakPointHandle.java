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

import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.dbe.InfoBreakPointsVo;

/**
 * Description: BreakPointHandle
 *
 * @since 3.0.0
 */
public class BreakPointHandle implements IQueryResConvertService {
    /**
     * covertList
     *
     * @param rs  the rs
     * @param <T>
     * @return the return value
     * @throws SQLException the SQL exception
     */
    @Override
    public <T> List<T> covertList(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<T>();
        List<InfoBreakPointsVo> infos = ParseVo.parseList(rs, InfoBreakPointsVo.class);
        infos.forEach(item -> {
            PositionVo vo = new PositionVo();
            vo.func = item.funcoid;
            vo.linenumber = item.lineno;
            vo.breakpointno = item.breakpointno;
            list.add((T) vo);
        });
        return list;
    }
}
