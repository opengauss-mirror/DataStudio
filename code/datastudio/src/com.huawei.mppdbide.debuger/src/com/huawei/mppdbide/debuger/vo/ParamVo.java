/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.vo;

/**
 * Title: the ParamVo class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/17]
 * @since 2020/11/17
 */
public class ParamVo {
    public static enum MODE {
        IN('i'),
        OUT('o'),
        INOUT('b'),
        VARIADIC('v');
        public final Character character;
        MODE(Character character) {
            this.character = character;
        }
    };
    public String paramName;
    public int paramIdx;
    public Long paramType;
    public String paramDefault;
    public String paramValue;
    public MODE paramMode;
}
