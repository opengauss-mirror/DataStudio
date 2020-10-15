/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.formatpref;

/**
 * 
 * Title: FormatterPreferenceKeys
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public interface FormatterPreferenceKeys {

    /** 
     * The Constant GEN_INDENT. 
     */
    public static final String GEN_INDENT = "editor.formatter.general.indent";

    /** 
     * The Constant GEN_RIGHT_MARGIN. 
     */
    public static final String GEN_RIGHT_MARGIN = "editor.formatter.general.rightMargin";

    /** 
     * The Constant GEN_TAB_CHAR. 
     */
    public static final String GEN_TAB_CHAR = "editor.formatter.general.useTabCharacter";

    /** 
     * The Constant GEN_CHAR_SIZE. 
     */
    public static final String GEN_CHAR_SIZE = "editor.formatter.general.tabCharacterSize";

    /** 
     * The Constant GEN_ALIGN_DECLARATION. 
     */
    public static final String GEN_ALIGN_DECLARATION = "editor.formatter.general.alignDeclarationGroups";

    /** 
     * The Constant GEN_ALIGN_ASSIGNMENT. 
     */
    public static final String GEN_ALIGN_ASSIGNMENT = "editor.formatter.general.alignAssignmentGroups";

    /** 
     * The Constant GEN_EMPTY_LINES. 
     */
    public static final String GEN_EMPTY_LINES = "editor.formatter.general.emptyLines";

    /** 
     * The Constant GEN_ITEMLIST_FORMAT. 
     */
    public static final String GEN_ITEMLIST_FORMAT = "editor.formatter.general.itemList.format";

    /**
     *  The Constant GEN_ITEMLIST_ALIGN. 
     */
    public static final String GEN_ITEMLIST_ALIGN = "editor.formatter.general.itemList.align";

    /** 
     * The Constant GEN_ITEMLIST_COMMA_AFTER. 
     */
    public static final String GEN_ITEMLIST_COMMA_AFTER = "editor.formatter.general.itemList.commaAfter";

    /** 
     * The Constant CTRL_STRUCT_THEN_ON_NEW_LINE. 
     */
    public static final String CTRL_STRUCT_THEN_ON_NEW_LINE = "editor.formatter.control.thenOnNewLine";

    /** 
     * The Constant CTRL_STRUCT_SPLIT_AND_OR. 
     */
    public static final String CTRL_STRUCT_SPLIT_AND_OR = "editor.formatter.control.splitAndOr";

    /** 
     * The Constant CTRL_STRUCT_AND_OR_AFTER_EXP. 
     */
    public static final String CTRL_STRUCT_AND_OR_AFTER_EXP = "editor.formatter.control.andOrAfterExpression";

    /** 
     * The Constant CTRL_STRUCT_LOOP_ONE_NEW_LINE. 
     */
    public static final String CTRL_STRUCT_LOOP_ONE_NEW_LINE = "editor.formatter.control.loopOnNewLine";

    /** 
     * The Constant DML_LEFT_ALIGN_KEYWORDS. 
     */
    public static final String DML_LEFT_ALIGN_KEYWORDS = "editor.formatter.dml.leftAlignKeywords";

    /** 
     * The Constant DML_LEFT_ALIGN_ITEMS. 
     */
    public static final String DML_LEFT_ALIGN_ITEMS = "editor.formatter.dml.leftAlignItems";

    /** 
     * The Constant DML_ON_ONE_LINE_IFPOSSIBLE. 
     */
    public static final String DML_ON_ONE_LINE_IFPOSSIBLE = "editor.formatter.dml.onOneLineIfPossible";

    /** 
     * The Constant DML_SELECT_FORMAT. 
     */
    public static final String DML_SELECT_FORMAT = "editor.formatter.dml.select.format";

    /** 
     * The Constant DML_SELECT_ALIGN. 
     */
    public static final String DML_SELECT_ALIGN = "editor.formatter.dml.select.align";

    /** 
     * The Constant DML_SELECT_COMMA_AFTER. 
     */
    public static final String DML_SELECT_COMMA_AFTER = "editor.formatter.dml.select.commaAfter";

    /** 
     * The Constant DML_SPLIT_AND_OR. 
     */
    public static final String DML_SPLIT_AND_OR = "editor.formatter.dml.whereSplitAndOr";

    /** 
     * The Constant DML_AND_OR_AFTER_EXP. 
     */
    public static final String DML_AND_OR_AFTER_EXP = "editor.formatter.dml.whereAndOrAfterExpression";

    /** 
     * The Constant DML_AND_OR_UDER_WHERE. 
     */
    public static final String DML_AND_OR_UDER_WHERE = "editor.formatter.dml.whereAndOrUnderWhere";

    /** 
     * The Constant DML_INSERT_FORMAT. 
     */
    public static final String DML_INSERT_FORMAT = "editor.formatter.dml.insert.format";

    /** 
     * The Constant DML_INSERT_COMMA_AFTER. 
     */
    public static final String DML_INSERT_COMMA_AFTER = "editor.formatter.dml.insert.commaAfter";

    /** 
     * The Constant DML_JOIN_SPLIT_BEFORE_ON. 
     */
    public static final String DML_JOIN_SPLIT_BEFORE_ON = "editor.formatter.dml.joinSplitBeforeOn";

    /** 
     * The Constant DML_UPDATE_FORMAT. 
     */
    public static final String DML_UPDATE_FORMAT = "editor.formatter.dml.update.format";

    /** 
     * The Constant DML_UPDATE_ALIGN. 
     */
    public static final String DML_UPDATE_ALIGN = "editor.formatter.dml.update.align";

    /** 
     * The Constant DML_UPDATE_COMMA_AFTER. 
     */
    public static final String DML_UPDATE_COMMA_AFTER = "editor.formatter.dml.update.commaAfter";

    /** 
     * The Constant PARAM_FORMAT. 
     */
    public static final String PARAM_FORMAT = "editor.formatter.parameterDeclaration.format";

    /** 
     * The Constant PARAM_ALIGN. 
     */
    public static final String PARAM_ALIGN = "editor.formatter.parameterDeclaration.align";

    /** 
     * The Constant PARAM_COMMA_AFTER. 
     */
    public static final String PARAM_COMMA_AFTER = "editor.formatter.parameterDeclaration.commaAfter";

    /** 
     * The Constant PARAM_AT_LEFT_MARGIN. 
     */
    public static final String PARAM_AT_LEFT_MARGIN = "editor.formatter.parameterDeclaration.atLeftMargin";

    /** 
     * The Constant RECORD_TYPE_ALIGN. 
     */
    public static final String RECORD_TYPE_ALIGN = "editor.formatter.recordFieldList.align";

    /** 
     * The Constant RECORD_TYPE_COMMA_AFTER. 
     */
    public static final String RECORD_TYPE_COMMA_AFTER = "editor.formatter.recordFieldList.commaAfter";
}
