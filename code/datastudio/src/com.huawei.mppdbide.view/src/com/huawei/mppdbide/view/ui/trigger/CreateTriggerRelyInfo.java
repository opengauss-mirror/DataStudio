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

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;

/**
 * Title: Class
 * Description: the class CreateTriggerRelyInfo
 *
 * @since 3.0.0
 */
public interface CreateTriggerRelyInfo {
    /**
     * Gets namespace
     *
     * @return String the namespace
     */
    String getNamespaceName();

    /**
     * Sets the namespace
     *
     * @param Namespace the namespace to set
     */
    void setNamespace(Namespace namespace);

    /**
     * description: get line separator
     *
     * @return String the system line separator
     */
    default String getLineSeparator() {
        return System.lineSeparator();
    }

    /**
     *
     * description: get table names
     *
     * @return List<String> the table name list
     */
    List<String> getTableNames();

    /**
     * description: get function names
     *
     * @return List<String> the function name list
     */
    List<String> getFunctionNames();

    /**
     * description: get table columns
     *
     * @param String the table name
     * @return List<CreateTriggerParam> the trigger param list
     */
    List<CreateTriggerParam> getTableColumns(String tableName);

    /**
     * description: run source code, this interface must be async!
     *
     * @param String the source code
     */
    void execute(String sourceCode);
}
