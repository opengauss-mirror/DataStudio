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
import java.util.Optional;

import org.eclipse.jface.preference.PreferenceStore;

import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.common.VersionHelper;
import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.IHandlerManger;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.service.QueryService;
import org.opengauss.mppdbide.debuger.service.ServiceFactory;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.service.WrappedDebugService;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.debuger.service.DebuggerReportService;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import org.opengauss.mppdbide.view.coverage.CoverageService;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class DebugServiceHelper {
    private static DebugServiceHelper debugServiceHelper = new DebugServiceHelper();
    private IDebugObject debugObject = null;
    private ServiceFactory serviceFactory = null;
    private WrappedDebugService debugService = null;
    private DebuggerReportService debuggerReportService = null;
    private FunctionVo functionVo = null;
    private QueryService queryService = null;
    private SourceCodeService codeService = null;
    private CoverageService coverageService = null;

    private DebugServiceHelper() {
    }

    /**
     * description: get singleton of DebugServiceHelper
     *
     * @return DebugServiceHelper the instance
     */
    public static DebugServiceHelper getInstance() {
        return debugServiceHelper;
    }

    /**
     * description: create service factory
     *
     * @param debugObject the debug object
     * @return boolean true if success
     * @throws SQLException the execute sql exception
     */
    public boolean createServiceFactory(IDebugObject debugObject) throws SQLException {
        if (debugObject == null) {
            return false;
        }
        if (!isCommonDatabase(debugObject)) {
            IConnectionProvider provider = new DBConnectionProvider(debugObject.getDatabase());
            serviceFactory = new ServiceFactory(provider);
            checkSupportDebug();
            checkDebugVersion(provider);
            queryService = serviceFactory.getQueryService();
            functionVo = queryService.queryFunction(debugObject.getName());
            debugService = new WrappedDebugService(serviceFactory.getDebugService(functionVo));
            debugService.addHandler(new UiEventHandler());
            debugService.addHandler(new DebugEventHandler());
            debuggerReportService = DebuggerReportService.getInstance();
            if (!VariableRunLine.isPldebugger) {
                debuggerReportService.setAttr(provider, functionVo);
            }
            codeService = serviceFactory.getCodeService();
            Optional<SourceCodeVo> sourceCode = queryService.getSourceCode(functionVo.oid);
            if (sourceCode.isPresent()) {
                codeService.setBaseCode(sourceCode.get().getSourceCode());
                debuggerReportService.setBaseCode(codeService.getBaseCodeDesc());
            } else {
                throw new SQLException("get source code failed!");
            }
            codeService.setTotalCode(debugObject.getSourceCode().getCode());
            debuggerReportService.setTotalCode(codeService.getTotalCodeDesc());
            this.debugObject = debugObject;
        }
        if (debugService != null) {
            debugService.setRollback(getRollbackPreference());
        }
        return debugService != null;
    }

    /**
     * description: is common function to debug
     *
     * @param debugObject the debug object
     * @return true if is common
     */
    public boolean isCommonDatabase(IDebugObject debugObject) {
        return (this.debugObject != null) && (this.debugObject.getOid() == debugObject.getOid());
    }

    /**
     * description: get debug object
     *
     * @return IDebugObject the debug object
     */
    public IDebugObject getDebugObject() {
        return debugObject;
    }

    /**
     * description: get wrapped debug service
     *
     * @return WrappedDebugService the debug service
     */
    public WrappedDebugService getDebugService() {
        return debugService;
    }

    /**
     * description: get handler manager
     *
     * @return IHandlerManger the handler manger
     */
    public IHandlerManger getHandlerManger() {
        return debugService;
    }

    /**
     * description: get query service
     *
     * @return QueryService the query service
     */
    public QueryService getQueryService() {
        return queryService;
    }

    /**
     * description: get coverage service
     *
     * @return CoverageService the coverage service
     */
    public CoverageService getCoverageService() {
        return coverageService;
    }

    /**
     * description: get code service
     *
     * @return SourceCodeService get code service
     */
    public SourceCodeService getCodeService() {
        return codeService;
    }

    /**
     * description: notify breakpoint change event
     *
     * @param annotation the breakpoint status
     * @return void
     */
    public void notifyBreakPointChange(BreakpointAnnotation annotation) {
        if (canStepDebugRun()) {
            debugService.notifyAllHandler(new Event(EventMessage.BREAKPOINT_CHANGE, annotation));
        }
    }

    /**
     * description: notify breakpoint add or delete
     *
     * @param annotation the breakpoint status
     * @param add true if add else if delete
     * @return void
     */
    public void notifyBreakPointStatus(BreakpointAnnotation annotation, boolean add) {
        if (canStepDebugRun()) {
            EventMessage msg = add ? EventMessage.BREAKPOINT_ADD : EventMessage.BREAKPOINT_DELETE;
            debugService.notifyAllHandler(new Event(msg, annotation));
        }
    }

    /**
     * description: notify cancel highlight
     *
     * @param annotation the breakpoint status
     * @param add true if add else if delete
     * @return void
     */
    public void notifyCancelHighlight(int lineNum) {
        EventMessage msg = EventMessage.CANCEL_HIGHLIGHT;
        debugService.notifyAllHandler(new Event(msg, lineNum));
    }

    /**
     * description: close service
     *
     * @return void
     */
    public void closeService() {
        if (this.debugObject != null) {
            if (debugService != null) {
                debugService.end();
                debugService = null;
            }
            if (queryService != null) {
                queryService.closeService();
                queryService = null;
            }
            this.debugObject = null;
        }
    }

    /**
     * description: can step debug run
     *
     * @return void
     */
    public boolean canStepDebugRun() {
        return debugService != null
                && debugService.isRunning();
    }

    /**
     * check is support debug, if false throw SQLException
     *
     * @throws SQLException the throws exception
     */
    public void checkSupportDebug() throws SQLException {
        if (!this.serviceFactory.isSupportDebug()) {
            throw new DebugNotSupportException("server not support debuger!");
        }
    }

    private void checkDebugVersion(IConnectionProvider provider) throws SQLException {
        IConnection conn = null;
        try {
            conn = provider.getValidFreeConnection();
            VariableRunLine.isPldebugger = VersionHelper.getDebuggerVersion(conn).isPldebugger();
        } catch (SQLException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_HINT),
                    MessageConfigLoader.getProperty(IMessagesConstants.VERSION_CHECK_FAIL));
            MPPDBIDELoggerUtility.error(e.getMessage());
            return;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    private static boolean getRollbackPreference() {
        PreferenceStore store = PreferenceWrapper.getInstance().getPreferenceStore();
        if (store != null) {
            return store.getBoolean(MPPDBIDEConstants.DEBUG_PREFERENCE_IF_ROLLBACK);
        }
        return false;
    }

    /**
     * Title: if server not support debug, this error will be throw
     * Description: The Class DebugEditorItem.
     */
    private static class DebugNotSupportException extends SQLException {
        public DebugNotSupportException(String message) {
            super(message);
        }

        /**
         * get the default serial ID
         */
        private static final long serialVersionUID = 1L;

        @Override
        public String getLocalizedMessage() {
            return MessageConfigLoader.getProperty(
                    IMessagesConstants.DEBUG_NOT_SUPPORT_WARN);
        }
    }

    void closeDbConn() {
        queryService.closeService();
        debuggerReportService.close();
        debugService.closeService();
    }
}
