/**
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.view.handler.debug.chain;

import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.service.chain.IMsgChain;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.handler.debug.ui.UpdateDebugPositionTask;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 09,12,2020]
 * @since 09,12,2020
 */
public class ServerBeginRunChain extends IMsgChain {
    private DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    @Override
    public boolean matchMsg(Event event) {
        return event.getMsg() == EventMessage.DEBUG_BEGIN;
    }

    @Override
    protected void disposeMsg(Event event) {
        DebugAddtionMsg msg = (DebugAddtionMsg) event.getAddition().get();
        if (msg.getState() == State.END && !event.hasException()) {
            try {
                List<PositionVo> breakPoints = serviceHelper.getDebugService().getBreakPoints();
                MPPDBIDELoggerUtility.info(PositionVo.title());
                for (PositionVo vo: breakPoints) {
                    MPPDBIDELoggerUtility.info(vo.formatSelf());
                }
                Display.getDefault().syncExec(new UpdateDebugPositionTask(getCurLine()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private int getCurLine() {
        SourceCodeService codeService = serviceHelper.getCodeService();
        try {
            int beginDiff = codeService.getBeginTotalAndBaseDiff();
            return beginDiff + codeService.getFirstValidDebugPos();
        } catch (DebugPositionNotFoundException debugExp) {
            MPPDBIDELoggerUtility.error("receive invalid position:" + debugExp.toString());
        }
        return -1;
    }
}
