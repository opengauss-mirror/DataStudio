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

package org.opengauss.mppdbide.common;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opengauss.mppdbide.debuger.service.BreakPointHandle;
import org.opengauss.mppdbide.debuger.service.BreakPointListHandle;
import org.opengauss.mppdbide.debuger.service.IQueryResConvertService;
import org.opengauss.mppdbide.debuger.service.StackVoHandle;
import org.opengauss.mppdbide.debuger.service.VariableVoHandle;

/**
 * Description: ConvertHandleUtil
 *
 * @since 3.0.0
 */
public class ConvertHandleUtil {
    private static Map<ConvertVoEnum, IQueryResConvertService> strategiesDictionary =
            new ConcurrentHashMap<ConvertVoEnum, IQueryResConvertService>();

    static {
        strategiesDictionary.put(ConvertVoEnum.BREAK_POINT, new BreakPointHandle());
        strategiesDictionary.put(ConvertVoEnum.BREAK_POINT_LIST, new BreakPointListHandle());
        strategiesDictionary.put(ConvertVoEnum.STACK, new StackVoHandle());
        strategiesDictionary.put(ConvertVoEnum.VARIABLE, new VariableVoHandle());
    }

    /**
     * process
     *
     * @param type the type
     * @param rs   the rs
     * @param <T>  the generic type
     * @return the return value
     * @throws SQLException the exception
     */
    public static <T> List<T> process(ConvertVoEnum type, ResultSet rs) throws SQLException {
        return strategiesDictionary.get(type).covertList(rs);
    }
}
