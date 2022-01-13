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
 * @since 3.0.0
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
