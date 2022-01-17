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

package com.huawei.mppdbide.debuger.event;

import java.util.Locale;
import java.util.Optional;

import com.huawei.mppdbide.debuger.vo.PositionVo;

/**
 * Title: DebugAddtionMsg for use
 *
 * @since 3.0.0
 */
public class DebugAddtionMsg {
    /**
     *
     * Title: State for use
     */
    public static enum State {
        START,
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

    /**
     * description: get stateu
     *
     * @return State get state
     */
    public State getState() {
        return state;
    }

    /**
     * description: get position vo
     *
     * @return Optional<PositionVo> the position vo
     */
    public Optional<PositionVo> getPositionVo() {
        return Optional.ofNullable(positionVo);
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "DebugAdditionMsg{state: %s, position: %s}",
                this.state.toString(),
                positionVo == null ? "" : positionVo.formatSelf());
    }
}
