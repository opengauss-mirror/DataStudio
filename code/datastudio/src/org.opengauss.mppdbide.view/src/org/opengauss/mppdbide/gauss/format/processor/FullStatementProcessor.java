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

package org.opengauss.mppdbide.gauss.format.processor;

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang3.StringUtils;

import org.opengauss.mppdbide.gauss.format.option.FmtOptionsIf;
import org.opengauss.mppdbide.gauss.format.option.OptionsProcessData;
import org.opengauss.mppdbide.gauss.format.processor.utils.ProcessorUtils;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.parser.utils.FullNodeExpressionType;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.TParseTreeNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullListNode;
import org.opengauss.mppdbide.gauss.sqlparser.stmt.node.fullstmt.TFullStmt;
import org.opengauss.mppdbide.gauss.sqlparser.stmtbeanif.StatementBeanIf;

/**
 * Title: ExpressionProcessor
 *
 * @since 3.0.0
 */
public class FullStatementProcessor extends AbstractProcessor<TFullStmt> {

    @Override
    public void process(TFullStmt selectAstNode, FmtOptionsIf options, OptionsProcessData pData) {
        process(selectAstNode, options, pData, true);
    }

    @Override
    public void process(TFullStmt selectAstNode, FmtOptionsIf options, OptionsProcessData pData, boolean addPreSpace) {
        List<TParseTreeNode> stmtList = selectAstNode.getStmtList();

        // iterate all the assignment variables and find the offset for
        // alignment

        processAssignmentStmt(stmtList, options);

        int itemsSize = stmtList.size();
        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {
            TParseTreeNode fullStmtNode = stmtList.get(itemIndex);

            OptionsProcessData optionsCloneData = pData.clone();
            optionsCloneData.setOffSet(pData.getParentOffSet() + options.getIndend());
            if (fullStmtNode instanceof TCustomSqlStatement) {
                AbstractProcessorUtils.processParseTreeNode((TCustomSqlStatement) fullStmtNode, options,
                        optionsCloneData);
            } else if (fullStmtNode instanceof TFullStmt) {
                AbstractProcessorUtils.processParseTreeNode((TFullStmt) fullStmtNode, options, optionsCloneData);
            } else if (fullStmtNode instanceof TFullListNode) {
                TFullListNode lTFullListNode = (TFullListNode) fullStmtNode;

                // remove blank new line from backwards direction

                handleLastEmptyNewlineCharacters(lTFullListNode, lTFullListNode.getSqlTokenDataList().size());

                if (lTFullListNode.getExpressionType() != FullNodeExpressionType.NEWLINES) {
                    ProcessorUtils.addNewLineBefore(lTFullListNode, optionsCloneData.getOffSet(), options);
                }

                ListIterator<StatementBeanIf> listItems = lTFullListNode.getSqlTokenDataList().listIterator();

                while (listItems.hasNext()) {
                    StatementBeanIf stmtBean = listItems.next();
                    // calculate the offset and set to the TCustomSqlStatement
                    boolean putStmtNewLine = optionsCloneData.isPutStmtNewLine();
                    if (stmtBean instanceof TCustomSqlStatement) {
                        // remove the above first newline character from the
                        removeTillLastNewLineBlankDataCustom(listItems, stmtBean);

                        AbstractProcessorUtils.processParseTreeNode((TCustomSqlStatement) stmtBean, options,
                                optionsCloneData);
                    } else if (stmtBean instanceof TFullStmt) {
                        AbstractProcessorUtils.processParseTreeNode((TFullStmt) stmtBean, options, optionsCloneData);
                    } else {
                        // do nothing
                    }
                    optionsCloneData.setPutStmtNewLine(putStmtNewLine);

                }
            }
        }
    }

    private void processAssignmentStmt(List<TParseTreeNode> stmtList, FmtOptionsIf options) {
        if (!options.isAlignAssignment()) {
            return;
        }
        int itemsSize = stmtList.size();
        int maxNodeItemLength = 0;
        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {
            TParseTreeNode fullStmtNode = stmtList.get(itemIndex);
            if (fullStmtNode instanceof TFullListNode) {
                TFullListNode lTFullListNode = (TFullListNode) fullStmtNode;
                if (FullNodeExpressionType.ASSIGNMENTS == lTFullListNode.getExpressionType()) {
                    List<StatementBeanIf> sqlTokenDataList = lTFullListNode.getSqlTokenDataList();
                    ListIterator<StatementBeanIf> listIterator = sqlTokenDataList.listIterator();

                    boolean firstNonEmpty = false;
                    while (listIterator.hasNext()) {
                        StatementBeanIf lastElement = listIterator.next();
                        if (lastElement instanceof ISQLTokenData) {
                            String tokenStr = ((ISQLTokenData) lastElement).getTokenStr();
                            if (StringUtils.isBlank(tokenStr)) {
                                listIterator.remove();
                                continue;
                            }
                            if (!firstNonEmpty && !StringUtils.isBlank(tokenStr)) {
                                if (maxNodeItemLength < tokenStr.length()) {
                                    maxNodeItemLength = tokenStr.length();
                                }
                                firstNonEmpty = true;
                            }
                            if ("=".equals(tokenStr)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        alignAssignmentKeyWords(stmtList, options, itemsSize, maxNodeItemLength);
    }

    private void alignAssignmentKeyWords(List<TParseTreeNode> stmtList, FmtOptionsIf options, int itemsSize,
            int maxNodeItemLength) {
        for (int itemIndex = 0; itemIndex < itemsSize; itemIndex++) {
            TParseTreeNode fullStmtNode = stmtList.get(itemIndex);
            if (fullStmtNode instanceof TFullListNode) {
                TFullListNode lTFullListNode = (TFullListNode) fullStmtNode;
                if (FullNodeExpressionType.ASSIGNMENTS == lTFullListNode.getExpressionType()) {
                    ListIterator<StatementBeanIf> listIterator = lTFullListNode.getSqlTokenDataList().listIterator();
                    while (listIterator.hasNext()) {
                        StatementBeanIf lastElement = listIterator.next();
                        if (lastElement instanceof ISQLTokenData) {
                            String tokenStr = ((ISQLTokenData) lastElement).getTokenStr();
                            int paddingLength = tokenStr.length() < maxNodeItemLength
                                    ? maxNodeItemLength - tokenStr.length()
                                    : 0;
                            StringBuilder lsb = new StringBuilder(10);
                            ProcessorUtils.fillSBWithIndendationChars(paddingLength + 1, lsb, options);
                            ((ISQLTokenData) lastElement).setTokenStr(tokenStr + lsb.toString());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void removeTillLastNewLineBlankDataCustom(ListIterator<StatementBeanIf> listItems,
            StatementBeanIf stmtBean) {
        listItems.previous();
        removeTillLastNewLineBlankData(listItems);
        while (listItems.hasNext()) {
            if (stmtBean.equals(listItems.next())) {
                break;
            }
        }

    }

    private void handleLastEmptyNewlineCharacters(TFullListNode lTFullListNode, int size) {
        ListIterator<StatementBeanIf> listItems = lTFullListNode.getSqlTokenDataList().listIterator(size);
        removeTillLastNewLineBlankData(listItems);
    }

    private void removeTillLastNewLineBlankData(ListIterator<StatementBeanIf> listItems) {
        while (listItems.hasPrevious()) {
            StatementBeanIf lastElement = listItems.previous();
            if (lastElement instanceof ISQLTokenData) {
                String tokenStr = ((ISQLTokenData) lastElement).getTokenStr();
                if (StringUtils.isBlank(tokenStr)) {
                    if (isLineSeparator(tokenStr)) {
                        // remove all the elements from here to end
                        removeAllElementsTillEnd(listItems);
                        break;
                    }

                } else {
                    break;
                }

            } else {
                break;
            }
        }
    }

    private boolean isLineSeparator(String tokenStr) {
        return null != tokenStr && (System.lineSeparator().equals(tokenStr) || tokenStr.equals("\n"));
    }

    private void removeAllElementsTillEnd(ListIterator<StatementBeanIf> listItems) {
        listItems.remove();
        while (listItems.hasNext()) {
            StatementBeanIf next = listItems.next();
            if (next instanceof TCustomSqlStatement) {
                listItems.previous();
                break;
            }

            listItems.remove();
        }
    }

    @Override
    public TParseTreeNode getStartNode(TFullStmt selectAstNode) {
        return null;
    }
}
