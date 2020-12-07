/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.debug;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: the DebugState class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/16]
 * @since 2020/11/16
 */
public class DebugState {
    public static enum State {
        UNKNOWN(-1),
        PREPARED(0),
        RUNNING(1),
        ATTACHED(2),
        STOP(3),
        TERMINALED(4);
        public final int state;
        State(int state) {
            this.state = state;
        }
    }

    private State state = State.UNKNOWN;
    private boolean stateLocked = false;
    public void prepared() {
        setState(State.PREPARED);
    }

    public void running() {
        setState(State.RUNNING);
    }

    public void stop() {
        setState(State.STOP);
    }

    public void ternimaled() {
        setState(State.TERMINALED);
    }

    public void attached() {
        setState(State.ATTACHED);
    }

    public boolean isRunning() {
        return this.state == State.RUNNING;
    }

    public boolean isStopped() {
        return state == State.STOP || state == State.TERMINALED;
    }

    public void stateLocked() {
        this.stateLocked = true;
    }

    public boolean getLockState() {
        return this.stateLocked;
    }

    public State getState() {
        return this.state;
    }

    private void setState(State state) {
        if (state == getState()) {
            return;
        }

        if (!getLockState()) {
            this.state = state;
            return;
        }
        MPPDBIDELoggerUtility.warn("not allow modify state!");
    }
}
