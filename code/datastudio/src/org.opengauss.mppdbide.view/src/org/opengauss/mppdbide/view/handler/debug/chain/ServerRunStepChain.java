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
import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg.State;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.exception.DebugPositionNotFoundException;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.debug.DebugServiceHelper;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class ServerRunStepChain extends IMsgChain {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_RUN;
    }

    @Override
    protected void disposeMsg(Event event) {
        Object additionObj = event.getAddition().orElse(null);
        if (additionObj != null &&
                additionObj instanceof DebugAddtionMsg) {
            DebugAddtionMsg msg = (DebugAddtionMsg) additionObj;
            if (msg.getState() == State.END) {
                if (!event.hasException()) {
                    updateCurDebugLine(msg);
                } else {
                    showErrorDialog(event.getException());
                }
            }
        }
    }

    private void updateCurDebugLine(DebugAddtionMsg msg) {
        int line = msg
                .getPositionVo()
                .orElse(new PositionVo(null, -1, null))
                .linenumber;
        Display.getDefault().syncExec(new UpdateDebugPositionTask(getCurLine(line)));
    }

    private void showErrorDialog(Exception exp) {
        if (!(exp instanceof DebugExitException)) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(
                            MESSAGEDIALOGTYPE.WARNING,
                            true,
                            "debug step warning",
                            exp.getLocalizedMessage());
                }
            });
        }
    }

    private int getCurLine(int breakPointLine) {
        SourceCodeService codeService = serviceHelper.getCodeService();
        try {
            return codeService.codeLine2ShowLine(breakPointLine);
        } catch (DebugPositionNotFoundException dbgExp) {
            MPPDBIDELoggerUtility.error("receive invalid position:" + dbgExp.toString());
        }
        return -1;
    }

}
