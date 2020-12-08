/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.event;

import java.util.Locale;
import java.util.Optional;

import com.huawei.mppdbide.debuger.vo.PositionVo;

/**
 *
 * Title: DebugAddtionMsg for use
 *
 * Description: 
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
 */
public class DebugAddtionMsg {
    public static enum State {
        START,
        HAS_ERROR,
        END;
    }
    private State state;
    private PositionVo positionVo;
    public DebugAddtionMsg(State state) {
        this(state, null);
    }
    public DebugAddtionMsg(State state, PositionVo positionVo) {
        this.state = state;
        this.positionVo = positionVo;
    }
    
    public State getState() {
        return state;
    }
    
    public Optional<PositionVo> getPositionVo() {
        return Optional.ofNullable(positionVo);
    }
    
    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DebugAdditionMsg{state: %s, position: %s}",
                this.state.toString(),
                positionVo == null? "": positionVo.formatSelf());
    }
}
