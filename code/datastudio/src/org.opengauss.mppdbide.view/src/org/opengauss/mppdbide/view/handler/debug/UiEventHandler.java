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

package org.opengauss.mppdbide.view.handler.debug;

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.EventHander;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.debug.chain.BreakpointChain;
import org.opengauss.mppdbide.view.handler.debug.chain.ClearChain;
import org.opengauss.mppdbide.view.handler.debug.chain.RecongnizeChain;
import org.opengauss.mppdbide.view.handler.debug.chain.RefreshChain;

/**
 * Title: class
 * Description: The Class UiEventHandler.
 *
 * @since 3.0.0
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