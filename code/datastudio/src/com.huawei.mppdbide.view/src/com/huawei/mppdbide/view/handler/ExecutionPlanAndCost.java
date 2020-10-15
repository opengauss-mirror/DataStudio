/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.ExecutionPlanHandler;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanAndCost.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ExecutionPlanAndCost {

    /**
     * Execute.
     *
     * @param terminalID the terminal ID
     * @param analyzeFlag the analyze flag
     * @param shell the shell
     */

    @Execute
    public void execute(
            @Optional @Named("com.huawei.mppdbide.view.commandparameter.explainplan.terminal.id") String terminalID,
            @Optional @Named("com.huawei.mppdbide.view.commandparameter.explainplan.analyze") String analyzeFlag,
            Shell shell) {
        SQLTerminal terminal = UIElement.getInstance().getTerminal(terminalID);

        if (terminal == null) {
            if (UIElement.getInstance().isSqlTerminalOnTop()) {
                terminal = UIElement.getInstance().getSqlTerminalModel();
            } else {
                MPPDBIDELoggerUtility.error("error in getting terminal");
                return;
            }

            if (terminal == null) {
                MPPDBIDELoggerUtility.error("error in getting terminal");
                return;
            }
        }
        ExecutionPlanHandler executionPlanhandler = new ExecutionPlanHandler();

        boolean analyse;
        if (analyzeFlag != null) {
            analyse = analyzeFlag.equals("yes") ? true : false;
        } else {
            analyse = terminal.isIncludeAnalyze();
        }
        executionPlanhandler.execute(terminal, analyse);
    }

    /**
     * Can execute.
     *
     * @param activePart the active part
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Named(IServiceConstants.ACTIVE_PART) @Optional MPart activePart) {
        if (null == activePart) {
            return false;
        }

        Object partObject = activePart.getObject();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            return !terminal.explainPlanProgressState() && terminal.getSelectedDatabase() != null
                    && terminal.getSelectedDatabase().isConnected() && !"".equals(terminal.getDocumentContent().trim())
                    && (terminal.getSelectedDatabase().getDBType() == DBTYPE.OPENGAUSS);
        }

        return false;
    }

}
