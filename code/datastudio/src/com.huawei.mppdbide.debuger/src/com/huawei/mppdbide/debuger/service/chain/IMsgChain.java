/**
 * 
 */
package com.huawei.mppdbide.debuger.service.chain;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.DebugService;

/**
 * @author z00588921
 *
 */
public abstract class IMsgChain {
    protected IMsgChain msgChain = null;
    protected DebugService debugService;
    public IMsgChain(DebugService debugService) {
        this.debugService = debugService;
    }
    public IMsgChain getNext() {
        return this.msgChain;
    }
    void setNext(IMsgChain msgChain) {
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
