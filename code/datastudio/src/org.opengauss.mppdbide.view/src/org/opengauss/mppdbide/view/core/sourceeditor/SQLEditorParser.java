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

package org.opengauss.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import org.opengauss.mppdbide.gauss.sqlparser.SQLToken;
import org.opengauss.mppdbide.gauss.sqlparser.SQLTokenConstants;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import org.opengauss.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import org.opengauss.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import org.opengauss.mppdbide.gauss.sqlparser.comm.SQLEditorParserBase;
import org.opengauss.mppdbide.gauss.sqlparser.comm.SQLFoldingRuleManager;
import org.opengauss.mppdbide.gauss.sqlparser.handler.AbstractCreateHandler;
import org.opengauss.mppdbide.gauss.sqlparser.handler.AbstractRuleHandler;
import org.opengauss.mppdbide.gauss.sqlparser.handler.RuleHandlerConfig;
import org.opengauss.mppdbide.gauss.sqlparser.handler.SQLUnionRuleHandler;
import org.opengauss.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: SQLEditorParser
 *
 * @since 3.0.0
 */
public class SQLEditorParser extends SQLEditorParserBase {

    /**
     * Instantiates a new SQL editor parser.
     */
    public SQLEditorParser() {

    }

    /**
     * Checks if is folding enabled.
     *
     * @return true, if is folding enabled
     */
    public boolean isFoldingEnabled() {
        return true;
    }

    /**
     * Extract script queries.
     *
     * @param startOffset the start offset
     * @param length the length
     * @param scriptMode the script mode
     * @param keepDelimiters the keep delimiters
     * @param parseParameters the parse parameters
     * @return the list
     */
    public List<SQLScriptElement> extractScriptQueries(int startOffset, int length, boolean scriptMode,
            boolean keepDelimiters, boolean parseParameters) {
        List<SQLScriptElement> queryList = new ArrayList<>();

        IDocument document = getDocument();
        if (document == null) {
            return queryList;
        }

        for (int queryOffset = startOffset;;) {
            List<SQLScriptElement> query = parseQueryCheck(document, queryOffset, startOffset + length, queryOffset,
                    scriptMode, keepDelimiters);
            if (query == null) {
                break;
            }

            if (query.size() > 0) {
                queryList.addAll(query);
                queryOffset = queryList.get(queryList.size() - 1).getOffset()
                        + queryList.get(queryList.size() - 1).getLength();
            } else {
                break;
            }

        }

        return queryList;
    }

    private AbstractRuleHandler getRuleHandlarByToken(SQLToken lToken, ScriptBlockInfo curBlock) {

        AbstractRuleHandler ruleHandlarByToken = RuleHandlerConfig.getInstance().getRuleHandle(lToken.getType());

        if (ruleHandlarByToken instanceof SQLUnionRuleHandler) {
            return null;
        }

        if (null == curBlock) {
            return ruleHandlarByToken;
        }

        RuleHandlerIf currentRuleHandler = curBlock.getAbstractRuleHandler();

        if ((lToken.getType() == SQLTokenConstants.T_SQL_DDL_CREATE_FUNC
                || lToken.getType() == SQLTokenConstants.T_SQL_DDL_CREATE_PROC)) {
            if (currentRuleHandler instanceof AbstractCreateHandler) {
                AbstractCreateHandler createhandlar = (AbstractCreateHandler) currentRuleHandler;
                boolean packageStmt = createhandlar.isPackageStmt(currentRuleHandler, curBlock, lToken);
                if (packageStmt) {
                    return ruleHandlarByToken;
                }
            }
            return null;
        }

        if (currentRuleHandler.isRuleHandlerValid(ruleHandlarByToken, curBlock, lToken)) {
            return ruleHandlarByToken;
        }

        return null;

    }

    /**
     * Parses the query check.
     *
     * @param document the document
     * @param startPos the start pos
     * @param endPos the end pos
     * @param currentPos the current pos
     * @param scriptMode the script mode
     * @param keepDelimiters the keep delimiters
     * @return the list
     */
    protected List<SQLScriptElement> parseQueryCheck(final IDocument document, final int startPos, final int endPos,
            final int currentPos, final boolean scriptMode, final boolean keepDelimiters) {
        if (endPos - startPos <= 0) {
            return null;
        }
        return parseQuery(document, startPos, endPos, currentPos, scriptMode, keepDelimiters);
    }

    /**
     * Parses the query.
     *
     * @param document the document
     * @param startPos the start pos
     * @param endPos the end pos
     * @param currentPos the current pos
     * @param scriptMode the script mode
     * @param keepDelimiters the keep delimiters
     * @return the list
     */
    protected List<SQLScriptElement> parseQuery(final IDocument document, final int startPos, final int endPos,
            final int currentPos, final boolean scriptMode, final boolean keepDelimiters) {
        List<SQLScriptElement> lretList = new ArrayList<SQLScriptElement>();
        ruleManager.setRange(document, startPos, endPos - startPos);
        ScriptBlockInfo curBlock = null;
        for (;;) {
            IToken token = ruleManager.nextToken();
            int tokenOffset = ruleManager.getTokenOffset();
            int tokenLength = ruleManager.getTokenLength();
            String currentToken = getCurrentToken(tokenOffset, tokenLength);
            int tokenType = token instanceof SQLToken ? ((SQLToken) token).getType() : SQLTokenConstants.T_SQL_UNKNOWN;
            if (tokenOffset < startPos) {
                // This may happen with EOF tokens (bug in jface?)
                return lretList;
            }
            ISQLTokenData sqlTokenData = getSQLTokenData(ruleManager, token, currentToken);
            // if it is not of the token specified then can return handle
            // optional token end
            RuleBean optRuleBean = checkBlockEndWithOptionalToken(lretList, curBlock, token, tokenType, currentToken);
            if (null != optRuleBean) {
                curBlock = optRuleBean.getScriptBlockInfo();
                if (optRuleBean.isContinueLoop()) {
                    continue;
                }
                if (null == curBlock) {
                    return lretList;
                }
            }
            if (tokenType != SQLTokenConstants.T_SQL_UNKNOWN) {
                // 1. if the curBlock is null then get the handler by token
                AbstractRuleHandler ruleHandlarByToken = getRuleHandlarByToken((SQLToken) token, curBlock);
                if (null == curBlock) {
                    if (null == ruleHandlarByToken) {
                        // some thing went wrong the token is not associated
                        // with handler the token in not folding token
                        continue;
                    }
                    curBlock = ruleHandlarByToken.createScriptBlock(null, sqlTokenData, tokenOffset);
                } else {
                    // get rule manager wrapper
                    curBlock.setCurrentKnownToken(currentToken);
                    curBlock = getCurrentBlockHandle(lretList, curBlock, token, ruleHandlarByToken, sqlTokenData);
                    if (null == curBlock) {
                        return handleEndLineStmt(lretList, ruleManager, document);
                    }
                    addLastKnownTokenDetails(curBlock, tokenOffset, tokenLength, currentToken);
                    if (curBlock.isInvokeParent()) {
                        curBlock = getCurrentBlockHandle(lretList, curBlock, token, ruleHandlarByToken, sqlTokenData);
                        if (null == curBlock) {
                            return handleEndLineStmt(lretList, ruleManager, document);
                        }
                    }
                }
            }
            handleLastTokenData(curBlock, token, tokenOffset, tokenLength, currentToken);
            if (token.isEOF()) {
                return getEOFRetVal(curBlock, lretList);
            }
        }
    }

    private List<SQLScriptElement> handleEndLineStmt(List<SQLScriptElement> lretList, SQLFoldingRuleManager ruleManager,
            IDocument document) {
        if (!lretList.isEmpty()) {
            ruleManager.nextToken();
            int tokenOffset = ruleManager.getTokenOffset();
            int tokenLength = ruleManager.getTokenLength();
            String currentToken = getCurrentToken(tokenOffset, tokenLength);

            // the below condition will be matched
            // and after the statement is the EOF and there is no new line chars
            // is available
            // Happens only in windows.

            if ("\r".equalsIgnoreCase(currentToken)) {
                while (!ruleManager.nextToken().isEOF()) {
                    int tokenOffsetN = ruleManager.getTokenOffset();
                    int tokenLengthN = ruleManager.getTokenLength();
                    currentToken = getCurrentToken(tokenOffsetN, tokenLengthN);

                    if ("\n".equalsIgnoreCase(currentToken)) {

                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    document.replace(tokenOffset, (tokenOffsetN + tokenLengthN) - tokenOffset,
                                            System.lineSeparator() + document.get(tokenOffset + tokenLength,
                                                    tokenOffsetN - (tokenOffset + tokenLength)));
                                } catch (BadLocationException badPadEx) {
                                    MPPDBIDELoggerUtility.error(
                                            "Exception while Document replace while Folding and Unfolding Change",
                                            badPadEx);
                                }

                            }
                        });
                        return lretList;
                    }

                }

            }
        }

        return lretList;

    }

    /**
     * Check block end with optional token.
     *
     * @param lretList the lret list
     * @param curBlock the cur block
     * @param token the token
     * @param tokenType the token type
     * @param currentToken the current token
     * @return the rule bean
     */
    public RuleBean checkBlockEndWithOptionalToken(List<SQLScriptElement> lretList, ScriptBlockInfo curBlock,
            IToken token, int tokenType, String currentToken) {
        RuleBean ruleBean = null;

        if (null == currentToken || StringUtils.isEmpty(currentToken.trim())) {
            return null;
        }

        if (null != curBlock && curBlock.getNextOptTokenToEnd() != SQLTokenConstants.T_SQL_UNKNOWN) {

            if (curBlock.getNextOptTokenToEnd() == tokenType) {
                ISQLTokenData sqlTokenData = getSQLTokenData(ruleManager, token, currentToken);
                ruleBean = curBlock.getAbstractRuleHandler().endOptionalScript(curBlock, lretList, getSQLToken(token),
                        sqlTokenData);

                ruleBean.setContinueLoop(true);

            } else if (curBlock.getNextMiddleOptTokenToEnd() == tokenType) {
                ruleBean = new RuleBean();
                ruleBean.setScriptBlockInfo(curBlock);
                ruleBean.setContinueLoop(true);

            } else {

                ISQLTokenData lastTokenData = getSQLTokenData(ruleManager, token, currentToken);
                lastTokenData.setTokenLength(curBlock.getLastKnownTokenlenght());
                lastTokenData.setTokenOffset(curBlock.getLastKnownTokenOffSet());

                SQLToken dummyToken = null;

                if (!(token instanceof SQLToken)) {
                    dummyToken = new SQLToken(SQLTokenConstants.T_SQL_DUMMY, SQLFoldingConstants.SQL_TOKEN_DUMMY);
                } else {
                    dummyToken = (SQLToken) token;
                }

                ruleBean = curBlock.getAbstractRuleHandler().endOptionalScript(curBlock, lretList, dummyToken,
                        lastTokenData);
            }

        }
        return ruleBean;
    }

}
