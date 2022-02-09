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

package org.opengauss.mppdbide.view.synonym.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.synonym.olap.InitializeCreateSynonymWorker;

/**
 * 
 * Title: Class
 * 
 * Description: The Class CreateSynonymHandler.
 *
 * @since 3.0.0
 */
public class CreateSynonymHandler {
    /**
     * Execute.
     *
     * @param shell the shell
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Execute
    public void execute(final Shell shell) throws MPPDBIDEException {

        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != obj) {
            if (obj instanceof SynonymObjectGroup) {
                InitializeCreateSynonymWorker createSynonymWorker = new InitializeCreateSynonymWorker(shell,
                        IMessagesConstants.CREATE_NEW_SYNONYM, MPPDBIDEConstants.CANCELABLEJOB,
                        IMessagesConstants.CREATE_NEW_SYNONYM);
                createSynonymWorker.schedule();
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj == null) {
            return false;
        }
        if (obj instanceof SynonymObjectGroup) {
            return true;
        }
        return false;
    }
}
