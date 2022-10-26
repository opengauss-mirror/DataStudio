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

package org.opengauss.mppdbide.view.service;

import java.sql.SQLException;
import java.util.Optional;

import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.view.coverage.CoverageService;

/**
 * Title: the ServiceFactory class
 *
 * @since 3.0.0
 */
public class CoverageServiceFactory {
    private IConnectionProvider provider;

    public CoverageServiceFactory(IConnectionProvider provider) {
        this.provider = provider;
    }

    /**
     * getCoverageService
     *
     * @return the value
     * @throws SQLException the exception
     */
    public Optional<CoverageService > getCoverageService() throws SQLException {
        return createCoverageService(provider.getValidFreeConnection());
    }

    private static Optional<CoverageService > createCoverageService(IConnection conn) {
        if (VariableRunLine.isPldebugger != null && VariableRunLine.isPldebugger) {
            return Optional.empty();
        }
        CoverageService service = new CoverageService();
        service.setConn(conn);
        return Optional.of(service);
    }
}
