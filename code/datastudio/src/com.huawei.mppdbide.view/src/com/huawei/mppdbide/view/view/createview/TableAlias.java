/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class TableAlias.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class TableAlias {
    private String tableFullName;
    private String tableAliasName;

    public TableAlias (String tableFullName, String tableAliasName) {
        this.tableFullName = tableFullName;
        this.tableAliasName = tableAliasName;
    }

    /**
     * Gets table full name
     *
     * @return String the table full name
     */
    public String getTableFullName () {
        return tableFullName;
    }

    /**
     * Gets table alias name
     *
     * @return String the table alias name
     */
    public String getTableAliasName () {
        return tableAliasName;
    }
}
