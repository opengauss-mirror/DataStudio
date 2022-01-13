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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
 *
 * @since 3.0.0
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
        Object eventObject = event.getAddition().orElse(null);
        if (eventObject == null || !(eventObject instanceof DebugAddtionMsg)) {
            return;
        }
        DebugAddtionMsg msg = (DebugAddtionMsg) eventObject;
        if (msg.getState() == State.END && !event.hasException()) {
            PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            List<BreakpointAnnotation> breakpointAnnotationList =
                    plSourceEditor.getBreakpointAnnotation();
            BreakpointList.initialInstance();
            Map<Integer, BreakpointVo> breakpointList = BreakpointList.getInstance();
            try {
                for (int i = 0; i < breakpointAnnotationList.size(); i++) {
                    BreakpointAnnotation annotation = breakpointAnnotationList.get(i);
                    int line = annotation.getLine();
                    String statement = getLineStatement(plSourceEditor, line);
                    BreakpointVo breakpointVo;
                    line++;
                    if (breakpointAnnotationList.get(i).getEnable()) {
                        breakpointVo = new BreakpointVo(line, statement, true);
                        breakpointList.put(line, breakpointVo);
                        int codeLine = -1;
                        DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
                        SourceCodeService codeService = serviceHelper.getCodeService();
                        codeLine = codeService.showLine2CodeLine(annotation.getLine());
                        WrappedDebugService debugService = serviceHelper.getDebugService();
                        debugService.setBreakPoint(new PositionVo(null, codeLine, null));
                    } else {
                        breakpointVo = new BreakpointVo(line, statement, false);
                        breakpointList.put(line, breakpointVo);
                    }
                }
            } catch (DebugPositionNotFoundException dbgExp) {
                MPPDBIDELoggerUtility.error("get breakpoint line failed!" + dbgExp.getMessage());
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.warn("set breakpoint failed!" + e.getMessage());
            }
            BreakpointList.setBreakpointList(breakpointList);
            Display.getDefault().syncExec(new RefreshAll());
        }
    }

    private String getLineStatement(PLSourceEditor plSourceEditor, int line) {
        PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
        try {
            IRegion iRegin = sourceEditor.getDocument().getLineInformation(line);
            int offset = iRegin.getOffset();
            int length = iRegin.getLength();
            return sourceEditor.getDocument().get(offset, length);
        } catch (BadLocationException e) {
            MPPDBIDELoggerUtility.warn("dispose breakpoint failed!" + e.getMessage());
        }
        return "";
    }
}