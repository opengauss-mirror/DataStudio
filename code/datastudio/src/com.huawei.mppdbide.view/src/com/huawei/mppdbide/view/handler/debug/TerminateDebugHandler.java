/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 30,11,2020]
 * @since 30,11,2020
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
        return debugUtils.canTerminateDebug();
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
        if (plSourceEditor != null) {
            plSourceEditor.setEditable(true);
        }
        debugUtils.setDebugStart(false);
        serviceHelper.closeService();
    }
}
