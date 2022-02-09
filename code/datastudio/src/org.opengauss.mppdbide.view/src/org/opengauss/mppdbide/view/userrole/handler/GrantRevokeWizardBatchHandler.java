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

import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.userrole.GrantRevokeWizardDialog;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class GrantRevokeWizardBatchHandler.
 *
 * @since 3.0.0
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
