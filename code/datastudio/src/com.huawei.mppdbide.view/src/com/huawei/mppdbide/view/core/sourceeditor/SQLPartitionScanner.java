/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

import com.huawei.mppdbide.gauss.sqlparser.comm.ISQLSyntax;
import com.huawei.mppdbide.gauss.sqlparser.comm.NestedMultipleLineRule;
import com.huawei.mppdbide.gauss.sqlparser.comm.NestedQuotesRule;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLPartitionScanner.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLPartitionScanner extends RuleBasedPartitionScanner implements ISQLSyntax {

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
     * The rules.
     */
    List<IPredicateRule> rules = new ArrayList<IPredicateRule>();

    /**
     * Instantiates a new SQL partition scanner.
     */

    public SQLPartitionScanner() {
        super();
        initializeRules();

        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }

    /**
     * Initialize rules.
     */
    private void initializeRules() {
        rules.add(new EndOfLineRule("--", tknComment));
        rules.add(new EmptyBlockCommentRule(tknMultilineComment));
        rules.add(new NestedMultipleLineRule("/*", "*/", tknMultilineComment, (char) 0, true));
        rules.add(new NestedQuotesRule("\"", "\"", tknSqlDoubleQuotesIdentifier, '"', true));
        rules.add(new NestedQuotesRule("'", "'", tknSqlString, '\'', true));
    }

    /**
     * Clear rules.
     */
    private void clearRules() {
        for (IPredicateRule rule : rules) {
            rule = null;
        }
        rules.clear();
    }

    /**
     * Gets the scanned partition string.
     *
     * @return the scanned partition string
     */
    public String getScannedPartitionString() {
        try {
            return fDocument.get(fPartitionOffset, fOffset - fPartitionOffset);
        } catch (BadLocationException e) {
            // Ignore, nothing to be done.
            MPPDBIDELoggerUtility.debug("Bad Location Exception for scanner.");
        }

        return "";
    }

    /**
     * Gets the document regions.
     *
     * @param doc the doc
     * @return the document regions
     */
    public static ITypedRegion[] getDocumentRegions(IDocument doc) {
        ITypedRegion[] retRegions = null;

        try {
            retRegions = TextUtilities.computePartitioning(doc, SQL_PARTITIONING, 0, doc.getLength(), false);
        } catch (BadLocationException e) {
            // Ignore, nothing to be done.
            MPPDBIDELoggerUtility.debug("Bad Location Exception for scanner.");
        }

        return retRegions;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EmptyBlockCommentDetector.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class EmptyBlockCommentDetector implements IWordDetector {

        /**
         * Checks if is word start.
         *
         * @param chr the c
         * @return true, if is word start
         */
        public boolean isWordStart(char chr) {
            return chr == '/';
        }

        /**
         * Checks if is word part.
         *
         * @param chr the c
         * @return true, if is word part
         */
        public boolean isWordPart(char chr) {
            return chr == '*' || chr == '/';
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EmptyBlockCommentRule.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class EmptyBlockCommentRule extends WordRule implements IPredicateRule {
        private IToken emptyCommentToken;

        /**
         * Instantiates a new empty block comment rule.
         *
         * @param token the token
         */
        public EmptyBlockCommentRule(IToken token) {
            super(new EmptyBlockCommentDetector());
            emptyCommentToken = token;
            addWord("/**/", emptyCommentToken);
        }

        @Override
        public IToken getSuccessToken() {
            return emptyCommentToken;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner, boolean resume) {
            return evaluate(scanner);
        }
    }
    
    /**
     * Pre destroy
     */
    @PreDestroy
    public void preDestroy() {
        clearRules();
    }
}
