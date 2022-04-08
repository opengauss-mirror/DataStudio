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

package org.opengauss.mppdbide.view.handler.debug;

import java.sql.SQLException;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.vo.PositionVo;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class ContinueDebugHandler extends BaseDebugStepHandler {
    /**
     * description: can execute
     *
     * @return boolean true if can execute
     */
    @CanExecute
    public boolean canExecute() {
        return canStepDebugRun();
    }

    /**
     * description: execute
     *
     * @return void
     */
    @Execute
    public void execute() {
        executeRun();
    }

    @Override
    public Optional<PositionVo> debugRun() throws SQLException, DebugExitException {
        return getDebugService().continueExec();
    }
}
