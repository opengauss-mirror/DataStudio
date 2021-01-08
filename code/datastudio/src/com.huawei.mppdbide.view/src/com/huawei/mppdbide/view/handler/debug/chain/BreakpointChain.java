/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.chain;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateBreakpoint;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class BreakpointChain.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
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
            PLSourceEditorCore sourceEditor = plSourceEditor.getSourceEditorCore();
            sourceEditor.setHighlightLineNum(-1);
            return;
        }
        Object additionObj = event.getAddition().get();
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