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
 * @since 3.0.0
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
