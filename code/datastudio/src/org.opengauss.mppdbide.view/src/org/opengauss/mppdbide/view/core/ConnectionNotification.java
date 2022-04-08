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

package org.opengauss.mppdbide.view.core;

import org.opengauss.mppdbide.bl.serverdatacache.Database;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionNotification.
 *
 * @since 3.0.0
 */
public class ConnectionNotification {

    private Database db;

    /**
     * Instantiates a new connection notification.
     *
     * @param db the db
     */
    public ConnectionNotification(Database db) {
        this.db = db;
    }

    /**
     * Loadnotification.
     */
    public void loadnotification() {
        LastLoginSecurityPopupJob loginWorker = new LastLoginSecurityPopupJob(db);
        loginWorker.schedule();
    }

}
