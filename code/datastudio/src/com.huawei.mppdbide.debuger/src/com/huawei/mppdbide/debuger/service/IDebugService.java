/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.debuger.vo.VariableVo;

/**
 *
 * Title: IDebugService for use
 *
 * Description: 
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
 */
public interface IDebugService extends IService {
    void begin(Object[] args) throws SQLException;
    void end();

    Optional<PositionVo> stepInto() throws SQLException, DebugExitException;
    Optional<PositionVo> stepOver() throws SQLException, DebugExitException;
    Optional<PositionVo> stepOut() throws SQLException, DebugExitException;
    Optional<PositionVo> continueExec() throws SQLException, DebugExitException;
    Optional<PositionVo> getPositionVo(DebugOpt debugOpt) throws SQLException, DebugExitException;

    List<VariableVo> getVariables() throws SQLException;
    List<StackVo> getStacks() throws SQLException;
    List<PositionVo> getBreakPoints() throws SQLException;
    boolean setBreakPoint(PositionVo positionVo) throws SQLException;
    boolean dropBreakPoint(PositionVo positionVo) throws SQLException;
    
    void addServerExistListener(EventHander handler);
    
    Optional<Object> getResult();
    boolean isNormalEnd();
    boolean isRunning();
}