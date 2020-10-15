/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;

/**
 * 
 * Title: class
 * 
 * Description: The Class TablespaceObjectGroup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class TablespaceObjectGroup extends OLAPObjectGroup<Tablespace> {

    /**
     * Instantiates a new tablespace object group.
     *
     * @param type the type
     * @param server the server
     */
    public TablespaceObjectGroup(OBJECTTYPE type, Server server) {
        super(type, server);
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return (Server) getParent();
    }
}
