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

package com.huawei.mppdbide.view.createobj.olap;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.view.createobj.ICreateTable;
import com.huawei.mppdbide.view.ui.table.CreateTable;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateTableImpl.
 *
 * @since 3.0.0
 */
public class CreateTableImpl implements ICreateTable {

    private Shell shell;
    private Namespace namespace;
    private Server server;

    /**
     * Instantiates a new creates the table impl.
     *
     * @param shell the shell
     * @param namespace the namespace
     * @param server the server
     */
    public CreateTableImpl(Shell shell, INamespace namespace, Server server) {
        this.shell = shell;
        this.namespace = (Namespace) namespace;
        this.server = server;
    }

    /**
     * Inits the.
     */
    @Override
    public void init() {
        CreateTable dlog = new CreateTable(shell, server, namespace);
        dlog.open();
    }

}
