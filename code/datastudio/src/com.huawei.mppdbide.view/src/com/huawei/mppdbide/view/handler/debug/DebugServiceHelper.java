/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import java.sql.SQLException;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.IHandlerManger;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.QueryService;
import com.huawei.mppdbide.debuger.service.ServiceFactory;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.WrappedDebugService;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.view.core.sourceeditor.BreakpointAnnotation;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class DebugServiceHelper {
    private static DebugServiceHelper debugServiceHelper = new DebugServiceHelper();
    private IDebugObject debugObject;
    private ServiceFactory serviceFactory;
    private WrappedDebugService debugService;
    private FunctionVo functionVo;
    private QueryService queryService;
    private SourceCodeService codeService;

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
        if (!isCommonDatabase(debugObject)) {
            this.debugObject = debugObject;
            serviceFactory = new ServiceFactory(new DBConnectionProvider(debugObject.getDatabase()));
            queryService = serviceFactory.getQueryService();
            functionVo = queryService.queryFunction(debugObject.getName());
            debugService = new WrappedDebugService(serviceFactory.getDebugService(functionVo));
            debugService.addHandler(new DebugEventHandler());
            debugService.addHandler(new UiEventHandler());
            codeService = serviceFactory.getCodeService();
            codeService.setBaseCode(queryService.getSourceCode(functionVo.oid).get().getSourceCode());
            codeService.setTotalCode(this.debugObject.getSourceCode().getCode());
        }
        return true;
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
     * description: close service
     *
     * @return void
     */
    public void closeService() {
        if (this.debugObject != null) {
            debugService.end();
            queryService.closeService();
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
}
