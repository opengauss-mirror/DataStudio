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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;

/**
 * 
 * Title: class
 * 
 * Description: The Class NestedQuotesRule.
 *
 * @since 3.0.0
 */
public class NestedQuotesRule extends MultiLineRule {

    /**
     * The quote nesting depth.
     */
    protected int quoteNestingDepth = 0;

    /**
     * Instantiates a new nested quotes rule.
     *
     * @param startSequence the start sequence
     * @param endSequence the end sequence
     * @param token the token
     * @param escapeCharacter the escape character
     * @param isBreaksOnEOF the is breaks on EOF
     */
    public NestedQuotesRule(String startSequence, String endSequence, IToken token, char escapeCharacter,
            boolean isBreaksOnEOF) {
        super(startSequence, endSequence, token, escapeCharacter, isBreaksOnEOF);
    }

    /**
     * End sequence detected.
     *
     * @param characterScanner the character scanner
     * @return true, if successful
     */
    @Override
    protected boolean endSequenceDetected(ICharacterScanner characterScanner) {
        int currChar = characterScanner.read();
        int nextChar = -1;
        int nestedDepth = 0;
        while (currChar != ICharacterScanner.EOF) {

            if (currChar == fEndSequence[0]) {
                nestedDepth++;
                nextChar = characterScanner.read();
                if (nextChar == fEndSequence[0]) {
                    nestedDepth--;
                } else if (nestedDepth == 1 && currChar == fEndSequence[0] && nextChar != fEndSequence[0]) {
                    characterScanner.unread();
                    return true;
                }
            }
            currChar = characterScanner.read();
        }
        if (fBreaksOnEOF) {
            return true;
        }
        characterScanner.unread();
        return false;
    }

    /**
     * Do evaluate.
     *
     * @param charScanner the char scanner
     * @param isResume the is resume
     * @return the i token
     */
    @Override
    protected IToken doEvaluate(ICharacterScanner charScanner, boolean isResume) {
        if (isResume) {
            quoteNestingDepth = 0;

            if (endSequenceDetected(charScanner)) {
                return fToken;
            }
        } else {
            int character = charScanner.read();
            if (character == fStartSequence[0]) {
                if (sequenceDetected(charScanner, fStartSequence, false)) {
                    if (endSequenceDetected(charScanner)) {
                        return fToken;
                    }
                }
            }
        }

        charScanner.unread();
        return Token.UNDEFINED;
    }

}
