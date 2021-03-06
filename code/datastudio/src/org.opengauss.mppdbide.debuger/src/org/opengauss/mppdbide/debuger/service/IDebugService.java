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

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.event.EventHander;
import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;
import org.opengauss.mppdbide.debuger.vo.VersionVo;

/**
 * Title: IDebugService for use
 *
 * @since 3.0.0
 */
public interface IDebugService extends IService {
    /**
     * get version of debug, if any error or null mains not support debug
     *
     * @return Optional<VersionVo> the version vo
     * @throws SQLException sql error
     */
    Optional<VersionVo> version() throws SQLException;

    /**
     * begin debug
     *
     * @param args function input args
     * @return void
     * @throws SQLException sql exception
     */
    void begin(List<?> args) throws SQLException;

    /**
     * end debug
     *
     * @return void
     */
    void end();

    /**
     * init debug
     *
     * @return void
     */
    void init();

    /**
     * description: is need roll back
     *
     * @return boolean true if need roll back
     */
    boolean isRollback();

    /**
     * description: set roll back flag
     *
     * @param isRollback true if need roll back
     */
    void setRollback(boolean isRollback);

    /**
     * step into run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    Optional<PositionVo> stepInto() throws SQLException, DebugExitException;

    /**
     * step over run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    Optional<PositionVo> stepOver() throws SQLException, DebugExitException;

    /**
     * step out run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    Optional<PositionVo> stepOut() throws SQLException, DebugExitException;

    /**
     * continue exec run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    Optional<PositionVo> continueExec() throws SQLException, DebugExitException;

    /**
     * step run common command
     *
     * @param debugOpt which opteration to exec
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    Optional<PositionVo> getPositionVo(DebugOpt debugOpt) throws SQLException, DebugExitException;

    /**
     * get cur variables
     *
     * @return List<VariableVo>  all variables
     * @throws SQLException the exp
     */
    List<VariableVo> getVariables() throws SQLException;

    /**
     * get cur stacks
     *
     * @return List<VariableVo>  all stacks
     * @throws SQLException the exp
     */
    List<StackVo> getStacks() throws SQLException;

    /**
     * get cur breakpoints
     *
     * @return List<VariableVo>  all breakpoints
     * @throws SQLException the exp
     */
    List<PositionVo> getBreakPoints() throws SQLException;

    /**
     * set breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @return boolean true if success
     * @throws SQLException the exp
     */
    boolean setBreakPoint(PositionVo positionVo) throws SQLException;

    /**
     * delete breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @return boolean true if success
     * @throws SQLException the exp
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