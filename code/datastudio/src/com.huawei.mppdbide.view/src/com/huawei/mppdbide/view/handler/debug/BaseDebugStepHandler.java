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
 *
 * @since 3.0.0
 */
public abstract class BaseDebugStepHandler {
    /**
     * the singleton instance of DebugServiceHelper
     */
    protected DebugServiceHelper serviceHelper = DebugServiceHelper.getInstance();

    /**
     * description: then step run of debugService
     *
     * @return Optional<PositionVo> the position of breakpoint paused
     * @throws SQLException the sql exp
     * @throws DebugExitException the debug end exp
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
            MPPDBIDELoggerUtility.error("step have some error!" + e.getLocalizedMessage());
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
