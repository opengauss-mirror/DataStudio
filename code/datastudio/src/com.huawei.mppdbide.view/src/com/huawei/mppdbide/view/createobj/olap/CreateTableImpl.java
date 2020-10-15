/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
