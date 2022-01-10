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

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DefaultParameter.
 * 
 */

public class DefaultParameter {

    private String defaultParameterName; // same as ParameterInputGridLayer.PARAMETER_NAME

    private String defaultParameterType;  // same as ParameterInputGridLayer.PARAMETER_TYPE

    private String defaultParameterValue; // same as ParameterInputGridLayer.PARAMETER_VALUE
    
    private PARAMETERTYPE defaultParameterMode;

    /**
     * Instantiates a new default parameter.
     *
     * @param defaultParameterName the default parameter name
     * @param defaultParameterType the default parameter type
     * @param defaultParameterValue the default parameter value
     * @param defaultParameterMode the default parameter mode
     */
    public DefaultParameter(String defaultParameterName, String defaultParameterType, String defaultParameterValue,
            PARAMETERTYPE defaultParameterMode) {
        this.defaultParameterName = defaultParameterName;
        this.defaultParameterType = defaultParameterType;
        this.defaultParameterValue = defaultParameterValue;
        this.defaultParameterMode = defaultParameterMode;
    }

    /**
     * Gets the default parameter name.
     *
     * @return the default parameter name
     */
    public String getDefaultParameterName() {
        return defaultParameterName;
    }

    /**
     * Sets the default parameter name.
     *
     * @param defaultParameterName the new default parameter name
     */
    public void setDefaultParameterName(String defaultParameterName) {
        this.defaultParameterName = defaultParameterName;
    }

    /**
     * Gets the default parameter type.
     *
     * @return the default parameter type
     */
    public String getDefaultParameterType() {
        return defaultParameterType;
    }

    /**
     * Sets the default parameter type.
     *
     * @param defaultParameterType the new default parameter type
     */
    public void setDefaultParameterType(String defaultParameterType) {
        this.defaultParameterType = defaultParameterType;
    }

    /**
     * Gets the default parameter value.
     *
     * @return the default parameter value
     */
    public String getDefaultParameterValue() {
        return defaultParameterValue;
    }

    /**
     * Sets the default parameter value.
     *
     * @param defaultParameterValue the new default parameter value
     */
    public void setDefaultParameterValue(String defaultParameterValue) {
        this.defaultParameterValue = defaultParameterValue;
    }

    /**
     * Gets the default parameter mode.
     *
     * @return the default parameter mode
     */
    public PARAMETERTYPE getDefaultParameterMode() {
        return defaultParameterMode;
    }

    /**
     * Sets the default parameter mode.
     *
     * @param defaultParameterMode the new default parameter mode
     */
    public void setDefaultParameterMode(PARAMETERTYPE defaultParameterMode) {
        this.defaultParameterMode = defaultParameterMode;
    }

}
