/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.chain.BreakpointChain;
import com.huawei.mppdbide.view.handler.debug.chain.ClearChain;
import com.huawei.mppdbide.view.handler.debug.chain.RecongnizeChain;
import com.huawei.mppdbide.view.handler.debug.chain.RefreshChain;

/**
 * Title: class
 * Description: The Class UiEventHandler.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class UiEventHandler implements EventHander {
    private IMsgChain disposeChain = null;

    /**
     * Instantiates a new ui event handler.
     */
    public UiEventHandler() {
        disposeChain = new RecongnizeChain();
        IMsgChain refreshChain = new RefreshChain();
        disposeChain.setNext(refreshChain);
        IMsgChain breakpointChain = new BreakpointChain();
        refreshChain.setNext(breakpointChain);
        IMsgChain clearChain = new ClearChain();
        breakpointChain.setNext(clearChain);
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        MPPDBIDELoggerUtility.error("Event:" + event);
        disposeChain.handleMsg(event);
    }
}