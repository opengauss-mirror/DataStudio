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

package org.opengauss.mppdbide.view.createobj.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.INamespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.view.createobj.ICreateTable;
import org.opengauss.mppdbide.view.createobj.factory.CreateTableUiFactory;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.search.SearchWindow;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTableHandler.
 *
 * @since 3.0.0
 */
public class CreateTableHandler {

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        INamespace ns = IHandlerUtilities.getSelectedTableGroup();
        if (null != ns) {
            Server server = ns.getDatabase().getServer();
            ICreateTable createTableUIInitializer = CreateTableUiFactory.getCreateTableUIInitializer(shell, ns, server);
            if (createTableUIInitializer == null) {
                return;
            }
            createTableUIInitializer.init();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        INamespace ns = IHandlerUtilities.getSelectedTableGroup();
        return null != ns;
    }
}
