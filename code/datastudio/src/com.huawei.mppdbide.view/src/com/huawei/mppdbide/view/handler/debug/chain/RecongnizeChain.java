/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.chain;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.WrappedDebugService;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.handler.debug.ui.RefreshAll;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class RecongnizeChain.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class RecongnizeChain extends IMsgChain {
    /**
     * Match message.
     *
     * @param event the event
     * @return boolean, true if successful
     */
    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_BEGIN;
    }

    /**
     * Dispose message.
     *
     * @param event the event
     */
    @Override
    protected void disposeMsg(Event event) {
        if (!(event.getAddition().get() instanceof DebugAddtionMsg)) {
            return;
        }
        DebugAddtionMsg msg = (DebugAddtionMsg) event.getAddition().get();
        if (msg.getState() == State.END && !event.hasException()) {
            PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
            List<BreakpointAnnotation> breakpointAnnotationList = plSourceEditor.getBreakpointAnnotation();
            BreakpointList.initialInstance();
            Map<Integer, BreakpointVo> breakpointList = BreakpointList.getInstance();
            try {
                for (int i = 0; i < breakpointAnnotationList.size(); i++) {
                    BreakpointAnnotation annotation = breakpointAnnotationList.get(i);
                    int line = annotation.getLine();
                    String statement = null;
                    IRegion iRegin = sourceEditor.getDocument().getLineInformation(line);
                    int offset = iRegin.getOffset();
                    int length = iRegin.getLength();
                    statement = sourceEditor.getDocument().get(offset, length);
                    BreakpointVo breakpointVo;
                    line++;
                    if (breakpointAnnotationList.get(i).getEnable()) {
                        breakpointVo = new BreakpointVo(line, statement, true);
                        breakpointList.put(line, breakpointVo);
                        int codeLine = -1;
                        DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
                        SourceCodeService codeService = serviceHelper.getCodeService();
                        codeLine = codeService.showLine2CodeLine(annotation.getLine());
                        PositionVo positionVo = new PositionVo(null, codeLine, null);
                        WrappedDebugService debugService = serviceHelper.getDebugService();
                        debugService.setBreakPoint(positionVo);
                    } else {
                        breakpointVo = new BreakpointVo(line, statement, false);
                        breakpointList.put(line, breakpointVo);
                    }
                }
            } catch (BadLocationException e) {
                MPPDBIDELoggerUtility.warn("dispose breakpoint failed!" + e.getMessage());
            } catch (DebugPositionNotFoundException dbgExp) {
                MPPDBIDELoggerUtility.error("get breakpoint line failed!" + dbgExp.getMessage());
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.warn("set breakpoint failed!" + e.getMessage());
            }
            BreakpointList.setBreakpointList(breakpointList);
            Display.getDefault().syncExec(new RefreshAll());
        }
    }
}