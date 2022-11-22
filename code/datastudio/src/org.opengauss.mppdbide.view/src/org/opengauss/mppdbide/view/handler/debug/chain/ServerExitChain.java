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

import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.service.DebuggerReportService;
import org.opengauss.mppdbide.debuger.service.chain.IMsgChain;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.debug.DebugHandlerUtils;
import org.opengauss.mppdbide.view.handler.debug.DebugServiceHelper;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateDebugResultTask;
import org.opengauss.mppdbide.view.handler.debug.ui.UpdateHighlightLineNumTask;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class ServerExitChain.
 *
 * @since 3.0.0
 */
public class ServerExitChain extends IMsgChain {
    /**
     * The Debugger Report Service
     */
    public DebuggerReportService reportService = DebuggerReportService.getInstance();
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

        if (VariableRunLine.isPldebugger != null && !VariableRunLine.isPldebugger) {
            PLSourceEditor pl = UIElement.getInstance().getVisibleSourceViewer();
            Boolean hasUpdate = VariableRunLine.hasUpdateStatus.get(VariableRunLine.currentOid);
            pl.setDirty(hasUpdate == null ? false : hasUpdate);
            reportService.makeReport(event.hasException());
            if (VariableRunLine.isContinue != null && VariableRunLine.isContinue) {
                Display.getDefault().asyncExec(() -> UpdateDebugPositionTask.continueDebug());
            }
        }
        if (!isResultUpdated) {
            isResultUpdated = true;
            Display.getDefault().asyncExec(new UpdateDebugResultTask(event));
            debugUtils.setDebugStart(false);
            serviceHelper.closeService();
        }
    }
}
