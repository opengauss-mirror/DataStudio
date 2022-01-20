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

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.comm.NestedMultipleLineRule;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLCodeScanner.
 *
 * @since 3.0.0
 */
public class SQLCodeScanner extends RuleBasedScanner implements ISQLSyntax {

    /**
     * The syntax.
     */
    SQLSyntax syntax;

    /**
     * Instantiates a new SQL code scanner.
     *
     * @param sqlSyntax the sql syntax
     */
    public SQLCodeScanner(SQLSyntax sqlSyntax) {
        this.syntax = sqlSyntax;
        SQLSyntaxColorProvider provider = SQLEditorPlugin.getDefault().getColorProvider();

        IToken predicate = new Token(
                new TextAttribute(provider.getColor(SQLSyntaxColorProvider.getPREDICATES()), null, SWT.BOLD, null));

        IToken string = new Token(
                new TextAttribute(provider.getColor(SQLSyntaxColorProvider.getSTRING()), null, SWT.NORMAL));
        IToken singlecomment = new Token(new TextAttribute(
                provider.getColor(SQLSyntaxColorProvider.getSQLSingleLineComment()), null, SWT.NORMAL));
        IToken multi = new Token(
                new TextAttribute(provider.getColor(SQLSyntaxColorProvider.SQL_MULTI_LINE_COMMENT), null, SWT.NORMAL));

        IToken other = new Token(new TextAttribute(provider.getColor(SQLSyntaxColorProvider.getDEFAULT())));

        setDefaultReturnToken(other);
        List<IRule> rules = new ArrayList<IRule>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        addCommentRules(singlecomment, multi, rules);

        addStringQuoteRules(string, rules);

        addPredicateRules(predicate, other, rules);

        rules.add(new WhitespaceRule(new SQLWhiteSpaceDetector()));

        WordRule wordRule = addKeywordRules(provider, other);

        rules.add(wordRule);

        IRule[] res = new IRule[rules.size()];
        rules.toArray(res);
        setRules(res);
        wordRule = null;
    }

    /**
     * Adds the keyword rules.
     *
     * @param provider the provider
     * @param other the other
     * @return the word rule
     */
    private WordRule addKeywordRules(SQLSyntaxColorProvider provider, IToken other) {
        IToken unreservedKeyword = new Token(new TextAttribute(
                provider.getColor(SQLSyntaxColorProvider.getUnreservedKeyword()), null, SWT.BOLD, null));

        IToken reservedkeywords = new Token(new TextAttribute(
                provider.getColor(SQLSyntaxColorProvider.getReservedKeyword()), null, SWT.BOLD, null));

        IToken constant = new Token(
                new TextAttribute(provider.getColor(SQLSyntaxColorProvider.getCONSTANTS()), null, SWT.BOLD, null));

        IToken type = new Token(
                new TextAttribute(provider.getColor(SQLSyntaxColorProvider.getTYPE()), null, SWT.BOLD, null));

        WordRule wordRule = new WordRule(new SQLWordDetector(syntax), other, true);

        for (Entry<String, String> entry : syntax.getConstants().entrySet()) {
            wordRule.addWord(entry.getKey(), constant);
        }

        for (Entry<String, String> entry : syntax.getUnreservedkrywords().entrySet()) {
            wordRule.addWord(entry.getKey(), unreservedKeyword);
        }

        for (Entry<String, String> entry : syntax.getReservedkrywords().entrySet()) {
            wordRule.addWord(entry.getKey(), reservedkeywords);
        }

        for (Entry<String, String> entry : syntax.getTypes().entrySet()) {
            wordRule.addWord(entry.getKey(), type);
        }
        return wordRule;
    }

    /**
     * Adds the predicate rules.
     *
     * @param predicate the predicate
     * @param other the other
     * @param rules the rules
     */
    private void addPredicateRules(IToken predicate, IToken other, List<IRule> rules) {
        WordRule predicateRule = new WordRule(new SQLPredicateDetector(syntax), other, true);
        for (Entry<String, String> entry : syntax.getPredicates().entrySet()) {
            predicateRule.addWord(entry.getKey(), predicate);
        }
        rules.add(predicateRule);
    }

    /**
     * Adds the string quote rules.
     *
     * @param string the string
     * @param rules the rules
     */
    private void addStringQuoteRules(IToken string, List<IRule> rules) {
        rules.add(new SingleLineRule("\"", "\"", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string, '\\'));
        rules.add(new SingleLineRule("'", "'", string));
    }

    /**
     * Adds the comment rules.
     *
     * @param singlecomment the singlecomment
     * @param multi the multi
     * @param rules the rules
     */
    private void addCommentRules(IToken singlecomment, IToken multi, List<IRule> rules) {
        rules.add(new EndOfLineRule("--", singlecomment, (char) 0));
        rules.add(new NestedMultipleLineRule("/*", "*/", multi, '\0', true));
    }

}
