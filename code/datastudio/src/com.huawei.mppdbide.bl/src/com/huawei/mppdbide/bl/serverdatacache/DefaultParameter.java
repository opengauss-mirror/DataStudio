/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DefaultParameter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
