/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.event.IHandlerManger;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

import com.huawei.mppdbide.debuger.event.Event.EventMessage;

/**
 *
 * Title: WrappedDebugService for use
 *
 * Description: 
 *
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-08]
 * @since 2020-12-08
 */
public class WrappedDebugService implements IDebugService, IHandlerManger {
    private static final int DEFALUT_HANDER_SIZE = 10;
    private List<EventHander> eventHandlers = new ArrayList<EventHander>(DEFALUT_HANDER_SIZE);
    private DebugService debugService;
    public WrappedDebugService(DebugService debugService) {
        this.debugService = debugService;
    }

    
    @Override
    public void begin(Object[] args) throws SQLException {
        try {
            new EventRunner(this, args, EventMessage.DEBUG_BEGIN) {
                @Override
                protected void innertRun() throws SQLException {
                    debugService.begin((Object[])this.args);
                }
            }.run();
        } catch (DebugExitException e) {
            MPPDBIDELoggerUtility.error("begin can\'t run here!!");
        }
    }
    
    @Override
    public Optional<PositionVo> stepOver() throws SQLException, DebugExitException {
        return runDebugRunStep(DebugOpt.STEP_OVER);
    }
    
    @Override
    public Optional<PositionVo> stepInto() throws SQLException, DebugExitException {
        return runDebugRunStep(DebugOpt.STEP_INTO);
    }
    
    public Optional<PositionVo> continueExec() throws SQLException, DebugExitException {
        return runDebugRunStep(DebugOpt.CONTINUE_EXEC);
    }

    public Optional<PositionVo> runDebugRunStep(DebugOpt debugOpt) throws SQLException, DebugExitException {
        EventRunner runner = new EventRunner(this, debugOpt, EventMessage.DEBUG_RUN) {
            @Override
            protected void innertRun() throws SQLException, DebugExitException {
                positionVo = debugService.getPositionVo((DebugOpt)args).orElse(null);
            }
        };
        runner.run();
        return Optional.ofNullable(runner.getPositionVo());
    }
    
    private static abstract class EventRunner {
        protected WrappedDebugService service;
        protected Object args;
        protected PositionVo positionVo = null;
        protected EventMessage msg;
        public EventRunner(WrappedDebugService service, Object args, EventMessage msg) {
            this.service = service;
            this.args = args;
            this.msg = msg;
        }
        
        protected abstract void innertRun() throws SQLException, DebugExitException;

        public PositionVo getPositionVo() {
            return positionVo;
        }

        public void run() throws SQLException, DebugExitException {
            Event beginEvent = new Event(msg, new DebugAddtionMsg(State.START));
            try {
                service.notifyAllHandler(beginEvent);
                innertRun();
            } catch (SQLException | DebugExitException debugExp) {
                service.notifyAllHandler(new Event(msg, new DebugAddtionMsg(State.HAS_ERROR, positionVo), debugExp, beginEvent.getId()));
                throw debugExp;
            } finally {
                service.notifyAllHandler(new Event(msg, new DebugAddtionMsg(State.END, positionVo), null, beginEvent.getId()));
            }
        }
    }

    @Override
    public void notifyAllHandler(Event event) {
        for (EventHander hander: eventHandlers) {
            hander.handleEvent(event);
        }
    }

    @Override
    public void addHandler(EventHander handler) {
        if (!eventHandlers.contains(handler)) {
            eventHandlers.add(handler);
        }
    }

    @Override
    public void removeHandler(EventHander handler) {
        eventHandlers.remove(handler);
    }


    @Override
    public void removeAllHandler() {
        eventHandlers.clear();
    }

    @Override
    public void closeService() {
        debugService.closeService();
    }


    @Override
    public void end() {
        debugService.end();
    }


    @Override
    public Optional<PositionVo> stepOut() throws SQLException, DebugExitException {
        return debugService.stepOut();
    }


    @Override
    public Optional<PositionVo> getPositionVo(DebugOpt debugOpt) throws SQLException, DebugExitException {
        return debugService.getPositionVo(debugOpt);
    }


    @Override
    public List<VariableVo> getVariables() throws SQLException {
        return debugService.getVariables();
    }


    @Override
    public List<StackVo> getStacks() throws SQLException {
        return debugService.getStacks();
    }


    @Override
    public List<PositionVo> getBreakPoints() throws SQLException {
        return debugService.getBreakPoints();
    }


    @Override
    public boolean setBreakPoint(PositionVo positionVo) throws SQLException {
        return debugService.setBreakPoint(positionVo);
    }


    @Override
    public boolean dropBreakPoint(PositionVo positionVo) throws SQLException {
        return debugService.dropBreakPoint(positionVo);
    }


    @Override
    public Optional<Object> getResult() {
        return debugService.getResult();
    }


    @Override
    public boolean isNormalEnd() {
        return debugService.isNormalEnd();
    }
    
    @Override
    public boolean isRunning() {
        return debugService.isRunning();
    }
}
