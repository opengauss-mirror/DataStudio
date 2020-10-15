/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole.handler;

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.userrole.GrantRevokeWizardDialog;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeWizardBatchHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GrantRevokeWizardBatchHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        List<?> objectBrowserSelectedObjects = IHandlerUtilities.getObjectBrowserSelectedObjects();

        if (objectBrowserSelectedObjects == null) {
            return;
        }

        // all object browser selected object should be in same group
        Object parent = null;
        Object currentObject = null;
        for (Object object : objectBrowserSelectedObjects) {
            ServerObject serverObject = (ServerObject) object;

            if (parent == null) {
                parent = serverObject.getParent();
            }
            if (currentObject == null) {
                currentObject = object;
            }

            if (!isSimilarObjects(object, currentObject) || parent != serverObject.getParent()) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_OBJECT_PRIVILEGE_DIALOG_TITLE),
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.UPDATE_OBJECT_PRIVILEGE_DIALOG_DIFF_OBJECT_TYPE));
                return;
            }
        }

        GrantRevokeWizardDialog grantRevokeWizard = new GrantRevokeWizardDialog(shell, objectBrowserSelectedObjects,
                true);
        grantRevokeWizard.open();
    }

    private boolean isSimilarObjects(Object object, Object currentObject) {
        /*
         * Utility function to check if object and currentObject have
         * parent/grand-parent AND child/grand-child relation
         */
        if (object.getClass().isAssignableFrom(currentObject.getClass())
                || currentObject.getClass().isAssignableFrom(object.getClass())) {
            return true;
        }
        return false;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }
}
