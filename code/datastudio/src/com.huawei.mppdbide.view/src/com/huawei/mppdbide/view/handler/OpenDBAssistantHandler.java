/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.DbAssistantSupportedVersions;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenDBAssistantHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class OpenDBAssistantHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {

        if (DBAssistantWindow.isEnable()) {
            if (DbAssistantSupportedVersions.isDbAssistantSupported(DBAssistantWindow.getDocRealVersion())) {
                DBAssistantWindow.setSupportedVersionEnableDisable(true);
            }

            DBAssistantWindow.toggleAssitant(DBAssistantWindow.getViewer(), DBAssistantWindow.getDatabase());
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        if (!DBAssistantWindow.isEnable()) {
            return false;
        }
        if (!DBAssistantWindow.isSupportedVersion()) {
            return false;
        }
        if (DBAssistantWindow.isVisible()) {
            return false;
        }
        return true;
    }
}
