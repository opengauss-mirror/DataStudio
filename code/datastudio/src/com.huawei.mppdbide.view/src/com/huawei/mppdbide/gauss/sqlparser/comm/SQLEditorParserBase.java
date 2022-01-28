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

package com.huawei.mppdbide.gauss.sqlparser.comm;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.RuleBean;
import com.huawei.mppdbide.gauss.sqlparser.bean.pos.SQLScriptElement;
import com.huawei.mppdbide.gauss.sqlparser.bean.scriptif.ScriptBlockInfo;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;
import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.SQLTokenFactory;
import com.huawei.mppdbide.gauss.sqlparser.handler.AbstractRuleHandler;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: SQLEditorParserBase
 *
 * @since 3.0.0
 */
public class SQLEditorParserBase {

    /**
     * The source viewer.
     */
    protected ProjectionViewer sourceViewer = null;

    /**
     *  The rule manager. 
     */
    protected SQLFoldingRuleManager ruleManager = null;

    /**
     * Instantiates a new SQL editor parser.
     */
    public SQLEditorParserBase() {

    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public IDocument getDocument() {
        return sourceViewer.getDocument();
    }

    /**
     * Gets the EOF ret val.
     *
     * @param curBlock the cur block
     * @param lretList the lret list
     * @return the EOF ret val
     */
    protected List<SQLScriptElement> getEOFRetVal(ScriptBlockInfo curBlock, List<SQLScriptElement> lretList) {
        if (null == curBlock) {
            return lretList;
        }

        int lastTokenOffSet = curBlock.getLastTokenOffSet();
        int lastTokenLength = curBlock.getLastTokenlenght();

        while (null != curBlock) {
            curBlock.getAbstractRuleHandler().endScriptBlock(curBlock, lretList, lastTokenOffSet, lastTokenLength);
            curBlock = curBlock.getParent();
        }

        return lretList;
    }

    /**
     * Gets the current block handle.
     *
     * @param lretList the lret list
     * @param curBlock the cur block
     * @param token the token
     * @param ruleHandlarByToken the rule handlar by token
     * @param sqlTokenData the sql token data
     * @return the current block handle
     */
    public ScriptBlockInfo getCurrentBlockHandle(List<SQLScriptElement> lretList, ScriptBlockInfo curBlock,
            IToken token, AbstractRuleHandler ruleHandlarByToken, ISQLTokenData sqlTokenData) {
        RuleBean ruleBean = curBlock.getAbstractRuleHandler().handle(curBlock, lretList, getSQLToken(token),
                sqlTokenData, ruleHandlarByToken);

        // is parent propagation is required

        curBlock = ruleBean.getScriptBlockInfo();

        return curBlock;
    }

    /**
     * Gets the SQL token.
     *
     * @param token the token
     * @return the SQL token
     */
    protected SQLToken getSQLToken(IToken token) {
        if (token instanceof SQLToken) {
            return (SQLToken) token;
        }
        return null;
    }

    /**
     * Gets the current token.
     *
     * @param tokenOffset the token offset
     * @param tokenLength the token length
     * @return the current token
     */
    public String getCurrentToken(int tokenOffset, int tokenLength) {
        String currentToken = null;
        try {
            currentToken = getDocument().get(tokenOffset, tokenLength);
        } catch (BadLocationException e) {
            MPPDBIDELoggerUtility.error(
                    "Exception while Document replace while getting documnet text within specified range, tokenoffset "
                            + tokenOffset + "token length" + tokenLength);
        }
        return currentToken;
    }

    /**
     * Adds the last known token details.
     *
     * @param curBlock the cur block
     * @param tokenOffset the token offset
     * @param tokenLength the token length
     * @param currentToken the current token
     */
    public void addLastKnownTokenDetails(ScriptBlockInfo curBlock, int tokenOffset, int tokenLength,
            String currentToken) {
        curBlock.setLastKnownTokenlenght(tokenLength);
        curBlock.setLastKnownTokenOffSet(tokenOffset);

        curBlock.addlastKnownToken(currentToken);
    }

    /**
     * Handle last token data.
     *
     * @param curBlock the cur block
     * @param token the token
     * @param tokenOffset the token offset
     * @param tokenLength the token length
     * @param currentToken the current token
     */
    public void handleLastTokenData(ScriptBlockInfo curBlock, IToken token, int tokenOffset, int tokenLength,
            String currentToken) {

        if (null == currentToken) {
            return;
        }

        if (null != curBlock) {
            if (System.lineSeparator().equalsIgnoreCase(currentToken)) {
                curBlock.incrRecentNewLineCount();
            } else if (StringUtils.isNotEmpty(currentToken.trim())) {
                curBlock.resetRecentNewLineCount();
            }
        }

        if (null != curBlock) {
            if (!token.isWhitespace() && !token.isEOF() && !System.lineSeparator().equalsIgnoreCase(currentToken)
                    && StringUtils.isNotEmpty(currentToken.trim())) {
                curBlock.setLastToken(currentToken);
                curBlock.setLastTokenlenght(tokenLength);
                curBlock.setLastTokenOffSet(tokenOffset);
            }
            if (System.lineSeparator().equalsIgnoreCase(currentToken)) {
                curBlock.setPreNewlineTokenlenght(curBlock.getAnyTokenlenght());
                curBlock.setPreNewlineTokenOffSet(curBlock.getAnyTokenOffSet());
            }

            curBlock.setAnyTokenlenght(tokenLength);
            curBlock.setAnyTokenOffSet(tokenOffset);

            if (ISQLSyntax.SQL_COMMENT.equals(token.getData())) {
                curBlock.setPreNewlineTokenlenght(curBlock.getAnyTokenlenght() - System.lineSeparator().length());
                curBlock.setPreNewlineTokenOffSet(curBlock.getAnyTokenOffSet());
            }

        }

    }

    /**
     * Contains ignore case.
     *
     * @param array the array
     * @param value the value
     * @return true, if successful
     */
    public boolean containsIgnoreCase(String[] array, String value) {
        if (isEmpty(array) || value == null) {
            return false;
        }
        for (int i = 0; i < array.length; i++) {
            if (value.equalsIgnoreCase(array[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is empty.
     *
     * @param arr the arr
     * @return true, if is empty
     */
    public boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    /**
     * Gets the source viewer.
     *
     * @return the source viewer
     */
    public ProjectionViewer getSourceViewer() {
        return sourceViewer;
    }

    /**
     * Sets the source viewer.
     *
     * @param lSourceViewer the new source viewer
     */
    public void setSourceViewer(ProjectionViewer lSourceViewer) {
        this.sourceViewer = lSourceViewer;
    }

    /**
     * Gets the rule manager.
     *
     * @return the rule manager
     */
    public SQLFoldingRuleManager getRuleManager() {
        return ruleManager;
    }

    /**
     * Sets the rule manager.
     *
     * @param ruleManager the new rule manager
     */
    public void setRuleManager(SQLFoldingRuleManager ruleManager) {
        this.ruleManager = ruleManager;
    }

    /**
     * Gets the projection annotation model.
     *
     * @return the projection annotation model
     */
    public ProjectionAnnotationModel getProjectionAnnotationModel() {
        return sourceViewer.getProjectionAnnotationModel();
    }

    /**
     * 
     * creates a new SQLToken data and return the same.
     *
     * @param ruleManager current RuleManager
     * @param token current IToken
     * @param currentToken current token data
     * @return the sql token data
     */
    protected ISQLTokenData getSQLTokenData(SQLFoldingRuleManager ruleManager, IToken token, String currentToken) {
        ISQLTokenData sqlTokenData = SQLTokenFactory.getTokenData();
        sqlTokenData.setToken(token);
        sqlTokenData.setTokenOffset(ruleManager.getTokenOffset());
        sqlTokenData.setTokenLength(ruleManager.getTokenLength());
        sqlTokenData.setTokenStr(currentToken);
        return sqlTokenData;
    }

}
