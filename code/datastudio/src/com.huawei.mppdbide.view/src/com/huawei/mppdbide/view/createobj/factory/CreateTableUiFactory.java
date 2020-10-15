/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createobj.factory;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.view.createobj.ICreateTable;
import com.huawei.mppdbide.view.createobj.olap.CreateTableImpl;

/**
 * Title: CreateTableUiFactory
 * 
 * Description:A factory for creating CreateTableUi objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public final class CreateTableUiFactory {

    private CreateTableUiFactory() {

    }

    /**
     * Gets the creates the table UI initializer.
     *
     * @param shell the shell
     * @param namespace the namespace
     * @param server the server
     * @return the creates the table UI initializer
     */
    public static ICreateTable getCreateTableUIInitializer(Shell shell, INamespace namespace, Server server) {

        DBTYPE dbType = namespace.getDatabase().getDBType();

        if (dbType == DBTYPE.OPENGAUSS) {
            return new CreateTableImpl(shell, namespace, server);
        }
        return null;

    }
}
