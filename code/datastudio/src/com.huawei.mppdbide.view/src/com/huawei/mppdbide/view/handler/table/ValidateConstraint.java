/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class ValidateConstraint.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
