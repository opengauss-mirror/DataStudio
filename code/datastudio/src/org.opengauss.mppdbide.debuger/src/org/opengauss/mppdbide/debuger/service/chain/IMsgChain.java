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

package org.opengauss.mppdbide.debuger.service.chain;

import org.opengauss.mppdbide.debuger.event.Event;

/**
 * Title: IMsgChain for use
 * Description: IMsgChain is base chain of Responsibility
 *
 * @since 3.0.0
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
