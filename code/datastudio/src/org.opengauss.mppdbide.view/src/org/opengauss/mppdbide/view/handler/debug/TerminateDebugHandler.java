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

package org.opengauss.mppdbide.view.handler.debug;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.vo.DebuggerStartInfoVo;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class TerminateDebugHandler {
    private DebugHandlerUtils debugUtils = DebugHandlerUtils.getInstance();
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    /**
     * description: can execute
     *
     * @return void
     */
    @CanExecute
    public boolean canExecute() {
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        return plSourceEditor != null;
    }

    /**
     * description: execute
     *
     * @return void
     */
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.error("terminate debugint:" + "null");
        debugUtils.showAllDebugView(false);
        PLSourceEditor plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        Long oid = plSourceEditor.getDebugObject().getOid();
        DebuggerStartInfoVo debuggerStartInfoVo = DebuggerStartVariable.getStartInfo(oid);
        debuggerStartInfoVo.isMakeReport = false;
        DebuggerStartVariable.setStartInfo(oid, debuggerStartInfoVo);
        if (plSourceEditor != null) {
            plSourceEditor.setEditable(true);
            plSourceEditor.setExecuteInProgress(false);
        }
        debugUtils.setDebugStart(false);
        debugUtils.terminateDebug();
        serviceHelper.closeService();
        if (VariableRunLine.isPldebugger != null && !VariableRunLine.isPldebugger) {
            Boolean hasUpdate = VariableRunLine.hasUpdateStatus.get(oid);
            plSourceEditor.setDirty(hasUpdate == null ? false : hasUpdate);
        }
    }
}
