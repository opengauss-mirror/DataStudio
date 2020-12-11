/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.dao.FunctionDao;
import com.huawei.mppdbide.debuger.annotation.ParseVo;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.debuger.vo.SourceCodeVo;
import com.huawei.mppdbide.debuger.vo.TotalSourceCodeVo;
import com.huawei.mppdbide.common.IConnection;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Title: the QueryService class
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/17]
 * @since 2020/11/17
 */
public class QueryService implements IService {
    private IConnection conn;
    private FunctionDao functionDao;
    
    /**
     * query function vo
     *
     * @param proname the debug function name  
     * @return FunctionVo the function vo
     * @throws SQLException sql exp
     */
    public FunctionVo queryFunction(String proname) throws SQLException {
        try (ResultSet rs = conn.getStatement(functionDao.getSql(proname)).executeQuery()) {
            if (rs.next()) {
                return functionDao.parse(rs);
            }
            throw new SQLException("proname:" + proname + " not found!");
        }
    }

    /**
     * get base source code
     *
     * @param oid function oid
     * @return Optional<SourceCodeVo> the source code
     * @throws SQLException sql exp
     */
    public Optional<SourceCodeVo> getSourceCode(Long oid) throws SQLException {
        return getTempSourceCode(oid,
                DebugConstants.DebugOpt.GET_SOURCE_CODE,
                SourceCodeVo.class);
    }

    /**
     * get sourceCode source code
     *
     * @param oid function oid
     * @return Optional<TotalSourceCode> the source code
     * @throws SQLException sql exp
     */
    public Optional<TotalSourceCodeVo> getTotalSourceCode(Long oid) throws SQLException {
        return getTempSourceCode(oid,
                DebugConstants.DebugOpt.GET_TOTAL_SOURCE_CODE,
                TotalSourceCodeVo.class);
    }

    private <T> Optional<T> getTempSourceCode(
            Long oid,
            DebugConstants.DebugOpt debugOpt,
            Class<T> clazz) throws SQLException {
        List<Object> inputParams = Arrays.asList(oid);
        try (ResultSet rs = conn.getDebugOptPrepareStatement(
                debugOpt,
                inputParams
                ).executeQuery()) {
            if (rs.next()) {
                return Optional.of(ParseVo.parse(rs, clazz));
            }
            return Optional.empty();
        }
    }

    /**
     * set conn
     *
     * @param conn connection
     * @return void
     */
    public void setConn(IConnection conn) {
        this.conn = conn;
    }

    /**
     * get conn
     *
     * @return IConnection the connection
     */
    public IConnection getConn() {
        return this.conn;
    }

    /**
     * set function dao
     *
     * @param dao set function dao
     * @return void
     */
    public void setFunctionDao(FunctionDao dao) {
        this.functionDao = dao;
    }

    /**
     * get function dao
     *
     * @return FunctionDao get function dao
     */
    public FunctionDao getFunctionDao() {
        return this.functionDao;
    }

    /**
     * close service
     *
     * @return void
     */
    @Override
    public void closeService() {
        try {
            if (this.conn != null) {
                this.conn.close();
                this.conn = null;
            }
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.warn("close conn with err:" + e.toString());
        }
    }
}
