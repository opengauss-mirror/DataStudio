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

package org.opengauss.mppdbide.debuger.service;

import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.dao.FunctionDao;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Title: the ServiceFactory class
 *
 * @since 3.0.0
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
     * @throws SQLException the sql exception
     */
    public QueryService getQueryService() throws SQLException {
        return createQueryService(provider.getValidFreeConnection());
    }

    /**
     * description: get debug service
     *
     * @param functionVo the functionVo
     * @return DebugService debug service
     * @throws SQLException the null connection sqlexception
     */
    public DebugService getDebugService(FunctionVo functionVo) throws SQLException {
        return createDebugService(functionVo,
                provider.getValidFreeConnection(),
                provider.getValidFreeConnection());
    }

    /**
     * judge is support debug
     *
     * @return boolean true if support
     */
    public boolean isSupportDebug() {
        try {
            return getVersion().isPresent();
        } catch (SQLException | NoSuchElementException notSupportExp) {
            return false;
        }
    }

    /**
     * get version of server
     *
     * @return Optional<VersionVo> the version vo
     * @throws SQLException sql error
     */
    public Optional<VersionVo> getVersion() throws SQLException {
        IConnection conn = provider.getValidFreeConnection();
        try (PreparedStatement ps = conn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DEBUG_VERSION,
                new ArrayList<>(1))) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(ParseVo.parse(rs, VersionVo.class));
                }
                return Optional.empty();
            }
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
        if (!VariableRunLine.isPldebugger) {
            return new DbeSourceCodeService();
        }
        return new SourceCodeService();
    }

    private static DebugService createDebugService(
            FunctionVo functionVo,
            IConnection serverConn,
            IConnection clientConn) {
        DebugService debugService = getDebugService(clientConn);
        debugService.setFunctionVo(functionVo);
        debugService.setServerConn(serverConn);
        debugService.setClientConn(clientConn);
        return debugService;
    }

    private static DebugService getDebugService(IConnection conn) {
        if (!VariableRunLine.isPldebugger) {
            return new DbeDebugService();
        }
        return new DebugService();
    }

    private static QueryService createQueryService(IConnection conn) {
        QueryService queryService;
        if (!VariableRunLine.isPldebugger) {
            queryService = new DbeQueryService();
        } else {
            queryService = new QueryService();
        }
        queryService.setFunctionDao(new FunctionDao());
        queryService.setConn(conn);
        return queryService;
    }
}
