/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: DSFormatterParameterDeclarationRules
 * 
 * Description: The Class DSFormatterParameterDeclarationRules.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class DSFormatterParameterDeclarationRules {
    /** 
     * The parameter format. 
     */
    @Expose
    @SerializedName("Format")
    private int parameterFormat;

    /** 
     * The parameter align. 
     */
    @Expose
    @SerializedName("Align_datatypes")
    private boolean parameterAlign;

    /** 
     * The parameter comma after. 
     */
    @Expose
    @SerializedName("Comma_after_datatype")
    private boolean parameterCommaAfter;

    /** 
     * The parameter at left margin. 
     */
    @Expose
    @SerializedName("List_at_left margin")
    private boolean parameterAtLeftMargin;

    /**
     * Gets the parameter format.
     *
     * @return the parameter format
     */
    public int getParameterFormat() {
        return parameterFormat;
    }

    /**
     * Sets the parameter format.
     *
     * @param parameterFormat the new parameter format
     */
    public void setParameterFormat(int parameterFormat) {
        this.parameterFormat = parameterFormat;
    }

    /**
     * Checks if is parameter align.
     *
     * @return true, if is parameter align
     */
    public boolean isParameterAlign() {
        return parameterAlign;
    }

    /**
     * Sets the parameter align.
     *
     * @param parameterAlign the new parameter align
     */
    public void setParameterAlign(boolean parameterAlign) {
        this.parameterAlign = parameterAlign;
    }

    /**
     * Checks if is parameter comma after.
     *
     * @return true, if is parameter comma after
     */
    public boolean isParameterCommaAfter() {
        return parameterCommaAfter;
    }

    /**
     * Sets the parameter comma after.
     *
     * @param parameterCommaAfter the new parameter comma after
     */
    public void setParameterCommaAfter(boolean parameterCommaAfter) {
        this.parameterCommaAfter = parameterCommaAfter;
    }

    /**
     * Checks if is parameter at left margin.
     *
     * @return true, if is parameter at left margin
     */
    public boolean isParameterAtLeftMargin() {
        return parameterAtLeftMargin;
    }

    /**
     * Sets the parameter at left margin.
     *
     * @param parameterAtLeftMargin the new parameter at left margin
     */
    public void setParameterAtLeftMargin(boolean parameterAtLeftMargin) {
        this.parameterAtLeftMargin = parameterAtLeftMargin;
    }
}
