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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.huawei.mppdbide.gauss.sqlparser.SQLDDLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLDMLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLDelimiterRule;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLToken;
import com.huawei.mppdbide.gauss.sqlparser.SQLTokenConstants;
import com.huawei.mppdbide.gauss.sqlparser.SQLWordRule;

/**
 * Title: SQLFoldingRuleManager
 *
 * @since 3.0.0
 */
public class SQLFoldingRuleManager extends BufferedRuleBasedScanner implements ISQLSyntax {

    /**
     * The tkn comment.
     */
    IToken tknComment = new Token(SQL_COMMENT);

    /**
     * The tkn multiline comment.
     */
    IToken tknMultilineComment = new Token(SQL_MULTILINE_COMMENT);

    /**
     * The tkn sql double quotes identifier.
     */
    IToken tknSqlDoubleQuotesIdentifier = new Token(SQL_DOUBLE_QUOTES_IDENTIFIER);

    /**
     * The tkn sql string.
     */
    IToken tknSqlString = new Token(SQL_STRING);

    /**
     * Instantiates a new SQL folding rule manager.
     */
    public SQLFoldingRuleManager() {
        super();
    }

    /**
     * Refresh rules.
     */
    public void refreshRules() {

        List<IRule> rules = new ArrayList<IRule>();

        final IToken otherToken = new Token(SQLFoldingConstants.SQL_TOKEN_UNKNOWN);

        final IToken newlineChar = new Token(System.lineSeparator());

        IToken blockBeginToken = new SQLToken(SQLTokenConstants.T_SQL_BLOCK_BEGIN,
                SQLFoldingConstants.SQL_KEYWORK_BEGIN);

        IToken blockEndToken = new SQLToken(SQLTokenConstants.T_SQL_BLOCK_END, SQLFoldingConstants.SQL_KEYWORK_END);

        IToken blockDeclareToken = new SQLToken(SQLTokenConstants.T_SQL_BLOCK_DECLARE,
                SQLFoldingConstants.SQL_KEYWORK_DECLARE);

        IToken bracketStartToken = new SQLToken(SQLTokenConstants.T_SQL_BRACKET_BEGIN,
                SQLFoldingConstants.SQL_BRACKET_START);
        IToken bracketEndToken = new SQLToken(SQLTokenConstants.T_SQL_BRACKET_END, SQLFoldingConstants.SQL_BRACKET_END);

        IToken sqldDelim = new SQLToken(SQLTokenConstants.T_SQL_DELIMITER, SQLFoldingConstants.SQL_DELIM_NAME);

        IToken sqlFunctionDelim = new SQLToken(SQLTokenConstants.T_SQL_FUNCTION_END,
                SQLFoldingConstants.SQL_DOUBLE_DOLLER);

        IToken sqlNumberToken = new SQLToken(SQLTokenConstants.T_SQL_NUMBER, SQLTokenConstants.T_SQL_NUMBER);

        IToken sqlDelimForwardSlash = new SQLToken(SQLTokenConstants.T_SQL_DELIMITER_FSLASH,
                SQLFoldingConstants.SQL_DELIM_FSLASH);

        String[] delims = {SQLFoldingConstants.SQL_DELIM_SEMICOLON, SQLFoldingConstants.SQL_BRACKET_START,
            SQLFoldingConstants.SQL_BRACKET_END};

        SQLDelimiterRule delimRule = new SQLDelimiterRule(delims, sqldDelim);

        SQLDelimiterRule endDelimRule = getEndDelimRule(sqldDelim);

        SQLDelimiterRule brackerStartRule = getBrackerStartRule(bracketStartToken);

        SQLDelimiterRule brackerEndRule = getBrackerEndRule(bracketEndToken);

        SQLDelimiterRule functionEndRule = getFunEndRule(sqlFunctionDelim);

        SQLDelimiterRule newlineRule = getNewLineEndRule(newlineChar);

        String[] forwardSlash = {SQLFoldingConstants.SQL_DELIM_FSLASH};

        SQLDelimiterRule forwardSlashRule = new SQLDelimiterRule(forwardSlash, sqlDelimForwardSlash);

        SQLWordRule wordRule = new SQLWordRule(delimRule, otherToken);

        addWordRules(newlineChar, blockBeginToken, blockEndToken, blockDeclareToken, wordRule);

        NumberDetector lDetector = new NumberDetector();

        WordRule numberRule = new WordRule(lDetector, sqlNumberToken, true);

        addRules(wordRule, getKeywordRules());

        IRule[] result = addRules(rules, endDelimRule, brackerStartRule, brackerEndRule, functionEndRule, newlineRule,
                forwardSlashRule, wordRule, numberRule);
        setRules(result);
    }

    private SQLDelimiterRule getEndDelimRule(IToken sqldDelim) {
        String[] endDelim = {SQLFoldingConstants.SQL_DELIM_SEMICOLON};

        SQLDelimiterRule endDelimRule = new SQLDelimiterRule(endDelim, sqldDelim);
        return endDelimRule;
    }

    private SQLDelimiterRule getNewLineEndRule(final IToken newlineChar) {
        String[] newlineEnd = {System.lineSeparator()};

        SQLDelimiterRule newlineRule = new SQLDelimiterRule(newlineEnd, newlineChar);
        return newlineRule;
    }

    private SQLDelimiterRule getFunEndRule(IToken sqlFunctionDelim) {
        String[] funcEnd = {SQLFoldingConstants.SQL_DOUBLE_DOLLER};

        SQLDelimiterRule functionEndRule = new SQLDelimiterRule(funcEnd, sqlFunctionDelim);
        return functionEndRule;
    }

    private SQLDelimiterRule getBrackerEndRule(IToken bracketEndToken) {
        String[] brackerEnd = {SQLFoldingConstants.SQL_BRACKET_END};

        SQLDelimiterRule brackerEndRule = new SQLDelimiterRule(brackerEnd, bracketEndToken);
        return brackerEndRule;
    }

    private SQLDelimiterRule getBrackerStartRule(IToken bracketStartToken) {
        String[] brackerStart = {SQLFoldingConstants.SQL_BRACKET_START};

        SQLDelimiterRule brackerStartRule = new SQLDelimiterRule(brackerStart, bracketStartToken);
        return brackerStartRule;
    }

    private void addWordRules(final IToken newlineChar, IToken blockBeginToken, IToken blockEndToken,
            IToken blockDeclareToken, SQLWordRule wordRule) {
        // rules for begin & end
        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORK_BEGIN, blockBeginToken);
        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORK_END, blockEndToken);
        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORK_DECLARE, blockDeclareToken);
        wordRule.addWord(System.lineSeparator(), newlineChar);

        // rules for dml
        addRules(wordRule, getDMLRules());

        // rules for create

        addRules(wordRule, getCreateRules());

        // rules for if then else

        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORD_IF,
                new SQLToken(SQLTokenConstants.T_SQL_DDL_CONTROL_IF, SQLFoldingConstants.SQL_KEYWORD_IF));

        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORD_ELSE,
                new SQLToken(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSE, SQLFoldingConstants.SQL_KEYWORD_ELSE));

        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORD_ELSIF,
                new SQLToken(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF, SQLFoldingConstants.SQL_KEYWORD_ELSIF));

        wordRule.addWord(SQLFoldingConstants.SQL_KEYWORD_ELSEIF,
                new SQLToken(SQLTokenConstants.T_SQL_DDL_CONTROL_ELSIF, SQLFoldingConstants.SQL_KEYWORD_ELSEIF));
    }

    private IRule[] addRules(List<IRule> rules, SQLDelimiterRule endDelimRule, SQLDelimiterRule brackerStartRule,
            SQLDelimiterRule brackerEndRule, SQLDelimiterRule functionEndRule, SQLDelimiterRule newlineRule,
            SQLDelimiterRule forwardSlashRule, SQLWordRule wordRule, WordRule numberRule) {
        rules.add(new EndOfLineRule(SQLFoldingConstants.SQL_COMMENT_SINGLELINE, tknComment));
        rules.add(new NestedMultipleLineRule(SQLFoldingConstants.SQL_COMMENT_MULTILINE_START,
                SQLFoldingConstants.SQL_COMMENT_MULTILINE_END, tknMultilineComment, (char) 0, true));
        rules.add(new NestedQuotesRule(SQLFoldingConstants.SQL_LITERAL_DOUBLE_QUOTES,
                SQLFoldingConstants.SQL_LITERAL_DOUBLE_QUOTES, tknSqlDoubleQuotesIdentifier,
                SQLFoldingConstants.SQL_LITERAL_DOUBLE_QUOTES_CHAR, true));
        rules.add(new NestedQuotesRule(SQLFoldingConstants.SQL_LITERAL_SINGLE_QUOTES,
                SQLFoldingConstants.SQL_LITERAL_SINGLE_QUOTES, tknSqlString,
                SQLFoldingConstants.SQL_LITERAL_SINGLE_QUOTES_CHAR, true));

        rules.add(forwardSlashRule);

        rules.add(endDelimRule);

        rules.add(brackerStartRule);

        rules.add(brackerEndRule);

        rules.add(functionEndRule);

        rules.add(newlineRule);

        rules.add(wordRule);

        rules.add(numberRule);

        IRule[] result = new IRule[rules.size()];
        rules.toArray(result);
        return result;
    }

    private void addRules(SQLWordRule wordRule, Map<String, IToken> dmlRules) {
        for (Entry<String, IToken> tokenEntry : dmlRules.entrySet()) {
            wordRule.addWord(tokenEntry.getKey(), tokenEntry.getValue());
        }
    }

    private Map<String, IToken> getKeywordRules() {

        Map<String, IToken> retVal = new HashMap<String, IToken>(10);

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_LANGUAGE,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_LANGUAGE, SQLFoldingConstants.SQL_KEYWORD_LANGUAGE));

        retVal.put(SQLFoldingConstants.SQL_KEYWORK_AS,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_AS, SQLFoldingConstants.SQL_KEYWORK_AS));

        retVal.put(SQLFoldingConstants.SQL_KEYWORK_IS,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_IS, SQLFoldingConstants.SQL_KEYWORK_IS));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_UNION,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_UNION, SQLFoldingConstants.SQL_KEYWORD_UNION));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_INTERSECT,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_INTERSECT, SQLFoldingConstants.SQL_KEYWORD_INTERSECT));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_EXCEPT,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_EXCEPT, SQLFoldingConstants.SQL_KEYWORD_EXCEPT));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_CASE,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_CASE, SQLFoldingConstants.SQL_KEYWORD_CASE));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_LOOP,
                new SQLToken(SQLTokenConstants.T_SQL_LOOP, SQLFoldingConstants.SQL_KEYWORD_LOOP));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_FOR,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORD_FOR, SQLFoldingConstants.SQL_KEYWORD_FOR));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_WHILE,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORD_FOR, SQLFoldingConstants.SQL_KEYWORD_WHILE));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_CURSOR,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORD_CURSOR, SQLFoldingConstants.SQL_KEYWORD_CURSOR));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_UNION,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_UNION, SQLFoldingConstants.SQL_KEYWORD_UNION));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_INTERSECT,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_UNION, SQLFoldingConstants.SQL_KEYWORD_INTERSECT));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_EXCEPT,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_UNION, SQLFoldingConstants.SQL_KEYWORD_EXCEPT));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_MINUS,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_UNION, SQLFoldingConstants.SQL_KEYWORD_MINUS));

        return retVal;
    }

    private Map<String, IToken> getCreateRules() {

        Map<String, IToken> retVal = new HashMap<String, IToken>(10);

        retVal.put(SQLFoldingConstants.SQL_CREATE,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE, SQLFoldingConstants.SQL_CREATE));

        retVal.put(SQLFoldingConstants.SQL_FUNCTION,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE_FUNC, SQLFoldingConstants.SQL_FUNCTION));

        retVal.put(SQLFoldingConstants.SQL_PROCEDURE,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE_PROC, SQLFoldingConstants.SQL_PROCEDURE));

        retVal.put(SQLFoldingConstants.SQL_PACKAGE,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE_PACKAGE, SQLFoldingConstants.SQL_PACKAGE));

        retVal.put(SQLFoldingConstants.SQL_PACKAGE_BODY,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE_PACKAGE_BODY, SQLFoldingConstants.SQL_PACKAGE_BODY));

        retVal.put(SQLFoldingConstants.SQL_TABLE,
                new SQLDDLToken(SQLTokenConstants.T_SQL_DDL_CREATE_TABLE, SQLFoldingConstants.SQL_TABLE));

        retVal.put(SQLFoldingConstants.SQL_TRIGGER,
                new SQLToken(SQLTokenConstants.T_SQL_TRIGGER, SQLFoldingConstants.SQL_TRIGGER));

        retVal.put(SQLFoldingConstants.SQL_ALTER,
                new SQLToken(SQLTokenConstants.T_SQL_DDL_ALTER, SQLFoldingConstants.SQL_ALTER));

        retVal.put(SQLFoldingConstants.SQL_VIEW,
                new SQLToken(SQLTokenConstants.T_SQL_KEYWORK_VIEW, SQLFoldingConstants.SQL_VIEW));

        retVal.put(SQLFoldingConstants.SQL_DROP,
                new SQLToken(SQLTokenConstants.T_SQL_DROP, SQLFoldingConstants.SQL_DROP));

        retVal.put(SQLFoldingConstants.SQL_GRANT,
                new SQLToken(SQLTokenConstants.T_SQL_GRANT, SQLFoldingConstants.SQL_GRANT));

        retVal.put(SQLFoldingConstants.SQL_REVOKE,
                new SQLToken(SQLTokenConstants.T_SQL_REVOKE, SQLFoldingConstants.SQL_REVOKE));

        return retVal;
    }

    private Map<String, IToken> getDMLRules() {

        Map<String, IToken> retVal = new HashMap<String, IToken>(10);

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_SELECT,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_SELECT, SQLFoldingConstants.SQL_KEYWORD_SELECT));
        retVal.put(SQLFoldingConstants.SQL_KEYWORD_INSERT,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_INSERT, SQLFoldingConstants.SQL_KEYWORD_INSERT));
        retVal.put(SQLFoldingConstants.SQL_KEYWORD_DELETE,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_DELETE, SQLFoldingConstants.SQL_KEYWORD_DELETE));
        retVal.put(SQLFoldingConstants.SQL_KEYWORD_UPDATE,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_UPDATE, SQLFoldingConstants.SQL_KEYWORD_UPDATE));
        retVal.put(SQLFoldingConstants.SQL_KEYWORD_TRUNCATE,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_TRUNCATE, SQLFoldingConstants.SQL_KEYWORD_TRUNCATE));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_WITH,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_WITH, SQLFoldingConstants.SQL_KEYWORD_WITH));

        retVal.put(SQLFoldingConstants.SQL_KEYWORK_VALUES,
                new SQLDMLToken(SQLTokenConstants.T_SQL_DML_INSERT_VALUES, SQLFoldingConstants.SQL_KEYWORK_VALUES));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_MERGE,
                new SQLDMLToken(SQLTokenConstants.T_SQL_MERGE, SQLFoldingConstants.SQL_KEYWORD_MERGE));

        retVal.put(SQLFoldingConstants.SQL_KEYWORD_WHEN,
                new SQLDMLToken(SQLTokenConstants.T_SQL_WHEN, SQLFoldingConstants.SQL_KEYWORD_WHEN));

        return retVal;
    }
}
