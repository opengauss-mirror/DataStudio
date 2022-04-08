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

package org.opengauss.mppdbide.view.userrole.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.userrole.GrantRevokeWizardDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeWizardHandler.
 *
 * @since 3.0.0
 */
public class GrantRevokeWizardHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object selectedObject = IHandlerUtilities.getObjectBrowserSelectedObject();
        GrantRevokeWizardDialog grantRevokeWizard = new GrantRevokeWizardDialog(shell, selectedObject, false);
        grantRevokeWizard.open();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {

        // check if object group without object
        Object selectedObject = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != selectedObject && ((ObjectGroup<ServerObject>) selectedObject).getChildren().length == 0) {
            return false;
        }

        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

}
