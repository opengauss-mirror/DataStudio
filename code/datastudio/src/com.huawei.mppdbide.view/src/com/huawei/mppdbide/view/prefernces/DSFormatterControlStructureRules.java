/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * Title: DSFormatterControlStructureRules
 * 
 * Description: The Class DSFormatterControlStructureRules.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author aWX619007
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @since Nov 30, 2019
 */
public class DSFormatterControlStructureRules {
    /** 
     * The control structure then on new line. 
     */
    @Expose
    @SerializedName("THEN_on_new_line")
    private boolean controlStructureThenOnNewLine;

    /** 
     * The control structure split and or. 
     */
    @Expose
    @SerializedName("Split_at_zero-level_AND/OR")
    private boolean controlStructureSplitAndOr;

    /** 
     * The control structure and or after expression. 
     */
    @Expose
    @SerializedName("AND/OR_after_expression")
    private boolean controlStructureAndOrAfterExpression;

    /**
     *  The control structure loop on new line. 
     */
    @Expose
    @SerializedName("LOOP_on_new_line")
    private boolean controlStructureLoopOnNewLine;

    /**
     * Checks if is control structure then on new line.
     *
     * @return true, if is control structure then on new line
     */
    public boolean isControlStructureThenOnNewLine() {
        return controlStructureThenOnNewLine;
    }

    /**
     * Sets the control structure then on new line.
     *
     * @param controlStructureThenOnNewLine the new control structure then on
     * new line
     */
    public void setControlStructureThenOnNewLine(boolean controlStructureThenOnNewLine) {
        this.controlStructureThenOnNewLine = controlStructureThenOnNewLine;
    }

    /**
     * Checks if is control structure split and or.
     *
     * @return true, if is control structure split and or
     */
    public boolean isControlStructureSplitAndOr() {
        return controlStructureSplitAndOr;
    }

    /**
     * Sets the control structure split and or.
     *
     * @param controlStructureSplitAndOr the new control structure split and or
     */
    public void setControlStructureSplitAndOr(boolean controlStructureSplitAndOr) {
        this.controlStructureSplitAndOr = controlStructureSplitAndOr;
    }

    /**
     * Checks if is control structure and or after expression.
     *
     * @return true, if is control structure and or after expression
     */
    public boolean isControlStructureAndOrAfterExpression() {
        return controlStructureAndOrAfterExpression;
    }

    /**
     * Sets the control structure and or after expression.
     *
     * @param controlStructureAndOrAfterExpression the new control structure and
     * or after expression
     */
    public void setControlStructureAndOrAfterExpression(boolean controlStructureAndOrAfterExpression) {
        this.controlStructureAndOrAfterExpression = controlStructureAndOrAfterExpression;
    }

    /**
     * Checks if is control structure loop on new line.
     *
     * @return true, if is control structure loop on new line
     */
    public boolean isControlStructureLoopOnNewLine() {
        return controlStructureLoopOnNewLine;
    }

    /**
     * Sets the control structure loop on new line.
     *
     * @param controlStructureLoopOnNewLine the new control structure loop on
     * new line
     */
    public void setControlStructureLoopOnNewLine(boolean controlStructureLoopOnNewLine) {
        this.controlStructureLoopOnNewLine = controlStructureLoopOnNewLine;
    }
}
