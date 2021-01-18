/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.chain;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.DebugHandlerUtils;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateDebugResultTask;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateHighlightLineNumTask;

/**
 * Title: class
 * Description: The Class ServerExitChain.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class ServerExitChain extends IMsgChain {
    private boolean isResultUpdated = false;
    private DebugHandlerUtils debugUtils = DebugHandlerUtils.getInstance();
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.ON_EXIT;
    }

    @Override
    protected void disposeMsg(Event event) {
        if (event.hasException()) {
            MPPDBIDELoggerUtility.error("debug exit with exception:" + event.getException().getMessage());
        } else {
            MPPDBIDELoggerUtility.info("debug already exit: result:" + event.getAddition());
        }
        Display.getDefault().asyncExec(new UpdateDebugPositionTask(-1));
        Display.getDefault().asyncExec(new UpdateHighlightLineNumTask());
        if (!isResultUpdated) {
            isResultUpdated = true;
            Display.getDefault().asyncExec(new UpdateDebugResultTask(event));
            debugUtils.setDebugStart(false);
            serviceHelper.closeService();
        }
    }
}
