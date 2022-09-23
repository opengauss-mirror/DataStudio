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

import java.sql.SQLException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.VersionHelper;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.ui.CoverageHistory;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * DebugHistoryCoverageHandler
 *
 * @author wm
 * @since 2022-09-21
 */
public class DebugHistoryCoverageHandler {
    private DebugHandlerUtils debugUtils = DebugHandlerUtils.getInstance();
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    private PLSourceEditor plSourceEditor;

    /**
     * description: can execute
     *
     * @return boolean true if can
     */
    @CanExecute
    public boolean canExecute() {
        return debugUtils.canStartDebug();
    }

    /**
     * description: excute the command
     *
     * @return void
     */
    @Execute
    public void execute() {
        plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        IConnection conn = null;
        try {
            conn = new DBConnectionProvider(plSourceEditor.getDebugObject().getDatabase()).getValidFreeConnection();
            boolean isPldebugger = VersionHelper.getDebuggerVersion(conn).isPldebugger();
            if (isPldebugger) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_HINT),
                        MessageConfigLoader.getProperty(IMessagesConstants.COVERAGE_CHECK));
                return;
            }
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }
        MPPDBIDELoggerUtility.error("start debugint:" + "null");
        debugUtils.initDebugSourceView();
        Shell shell = Display.getDefault().getActiveShell();
        Server server = plSourceEditor.getDatabase().getServer();
        CoverageHistory coverageHistory = new CoverageHistory(shell,
                server.getServerConnectionInfo().getConectionName());
        coverageHistory.open();
    }

    private void showMsg(String msg) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING,
                        true,
                        "get debug history coverage data warning", msg);
            }
        });
    }
}
