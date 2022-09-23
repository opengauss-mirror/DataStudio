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

import java.util.stream.Stream;

import org.opengauss.mppdbide.debuger.vo.BreakPointListVo;
import org.opengauss.mppdbide.debuger.vo.BreakpointVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;

/**
 * Description: ConvertVoEnum
 *
 * @since 3.0.0
 */
public enum ConvertVoEnum {
    BREAK_POINT_LIST("BreakPointListVo", BreakPointListVo.class),
    BREAK_POINT("BreakpointVo", BreakpointVo.class),
    STACK("StackVo", StackVo.class),
    VARIABLE("VariableVo", VariableVo.class);

    private String name;
    private Class<?> clazz;

    ConvertVoEnum(String name, Class<?> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * getType
     *
     * @param clazz the class
     * @return the ConvertVoEnum
     */
    public static ConvertVoEnum getType(Class<?> clazz) {
        return Stream.of(ConvertVoEnum.values())
                .filter(item -> clazz.getSimpleName().equalsIgnoreCase(item.clazz.getSimpleName())).findFirst().get();
    }
}
