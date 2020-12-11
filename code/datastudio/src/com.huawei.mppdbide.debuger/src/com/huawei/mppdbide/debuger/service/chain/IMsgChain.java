/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;

/**
 * Title: IMsgChain for use
 * Description: IMsgChain is base chain of Responsibility
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public abstract class IMsgChain {
    /**
     *  next chain
     */
    protected IMsgChain msgChain = null;

    /**
     * get next chain
     *
     * @return IMsgChain get next chain
     */
    public IMsgChain getNext() {
        return this.msgChain;
    }

    /**
     * set next chain
     *
     * @param msgChain next chain to set
     * @return void no return value
     */
    public void setNext(IMsgChain msgChain) {
        this.msgChain = msgChain;
    }

    /**
     * if event matched, than this chain will dispose it
     *
     * @param event event to dispose
     * @return boolean true if matched
     */
    public abstract boolean matchMsg(Event event);

    /**
     * dispose Event msg
     *
     * @param event event to dispose
     * @return void
     */
    protected abstract void disposeMsg(Event event);

    /**
     * handleMsg by chain
     *
     * @param event event to dispose
     * @return void
     */
    public void handleMsg(Event event) {
        if (matchMsg(event)) {
            disposeMsg(event);
        } else {
            if (getNext() != null) {
                getNext().handleMsg(event);
            }
        }
    }
}
