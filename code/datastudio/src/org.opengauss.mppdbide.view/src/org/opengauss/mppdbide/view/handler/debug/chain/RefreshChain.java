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

package org.opengauss.mppdbide.view.handler.debug.chain;

import java.util.Optional;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg;
import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg.State;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateStackVariable;

/**
 * Title: class
 * Description: The Class RefreshChain.
 *
 * @since 3.0.0
 */
public class RefreshChain extends IMsgChain {
    /**
     * Match message.
     *
     * @param event the event
     * @return boolean, true if successful
     */
    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_RUN;
    }

    /**
     * Dispose message.
     *
     * @param event the event
     */
    @Override
    protected void disposeMsg(Event event) {
        Optional<Object> optional = event.getAddition();
        if (!optional.isPresent()) {
            return;
        }
        Object eventObject = optional.get();
        if (!(eventObject instanceof DebugAddtionMsg)) {
            return;
        }
        DebugAddtionMsg msg = (DebugAddtionMsg) eventObject;
        if (msg.getState() == State.END && !event.hasException()) {
            Display.getDefault().syncExec(new UpdateStackVariable());
        }
    }
}