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

package org.opengauss.mppdbide.view.utils;

import org.eclipse.core.runtime.Assert;

/**
 * 
 * Title: class
 * 
 * Description: The Class CharPairsUtil.
 *
 * @since 3.0.0
 */
public class CharPairsUtil {

    private final char[] charPairs;

    /**
     * Instantiates a new char pairs util.
     *
     * @param pairs the pairs
     */
    public CharPairsUtil(char[] pairs) {
        charPairs = pairs.clone();
    }

    /**
     * Contains.
     *
     * @param c the c
     * @return true, if successful
     */
    public boolean contains(char c) {
        char[] pairs = charPairs;
        for (int i = 0, n = pairs.length; i < n; i++) {
            if (c == pairs[i]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is opening punctuation.
     *
     * @param c the c
     * @param searchForward the search forward
     * @return true, if is opening punctuation
     */
    public boolean isOpeningPunctuation(char c, boolean searchForward) {
        for (int i = 0; i < charPairs.length; i += 2) {
            if (searchForward && getStartChar(i) == c) {
                return true;
            } else if (!searchForward && getEndChar(i) == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if is start punctuation.
     *
     * @param charValue the c
     * @return true, if is start punctuation
     */
    public boolean isStartPunctuation(char charValue) {
        return this.isOpeningPunctuation(charValue, true);
    }

    /**
     * Checks if is end punctuation.
     *
     * @param charValue the c
     * @return true, if is end punctuation
     */
    public boolean isEndPunctuation(char charValue) {
        return this.isOpeningPunctuation(charValue, false);
    }

    /**
     * Gets the matching.
     *
     * @param c the c
     * @return the matching
     */
    public char getMatching(char c) {
        for (int i = 0; i < charPairs.length; i += 2) {
            if (getStartChar(i) == c) {
                return getEndChar(i);
            } else if (getEndChar(i) == c) {
                return getStartChar(i);
            }
        }
        Assert.isTrue(false);
        return '\0';
    }

    /**
     * Gets the start char.
     *
     * @param i the i
     * @return the start char
     */
    private char getStartChar(int i) {
        return charPairs[i];
    }

    /**
     * Gets the end char.
     *
     * @param i the i
     * @return the end char
     */
    private char getEndChar(int i) {
        return charPairs[i + 1];
    }

}
