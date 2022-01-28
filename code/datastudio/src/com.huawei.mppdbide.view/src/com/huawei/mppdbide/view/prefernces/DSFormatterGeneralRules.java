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
 * Title: DSFormatterGeneralRules
 * 
 * Description: The Class DSFormatterGeneralRules.
 *
 * @since 3.0.0
 */
public class DSFormatterGeneralRules {
    /**
     * The general indent.
     */
    @Expose
    @SerializedName("Indent")
    private int generalIndent;

    /**
     * The general right margin.
     */
    @Expose
    @SerializedName("Right_margin")
    private int generalRightMargin;

    /**
     * The general use tab character.
     */
    @Expose
    @SerializedName("Use_tab_char")
    private boolean generalUseTabCharacter;

    /**
     * The general tab character size.
     */
    @Expose
    @SerializedName("Tab_character_size")
    private int generalTabCharacterSize;

    /**
     * The general align declaration groups.
     */
    @Expose
    @SerializedName("Align_declarations")
    private boolean generalAlignDeclarationGroups;

    /**
     * The general align assignment groups.
     */
    @Expose
    @SerializedName("Align_assignments")
    private boolean generalAlignAssignmentGroups;

    /**
     * The general item list format.
     */
    @Expose
    @SerializedName("Item_Lists-Format")
    private int generalItemListFormat;

    /**
     * The general item list align.
     */
    @Expose
    @SerializedName("Item_Lists-Align")
    private boolean generalItemListAlign;

    /**
     * The general item list comma after.
     */
    @Expose
    @SerializedName("Item_Lists-Comma_after_item")
    private boolean generalItemListCommaAfter;

    /**
     * Gets the general indent.
     *
     * @return the general indent
     */
    public int getGeneralIndent() {
        return generalIndent;
    }

    /**
     * Sets the general indent.
     *
     * @param generalIndent the new general indent
     */
    public void setGeneralIndent(int generalIndent) {
        this.generalIndent = generalIndent;
    }

    /**
     * Gets the general right margin.
     *
     * @return the general right margin
     */
    public int getGeneralRightMargin() {
        return generalRightMargin;
    }

    /**
     * Sets the general right margin.
     *
     * @param genaralRightMargin the new general right margin
     */
    public void setGeneralRightMargin(int genaralRightMargin) {
        this.generalRightMargin = genaralRightMargin;
    }

    /**
     * Checks if is general use tab character.
     *
     * @return true, if is general use tab character
     */
    public boolean isGeneralUseTabCharacter() {
        return generalUseTabCharacter;
    }

    /**
     * Sets the general use tab character.
     *
     * @param generalUseTabCharacter the new general use tab character
     */
    public void setGeneralUseTabCharacter(boolean generalUseTabCharacter) {
        this.generalUseTabCharacter = generalUseTabCharacter;
    }

    /**
     * Gets the general tab character size.
     *
     * @return the general tab character size
     */
    public int getGeneralTabCharacterSize() {
        return generalTabCharacterSize;
    }

    /**
     * Sets the general tab character size.
     *
     * @param generalTabCharacterSize the new general tab character size
     */
    public void setGeneralTabCharacterSize(int generalTabCharacterSize) {
        this.generalTabCharacterSize = generalTabCharacterSize;
    }

    /**
     * Checks if is general align declaration groups.
     *
     * @return true, if is general align declaration groups
     */
    public boolean isGeneralAlignDeclarationGroups() {
        return generalAlignDeclarationGroups;
    }

    /**
     * Sets the general align declaration groups.
     *
     * @param generalAlignDeclarationGroups the new general align declaration
     * groups
     */
    public void setGeneralAlignDeclarationGroups(boolean generalAlignDeclarationGroups) {
        this.generalAlignDeclarationGroups = generalAlignDeclarationGroups;
    }

    /**
     * Checks if is general align assignment groups.
     *
     * @return true, if is general align assignment groups
     */
    public boolean isGeneralAlignAssignmentGroups() {
        return generalAlignAssignmentGroups;
    }

    /**
     * Sets the general align assignment groups.
     *
     * @param generalAlignAssignmentGroups the new general align assignment
     * groups
     */
    public void setGeneralAlignAssignmentGroups(boolean generalAlignAssignmentGroups) {
        this.generalAlignAssignmentGroups = generalAlignAssignmentGroups;
    }

    /**
     * Gets the general item list format.
     *
     * @return the general item list format
     */
    public int getGeneralItemListFormat() {
        return generalItemListFormat;
    }

    /**
     * Sets the general item list format.
     *
     * @param generalItemListFormat the new general item list format
     */
    public void setGeneralItemListFormat(int generalItemListFormat) {
        this.generalItemListFormat = generalItemListFormat;
    }

    /**
     * Checks if is general item list align.
     *
     * @return true, if is general item list align
     */
    public boolean isGeneralItemListAlign() {
        return generalItemListAlign;
    }

    /**
     * Sets the general item list align.
     *
     * @param generalItemListAlign the new general item list align
     */
    public void setGeneralItemListAlign(boolean generalItemListAlign) {
        this.generalItemListAlign = generalItemListAlign;
    }

    /**
     * Checks if is general item list comma after.
     *
     * @return true, if is general item list comma after
     */
    public boolean isGeneralItemListCommaAfter() {
        return generalItemListCommaAfter;
    }

    /**
     * Sets the general item list comma after.
     *
     * @param generalItemListCommaAfter the new general item list comma after
     */
    public void setGeneralItemListCommaAfter(boolean generalItemListCommaAfter) {
        this.generalItemListCommaAfter = generalItemListCommaAfter;
    }

    /**
     * validateGeneralIndent
     * 
     * @return boolean value
     */
    public boolean validateGeneralIndent() {
        return generalIndent < 0 || generalIndent > DSFormatterPreferencePage.INDENT_TAB_LIMIT;
    }

    /**
     * validateGeneralRightMargin
     * 
     * @return boolean value
     */
    public boolean validateGeneralRightMargin() {
        return generalRightMargin < 0 || generalRightMargin > DSFormatterPreferencePage.RIGHT_MARGIN_LIMIT;
    }

    /**
     * validateGeneralTabCharacterSize
     * 
     * @return boolean value
     */
    public boolean validateGeneralTabCharacterSize() {
        return generalTabCharacterSize < 0 || generalTabCharacterSize > DSFormatterPreferencePage.INDENT_TAB_LIMIT;
    }

}
