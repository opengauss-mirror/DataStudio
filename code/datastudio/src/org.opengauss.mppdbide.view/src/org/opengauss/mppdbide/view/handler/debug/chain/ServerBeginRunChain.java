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

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg;
import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg.State;
import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.exception.DebugPositionNotFoundException;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.debug.DebugServiceHelper;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class ServerBeginRunChain extends IMsgChain {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_BEGIN;
    }

    @Override
    protected void disposeMsg(Event event) {
        Object additionObj = event.getAddition().orElse(null);
        if (additionObj != null &&
                additionObj instanceof DebugAddtionMsg) {
            DebugAddtionMsg msg = (DebugAddtionMsg) additionObj;
            if (msg.getState() == State.END && !event.hasException()) {
                Display.getDefault().syncExec(new UpdateDebugPositionTask(getCurLine()));
            }
        }
    }

    private int getCurLine() {
        SourceCodeService codeService = serviceHelper.getCodeService();
        try {
            return codeService.getBeginDebugCodeLine();
        } catch (DebugPositionNotFoundException debugExp) {
            MPPDBIDELoggerUtility.error("receive invalid position:" + debugExp.toString());
        }
        return -1;
    }
}
