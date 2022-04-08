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

package org.opengauss.mppdbide.view.prefernces;

/**
 * 
 * Title: FormatterPreferenceKeys
 * 
 * Description: The Class FormatterPreferenceKeys.
 *
 * @since 3.0.0
 */
public interface FormatterPreferenceKeys {

    // General Keys
    /**
     * The gen indent. 
     */
    String GEN_INDENT_PREF = "editor.formatter.general.indent";

    /** 
     * The gen right margin. 
     */
    String GEN_RIGHT_MARGIN_PREF = "editor.formatter.general.rightMargin";

    /** 
     * The gen tab char. 
     */
    String GEN_TAB_CHAR_PREF = "editor.formatter.general.useTabCharacter";

    /** 
     * The gen char size. 
     */
    String GEN_CHAR_SIZE_PREF = "editor.formatter.general.tabCharacterSize";

    /** 
     * The gen align declaration. 
     */
    String GEN_ALIGN_DECLARATION_PREF = "editor.formatter.general.alignDeclarationGroups";

    /** 
     * The gen align assignment. 
     */
    String GEN_ALIGN_ASSIGNMENT_PREF = "editor.formatter.general.alignAssignmentGroups";

    /** 
     * The gen empty lines. 
     */
    String GEN_EMPTY_LINES_PRFE = "editor.formatter.general.emptyLines";

    /** 
     * The gen itemlist format. 
     */
    String GEN_ITEMLIST_FORMAT_PREF = "editor.formatter.general.itemList.format";

    /** 
     * The gen itemlist align.
     */
    String GEN_ITEMLIST_ALIGN_PREF = "editor.formatter.general.itemList.align";

    /** 
     * The gen itemlist comma after. 
     */
    String GEN_ITEMLIST_COMMA_AFTER_PREF = "editor.formatter.general.itemList.commaAfter";

    // Control structures Keys

    /** 
     * The ctrl struct then on new line.
     */
    String CTRL_STRUCT_THEN_ON_NEW_LINE_PREF = "editor.formatter.control.thenOnNewLine";

    /** 
     * The ctrl struct split and or. 
     */
    String CTRL_STRUCT_SPLIT_AND_OR_PREF = "editor.formatter.control.splitAndOr";

    /** 
     * The ctrl struct and or after exp. 
     */
    String CTRL_STRUCT_AND_OR_AFTER_EXP_PREF = "editor.formatter.control.andOrAfterExpression";

    /** 
     * The ctrl struct loop one new line.
     */
    String CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF = "editor.formatter.control.loopOnNewLine";

    // DML Keys

    /** 
     * The dml left align keywords. 
     */
    String DML_LEFT_ALIGN_KEYWORDS_PREF = "editor.formatter.dml.leftAlignKeywords";

    /** 
     * The dml left align items. 
     */
    String DML_LEFT_ALIGN_ITEMS_PREF = "editor.formatter.dml.leftAlignItems";

    /** 
     * The dml on one line ifpossible. 
     */
    String DML_ON_ONE_LINE_IFPOSSIBLE_PREF = "editor.formatter.dml.onOneLineIfPossible";

    /** 
     * The dml select format. 
     */
    String DML_SELECT_FORMAT_PREF = "editor.formatter.dml.select.format";

    /** 
     * The dml select align. 
     */
    String DML_SELECT_ALIGN_PREF = "editor.formatter.dml.select.align";

    /** 
     * The dml select comma after. 
     */
    String DML_SELECT_COMMA_AFTER_PREF = "editor.formatter.dml.select.commaAfter";

    /** 
     * The dml split and or. 
     */
    String DML_SPLIT_AND_OR_PREF = "editor.formatter.dml.whereSplitAndOr";

    /** 
     * The dml and or after exp. 
     */
    String DML_AND_OR_AFTER_EXP_PREF = "editor.formatter.dml.whereAndOrAfterExpression";

    /** 
     * The dml and or uder where. 
     */
    String DML_AND_OR_UDER_WHERE_PREF = "editor.formatter.dml.whereAndOrUnderWhere";

    /** 
     * The dml insert format. 
     */
    String DML_INSERT_FORMAT_PREF = "editor.formatter.dml.insert.format";

    /** 
     * The dml insert comma after.
     */
    String DML_INSERT_COMMA_AFTER_PREF = "editor.formatter.dml.insert.commaAfter";

    /** 
     * The dml join split before on. 
     */
    String DML_JOIN_SPLIT_BEFORE_ON_PREF = "editor.formatter.dml.joinSplitBeforeOn";

    /** 
     * The dml update format. 
     */
    String DML_UPDATE_FORMAT_PREF = "editor.formatter.dml.update.format";

    /** 
     * The dml update align. 
     */
    String DML_UPDATE_ALIGN_PREF = "editor.formatter.dml.update.align";

    /** 
     * The dml update comma after. 
     */
    String DML_UPDATE_COMMA_AFTER_PREF = "editor.formatter.dml.update.commaAfter";

    // Parameter Declaration Keys

    /** 
     * The param format. 
     */
    String PARAM_FORMAT_PREF = "editor.formatter.parameterDeclaration.format";

    /** 
     * The param align.
     */
    String PARAM_ALIGN_PREF = "editor.formatter.parameterDeclaration.align";

    /** 
     * The param comma after. 
     */
    String PARAM_COMMA_AFTER_PREF = "editor.formatter.parameterDeclaration.commaAfter";

    /** 
     * The param at left margin. 
     */
    String PARAM_AT_LEFT_MARGIN_PREF = "editor.formatter.parameterDeclaration.atLeftMargin";

    // Record Declaration Keys

    /** 
     * The record type align. 
     */
    String RECORD_TYPE_ALIGN_PREF = "editor.formatter.recordFieldList.align";

    /** 
     * The record type comma after. 
     */
    String RECORD_TYPE_COMMA_AFTER_PREF = "editor.formatter.recordFieldList.commaAfter";
}