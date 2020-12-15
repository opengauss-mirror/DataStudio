/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.chain;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateStackVariable;

/**
 * Title: class
 * Description: The Class RefreshChain.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
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
        if (!(event.getAddition().get() instanceof DebugAddtionMsg)) {
            return;
        }
        DebugAddtionMsg msg = (DebugAddtionMsg) event.getAddition().get();
        if (msg.getState() == State.END && !event.hasException()) {
            Display.getDefault().syncExec(new UpdateStackVariable());
        }
    }
}