/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createobj.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.view.createobj.ICreateTable;
import com.huawei.mppdbide.view.createobj.factory.CreateTableUiFactory;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTableHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
