/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Map.Entry;

import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLPredicateDetector.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLPredicateDetector extends RuleBasedScanner implements IWordDetector {

    /**
     * The syntax.
     */
    SQLSyntax syntax;

    /**
     * Instantiates a new SQL predicate detector.
     *
     * @param syntax the syntax
     */
    public SQLPredicateDetector(SQLSyntax syntax) {
        this.syntax = syntax;
    }

    /**
     * Checks if is word start.
     *
     * @param chr the chr
     * @return true, if is word start
     */
    @Override
    public boolean isWordStart(char chr) {

        for (Entry<String, String> entry : syntax.getPredicates().entrySet()) {
            if (entry.getKey().charAt(0) == chr) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if is word part.
     *
     * @param chr the chr
     * @return true, if is word part
     */
    @Override
    public boolean isWordPart(char chr) {

        for (Entry<String, String> entry : syntax.getPredicates().entrySet()) {
            if (entry.getKey().indexOf(chr) != -1) {
                return true;
            }
        }

        return false;
    }

}
