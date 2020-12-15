/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.chain.ServerBeginRunChain;
import com.huawei.mppdbide.view.handler.debug.chain.ServerBreakPointChain;
import com.huawei.mppdbide.view.handler.debug.chain.ServerEndRunChain;
import com.huawei.mppdbide.view.handler.debug.chain.ServerExitChain;
import com.huawei.mppdbide.view.handler.debug.chain.ServerRunStepChain;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 08,12,2020]
 * @since 08,12,2020
 */
public class DebugEventHandler implements EventHander {
    private IMsgChain disposeChain = null;

    public DebugEventHandler() {
        disposeChain = new ServerBeginRunChain();

        IMsgChain stepRun = new ServerRunStepChain();
        disposeChain.setNext(stepRun);

        IMsgChain serviceExit = new ServerExitChain();
        stepRun.setNext(serviceExit);

        IMsgChain serviceEnd = new ServerEndRunChain();
        serviceExit.setNext(serviceEnd);

        IMsgChain serveiceBreakPoint = new ServerBreakPointChain();
        serviceEnd.setNext(serveiceBreakPoint);
    }

    @Override
    public void handleEvent(Event event) {
        MPPDBIDELoggerUtility.error("Event:" + event);
        disposeChain.handleMsg(event);
    }
}
