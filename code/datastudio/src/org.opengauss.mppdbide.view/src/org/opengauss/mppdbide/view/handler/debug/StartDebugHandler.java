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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.debuger.service.DebugService;
import org.opengauss.mppdbide.debuger.service.WrappedDebugService;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayUtil;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class StartDebugHandler {
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
        MPPDBIDELoggerUtility.error("start debugint:" + "null");
        debugUtils.initDebugSourceView();
        plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        plSourceEditor.setEditable(false);
        try {
            if (!serviceHelper.createServiceFactory(plSourceEditor.getDebugObject())) {
                showMsg("create debug service failed!");
                return;
            }
        } catch (SQLException sqlExp) {
            MPPDBIDELoggerUtility.warn("create servicefactory with error:" + sqlExp.getMessage());
            showMsg(sqlExp.getLocalizedMessage());
            return;
        }
        plSourceEditor.setExecuteInProgress(true);
        debugUtils.showAllDebugView(true);
        startInputParamDialog();
    }

    private void startInputParamDialog() {
        new InputParamsJob("input dialog", null, this).schedule();
    }

    private void showMsg(String msg) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(
                        MESSAGEDIALOGTYPE.WARNING,
                        true,
                        "start debug warning",
                        msg);
            }
        });
    }

    /**
     * description: get PLSourceEditor instance
     *
     * @return PLSourceEditor the editor
     */
    public PLSourceEditor getSourceEditor() {
        return plSourceEditor;
    }

    /**
     * description: Execute SQL obj window.
     *
     * @param debugObject the debug object
     * @return void
     */
    public void executeSQLObjWindow(IDebugObject debugObject) {
        DebugInputParamsDialog debugInputDlg = new DebugInputParamsDialog(
                Display.getDefault().getActiveShell());
        debugInputDlg.setSyntax(getSqlSyntax(debugObject));
        try {
            debugInputDlg.setDebugObject(debugObject);
            debugInputDlg.defaultParameterValues();
            List<?> debugParams = new ArrayList<>(1);
            ArrayList<ObjectParameter> params = debugObject.getTemplateParameters();
            if (params != null && params.size() > 0) {
                int code = debugInputDlg.open();
                if (code != UIConstants.OK_ID) {
                    return;
                }
                ArrayList<DefaultParameter> serverParams = debugObject
                        .getDatabase()
                        .getServer()
                        .getDefaulParametertMap()
                        .get(debugObject.getOid());
                debugParams = getDebugParams(serverParams);
            }
            WrappedDebugService debugService = serviceHelper.getDebugService();
            if (debugService == null) {
                throw new SQLException("you operation too quick, please retry slowly!");
            }
            debugService.begin(debugParams);
            debugUtils.setDebugStart(true);
        } catch (DatabaseCriticalException exception) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, debugObject.getDatabase());
            return;
        } catch (MPPDBIDEException exception) {
            handleMppDbIdeException(exception);
            return;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STARTING_TO_EXECUTE_SQL_OBJECT),
                    exception);
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), exception);
        } catch (Exception allExp) {
            MPPDBIDELoggerUtility.error(
                    "unknown exception occur " + allExp.getMessage(),
                    allExp);
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), allExp);
        }
    }

    private List<Object> getDebugParams(List<DefaultParameter> serverParams) throws DatabaseOperationException {
        List<DefaultParameter> filterInParams = serverParams.stream().filter(
                param -> (PARAMETERTYPE.IN.equals(param.getDefaultParameterMode())
                || PARAMETERTYPE.INOUT.equals(param.getDefaultParameterMode()))
                ).collect(Collectors.toList());
        List<Object> params = new ArrayList<>(filterInParams.size());
        for (int i = 0; i < filterInParams.size(); i ++) {
            DefaultParameter defaultParameter = filterInParams.get(i);
            if ("refcursor".equals(defaultParameter.getDefaultParameterType())) {
                throw new DatabaseOperationException(
                        IMessagesConstants.ERR_BL_REFCUR_EXECUTION_TEMPLATE_FAILURE);
            }
            params.add(defaultParameter.getDefaultParameterValue());
        }
        return params;
    }

    private void handleMppDbIdeException(MPPDBIDEException exception) {
        UIDisplayUtil.getDebugConsole().logError(MessageConfigLoader
                .getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD, exception.getMessage()));
        MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD,
                        exception.getServerMessage()),
                exception);
    }

    /**
     * Gets the sql syntax.
     *
     * @param debugObject the debug object
     * @return the sql syntax
     */
    private SQLSyntax getSqlSyntax(IDebugObject debugObject) {
        return debugObject.getDatabase().getSqlSyntax();
    }
}
