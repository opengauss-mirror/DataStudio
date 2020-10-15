/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.comm;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;

import com.huawei.mppdbide.gauss.format.option.FmtOptions;
import com.huawei.mppdbide.gauss.format.option.FmtOptionsIf;
import com.huawei.mppdbide.gauss.format.stmtformatter.FormatterFactory;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.bean.ScriptBlockInfoImpl;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenListBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLStmtTokenManager;
import com.huawei.mppdbide.gauss.sqlparser.handler.AbstractCreateHandler;
import com.huawei.mppdbide.gauss.sqlparser.handler.AbstractRuleHandler;
import com.huawei.mppdbide.gauss.sqlparser.handler.RuleHandlerConfig;
import com.huawei.mppdbide.gauss.sqlparser.handler.SQLUnionRuleHandler;
import com.huawei.mppdbide.gauss.sqlparser.handlerif.RuleHandlerIf;
import com.huawei.mppdbide.gauss.sqlparser.parser.ParserFactory;
import com.huawei.mppdbide.gauss.sqlparser.parser.init.ParserInitilizer;
import com.huawei.mppdbide.gauss.sqlparser.stmt.custom.TCustomSqlStatement;

/**
 * Title: SQLFormatEditorParser Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 30-Nov-2019]
 * @since 30-Nov-2019
 */
public class SQLFormatEditorParser extends SQLEditorParserBase {

    private IDocument sourceViewerDocument = null;

    /**
     * Instantiates a new SQL editor parser.
     */
    public SQLFormatEditorParser() {
        ParserInitilizer.initParser();
    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public IDocument getDocument() {
        return sourceViewerDocument;
    }

    /**
     * Sets the document.
     *
     * @param sourceDocument the new document
     */
    public void setDocument(IDocument sourceDocument) {
        this.sourceViewerDocument = sourceDocument;
    }

    private AbstractRuleHandler getRuleHandlarByToken(IToken lToken, ScriptBlockInfo curBlock,
            SQLStmtTokenManager lSQLStmtTokenManager, RuleBean lRuleBean) {
        if (!(lToken instanceof SQLToken)) {
            return null;
        }

        try {
            AbstractRuleHandler ruleHandlarByToken = RuleHandlerConfig.getInstance()
                    .getNewRuleHandle(((SQLToken) lToken).getType());

            if (null == ruleHandlarByToken) {
                return null;
            }

            ruleHandlarByToken.setSqlStmtTokenManager(lSQLStmtTokenManager);
            ruleHandlarByToken.setRuleScanner(this.getRuleManager());

            if (isInvalidRuleHandlar(lRuleBean, ruleHandlarByToken)) {
                return null;
            }

            if (null == curBlock) {
                return ruleHandlarByToken;
            }

            RuleHandlerIf currentRuleHandler = curBlock.getAbstractRuleHandler();
            SQLToken lSqlToken = (SQLToken) lToken;
            if ((lSqlToken.getType() == SQLTokenConstants.T_SQL_DDL_CREATE_FUNC
                    || lSqlToken.getType() == SQLTokenConstants.T_SQL_DDL_CREATE_PROC)) {
                if (currentRuleHandler instanceof AbstractCreateHandler) {
                    AbstractCreateHandler createhandlar = (AbstractCreateHandler) currentRuleHandler;
                    boolean packageStmt = createhandlar.isPackageStmt(currentRuleHandler, curBlock, lSqlToken);
                    if (packageStmt) {
                        return ruleHandlarByToken;
                    }
                }
                return null;
            }

            if (currentRuleHandler.isRuleHandlerValid(ruleHandlarByToken, curBlock, (SQLToken) lToken)) {
                return ruleHandlarByToken;
            }
        } catch (Exception e) {
            return null;
        }

        return null;

    }

    private boolean isInvalidRuleHandlar(RuleBean lRuleBean, AbstractRuleHandler ruleHandlarByToken) {
        return ruleHandlarByToken instanceof SQLUnionRuleHandler && !isPreviousScriptBlockSelect(lRuleBean);
    }

    private boolean isPreviousScriptBlockSelect(RuleBean lRuleBean) {
        return lRuleBean != null && null != lRuleBean.getPreviousScriptBlock()
                && SQLTokenConstants.T_SQL_DML_SELECT == lRuleBean.getPreviousScriptBlock().getTokenType();
    }

    /**
     * Parses the SQL docuement.
     *
     * @param preferenceStore the preference store
     * @param startOffset the start offset
     * @param endOffset the end offset
     * @param formatterOffset the formatter offset
     * @return the string
     */
    public String parseSQLDocuement(PreferenceStore preferenceStore, int startOffset, int endOffset,
            int formatterOffset) {
        IDocument document = getDocument();

        SQLStmtTokenManager parseQuery = parseQuery(document, startOffset, endOffset, 0, true, true);

        List<SQLStmtTokenListBean> sqlTokenStmtList = parseQuery.getSqlTokenStmtList();

        StringBuilder lRetBuilder = new StringBuilder();

        SQLStmtTokenListBean preNode = null;

        for (SQLStmtTokenListBean stmtTokenListBean : sqlTokenStmtList) {

            formatCustomSqlStmt(lRetBuilder, stmtTokenListBean, preferenceStore, formatterOffset, preNode);
            preNode = stmtTokenListBean;
        }

        return lRetBuilder.toString();

    }

    private void formatCustomSqlStmt(StringBuilder lRetBuilder, SQLStmtTokenListBean stmtTokenListBean,
            PreferenceStore preferenceStore, int formatterOffset, SQLStmtTokenListBean preNode) {
        TCustomSqlStatement customSqlStmt = ParserFactory.getCustomSqlStmt(stmtTokenListBean);

        if (null != customSqlStmt) {
            FmtOptionsIf options = new FmtOptions();
            options.setPrefStore(preferenceStore);
            // format the statement object
            String format = FormatterFactory.format(customSqlStmt, options, formatterOffset);

            if (lRetBuilder.length() == 0 && format.startsWith(System.lineSeparator())) {
                format = format.replaceFirst(System.lineSeparator(), "");
            }

            if (isStmtTypeUnion(stmtTokenListBean, preNode) || isDeclareBegin(stmtTokenListBean, preNode)) {
                lRetBuilder.append("");
            } else if (!isLastCharNewLine(lRetBuilder, true)) {
                lRetBuilder.append(System.lineSeparator());
            }
            lRetBuilder.append(format);
        } else {
            // add all the format data
            StringBuilder lNoFormatBuilder = new StringBuilder();
            appendUnformattedString(stmtTokenListBean, lNoFormatBuilder, preferenceStore, formatterOffset);

            // trim the lNoFormatBuilder
            String fullString = lNoFormatBuilder.toString().trim();

            if ("/".equals(fullString)) {
                lRetBuilder.append(System.lineSeparator());
            } else if (StringUtils.isNotBlank(fullString) && !isLastCharNewLine(lRetBuilder, true)) {
                lRetBuilder.append(System.lineSeparator());
                lRetBuilder.append(System.lineSeparator());
            }

            lRetBuilder.append(fullString);
        }
    }

    private boolean isDeclareBegin(SQLStmtTokenListBean stmtTokenListBean, SQLStmtTokenListBean preNode) {
        return preNode != null && (SQLTokenConstants.T_SQL_BLOCK_DECLARE == preNode.getStatementType()
                && SQLTokenConstants.T_SQL_BLOCK_BEGIN == stmtTokenListBean.getStatementType());
    }

    private boolean isStmtTypeUnion(SQLStmtTokenListBean stmtTokenListBean, SQLStmtTokenListBean preNode) {
        return null != preNode && (SQLTokenConstants.T_SQL_KEYWORK_UNION == preNode.getStatementType()
                || SQLTokenConstants.T_SQL_KEYWORK_UNION == stmtTokenListBean.getStatementType());
    }

    private boolean isLastCharNewLine(StringBuilder lRetBuilder, boolean emptyRetVal) {
        if (lRetBuilder.length() <= 0) {
            return emptyRetVal;
        }
        return "\n".equals(lRetBuilder.charAt(lRetBuilder.length() - 1) + "");
    }

    private void appendUnformattedString(SQLStmtTokenListBean stmtTokenListBean, StringBuilder lNoFormatBuilder,
            PreferenceStore preferenceStore, int formatterOffset) {
        List<ISQLTokenData> sqlTokenData = stmtTokenListBean.getSqlTokenData();
        for (ISQLTokenData lSQLTokenData : sqlTokenData) {
            if (null != lSQLTokenData.getSubTokenBean()) {
                String fullData = lNoFormatBuilder.toString();
                lNoFormatBuilder.delete(0, lNoFormatBuilder.length());
                // rtrim the string and then send it
                lNoFormatBuilder.append(rtrim(fullData));
                formatCustomSqlStmt(lNoFormatBuilder, lSQLTokenData.getSubTokenBean(), preferenceStore, formatterOffset,
                        null);
            } else {
                lNoFormatBuilder.append(lSQLTokenData.getTokenStr());
            }

        }
    }

    private String rtrim(String str) {
        int pos = str.length() - 1;
        while (pos >= 0 && Character.isWhitespace(str.charAt(pos))) {
            pos--;
        }
        return str.substring(0, pos + 1);
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
    protected SQLStmtTokenManager parseQuery(final IDocument document, final int startPos, final int endPos,
            final int currentPos, final boolean scriptMode, final boolean keepDelimiters) {
        List<SQLScriptElement> lretList = new ArrayList<SQLScriptElement>();
        ruleManager.setRange(document, startPos, endPos - startPos);
        ScriptBlockInfo curBlock = null;
        RuleBean lRuleBean = null;
        ScriptBlockInfo unSupportedBlockToken = new ScriptBlockInfoImpl();
        SQLStmtTokenManager lSQLStmtTokenManager = new SQLStmtTokenManager();
        for (;;) {
            IToken token = ruleManager.nextToken();
            int tokenOffset = ruleManager.getTokenOffset();
            int tokenLength = ruleManager.getTokenLength();
            String currentToken = getCurrentToken(tokenOffset, tokenLength);
            int tokenType = token instanceof SQLToken ? ((SQLToken) token).getType() : SQLTokenConstants.T_SQL_UNKNOWN;
            ISQLTokenData sqlTokenData = getSQLTokenData(ruleManager, token, currentToken);
            if (tokenOffset < startPos) {
                // This may happen with EOF tokens (bug in jface?)
                break;
            }
            // if it is not of the token specified then can return handle
            // optional token end
            RuleBean optRuleBean = checkBlockEndWithOptionalToken(lretList, curBlock, sqlTokenData, tokenType,
                    currentToken);
            if (null != optRuleBean) {
                curBlock = optRuleBean.getScriptBlockInfo();
                if (optRuleBean.isContinueLoop() || null == curBlock) {
                    continue;
                }
            }
            if (tokenType != SQLTokenConstants.T_SQL_UNKNOWN) {
                // 1. if the curBlock is null then get the handler by token
                AbstractRuleHandler ruleHandlarByToken = getRuleHandlarByToken(token, curBlock, lSQLStmtTokenManager,
                        lRuleBean);
                if (null == curBlock) {
                    if (null == ruleHandlarByToken) {
                        addUnSupportedBlockData(curBlock, unSupportedBlockToken, sqlTokenData);
                        // some thing went wrong the token is not associated
                        // with handler the token in not folding token
                        continue;
                    }
                    curBlock = ruleHandlarByToken.createScriptBlock(null, sqlTokenData, tokenOffset);
                    unSupportedBlockToken = addAndGetUnSupportedBlockToken(unSupportedBlockToken, lSQLStmtTokenManager);
                    curBlock.addSQLTokenData(sqlTokenData);
                } else {
                    // get rule manager wrapper
                    addSqlTokenAndCurrentTokenData(curBlock, currentToken, sqlTokenData);
                    lRuleBean = getCurrentBlockHandleRuleBean(lretList, curBlock, token, ruleHandlarByToken,
                            sqlTokenData);
                    curBlock = lRuleBean.getScriptBlockInfo();
                    if (null == curBlock) {
                        continue;
                    }
                    addLastKnownTokenDetails(curBlock, tokenOffset, tokenLength, currentToken);
                    if (curBlock.isInvokeParent()) {
                        lRuleBean = getCurrentBlockHandleRuleBean(lretList, curBlock, token, ruleHandlarByToken,
                                sqlTokenData);
                        curBlock = lRuleBean.getScriptBlockInfo();
                        if (null == curBlock) {
                            continue;
                        }
                    }
                }
            } else if (null != curBlock) {
                curBlock.addSQLTokenData(sqlTokenData);
            }
            addUnSupportedBlockData(curBlock, unSupportedBlockToken, sqlTokenData);
            handleLastTokenData(curBlock, token, tokenOffset, tokenLength, currentToken);
            if (token.isEOF()) {
                getEOFRetVal(curBlock, lretList);
                lSQLStmtTokenManager.addSQLStmtTokenListBean(unSupportedBlockToken);
                break;
            }
        }
        return lSQLStmtTokenManager;
    }

    private ScriptBlockInfo addAndGetUnSupportedBlockToken(ScriptBlockInfo unSupportedBlockToken,
            SQLStmtTokenManager lSQLStmtTokenManager) {
        lSQLStmtTokenManager.addSQLStmtTokenListBean(unSupportedBlockToken);
        unSupportedBlockToken = new ScriptBlockInfoImpl();
        return unSupportedBlockToken;
    }

    /**
     * Gets the current block handle rule bean.
     *
     * @param lretList the lret list
     * @param curBlock the cur block
     * @param token the token
     * @param ruleHandlarByToken the rule handlar by token
     * @param sqlTokenData the sql token data
     * @return the current block handle rule bean
     */
    public RuleBean getCurrentBlockHandleRuleBean(List<SQLScriptElement> lretList, ScriptBlockInfo curBlock,
            IToken token, AbstractRuleHandler ruleHandlarByToken, ISQLTokenData sqlTokenData) {
        RuleBean ruleBean = curBlock.getAbstractRuleHandler().handle(curBlock, lretList, getSQLToken(token),
                sqlTokenData, ruleHandlarByToken);
        return ruleBean;
    }

    private void addSqlTokenAndCurrentTokenData(ScriptBlockInfo curBlock, String currentToken,
            ISQLTokenData sqlTokenData) {
        curBlock.addSQLTokenData(sqlTokenData);
        curBlock.setCurrentKnownToken(currentToken);
    }

    private void addUnSupportedBlockData(ScriptBlockInfo curBlock, ScriptBlockInfo unSupportedBlockToken,
            ISQLTokenData sqlTokenData) {
        if (null == curBlock && (null != sqlTokenData.getTokenStr() || null != sqlTokenData.getSubTokenBean())) {
            unSupportedBlockToken.addSQLTokenData(sqlTokenData);
        }
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
            ISQLTokenData token, int tokenType, String currentToken) {
        RuleBean ruleBean = null;

        if (null == currentToken || StringUtils.isEmpty(currentToken.trim())) {
            return null;
        }

        if (null != curBlock && curBlock.getNextOptTokenToEnd() != SQLTokenConstants.T_SQL_UNKNOWN) {

            if (curBlock.getNextOptTokenToEnd() == tokenType) {
                curBlock.addSQLTokenData(token);
                ISQLTokenData sqlTokenData = getSQLTokenData(ruleManager, token.getToken(), currentToken);
                ruleBean = curBlock.getAbstractRuleHandler().endOptionalScript(curBlock, lretList,
                        getSQLToken(token.getToken()), sqlTokenData);

                ruleBean.setContinueLoop(true);

            } else if (curBlock.getNextMiddleOptTokenToEnd() == tokenType) {
                curBlock.addSQLTokenData(token);
                ruleBean = new RuleBean();
                ruleBean.setScriptBlockInfo(curBlock);
                ruleBean.setContinueLoop(true);

            } else {
                // FORMATPENDING: curBlock.addSQLTokenData(token); is not
                // handled here
                token.setTokenLength(curBlock.getLastKnownTokenlenght());
                token.setTokenOffset(curBlock.getLastKnownTokenOffSet());

                SQLToken dummyToken = null;

                if (!(token.getToken() instanceof SQLToken)) {
                    dummyToken = new SQLToken(SQLTokenConstants.T_SQL_DUMMY, SQLFoldingConstants.SQL_TOKEN_DUMMY);
                } else {
                    dummyToken = (SQLToken) token.getToken();
                }

                ruleBean = curBlock.getAbstractRuleHandler().endOptionalScript(curBlock, lretList, dummyToken, token);
            }

        }
        return ruleBean;
    }

}
