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
 * Description: The Class NestedMultipleLineRule.
 *
 * @since 3.0.0
 */
public class NestedMultipleLineRule extends MultiLineRule {

    /**
     * The comment nesting depth.
     */
    protected int commentNestingDepth = 0;

    /**
     * Instantiates a new nested multiple line rule.
     *
     * @param startSequence the start sequence
     * @param endSequence the end sequence
     * @param token the token
     * @param escapeCharacter the escape character
     * @param isBreaksOnEOF the is breaks on EOF
     */
    public NestedMultipleLineRule(String startSequence, String endSequence, IToken token, char escapeCharacter,
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
        int character = characterScanner.read();
        while (character != ICharacterScanner.EOF) {
            if (character == fEscapeCharacter) {
                // Skip escaped character.
                characterScanner.read();
            } else if (verifyStartSequence(character)) {
                // Check if nested start sequence has been found.
                if (sequenceDetected(characterScanner, fStartSequence, false)) {
                    commentNestingDepth++;
                }
            } else if (verifyEndSequence(character)) {
                // Check if specified end sequence has been found.
                if (sequenceDetected(characterScanner, fEndSequence, true)) {
                    commentNestingDepth--;
                    if (commentNestingDepth <= 0) {
                        return true;
                    }
                }
            }
            character = characterScanner.read();
        }
        if (fBreaksOnEOF) {
            return true;
        }
        characterScanner.unread();
        return false;
    }

    /**
     * Verify start sequence.
     *
     * @param chr the c
     * @return true, if successful
     */
    private boolean verifyStartSequence(int chr) {
        return fStartSequence.length > 0 && chr == fStartSequence[0];
    }

    /**
     * Verify end sequence.
     *
     * @param chr the c
     * @return true, if successful
     */
    private boolean verifyEndSequence(int chr) {
        return fEndSequence.length > 0 && chr == fEndSequence[0];
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
            commentNestingDepth = 0;

            if (endSequenceDetected(charScanner)) {
                return fToken;
            }
        } else {
            int chr = charScanner.read();
            if (chr == fStartSequence[0]) {
                if (sequenceDetected(charScanner, fStartSequence, false)) {
                    commentNestingDepth = 1;
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
