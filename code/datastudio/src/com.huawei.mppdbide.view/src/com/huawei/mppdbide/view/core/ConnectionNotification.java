/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import com.huawei.mppdbide.bl.serverdatacache.Database;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionNotification.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
