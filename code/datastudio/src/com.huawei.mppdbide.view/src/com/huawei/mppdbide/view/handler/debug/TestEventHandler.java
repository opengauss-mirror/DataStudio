/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 08,12,2020]
 * @since 08,12,2020
 */
public class TestEventHandler implements EventHander {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    private Map<Integer, State> preEventState = new HashMap<Integer, State>();
    @Override
    public void handleEvent(Event event) {
        MPPDBIDELoggerUtility.error("Event:" + event);
        if (event.getMsg() == EventMessage.ON_EXIT) {
            if (event.hasException()) {
                MPPDBIDELoggerUtility.error("debug exit with exception:" + event.getException().getMessage());
            } else {
                MPPDBIDELoggerUtility.info("debug already exit: result:" + event.getAddition());
            }
            return;
        }

        DebugAddtionMsg msg = (DebugAddtionMsg) event.getAddition().get();
        if (event.getMsg() == EventMessage.DEBUG_RUN) {
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
        }
        if (msg.getState() != State.END) {
            preEventState.put(event.getId(), msg.getState());
        } else {
            preEventState.remove(event.getId());
        }
    }

}
