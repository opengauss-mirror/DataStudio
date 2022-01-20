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

import java.util.ArrayList;
import java.util.List;

/**
 * Title: CreateTriggerParam class
 *
 * @since 3.0.0
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
