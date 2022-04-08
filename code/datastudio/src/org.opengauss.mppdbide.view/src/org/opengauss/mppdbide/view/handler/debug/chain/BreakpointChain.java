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

import java.util.Map;
import java.util.Optional;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.debuger.vo.BreakpointList;
import org.opengauss.mppdbide.debuger.vo.BreakpointVo;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import org.opengauss.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateBreakpoint;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class BreakpointChain.
 *
 * @since 3.0.0
 */
public class BreakpointChain extends IMsgChain {
    /**
     * Match message.
     *
     * @param event the event
     * @return true, if successful
     */
    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.BREAKPOINT_ADD
                || event.getMsg() == EventMessage.BREAKPOINT_DELETE
                || event.getMsg() == EventMessage.BREAKPOINT_CHANGE
                || event.getMsg() == EventMessage.CANCEL_HIGHLIGHT;
    }

    /**
     * Dispose message.
     *
     * @param event the event
     */
    @Override
    protected void disposeMsg(Event event) {
        if (event.getMsg() == EventMessage.CANCEL_HIGHLIGHT) {
            PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            int lineNum = event.getIntegerAddition();
            plSourceEditor.deHighlightLine(lineNum);
            if (lineNum != -1 && lineNum == plSourceEditor.getdebugPositionLine()) {
                plSourceEditor.highlightLine(lineNum);
            }
            PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
            sourceEditor.setHighlightLineNum(-1);
            return;
        }

        Optional<Object> optional = event.getAddition();
        if (!optional.isPresent()) {
            return;
        }
        Object additionObj = optional.get();
        if (additionObj instanceof BreakpointAnnotation) {
            BreakpointAnnotation annotation = (BreakpointAnnotation) additionObj;
            int line = annotation.getLine();
            PLSourceEditor plSourceEditor;
            plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
            String statement = null;
            try {
                IRegion iRegin = sourceEditor.getDocument().getLineInformation(line);
                int offset = iRegin.getOffset();
                int length = iRegin.getLength();
                statement = sourceEditor.getDocument().get(offset, length);
            } catch (BadLocationException e) {
                MPPDBIDELoggerUtility.warn("dispose breakpoint failed!" + e.getMessage());
                return;
            }
            BreakpointVo breakpointVo;
            Map<Integer, BreakpointVo> breakpointList = BreakpointList.getInstance();
            line++;
            if (event.getMsg() == EventMessage.BREAKPOINT_ADD) {
                breakpointVo = new BreakpointVo(line, statement, true);
                breakpointList.put(line, breakpointVo);
            } else if (event.getMsg() == EventMessage.BREAKPOINT_DELETE) {
                breakpointList.remove(line);
            } else {
                breakpointVo = breakpointList.get(line);
                Boolean enableString = breakpointVo.getEnable();
                breakpointList.put(line, new BreakpointVo(line, statement, !enableString));
            }
            BreakpointList.setBreakpointList(breakpointList);
            Display.getDefault().syncExec(new UpdateBreakpoint());
        }
    }
}