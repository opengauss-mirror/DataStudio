/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug.chain;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class ServerRunStepChain extends IMsgChain {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    private Map<Integer, State> preEventState = new HashMap<Integer, State>();
    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_RUN;
    }

    @Override
    protected void disposeMsg(Event event) {
        DebugAddtionMsg msg = (DebugAddtionMsg) event.getAddition().get();
        if (msg.getState() == State.END && preEventState.get(event.getId()) != State.HAS_ERROR) {
            try {
                List<VariableVo> variableVos = serviceHelper.getDebugService().getVariables();
                MPPDBIDELoggerUtility.debug(VariableVo.title());
                for (VariableVo vo: variableVos) {
                    MPPDBIDELoggerUtility.debug(vo.formatSelf());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (msg.getState() != State.END) {
            preEventState.put(event.getId(), msg.getState());
        } else {
            preEventState.remove(event.getId());
        }
    }

}
