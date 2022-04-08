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

package org.opengauss.mppdbide.gauss.format.processor.utils;

import org.opengauss.mppdbide.gauss.format.consts.FormatPaddingEnum;
import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: ProcessorUtils
 *
 * @since 3.0.0
 */
public class ProcessorUtils {

    /**
     * Adds the pre empty text.
     *
     * @param node the node
     * @param bean the bean
     */
    public static void addPreEmptyText(TParseTreeNode node, OptionsProcessData bean) {
        addPreText(node, " ", bean);
    }

    /**
     * Adds the pre text.
     *
     * @param node the node
     * @param preText the pre text
     * @param bean the bean
     */
    public static void addPreText(TParseTreeNode node, String preText, OptionsProcessData bean) {
        node.addPreText(preText);
        bean.addOffSet(preText.length());
    }

    /**
     * Adds the pre text.
     *
     * @param node the node
     * @param offset the offset
     * @param preText the pre text
     * @return the int
     */
    public static int addPreText(TParseTreeNode node, int offset, String preText) {
        int offSetRet = offset;
        if (null == node) {
            return offSetRet;
        }
        node.addPreText(preText);
        offSetRet += preText.length();

        return offSetRet;
    }

    /**
     * Adds the pre text.
     *
     * @param node the node
     * @param offSet the off set
     * @param preText the pre text
     * @return the int
     */
    public static int addPreText(TSqlNode node, int offSet, String preText) {
        int offSetRet = offSet;

        if (null == node) {
            return offSetRet;
        }

        node.addPreText(preText);
        offSetRet += preText.length();

        offSetRet += node.getNodeText().length();

        return offSetRet;
    }

    /**
     * Adds the new line before withindent.
     *
     * @param node the node
     * @param pData the data
     * @param options the options
     */
    public static void addNewLineBeforeWithindent(TParseTreeNode node, OptionsProcessData pData, FmtOptionsIf options) {
        addNewLineBefore(node, pData.getOffSet(), options);
        addIndentBefore(node, pData, options);

    }

    /**
     * Adds the new line before.
     *
     * @param node the node
     * @param offSet the off set
     * @param options the options
     */
    public static void addNewLineBefore(TParseTreeNode node, int offSet, FmtOptionsIf options) {

        node.addPreText(System.lineSeparator());

        StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        fillSBWithIndendationChars(offSet, lsb, options);
        node.addPreText(lsb.toString());

    }

    /**
     * Adds the indent before.
     *
     * @param node the node
     * @param pData the data
     * @param options the options
     */
    public static void addIndentBefore(TParseTreeNode node, OptionsProcessData pData, FmtOptionsIf options) {

        StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        fillSBWithIndendationChars(options.getIndend(), lsb, options);
        node.addPreText(lsb.toString());

        pData.addOffSet(options.getIndend());
        pData.setPreIndentOffSet(pData.getOffSet());
    }

    /**
     * Adds the new line after.
     *
     * @param node the node
     * @param offset the offset
     * @param options the options
     */
    public static void addNewLineAfter(TParseTreeNode node, int offset, FmtOptionsIf options) {
        node.addPostText(System.lineSeparator());
        StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        fillSBWithIndendationChars(offset, lsb, options);
        node.addPostText(lsb.toString());
    }

    /**
     * Adds the post text.
     *
     * @param node the node
     * @param pData the data
     * @param postText the post text
     */
    public static void addPostText(TParseTreeNode node, OptionsProcessData pData, String postText) {
        node.addPostText(postText);
        pData.addOffSet(postText.length());
    }

    /**
     * Format start node.
     *
     * @param select the select
     * @param options the options
     * @param pData the data
     */
    public static void formatStartNode(TSqlNode select, FmtOptionsIf options, OptionsProcessData pData) {

        int offsetLen = pData.getMaxKeywordLength();
        if (pData.getMaxKeywordLength() == 0) {
            offsetLen = select.getNodeText().length();
        }
        formatStartNode(select, options, pData.getMaxKeywordLength());
        pData.addOffSet(offsetLen);

    }

    /**
     * Format start node.
     *
     * @param select the select
     * @param options the options
     * @param maxKeyword the max keyword
     */
    public static void formatStartNode(TSqlNode select, FmtOptionsIf options, int maxKeyword) {

        FormatPaddingEnum paddingType = StmtKeywordAlignUtil.getPaddingType(options);

        int paddingLength = select.getNodeText().length() < maxKeyword ? maxKeyword - select.getNodeText().length() : 0;

        if (paddingLength > 0) {

            StringBuilder lsb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            fillSBWithIndendationChars(paddingLength, lsb, options);

            if (FormatPaddingEnum.RPAD == paddingType) {
                select.addPostText(lsb.toString());
            } else {
                select.addPreText(lsb.toString());
            }
        }
    }

    /**
     * Fill SB with indendation chars.
     *
     * @param offset the offset
     * @param sb the sb
     * @param options the options
     */
    public static void fillSBWithIndendationChars(int offset, StringBuilder sb, FmtOptionsIf options) {
        int actualOffset = offset;
        int numTabs = 0;

        int numSpaces = 0;
        if (options.useTabChar() && options.getTabCharSize() > 0) {
            numTabs = actualOffset / options.getTabCharSize();
            numSpaces = actualOffset % options.getTabCharSize();
        } else {
            numSpaces = actualOffset;
        }
        while (numTabs-- > 0) {
            sb.append("\t");
        }
        while (numSpaces-- > 0) {
            sb.append(" ");
        }
    }

    /**
     * Checks if is node list available.
     *
     * @param itemList the item list
     * @return true, if is node list available
     */
    public static boolean isNodeListAvailable(TParseTreeNodeList<?> itemList) {
        if (null != itemList && !itemList.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

}
