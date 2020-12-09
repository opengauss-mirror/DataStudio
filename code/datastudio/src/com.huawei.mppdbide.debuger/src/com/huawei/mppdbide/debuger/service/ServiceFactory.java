/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.dao.FunctionDao;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.common.IConnection;
import com.huawei.mppdbide.common.IConnectionProvider;

/**
 * Title: the ServiceFactory class
 * <p>
 * Description:
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/23]
 * @since 2020/11/23
 */
public class ServiceFactory {
    private IConnectionProvider provider;
    public ServiceFactory(IConnectionProvider provider) {
        this.provider = provider;
    }

    public QueryService getQueryService() {
        return createQueryService(provider.getFreeConnection().get());
    }

    public DebugService getDebugService(FunctionVo functionVo) {
        return createDebugService(functionVo,
                provider.getFreeConnection().get(),
                provider.getFreeConnection().get());
    }

    public SourceCodeService getCodeService() {
        return new SourceCodeService();
    }

    private static DebugService createDebugService(FunctionVo functionVo, IConnection serverConn, IConnection clientConn) {
        DebugService debugService = new DebugService();
        debugService.setFunctionVo(functionVo);
        debugService.setServerConn(serverConn);
        debugService.setClientConn(clientConn);
        return debugService;
    }

    private static QueryService createQueryService(IConnection conn) {
        QueryService queryService = new QueryService();
        queryService.setFunctionDao(new FunctionDao());
        queryService.setConn(conn);
        return queryService;
    }
}
