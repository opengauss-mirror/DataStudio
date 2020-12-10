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
    /**
     * begin debug
     *
     * @param args function input args
     * @return void
     */
    void begin(List<?> args) throws SQLException;
    
    /**
     * end debug
     *
     * @return void
     */ 
    void end();

    /**
     * step into run
     *
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    Optional<PositionVo> stepInto() throws SQLException, DebugExitException;
    
    /**
     * step over run
     *
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    Optional<PositionVo> stepOver() throws SQLException, DebugExitException;
    
    /**
     * step out run
     *
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    Optional<PositionVo> stepOut() throws SQLException, DebugExitException;
    
    /**
     * continue exec run
     *
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    Optional<PositionVo> continueExec() throws SQLException, DebugExitException;
    
    /**
     * step run common command
     *
     * @param debugOpt which opteration to exec
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    Optional<PositionVo> getPositionVo(DebugOpt debugOpt) throws SQLException, DebugExitException;

    /**
     * get cur variables
     *
     * @throws SQLException the exp
     * @return List<VariableVo>  all variables
     */
    List<VariableVo> getVariables() throws SQLException;
    
    /**
     * get cur stacks
     *
     * @throws SQLException the exp
     * @return List<VariableVo>  all stacks
     */
    List<StackVo> getStacks() throws SQLException;
    
    /**
     * get cur breakpoints 
     *
     * @throws SQLException the exp
     * @return List<VariableVo>  all breakpoints
     */
    List<PositionVo> getBreakPoints() throws SQLException;
    
    /**
     * set breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @throws SQLException the exp
     * @return boolean true if success
     */
    boolean setBreakPoint(PositionVo positionVo) throws SQLException;
    
    /**
     * delete breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @throws SQLException the exp
     * @return boolean true if success
     */
    boolean dropBreakPoint(PositionVo positionVo) throws SQLException;
    
    /**
     * add server debug exit listener
     *
     * @param handler event handler of exit
     * @return void
     */
    void addServerExistListener(EventHander handler);
    
    /**
     * get normal end result
     *
     * @return Optional<Object> the result
     */
    Optional<Object> getResult();
    
    /**
     * is normal end
     *
     * @return true if normal end
     */
    boolean isNormalEnd();
    
    /**
     * is debug server still running
     *
     * @return true if running
     */
    boolean isRunning();
}