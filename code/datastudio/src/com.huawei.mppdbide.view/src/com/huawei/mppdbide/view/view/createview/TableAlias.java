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

package com.huawei.mppdbide.view.view.createview;

/**
 * Title: class
 * Description: The Class TableAlias.1
 *
 * @since 3.0.0
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
