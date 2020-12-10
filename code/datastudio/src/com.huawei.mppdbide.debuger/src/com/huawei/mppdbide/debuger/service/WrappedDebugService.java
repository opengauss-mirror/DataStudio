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
    
    /**
     * begin debug
     *
     * @param args function input args
     * @return void
     */
    @Override
    public void begin(List<?> args) throws SQLException {
        try {
            new EventRunner(this, args, EventMessage.DEBUG_BEGIN) {
                @Override
                protected void innertRun() throws SQLException {
                    Object inputArgs = this.args;
                    if (inputArgs instanceof List) {
                        List<?> beginParams = (List<?>) inputArgs;
                        debugService.begin(beginParams);
                    }
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
    
    @Override
    public Optional<PositionVo> continueExec() throws SQLException, DebugExitException {
        return runDebugRunStep(DebugOpt.CONTINUE_EXEC);
    }

    /**
     * step run debug step
     *
     * @param debugOpt which debug opt to run
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     * @return Optional<PositionVo> the breakpoint line position
     */
    public Optional<PositionVo> runDebugRunStep(DebugOpt debugOpt) throws SQLException, DebugExitException {
        EventRunner runner = new EventRunner(this, debugOpt, EventMessage.DEBUG_RUN) {
            @Override
            protected void innertRun() throws SQLException, DebugExitException {
                if (args instanceof DebugOpt) {
                    positionVo = debugService.getPositionVo((DebugOpt) args).orElse(null);
                }
            }
        };
        runner.run();
        return Optional.ofNullable(runner.getPositionVo());
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
        addServerExistListener(handler);
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
        try {
            new EventRunner(this, null, EventMessage.DEBUG_END) {
                
                @Override
                protected void innertRun() throws SQLException, DebugExitException {
                    debugService.end();
                }
            }.run();
        } catch (SQLException | DebugExitException sqlExp) {
            MPPDBIDELoggerUtility.error("can\'t run here!");
        }
    }


    @Override
    public Optional<PositionVo> stepOut() throws SQLException, DebugExitException {
        return debugService.stepOut();
    }


    @Override
    public Optional<PositionVo> getPositionVo(DebugOpt debugOpt)
            throws SQLException, DebugExitException {
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

    @Override
    public void addServerExistListener(EventHander handler) {
        debugService.addServerExistListener(handler);
    }

    /**
    *
    * Title: EventRunner for use
    * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
    *
    * @author z00588921
    * @version [DataStudio for openGauss 2020-12-08]
    * @since 2020-12-08
    */
    public static abstract class EventRunner {
        // the wrapped debug service
        protected WrappedDebugService service;
        // the start debug args
        protected Object args;
        // the event msg to send
        protected EventMessage msg;
        // result of debug breakpoint position
        protected PositionVo positionVo = null;

        public EventRunner(WrappedDebugService service, Object args, EventMessage msg) {
            this.service = service;
            this.args = args;
            this.msg = msg;
        }

        /**
         * inner run of debug step
         *
         * @throws SQLException the exp
         * @throws DebugExitException the debug exit exp
         * @return void
         */
        protected abstract void innertRun() throws SQLException, DebugExitException;

        /**
         * get result of position
         *
         * @return PositionVo the debug position
         */
        public PositionVo getPositionVo() {
            return positionVo;
        }

        /**
         * run of debug step ,and send event
         *
         * @throws SQLException the exp
         * @throws DebugExitException the debug exit exp
         * @return void
         */
        public void run() throws SQLException, DebugExitException {
            Event beginEvent = new Event(msg, new DebugAddtionMsg(State.START));
            Exception runException = null;
            try {
                service.notifyAllHandler(beginEvent);
                innertRun();
            } catch (SQLException | DebugExitException debugExp) {
                runException = debugExp;
                throw debugExp;
            } finally {
                service.notifyAllHandler(
                        new Event(
                                msg,
                                new DebugAddtionMsg(State.END, positionVo),
                                runException,
                                beginEvent.getId()
                                )
                        );
            }
        }
    }
}
