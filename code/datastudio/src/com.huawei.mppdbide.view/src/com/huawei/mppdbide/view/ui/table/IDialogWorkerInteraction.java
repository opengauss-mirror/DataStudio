/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IDialogWorkerInteraction.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IDialogWorkerInteraction {

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    void onSuccessUIAction(Object obj);

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    void onCriticalExceptionUIAction(DatabaseCriticalException e);

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    void onOperationalExceptionUIAction(DatabaseOperationException e);

    /**
     * On presetup failure UI action.
     *
     * @param e the e
     */
    void onPresetupFailureUIAction(MPPDBIDEException e);

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    Shell getShell();
}
