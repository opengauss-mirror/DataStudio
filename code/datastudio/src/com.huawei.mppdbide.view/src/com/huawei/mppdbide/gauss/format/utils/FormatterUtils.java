/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.utils;

import java.util.List;
import java.util.ListIterator;

import com.huawei.mppdbide.gauss.format.consts.EmptyLinesEnum;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.utils.wrapper.FormatStringWrapper;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.parser.utils.FullNodeExpressionType;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TAbstractListItem;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpression;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TExpressionNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNodeList;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.TSqlNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullListNode;
import com.huawei.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;
import com.huawei.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * Title: FormatterUtils Description: Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class FormatterUtils {

    /**
     * prepareFormatString from the given Parse Object
     * 
     * @param selectAstNode parse object
     * @param formatWrapper format string to keep the Format String
     */
    public static void prepareFormatString(TParseTreeNode selectAstNode, FormatStringWrapper formatWrapper,
            FmtOptionsIf options) {
        prepareFormatString(selectAstNode, formatWrapper, true, options);
    }

    /**
     * prepareFormatString from the given Parse Object
     * 
     * @param selectAstNode parse object
     * @param formatWrapper format string to keep the Format String
     * @param isAllNodes to iterate all nodes or only current node
     */
    public static void prepareFormatString(TParseTreeNode selectAstNode, FormatStringWrapper formatWrapper,
            boolean isAllNodes, FmtOptionsIf options) {
        TParseTreeNode startKeyworkNode = selectAstNode.getStartNode();

        while (null != startKeyworkNode) {
            addStringToList(formatWrapper, startKeyworkNode.getPreText(), startKeyworkNode);
            if (startKeyworkNode instanceof TSqlNode) {
                String strNodeText = ((TSqlNode) startKeyworkNode).getNodeText();
                if (MPPDBIDEConstants.ESCAPE_FORWARDSLASH.equals(strNodeText)) {
                    formatWrapper.append(System.lineSeparator());
                }
                formatWrapper.append(strNodeText);
            } else if (isInstanceOfTreeParseNodeList(startKeyworkNode)) {
                TParseTreeNodeList<TAbstractListItem> list = (TParseTreeNodeList<TAbstractListItem>) startKeyworkNode;
                for (TAbstractListItem lTListItem : list.getResultList()) {
                    prepareFormatString(lTListItem, formatWrapper, options);
                }
            } else if (isExpression(startKeyworkNode)) {
                TExpression exp = (TExpression) startKeyworkNode;
                for (TExpressionNode expNode : exp.getExpList()) {
                    addStringToList(formatWrapper, expNode.getPreText(), expNode);
                    prepareFormatString(expNode, formatWrapper, options);
                }
            } else if (startKeyworkNode instanceof TFullStmt) {
                prepareFullStmtFormatString(formatWrapper, startKeyworkNode, options);
            } else {
                prepareFormatString(startKeyworkNode, formatWrapper, options);
            }

            addCommentsAndPostText(formatWrapper, startKeyworkNode, options);
            if (isAllNodes) {
                startKeyworkNode = startKeyworkNode.getNextNode();
            } else {
                startKeyworkNode = null;
            }

        }
    }

    private static boolean isExpression(TParseTreeNode startKeyworkNode) {
        return startKeyworkNode instanceof TExpression;
    }

    private static boolean isInstanceOfTreeParseNodeList(TParseTreeNode startKeyworkNode) {
        return startKeyworkNode instanceof TParseTreeNodeList;
    }

    private static void prepareFullStmtFormatString(FormatStringWrapper formatWrapper, TParseTreeNode startKeyworkNode,
            FmtOptionsIf options) {
        TFullStmt fullStmt = (TFullStmt) startKeyworkNode;
        EmptyLinesEnum emptyLines = options.getEmptyLines();
        for (TParseTreeNode parserFullStmtNode : fullStmt.getStmtList()) {
            if (parserFullStmtNode instanceof TFullListNode) {
                TFullListNode fullNodeList = (TFullListNode) parserFullStmtNode;
                addStringToList(formatWrapper, fullNodeList.getPreText(), fullNodeList);

                if (fullNodeList.getExpressionType() == FullNodeExpressionType.NEWLINES
                        && fullNodeList.getSqlTokenDataList().size() > 0 && emptyLines != EmptyLinesEnum.PRESERVE) {
                    if (emptyLines == EmptyLinesEnum.REMOVE) {
                        addCommentsAndPostText(formatWrapper, startKeyworkNode, options);
                        continue;
                    } else if (emptyLines == EmptyLinesEnum.MERGE) {
                        formatWrapper.append(System.lineSeparator());
                        addCommentsAndPostText(formatWrapper, startKeyworkNode, options);
                        continue;
                    }
                }

                for (StatementBeanIf sqlTokenDataList : fullNodeList.getSqlTokenDataList()) {
                    if (sqlTokenDataList instanceof ISQLTokenData) {
                        formatWrapper.append(((ISQLTokenData) sqlTokenDataList).getTokenStr());
                    } else if (isFullStmtIns(sqlTokenDataList)) {
                        prepareFullStmtFormatString(formatWrapper, (TParseTreeNode) sqlTokenDataList, options);
                    } else {
                        prepareFormatString((TParseTreeNode) sqlTokenDataList, formatWrapper, options);
                    }
                }
                addCommentsAndPostText(formatWrapper, startKeyworkNode, options);
            } else {
                prepareFormatString(parserFullStmtNode, formatWrapper, options);
            }
        }
    }

    private static boolean isFullStmtIns(StatementBeanIf sqlTokenDataList) {
        return sqlTokenDataList instanceof TFullStmt;
    }

    private static void addCommentsAndPostText(FormatStringWrapper formatWrapper, TParseTreeNode startKeyworkNode,
            FmtOptionsIf options) {
        addStringToListComments(formatWrapper, startKeyworkNode.getNextComments(), startKeyworkNode, options);
        addStringToList(formatWrapper, startKeyworkNode.getPostText(), startKeyworkNode);
    }

    private static void addStringToListComments(FormatStringWrapper formatWrapper, List<String> preText,
            TParseTreeNode parseNode, FmtOptionsIf options) {
        if (null == preText) {
            return;
        }

        boolean isFirstLineIgnored = false;

        ListIterator<String> commentsListItr = preText.listIterator();
        EmptyLinesEnum emptyLines = EmptyLinesEnum.REMOVE;
        int newLineCount = 0;

        while (commentsListItr.hasNext()) {
            String formatStr = commentsListItr.next();

            String nextFormatStr = getNextCommentsString(commentsListItr);

            if (System.lineSeparator().equals(formatStr)) {
                boolean isNextLineCom = isNextLineComment(nextFormatStr);
                if (!isFirstLineIgnored && !isNextLineCom) {
                    isFirstLineIgnored = true;
                    continue;
                } else {
                    newLineCount++;
                }

                if (emptyLines == EmptyLinesEnum.REMOVE && !isNextLineCom) {
                    commentsListItr.remove();
                    continue;
                } else if (emptyLines == EmptyLinesEnum.MERGE && newLineCount > 1 && !isNextLineCom) {
                    commentsListItr.remove();
                    continue;
                }

            } else {
                isFirstLineIgnored = false;
                newLineCount = 0;
            }

            formatWrapper.append(formatStr);

        }
    }

    private static boolean isNextLineComment(String nextFormatStr) {
        return null != nextFormatStr && (nextFormatStr.startsWith("--") || nextFormatStr.startsWith("/*"));
    }

    private static String getNextCommentsString(ListIterator<String> commentsListItr) {
        String retVal = null;
        if (commentsListItr.hasNext()) {
            retVal = commentsListItr.next();
            commentsListItr.previous();
        }
        return retVal;
    }

    private static void addStringToList(FormatStringWrapper formatWrapper, List<String> preText,
            TParseTreeNode parseNode) {
        if (null == preText) {
            return;
        }

        for (int itemCount = 0; itemCount < preText.size(); itemCount++) {
            if (itemCount == 0 && parseNode.getCheckPreviousNewLine()) {
                if (System.lineSeparator().equals(formatWrapper.getLastStringAdded())
                        && System.lineSeparator().equals(preText.get(itemCount))) {
                    continue;
                }
            }
            formatWrapper.append(preText.get(itemCount));
        }
    }

    /**
     * get the format string from parse noew
     * 
     * @param itemListNode parse node
     * @return return the parse String
     */
    public static String getFormatString(TParseTreeNode itemListNode, FmtOptionsIf options) {
        FormatStringWrapper formatWrapper = new FormatStringWrapper();

        FormatterUtils.prepareFormatString(itemListNode, formatWrapper, false, options);

        return formatWrapper.toString();
    }

}
