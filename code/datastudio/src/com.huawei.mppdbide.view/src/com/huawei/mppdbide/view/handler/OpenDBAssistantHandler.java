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
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.DbAssistantSupportedVersions;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpenDBAssistantHandler.
 *
 * @since 3.0.0
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
