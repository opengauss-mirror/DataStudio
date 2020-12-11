/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug;

import java.sql.SQLException;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.vo.PositionVo;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [openGauss DataStudio 1.0.1, 02,12,2020]
 * @since 02,12,2020
 */
public class StepOutDebugHandler extends BaseDebugStepHandler {
    /**
     * description: can execute
     * 
     * @return void
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
        return getDebugService().stepOut();
    }
}
