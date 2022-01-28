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

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

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
