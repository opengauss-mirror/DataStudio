/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayUtil;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 26,11,2020]
 * @since 26,11,2020
 */
public class StartDebugHandler {
    private DebugHandlerUtils debugUtils = DebugHandlerUtils.getInstance();
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    private PLSourceEditor plSourceEditor;
    @CanExecute
    public boolean canExecute() {
        return debugUtils.canStartDebug();
    }
    
    @Execute
    public void execute() {
        MPPDBIDELoggerUtility.error("start debugint:" + "null");
        debugUtils.initDebugSourceView();
        plSourceEditor = UIElement.getInstance().getVisibleSourceViewer();
        plSourceEditor.setEditable(false);
        try {
            serviceHelper.createServiceFactory(plSourceEditor.getDebugObject());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        plSourceEditor.setExecuteInProgress(true);
        plSourceEditor.enabledisableTextWidget(false);
        debugUtils.showAllDebugView(true);
        debugUtils.setDebugStart(true);
        startInputParamDialog();
    }
    
    private void startInputParamDialog() {
        new InputParamsJob("input dialog", null, this).schedule();
    }
    
    public PLSourceEditor getSourceEditor() {
        return plSourceEditor;
    }
    
    /**
     * Execute SQL obj window.
     *
     * @param debugObject the debug object
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
            serviceHelper.getDebugService().begin(debugParams);
        } catch (DatabaseCriticalException exception) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, debugObject.getDatabase());
            return;
        } catch (MPPDBIDEException exception) {
            handleMppDbIdeException(exception);
            return;
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_STARTING_TO_EXECUTE_SQL_OBJECT),
                    exception);
            MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), exception);
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
