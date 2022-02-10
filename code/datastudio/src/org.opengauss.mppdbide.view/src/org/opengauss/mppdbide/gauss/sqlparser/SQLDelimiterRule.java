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

package org.opengauss.mppdbide.gauss.sqlparser;

import java.util.Locale;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * Title: SQLDelimiterRule
 *
 * @since 3.0.0
 */
public class SQLDelimiterRule implements IRule {
    private IToken token = null;
    private char[][] delimiters = null;
    private char[][] origDelimiters = null;
    private char[] buffer = null;
    private char[] origBuffer = null;

    /**
     * Instantiates a new SQL delimiter rule.
     *
     * @param delimiters the delimiters
     * @param token the token
     */
    public SQLDelimiterRule(String[] delimiters, IToken token) {
        this.token = token;
        this.origDelimiters = this.delimiters = new char[delimiters.length][];
        int index = 0;
        int maxLength = 0;
        for (String delim : delimiters) {
            this.delimiters[index] = delim.toCharArray();
            for (int cnt = 0; cnt < this.delimiters[index].length; cnt++) {
                this.delimiters[index][cnt] = Character.toUpperCase(this.delimiters[index][cnt]);
            }
            maxLength = Math.max(maxLength, this.delimiters[index].length);
            index++;
        }
        this.origBuffer = this.buffer = new char[maxLength];
    }

    /**
     * Gets the delimiters.
     *
     * @return the delimiters
     */
    public char[][] getDelimiters() {
        return delimiters.clone();
    }

    /**
     * Evaluate.
     *
     * @param scanner the scanner
     * @return the i token
     */
    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        for (int cnt = 0;; cnt++) {
            int character = scanner.read();
            boolean matches = false;
            if (character != ICharacterScanner.EOF) {
                character = Character.toUpperCase(character);
                for (int kIndex = 0; kIndex < delimiters.length; kIndex++) {
                    if (cnt < delimiters[kIndex].length && delimiters[kIndex][cnt] == character) {
                        buffer[cnt] = (char) character;
                        if (cnt == delimiters[kIndex].length - 1 && equalsBegin(delimiters[kIndex])) {
                            // Matched. Check next character
                            if (Character.isLetterOrDigit(character)) {
                                int cn = scanner.read();
                                scanner.unread();
                                if (Character.isLetterOrDigit(cn)) {
                                    matches = false;
                                    continue;
                                }
                            }
                            return token;
                        }
                        matches = true;
                        break;
                    }
                }
            }
            if (!matches) {
                for (int indx = 0; indx <= cnt; indx++) {
                    scanner.unread();
                }
                return Token.UNDEFINED;
            }
        }
    }

    private boolean equalsBegin(char[] delimiter) {
        for (int index = 0; index < delimiter.length; index++) {
            if (buffer[index] != delimiter[index]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if is empty.
     *
     * @param value the value
     * @return true, if is empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    /**
     * Change delimiter.
     *
     * @param newDelimiter the new delimiter
     */
    public void changeDelimiter(String newDelimiter) {
        if (isEmpty(newDelimiter)) {
            this.delimiters = this.origDelimiters;
            this.buffer = this.origBuffer;
        } else {
            for (char[] delim : delimiters) {
                String delimStr = String.valueOf(delim);
                if (newDelimiter.equals(delimStr)) {
                    return;
                }
                if (newDelimiter.endsWith(delimStr)) {
                    // New delimiter ends with old delimiter (as command
                    // terminator). Remove it.
                    newDelimiter = newDelimiter.substring(0, newDelimiter.length() - delimStr.length()).trim();
                }
            }
            this.delimiters = new char[1][];
            this.delimiters[0] = newDelimiter.toUpperCase(Locale.ENGLISH).toCharArray();
            this.buffer = new char[newDelimiter.length()];
        }
    }
}
