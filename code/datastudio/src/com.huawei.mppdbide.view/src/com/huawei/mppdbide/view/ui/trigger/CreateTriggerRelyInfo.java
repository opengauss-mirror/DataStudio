/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;

/**
 * Title: Class
 * Description: the class CreateTriggerRelyInfo
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
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
