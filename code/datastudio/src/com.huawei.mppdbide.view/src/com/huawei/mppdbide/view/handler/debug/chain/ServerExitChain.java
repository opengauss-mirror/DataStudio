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
 *
 * @since 3.0.0
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
