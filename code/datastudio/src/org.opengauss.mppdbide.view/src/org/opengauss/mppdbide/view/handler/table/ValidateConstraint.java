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

package org.opengauss.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class ValidateConstraint.
 *
 * @since 3.0.0
 */
public class ValidateConstraint {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();
        if (constraintMetaData == null) {
            return;
        }
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(constraintMetaData.getName(),
                constraintMetaData.getTable().getName(), constraintMetaData.getNamespace().getName(),
                constraintMetaData.getDatabase().getName(), constraintMetaData.getDatabase().getServerName(),
                IMessagesConstants.VALIDATE_CONSTRAINT_PROGRESS_NAME);
        ValidateConstraintWorker worker = new ValidateConstraintWorker(progressLabel, constraintMetaData);
        worker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ConstraintMetaData constraintMetaData = IHandlerUtilities.getSelectedConstraint();
        if (null != constraintMetaData) {
            return constraintMetaData.isConvalidated();
        }
        return false;
    }
}
