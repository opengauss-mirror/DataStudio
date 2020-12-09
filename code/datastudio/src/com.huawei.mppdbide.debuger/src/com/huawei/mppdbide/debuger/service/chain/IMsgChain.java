/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;

/**
 * 
 * Title: IMsgChain for use
 * 
 * Description: IMsgChain is base chain of Responsibility
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public abstract class IMsgChain {
    protected IMsgChain msgChain = null;
    public IMsgChain getNext() {
        return this.msgChain;
    }
    public void setNext(IMsgChain msgChain) {
        this.msgChain = msgChain;
    }

    public abstract boolean matchMsg(Event event);
    protected abstract void disposeMsg(Event event);

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
