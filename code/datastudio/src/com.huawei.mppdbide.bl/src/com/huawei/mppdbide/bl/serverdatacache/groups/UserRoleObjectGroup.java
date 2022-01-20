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

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserRoleObjectGroup.
 * 
 */

public class UserRoleObjectGroup extends OLAPObjectGroup<UserRole> {

    /**
     * Instantiates a new user role object group.
     *
     * @param type the type
     * @param server the server
     */
    public UserRoleObjectGroup(OBJECTTYPE type, Server server) {
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
