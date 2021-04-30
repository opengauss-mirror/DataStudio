/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: CreateTriggerParam class
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-4-30]
 * @since 2021-4-30
 */
public class CreateTriggerParam {
    private List<String> dataArrays = new ArrayList<>(4);

    /**
     * Create trigger param
     *
     * @param String the column name
     * @param String the data type
     */
    public CreateTriggerParam(String columnName, String dataType) {
        dataArrays.add("");
        dataArrays.add(columnName);
        dataArrays.add(dataType);
        dataArrays.add("");
    }

    /**
     * Gets the value
     *
     * @param int the title index
     * @return String the value
     */
    public String getValue(int titleIndex) {
        return dataArrays.get(titleIndex);
    }

    /**
     * Sets the object
     *
     * @param int the title index
     * @param String the value
     */
    public void setObject(int titleIndex, String value) {
        dataArrays.set(titleIndex, value);
    }

    /**
     * Gets data
     *
     * @return List<String> the data list
     */
    public List<String> getDatas() {
        return dataArrays;
    }
}
