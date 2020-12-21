/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.annotation.ParseVo;
import com.huawei.mppdbide.debuger.dao.FunctionDao;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.common.IConnection;
import com.huawei.mppdbide.common.IConnectionProvider;
import com.huawei.mppdbide.debuger.vo.VersionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Title: the ServiceFactory class
 * Description:
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

    /**
     * description: get query service
     *
     * @return QueryService the query service
     */
    public QueryService getQueryService() {
        return createQueryService(provider.getFreeConnection().get());
    }

    /**
     * description: get debug service
     *
     * @param functionVo the functionVo
     * @return DebugService debug service
     */
    public DebugService getDebugService(FunctionVo functionVo) {
        return createDebugService(functionVo,
                provider.getFreeConnection().get(),
                provider.getFreeConnection().get());
    }

    /**
     * judge is support debug
     *
     * @return boolean true if support
     */
    public boolean isSupportDebug() {
        try {
            return getVersion().isPresent();
        } catch (SQLException sqlExp) {
            return false;
        }
    }

    /**
     * get version of server
     *
     * @return Optional<VersionVo> the version vo
     * @throws SQLException sql error
     */
    public Optional<VersionVo> getVersion() throws SQLException  {
        IConnection conn = provider.getFreeConnection().get();
        try (ResultSet rs = conn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DEBUG_VERSION,
                new ArrayList<>(1)).executeQuery()) {
            if (rs.next()) {
                return Optional.ofNullable(ParseVo.parse(rs, VersionVo.class));
            }
            return Optional.empty();
        } finally {
            try {
                conn.close();
            } catch (SQLException sqlExp) {
                MPPDBIDELoggerUtility.warn("get version with err=" + sqlExp.getMessage());
            }
        }
    }

    /**
     * description: get code service
     *
     * @return SourceCodeService the code service
     */
    public SourceCodeService getCodeService() {
        return new SourceCodeService();
    }

    private static DebugService createDebugService(
            FunctionVo functionVo,
            IConnection serverConn,
            IConnection clientConn) {
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
