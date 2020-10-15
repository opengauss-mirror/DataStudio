/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IViewObjectGroups.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface IViewObjectGroups {

    /**
     * Gets the template code.
     *
     * @return the template code
     */
    String getTemplateCode();

    /**
     * Gets the database.
     *
     * @return the database
     */
    Database getDatabase();

    /**
     * Checks if is db connected.
     *
     * @return true, if is db connected
     */
    boolean isDbConnected();
}
