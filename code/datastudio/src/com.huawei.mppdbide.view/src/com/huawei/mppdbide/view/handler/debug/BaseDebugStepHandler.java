/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import java.sql.SQLException;
import java.util.Optional;

import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.service.IDebugService;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 07,12,2020]
 * @since 07,12,2020
 */
public abstract class BaseDebugStepHandler {
    /**
     * the singleton instance of DebugServiceHelper
     */
    protected DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();
    
    /**
     * description: then step run of debugService
     * 
     * @throws SQLException the sql exp
     * @throws DebugExitException the debug end exp
     * @return Optional<PositionVo> the position of breakpoint paused
     */
    public abstract Optional<PositionVo> debugRun() throws SQLException, DebugExitException;
    
    /**
     * description: execute step
     * 
     * @return void
     */
    public void executeRun() {
        try {
            Optional<PositionVo> positionVo = debugRun();
            if (positionVo.isPresent()) {
                MPPDBIDELoggerUtility.error("current position is" + positionVo.get().formatSelf());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MPPDBIDELoggerUtility.error("step have some error!");
        } catch (DebugExitException e) {
            MPPDBIDELoggerUtility.error("debug end!");
        }
    }
    
    /**
     * description: can this step runnable
     * 
     * @return boolean true if can run
     */
    public boolean canStepDebugRun() {
        return serviceHelper.canStepDebugRun();
    }
    
    /**
     * description: get debug service
     * 
     * @return IDebugService the debug service
     */
    public IDebugService getDebugService() {
        return serviceHelper.getDebugService();
    }
}
