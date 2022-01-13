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

package com.huawei.mppdbide.view.createfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: ListDebugSourceDataAdapter for use
 *
 * @since 3.0.0
 */
public class CreateFunctionParam {
    /**
     * Invalid param name
     */
    public static final String DEFAULT_PARAM_NAME = "<>";
    private static final int IDX_MODE = 1;
    private static final int IDX_TYPE = 2;
    private List<String> dataArrays = new ArrayList<>(4);
    private CreateFunctionParamsTitle titles;
    private CreateFunctionRelyInfo info;
    private Map<Integer, String[]> comboValues = new HashMap<>();
    private int indexOfMode = 0;
    private int indexOfType = 0;

    public CreateFunctionParam(CreateFunctionRelyInfo info, CreateFunctionParamsTitle titles) {
        this(info, titles, DEFAULT_PARAM_NAME, "");
    }

    public CreateFunctionParam(CreateFunctionRelyInfo info,
            CreateFunctionParamsTitle titles,
            String name,
            String dataDefault) {
        this.info = info;
        this.titles = titles;
        for (int i = 0, n = this.titles.getTitles().size(); i < n; i++) {
            comboValues.put(i, getSupportItems(i));
        }
        dataArrays.add(name);
        dataArrays.add(comboValues.get(1)[indexOfMode]);
        dataArrays.add(comboValues.get(2)[indexOfType]);
        dataArrays.add(dataDefault);
    }

    /**
     * Get value
     *
     * @param int the title index
     * @return Object the Object
     */
    public Object getValue(int titleIndex) {
        return dataArrays.get(titleIndex);
    }

    /**
     * Get combo value
     *
     * @param int the title index
     * @return Integer the combo value
     */
    public Integer getComboValue(int titleIndex) {
        if (titleIndex == IDX_MODE) {
            return indexOfMode;
        } else if (titleIndex == IDX_TYPE) {
            return indexOfType;
        } else {
            // can't run here!
            return 0;
        }
    }

    /**
     * Set combo value
     *
     * @param int the title index
     * @param int the index value
     */
    public void setComboValue(int titleIndex, int indexValue) {
        if (indexValue == -1) {
            return;
        }
        String value = comboValues.get(titleIndex)[indexValue];
        if (titleIndex == IDX_MODE) {
            indexOfMode = indexValue;
        } else if (titleIndex == IDX_TYPE) {
            indexOfType = indexValue;
        } else {
            // can't run here
        }
        setObject(titleIndex, value);
    }

    /**
     * Set object
     *
     * @param int the title index
     * @param String the value
     */
    public void setObject(int titleIndex, String value) {
        dataArrays.set(titleIndex, value);
    }

    /**
     * Is support combo
     *
     * @param int the title index
     * @return boolean true if is support combo
     */
    public boolean isSupportCombo(int titleIndex) {
        return titleIndex == IDX_MODE || titleIndex == IDX_TYPE;
    }

    /**
     * Get support items
     *
     * @param int the title index
     * @return String[] the support items
     */
    public String[] getSupportItems(int titleIndex) {
        if (titleIndex == IDX_MODE) {
            return new String[] {"IN", "OUT", "INOUT"};
        } else if (titleIndex == IDX_TYPE) {
            return info.getSupportTypes().toArray(new String[] {});
        } else {
            return new String[] {};
        }
    }

    /**
     * Get datas
     *
     * @return List<String> the data list
     */
    public List<String> getDatas() {
        return dataArrays;
    }
}
