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

package com.huawei.mppdbide.debuger.debug;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the DebugState class
 *
 * @since 3.0.0
 */
public class DebugState {
    /**
     * Title: state of debuger
     */
    public static enum State {
        UNKNOWN(-1),
        PREPARED(0),
        RUNNING(1),
        ATTACHED(2),
        STOP(3),
        TERMINALED(4);

        /**
         *  state to description
         */
        public final int state;

        State(int state) {
            this.state = state;
        }
    }

    private State state = State.UNKNOWN;
    private boolean stateLocked = false;

    /**
     * set prepared state
     *
     * @return void
     */
    public void prepared() {
        setState(State.PREPARED);
    }

    /**
     * set running state
     *
     * @return void
     */
    public void running() {
        setState(State.RUNNING);
    }

    /**
     * set stop state
     *
     * @return void
     */
    public void stop() {
        setState(State.STOP);
    }

    /**
     * set ternimaled state
     *
     * @return void
     */
    public void terminaled() {
        setState(State.TERMINALED);
    }

    /**
     * set attached state
     *
     * @return void
     */
    public void attached() {
        setState(State.ATTACHED);
    }

    /**
     * query is running
     *
     * @return boolean true if running
     */
    public boolean isRunning() {
        return this.state == State.RUNNING;
    }

    /**
     * query is stopped
     *
     * @return boolean true if stopped
     */
    public boolean isStopped() {
        return state == State.STOP || state == State.TERMINALED;
    }

    /**
     * query is normal stopped
     *
     * @return boolean true if normal stopped
     */
    public boolean isNormalStopped() {
        return state == State.STOP;
    }

    /**
     * lock state, and can't modify state after this
     *
     * @return void no ret
     */
    public void stateLocked() {
        this.stateLocked = true;
    }

    /**
     * query if state locked
     *
     * @return boolean true if locked
     */
    public boolean getLockState() {
        return this.stateLocked;
    }

    /**
     * query cur state
     *
     * @return State cur state
     */
    public State getState() {
        return this.state;
    }

    /**
     * set cur state
     *
     * @param state cur state to set
     * @return void no ret
     */
    public void setState(State state) {
        if (state == getState()) {
            return;
        }

        if (!getLockState()) {
            this.state = state;
            return;
        } else {
            MPPDBIDELoggerUtility.warn("not allow modify state!");
        }
    }
}
