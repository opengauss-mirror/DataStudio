/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.option;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.gauss.format.consts.EmptyLinesEnum;
import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.consts.ListItemOptionsEnum;
import com.huawei.mppdbide.view.formatpref.FormatterPreferenceKeys;

/**
 * Title: FmtOptions Description: Copyright (c) Huawei Technologies Co., Ltd.
 * 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class FmtOptions implements FmtOptionsIf {
    private PreferenceStore prefStore = null;

    /**
     * get preference store
     * 
     * @return get preference store
     */
    @Override
    public PreferenceStore getPrefStore() {
        return prefStore;
    }

    /**
     * set preference store
     * 
     * @param prefStore set preference store
     */
    @Override
    public void setPrefStore(PreferenceStore prefStore) {
        this.prefStore = prefStore;
    }

    /**
     * return true if keywords are left align
     * 
     * @return true if keywords are left align
     */
    @Override
    public boolean leftAlignKeywords() {
        return prefStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS);
    }

    /**
     * return true if items are left align
     * 
     * @return return true if items are left align
     */
    @Override
    public boolean leftAlignItems() {
        return prefStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_ITEMS);
    }

    /**
     * return the list of Item options
     * 
     * @param formatItemsType formatItemsType for which need ListItemOptionsEnum
     * @return return the list of Item options
     */
    @Override
    public ListItemOptionsEnum getItemOption(FormatItemsType formatItemsType) {
        return getFormatOptionByType(formatItemsType);
    }

    private ListItemOptionsEnum getFormatOptionByType(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case SELECT: {
                return getFormatOption(prefStore.getInt(FormatterPreferenceKeys.DML_SELECT_FORMAT));
            }
            case INSERT: {
                return getFormatOption(prefStore.getInt(FormatterPreferenceKeys.DML_INSERT_FORMAT));
            }
            case UPDATE: {
                return getFormatOption(prefStore.getInt(FormatterPreferenceKeys.DML_UPDATE_FORMAT));
            }
            default: {
                return getFormatOptionByTypeSecond(formatItemsType);
            }
        }
    }
    
    private ListItemOptionsEnum getFormatOptionByTypeSecond(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case PARAMETER: {
                return getFormatOption(prefStore.getInt(FormatterPreferenceKeys.PARAM_FORMAT));
            }
            case FIXED_ONEITEMPERLINE: {
                return ListItemOptionsEnum.ONEITEMPERLINE;
            }
            default: {
                return getFormatOption(prefStore.getInt(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT));
            }
        }
    }

    private ListItemOptionsEnum getFormatOption(int option) {
        switch (option) {
            case 0: {
                return ListItemOptionsEnum.ONEITEMPERLINE;
            }
            case 1: {
                return ListItemOptionsEnum.ONELINE;
            }
            case 2: {
                return ListItemOptionsEnum.FIT;
            }
            default: {
                return ListItemOptionsEnum.ONLINEIFPOSSIBLE;
            }
        }
    }

    /**
     * return right margin
     * 
     * @return return right margin
     */
    @Override
    public int getRightMargin() {
        return prefStore.getInt(FormatterPreferenceKeys.GEN_RIGHT_MARGIN);
    }

    /**
     * return true if comma is there after format item
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return true if comma is there after format item
     */
    @Override
    public boolean getCommaAfteritem(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case SELECT: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER);
            }
            case INSERT: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER);
            }
            default: {
                return getCommaAfteritemSecond(formatItemsType);
            }
        }
    }
    
    private boolean getCommaAfteritemSecond(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case UPDATE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER);
            }
            case PARAMETER: {
                return prefStore.getBoolean(FormatterPreferenceKeys.PARAM_COMMA_AFTER);
            }
            case RECORD_TYPE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.RECORD_TYPE_COMMA_AFTER);
            }
            default: {
                return prefStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER);
            }
        }
    }

    /**
     * return true if Tab char is used
     * 
     * @return return true if Tab char is used
     */
    @Override
    public boolean useTabChar() {
        return prefStore.getBoolean(FormatterPreferenceKeys.GEN_TAB_CHAR);
    }

    /**
     * return the size of tab char
     * 
     * @return return the size of tab char
     */
    @Override
    public int getTabCharSize() {
        return prefStore.getInt(FormatterPreferenceKeys.GEN_CHAR_SIZE);
    }

    /**
     * return true if item is aligned
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return return true if item is aligned
     */
    @Override
    public boolean isAlign(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case SELECT: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_ALIGN);
            }
            case INSERT: {
                return false;
            }
            case UPDATE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_ALIGN);
            }
            case PARAMETER: {
                return prefStore.getBoolean(FormatterPreferenceKeys.PARAM_ALIGN);
            }
            case RECORD_TYPE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.RECORD_TYPE_ALIGN);
            }
            default: {
                return prefStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN);
            }
        }
    }

    /**
     * return true is item can be split at zero level
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return true is item can be split at zero level
     */
    @Override
    public boolean splitAtZeroLevel(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case CONTROL_STRUCTURE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR);
            }
            default: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_SPLIT_AND_OR);
            }
        }
    }

    /**
     * return true if AND/OR is there after expr
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return return true if AND/OR is there after expr
     */
    @Override
    public boolean andOrAfterExp(FormatItemsType formatItemsType) {
        switch (formatItemsType) {
            case CONTROL_STRUCTURE: {
                return prefStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP);
            }
            default: {
                return prefStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP);
            }
        }
    }

    /**
     * return true if AND/OR is there under Where
     * 
     * @return return true if AND/OR is there under Where
     */
    @Override
    public boolean andOrUnderWhere() {
        return prefStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE);
    }

    /**
     * return Indendent space
     * 
     * @return return Indendent space
     */
    @Override
    public int getIndend() {
        return prefStore.getInt(FormatterPreferenceKeys.GEN_INDENT);
    }

    /**
     * return true if join can be split before on
     * 
     * @return return true if join can be split before on
     */
    @Override
    public boolean splitJoinBeforeOn() {
        return prefStore.getBoolean(FormatterPreferenceKeys.DML_JOIN_SPLIT_BEFORE_ON);
    }

    /**
     * return statement style
     * 
     * @return return statement style
     */
    @Override
    public boolean getStatementStyle() {
        return prefStore.getBoolean(FormatterPreferenceKeys.DML_ON_ONE_LINE_IFPOSSIBLE);
    }

    /**
     * return true if list is there after left margin
     * 
     * @return return true if list is there after left margin
     */
    @Override
    public boolean isListAtLeftMargin() {
        return prefStore.getBoolean(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN);
    }

    /**
     * return true id declaration stmt is aligned
     * 
     * @return return true id declaration stmt is aligned
     */
    @Override
    public boolean isAlignDeclaration() {
        return prefStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_DECLARATION);
    }

    /**
     * return true if assignment stmt is aligned
     * 
     * @return return true if assignment stmt is aligned
     */
    @Override
    public boolean isAlignAssignment() {
        return prefStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT);
    }

    /**
     * return empty lines
     * 
     * @return return empty lines
     */
    @Override
    public EmptyLinesEnum getEmptyLines() {
        switch (prefStore.getInt(FormatterPreferenceKeys.GEN_EMPTY_LINES)) {
            case 1: {
                return EmptyLinesEnum.REMOVE;
            }
            case 2: {
                return EmptyLinesEnum.MERGE;
            }
            case 3: {
                return EmptyLinesEnum.PRESERVE;
            }
            default: {
                return EmptyLinesEnum.PRESERVE;
            }
        }
    }

    /**
     * check whether Then is there on NewLine
     * 
     * @return return true when new line on control structure
     */
    @Override
    public boolean isThenOnNewLine() {
        return prefStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE);
    }

    /**
     * check whether Loop is there on NewLine
     * 
     * @return return true when LOOP on newline
     */
    @Override
    public boolean isLoopOnNewLine() {
        return prefStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE);
    }
}
