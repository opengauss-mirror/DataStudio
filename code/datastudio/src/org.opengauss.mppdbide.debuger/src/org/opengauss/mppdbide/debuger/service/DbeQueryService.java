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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.opengauss.mppdbide.common.QueryResVoConvertHelper;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;

/**
 * Title: the QueryService class
 *
 * @since 3.0.0
 */
public class DbeQueryService extends QueryService {
    /**
     * get base source code
     *
     * @param oid function oid
     * @return Optional<SourceCodeVo> the source code
     * @throws SQLException sql exp
     */
    @Override
    public Optional<SourceCodeVo> getSourceCode(Long oid) throws SQLException {
        return getTempSourceCode(oid, DebugConstants.DebugOpt.DBE_GET_SOURCE_CODE, SourceCodeVo.class);
    }

    private <T> Optional<T> getTempSourceCode(Long oid, DebugConstants.DebugOpt debugOpt, Class<T> clazz)
            throws SQLException {
        List<Object> inputParams = Arrays.asList(oid);
        try (PreparedStatement ps = getIConn().getDebugOptPrepareStatement(
                debugOpt, inputParams)) {
            try (ResultSet rs = ps.executeQuery()) {
                return QueryResVoConvertHelper.parse(rs, clazz, getIConn());
            }
        }
    }
}