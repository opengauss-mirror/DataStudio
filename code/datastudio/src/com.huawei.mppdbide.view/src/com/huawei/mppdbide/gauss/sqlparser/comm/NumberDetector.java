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

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * 
 * Title: NumberDetector
 *
 * @since 3.0.0
 */
public class NumberDetector implements IWordDetector {

    /**
     * Used to keep the state of the token
     */
    private FastStringBuffer buffer = new FastStringBuffer();

    /**
     * Defines if we are at an hexa number
     */
    private boolean isInHexa;

    /**
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
     */
    public boolean isWordStart(char wordStart) {
        isInHexa = false;
        buffer.clear();
        buffer.append(wordStart);
        return Character.isDigit(wordStart);
    }

    /**
     * Check if we are still in the number
     */
    public boolean isWordPart(char wordPart) {
        // ok, we have to test for scientific notation e.g.: 10.9e10

        if ((wordPart == 'x' || wordPart == 'X') && buffer.length() == 1 && buffer.charAt(0) == '0') {
            // it is an hexadecimal
            buffer.append(wordPart);
            isInHexa = true;
            return true;
        } else {
            buffer.append(wordPart);
        }

        if (isInHexa) {
            return Character.isDigit(wordPart) || wordPart == 'a' || wordPart == 'A' || wordPart == 'b'
                    || wordPart == 'B' || wordPart == 'c' || wordPart == 'C' || wordPart == 'd' || wordPart == 'D'
                    || wordPart == 'e' || wordPart == 'E' || wordPart == 'f' || wordPart == 'F';

        } else {
            return Character.isDigit(wordPart) || wordPart == 'e' || wordPart == '.';
        }
    }

}
