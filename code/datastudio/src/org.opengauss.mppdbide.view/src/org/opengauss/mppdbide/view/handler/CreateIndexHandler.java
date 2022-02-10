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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.bl.serverdatacache.groups.IndexList;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.table.CreateIndexDialog;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateIndexHandler.
 *
 * @since 3.0.0
 */
public class CreateIndexHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof IndexList) {
            TableMetaData tbl = ((IndexList) obj).getParent();
            Server server = tbl.getServer();

            CreateIndexDialog indexDialog = new CreateIndexDialog(shell, tbl, server);
            indexDialog.open();
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
        } else {
            TableMetaData tbl = ((IndexList) obj).getParent();
            if (tbl instanceof ForeignTable) {
                return false;
            } else {
                return true;
            }
        }
    }

}
