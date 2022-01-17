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

package com.huawei.mppdbide.view.prefernces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: DSFormatterDMLRules
 * 
 * Description: The Class DSFormatterDMLRules.
 *
 * @since 3.0.0
 */
public class DSFormatterDMLRules {
    /** 
     * The dml left align keywords. 
     */
    @Expose
    @SerializedName("Left_align_keywords")
    private boolean dmlLeftAlignKeywords;

    /** 
     * The dml left align items. 
     */
    @Expose
    @SerializedName("Left_align_items")
    private boolean dmlLeftAlignItems;

    /** 
     * The dml where split and or. 
     */
    @Expose
    @SerializedName("Split_at_zero-level_AND/OR")
    private boolean dmlWhereSplitAndOr;

    /** 
     * The dml where and or after expression. 
     */
    @Expose
    @SerializedName("AND/OR_after_expression")
    private boolean dmlWhereAndOrAfterExpression;

    /** 
     * The dml where and or under where. 
     */
    @Expose
    @SerializedName("AND/OR_under_where_clause")
    private boolean dmlWhereAndOrUnderWhere;

    /** 
     * The dml select format. 
     */
    @Expose
    @SerializedName("Select-Format")
    private int dmlSelectFormat;

    /** 
     * The dml select align. 
     */
    @Expose
    @SerializedName("Select-Align")
    private boolean dmlSelectAlign;

    /** 
     * The dml select comma after. 
     */
    @Expose
    @SerializedName("Select-Comma_after_item")
    private boolean dmlSelectCommaAfter;

    /** 
     * The dml insert format. 
     */
    @Expose
    @SerializedName("Insert-Format")
    private int dmlInsertFormat;

    /** 
     * The dml insert comma after.
     */
    @Expose
    @SerializedName("Insert-Comma_after_item")
    private boolean dmlInsertCommaAfter;

    /**
     *  The dml update format. 
     */
    @Expose
    @SerializedName("Update-Format")
    private int dmlUpdateFormat;

    /** 
     * The dml update align. 
     */
    @Expose
    @SerializedName("Update-Align")
    private boolean dmlUpdateAlign;

    /** 
     * The dml update comma after. 
     */
    @Expose
    @SerializedName("Update-Comma_after_item")
    private boolean dmlUpdateCommaAfter;

    /**
     * Checks if is dml left align keywords.
     *
     * @return true, if is dml left align keywords
     */
    public boolean isDmlLeftAlignKeywords() {
        return dmlLeftAlignKeywords;
    }

    /**
     * Sets the dml left align keywords.
     *
     * @param dmlLeftAlignKeywords the new dml left align keywords
     */
    public void setDmlLeftAlignKeywords(boolean dmlLeftAlignKeywords) {
        this.dmlLeftAlignKeywords = dmlLeftAlignKeywords;
    }

    /**
     * Checks if is dml left align items.
     *
     * @return true, if is dml left align items
     */
    public boolean isDmlLeftAlignItems() {
        return dmlLeftAlignItems;
    }

    /**
     * Sets the dml left align items.
     *
     * @param dmlLeftAlignItems the new dml left align items
     */
    public void setDmlLeftAlignItems(boolean dmlLeftAlignItems) {
        this.dmlLeftAlignItems = dmlLeftAlignItems;
    }

    /**
     * Checks if is dml where split and or.
     *
     * @return true, if is dml where split and or
     */
    public boolean isDmlWhereSplitAndOr() {
        return dmlWhereSplitAndOr;
    }

    /**
     * Sets the dml where split and or.
     *
     * @param dmlWhereSplitAndOr the new dml where split and or
     */
    public void setDmlWhereSplitAndOr(boolean dmlWhereSplitAndOr) {
        this.dmlWhereSplitAndOr = dmlWhereSplitAndOr;
    }

    /**
     * Checks if is dml where and or after expression.
     *
     * @return true, if is dml where and or after expression
     */
    public boolean isDmlWhereAndOrAfterExpression() {
        return dmlWhereAndOrAfterExpression;
    }

    /**
     * Sets the dml where and or after expression.
     *
     * @param dmlWhereAndOrAfterExpression the new dml where and or after
     * expression
     */
    public void setDmlWhereAndOrAfterExpression(boolean dmlWhereAndOrAfterExpression) {
        this.dmlWhereAndOrAfterExpression = dmlWhereAndOrAfterExpression;
    }

    /**
     * Checks if is dml where and or under where.
     *
     * @return true, if is dml where and or under where
     */
    public boolean isDmlWhereAndOrUnderWhere() {
        return dmlWhereAndOrUnderWhere;
    }

    /**
     * Sets the dml where and or under where.
     *
     * @param dmlWhereAndOrUnderWhere the new dml where and or under where
     */
    public void setDmlWhereAndOrUnderWhere(boolean dmlWhereAndOrUnderWhere) {
        this.dmlWhereAndOrUnderWhere = dmlWhereAndOrUnderWhere;
    }

    /**
     * Gets the dml select format.
     *
     * @return the dml select format
     */
    public int getDmlSelectFormat() {
        return dmlSelectFormat;
    }

    /**
     * Sets the dml select format.
     *
     * @param dmlSelectFormat the new dml select format
     */
    public void setDmlSelectFormat(int dmlSelectFormat) {
        this.dmlSelectFormat = dmlSelectFormat;
    }

    /**
     * Checks if is dml select align.
     *
     * @return true, if is dml select align
     */
    public boolean isDmlSelectAlign() {
        return dmlSelectAlign;
    }

    /**
     * Sets the dml select align.
     *
     * @param dmlSelectAlign the new dml select align
     */
    public void setDmlSelectAlign(boolean dmlSelectAlign) {
        this.dmlSelectAlign = dmlSelectAlign;
    }

    /**
     * Checks if is dml select comma after.
     *
     * @return true, if is dml select comma after
     */
    public boolean isDmlSelectCommaAfter() {
        return dmlSelectCommaAfter;
    }

    /**
     * Sets the dml select comma after.
     *
     * @param dmlSelectCommaAfter the new dml select comma after
     */
    public void setDmlSelectCommaAfter(boolean dmlSelectCommaAfter) {
        this.dmlSelectCommaAfter = dmlSelectCommaAfter;
    }

    /**
     * Gets the dml insert format.
     *
     * @return the dml insert format
     */
    public int getDmlInsertFormat() {
        return dmlInsertFormat;
    }

    /**
     * Sets the dml insert format.
     *
     * @param dmlInsertFormat the new dml insert format
     */
    public void setDmlInsertFormat(int dmlInsertFormat) {
        this.dmlInsertFormat = dmlInsertFormat;
    }

    /**
     * Checks if is dml insert comma after.
     *
     * @return true, if is dml insert comma after
     */
    public boolean isDmlInsertCommaAfter() {
        return dmlInsertCommaAfter;
    }

    /**
     * Sets the dml insert comma after.
     *
     * @param dmlInsertCommaAfter the new dml insert comma after
     */
    public void setDmlInsertCommaAfter(boolean dmlInsertCommaAfter) {
        this.dmlInsertCommaAfter = dmlInsertCommaAfter;
    }

    /**
     * Gets the dml update format.
     *
     * @return the dml update format
     */
    public int getDmlUpdateFormat() {
        return dmlUpdateFormat;
    }

    /**
     * Sets the dml update format.
     *
     * @param dmlUpdateFormat the new dml update format
     */
    public void setDmlUpdateFormat(int dmlUpdateFormat) {
        this.dmlUpdateFormat = dmlUpdateFormat;
    }

    /**
     * Checks if is dml update align.
     *
     * @return true, if is dml update align
     */
    public boolean isDmlUpdateAlign() {
        return dmlUpdateAlign;
    }

    /**
     * Sets the dml update align.
     *
     * @param dmlUpdateAlign the new dml update align
     */
    public void setDmlUpdateAlign(boolean dmlUpdateAlign) {
        this.dmlUpdateAlign = dmlUpdateAlign;
    }

    /**
     * Checks if is dml update comma after.
     *
     * @return true, if is dml update comma after
     */
    public boolean isDmlUpdateCommaAfter() {
        return dmlUpdateCommaAfter;
    }

    /**
     * Sets the dml update comma after.
     *
     * @param dmlUpdateCommaAfter the new dml update comma after
     */
    public void setDmlUpdateCommaAfter(boolean dmlUpdateCommaAfter) {
        this.dmlUpdateCommaAfter = dmlUpdateCommaAfter;
    }
}
