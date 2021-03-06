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

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import org.opengauss.mppdbide.gauss.format.consts.FormatPaddingEnum;
import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.astnode.common.TCTEASTNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;

/**
 * 
 * Title: StmtKeywordAlignUtil
 *
 * @since 3.0.0
 */
public abstract class StmtKeywordAlignUtil {

    /**
     * Gets the max keyword width.
     *
     * @param selectStmt the select stmt
     * @param fmtOpt the fmt opt
     * @return the max keyword width
     */
    public static int getMaxKeywordWidth(TCustomSqlStatement selectStmt, FmtOptionsIf fmtOpt) {

        int maxKeywordLength = 0;
        int nodeLength = 0;

        FormatPaddingEnum paddingType = getPaddingType(fmtOpt);

        if (FormatPaddingEnum.NOPAD != paddingType) {

            TParseTreeNode nextAstNode = selectStmt.getStartNode();

            while (null != nextAstNode) {

                if (nextAstNode instanceof TCTEASTNode) {
                    nodeLength = getMaxKeywordWithOfCTENode((TCTEASTNode) nextAstNode, fmtOpt);
                } else {

                    TSqlNode startKeyworkNode = (TSqlNode) nextAstNode.getStartNode();
                    if (null == startKeyworkNode) {
                        break;
                    }
                    nodeLength = startKeyworkNode.getNodeText().length();
                }
                if (maxKeywordLength < nodeLength) {
                    maxKeywordLength = nodeLength;
                }

                nextAstNode = nextAstNode.getNextNode();
            }
        }

        return maxKeywordLength;

    }

    private static int getMaxKeywordWithOfCTENode(TCTEASTNode customAstNode, FmtOptionsIf fmtOpt) {
        int maxKeywordLength = 0;

        TExpression stmtExpression = customAstNode.getStmtExpression();
        if (null != stmtExpression) {
            List<TExpressionNode> expList = stmtExpression.getExpList();
            int nodeLength = 0;
            for (TExpressionNode expNode : expList) {
                TParseTreeNode customStmt = expNode.getCustomStmt();
                if (null != customStmt && customStmt instanceof TCustomSqlStatement) {
                    nodeLength = getMaxKeywordWidth((TCustomSqlStatement) customStmt, fmtOpt);
                    if (maxKeywordLength < nodeLength) {
                        maxKeywordLength = nodeLength;
                    }

                }
            }
        }

        return maxKeywordLength;

    }

    /**
     * Gets the padding string.
     *
     * @param paddingType the padding type
     * @param maxKeywordLength the max keyword length
     * @param startKeyworkNode the start keywork node
     * @return the padding string
     */
    public static String getPaddingString(FormatPaddingEnum paddingType, int maxKeywordLength,
            TSqlNode startKeyworkNode) {
        if (FormatPaddingEnum.RPAD == paddingType) {
            return StringUtils.leftPad(startKeyworkNode.getNodeText(), maxKeywordLength, " ");
        }
        return startKeyworkNode.getNodeText();
    }

    /**
     * Gets the padding type.
     *
     * @param fmtOpt the fmt opt
     * @return the padding type
     */
    public static FormatPaddingEnum getPaddingType(FmtOptionsIf fmtOpt) {
        if (fmtOpt.leftAlignKeywords() && fmtOpt.leftAlignItems()) {
            return FormatPaddingEnum.RPAD;
        } else if (!fmtOpt.leftAlignKeywords()) {
            return FormatPaddingEnum.LPAD;
        }
        return FormatPaddingEnum.NOPAD;
    }

}
