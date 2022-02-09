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

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLWordDetector.
 *
 * @since 3.0.0
 */
public class SQLWordDetector extends RuleBasedScanner implements IWordDetector {
    private Set<Character> startChars = new HashSet<>();
    private Set<Character> keywordChars = new HashSet<>();

    /**
     * Instantiates a new SQL word detector.
     *
     * @param syntax the syntax
     */
    public SQLWordDetector(SQLSyntax syntax) {
        for (Entry<String, String> entry : syntax.getReservedkrywords().entrySet()) {
            startChars.add(entry.getKey().charAt(0));

            char[] chars = entry.getKey().toCharArray();
            for (int index = 0; index < chars.length; index++) {
                keywordChars.add(chars[index]);
            }
        }
        for (Entry<String, String> entry : syntax.getTypes().entrySet()) {
            startChars.add(entry.getKey().charAt(0));

            char[] chars = entry.getKey().toCharArray();
            for (int pos = 0; pos < chars.length; pos++) {
                keywordChars.add(chars[pos]);
            }
        }
        for (Entry<String, String> entry : syntax.getConstants().entrySet()) {
            startChars.add(entry.getKey().charAt(0));

            char[] chars = entry.getKey().toCharArray();
            for (int conIndex = 0; conIndex < chars.length; conIndex++) {
                keywordChars.add(chars[conIndex]);
            }
        }
        for (Entry<String, String> entry : syntax.getUnreservedkrywords().entrySet()) {
            startChars.add(entry.getKey().charAt(0));

            char[] chars = entry.getKey().toCharArray();
            for (int kwIndex = 0; kwIndex < chars.length; kwIndex++) {
                keywordChars.add(chars[kwIndex]);
            }
        }
    }

    /**
     * Checks if is word start.
     *
     * @param character the character
     * @return true, if is word start
     */
    public boolean isWordStart(char character) {
        char lowerC = Character.toUpperCase(character);

        return this.startChars.contains(lowerC) ? true : false;
    }

    /**
     * Checks if is word part.
     *
     * @param character the character
     * @return true, if is word part
     */
    public boolean isWordPart(char character) {
        char lowerC = Character.toUpperCase(character);

        return this.keywordChars.contains(lowerC) ? true : false;
    }

}
