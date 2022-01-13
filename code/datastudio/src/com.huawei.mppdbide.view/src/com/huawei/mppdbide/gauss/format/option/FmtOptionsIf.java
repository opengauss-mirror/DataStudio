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

package com.huawei.mppdbide.gauss.format.option;

import org.eclipse.jface.preference.PreferenceStore;

import com.huawei.mppdbide.gauss.format.consts.EmptyLinesEnum;
import com.huawei.mppdbide.gauss.format.consts.FormatItemsType;
import com.huawei.mppdbide.gauss.format.consts.ListItemOptionsEnum;

/**
 * Title: FmtOptions
 *
 * @since 3.0.0
 */
public interface FmtOptionsIf {
    /**
     * get preference store
     * 
     * @return get preference store
     */
    PreferenceStore getPrefStore();

    /**
     * set preference store
     * 
     * @param prefStore set preference store
     */
    void setPrefStore(PreferenceStore prefStore);

    /**
     * return true if keywords are left align
     * 
     * @return true if keywords are left align
     */
    boolean leftAlignKeywords();

    /**
     * return true if items are left align
     * 
     * @return return true if items are left align
     */
    boolean leftAlignItems();

    /**
     * return the list of Item options
     * 
     * @param formatItemsType formatItemsType for which need ListItemOptionsEnum
     * @return return the list of Item options
     */
    ListItemOptionsEnum getItemOption(FormatItemsType formatItemsType);

    /**
     * return right margin
     * 
     * @return return right margin
     */
    int getRightMargin();

    /**
     * return true if comma is there after format item
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return true if comma is there after format item
     */
    boolean getCommaAfteritem(FormatItemsType formatItemsType);

    /**
     * return true if Tab char is used
     * 
     * @return return true if Tab char is used
     */
    boolean useTabChar();

    /**
     * return the size of tab char
     * 
     * @return return the size of tab char
     */
    int getTabCharSize();

    /**
     * return true if item is aligned
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return return true if item is aligned
     */
    boolean isAlign(FormatItemsType formatItemsType);

    /**
     * return true is item can be split at zero level
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return true is item can be split at zero level
     */
    boolean splitAtZeroLevel(FormatItemsType formatItemsType);

    /**
     * return true if AND/OR is there after expr
     * 
     * @param formatItemsType FormatItemsType of the statement
     * @return return true if AND/OR is there after expr
     */
    boolean andOrAfterExp(FormatItemsType formatItemsType);

    /**
     * return true if AND/OR is there under Where
     * 
     * @return return true if AND/OR is there under Where
     */
    boolean andOrUnderWhere();

    /**
     * return Indendent space
     * 
     * @return return Indendent space
     */
    int getIndend();

    /**
     * return true if join can be split before on
     * 
     * @return return true if join can be split before on
     */
    boolean splitJoinBeforeOn();

    /**
     * return statement style
     * 
     * @return return statement style
     */
    boolean getStatementStyle();

    /**
     * return true if list is there after left margin
     * 
     * @return return true if list is there after left margin
     */
    boolean isListAtLeftMargin();

    /**
     * return true id declaration stmt is aligned
     * 
     * @return return true id declaration stmt is aligned
     */
    boolean isAlignDeclaration();

    /**
     * return true if assignment stmt is aligned
     * 
     * @return return true if assignment stmt is aligned
     */
    boolean isAlignAssignment();

    /**
     * return empty lines
     * 
     * @return return empty lines
     */
    EmptyLinesEnum getEmptyLines();

    /**
     * check whether Then is there on NewLine
     * 
     * @return return true when new line on control structure
     */
    boolean isThenOnNewLine();

    /**
     * check whether Loop is there on NewLine
     * 
     * @return return true when LOOP on newline
     */
    boolean isLoopOnNewLine();

}