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

import java.sql.SQLException;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.WrappedDebugService;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class ServerBreakPointChain extends IMsgChain {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.BREAKPOINT_ADD
                || event.getMsg() == EventMessage.BREAKPOINT_DELETE
                || event.getMsg() == EventMessage.BREAKPOINT_CHANGE;
    }

    @Override
    protected void disposeMsg(Event event) {
        Object additionObj = event.getAddition().orElse(null);
        if (additionObj != null &&
                additionObj instanceof BreakpointAnnotation) {
            BreakpointAnnotation annotation = (BreakpointAnnotation) additionObj;
            WrappedDebugService debugService = serviceHelper.getDebugService();
            try {
                int codeLine = getCurLine(annotation.getLine());
                if (codeLine == -1) {
                    MPPDBIDELoggerUtility.warn("invalid breakpoint line:" + annotation.getLine());
                    return;
                }

                PositionVo positionVo = new PositionVo(null, codeLine, null);
                if (getBreakPointStatus(annotation, event.getMsg())) {
                    debugService.setBreakPoint(positionVo);
                } else {
                    debugService.dropBreakPoint(positionVo);
                }
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.warn("set breakpoint failed!" + e.getMessage());
            }
        }
    }

    private boolean getBreakPointStatus(BreakpointAnnotation annotation, EventMessage msg) {
        if (msg == EventMessage.BREAKPOINT_ADD) {
            return true;
        } else if (msg == EventMessage.BREAKPOINT_DELETE) {
            return false;
        } else {
            return annotation.getEnable();
        }
    }

    private int getCurLine(int showLine) {
        SourceCodeService codeService = serviceHelper.getCodeService();
        try {
            return codeService.showLine2CodeLine(showLine);
        } catch (DebugPositionNotFoundException dbgExp) {
            MPPDBIDELoggerUtility.error("get breakpoint line failed!" + dbgExp.getMessage());
        }
        return -1;
    }

}
