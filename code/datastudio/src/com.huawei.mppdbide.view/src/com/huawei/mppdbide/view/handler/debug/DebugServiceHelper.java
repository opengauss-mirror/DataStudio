/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;

import java.sql.SQLException;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.debuger.event.IHandlerManger;
import com.huawei.mppdbide.debuger.service.QueryService;
import com.huawei.mppdbide.debuger.service.ServiceFactory;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.WrappedDebugService;
import com.huawei.mppdbide.debuger.vo.FunctionVo;

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
    
    public static DebugServiceHelper getInstance() {
        return debugServiceHelper;
    }
    
    public boolean createServiceFactory(IDebugObject debugObject) throws SQLException {
        if (!isCommonDatabase(debugObject)) {
            this.debugObject = debugObject;
            serviceFactory = new ServiceFactory(new DBConnectionProvider(debugObject.getDatabase()));
            queryService = serviceFactory.getQueryService();
            functionVo = queryService.queryFunction(debugObject.getName());
            debugService = new WrappedDebugService(serviceFactory.getDebugService(functionVo));
            debugService.addHandler(new TestEventHandler());
            codeService = serviceFactory.getCodeService();
            codeService.setBaseCode(queryService.getSourceCode(functionVo.oid).get().getSourceCode());
            codeService.setTotalCode(this.debugObject.getSourceCode().getCode());
        }
        return true;
    }
    
    public boolean isCommonDatabase(IDebugObject debugObject) {
        return (this.debugObject != null) && (this.debugObject.getOid() == debugObject.getOid());
    }
    
    public IDebugObject getDebugObject() {
        return debugObject;
    }
    
    public WrappedDebugService getDebugService() {
        return debugService;
    }
    
    public IHandlerManger getHandlerManger() {
        return debugService;
    }
    
    public QueryService getQueryService() {
        return queryService;
    }
    
    public SourceCodeService getCodeService() {
        return codeService;
    }
    
    public void closeService() {
        if (this.debugObject != null) {
            debugService.end();
            queryService.closeService();
            this.debugObject = null;
        }
    }

    public boolean canStepDebugRun() {
        return debugService != null
                && debugService.isRunning();
    }
}
