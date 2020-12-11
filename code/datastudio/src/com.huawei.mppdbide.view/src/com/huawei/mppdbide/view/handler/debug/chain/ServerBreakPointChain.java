/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
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
        Object additionObj = event.getAddition().get();
        if (additionObj instanceof BreakpointAnnotation) {
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
