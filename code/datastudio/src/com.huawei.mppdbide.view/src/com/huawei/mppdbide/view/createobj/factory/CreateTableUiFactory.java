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
 * @since 3.0.0
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
