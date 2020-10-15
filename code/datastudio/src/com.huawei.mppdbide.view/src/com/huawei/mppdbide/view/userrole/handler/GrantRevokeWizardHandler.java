/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.userrole.GrantRevokeWizardDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeWizardHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
