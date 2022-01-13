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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSchema.
 *
 * @since 3.0.0
 */
public class DropSchema {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        UserNamespace ns = IHandlerUtilities.getSelectedUserNamespace();

        if (null == ns) {
            return;
        }

        int returnType = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_SCHEMA),
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_SCHEMA_CONFIRMATION, ns.getName()));
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForSchema(ns.getName(),
                ns.getDatabase().getName(), ns.getDatabase().getServerName(),
                IMessagesConstants.DROP_SCHEMA_PROGRESS_NAME);
        if (0 == returnType) {
            DropSchemaWorker worker = new DropSchemaWorker(progressLabel, ns);
            worker.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = UIElement.getInstance().getActivePartObject();
        if (obj instanceof SearchWindow) {
            return false;
        }

        Namespace ns = IHandlerUtilities.getSelectedUserNamespace();
        if (ns == null) {
            return false;
        }
        return true;
    }
}
