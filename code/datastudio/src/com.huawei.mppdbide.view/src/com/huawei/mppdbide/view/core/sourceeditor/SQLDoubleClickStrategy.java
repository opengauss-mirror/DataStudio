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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextViewer;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.CharPairsUtil;
import com.huawei.mppdbide.view.utils.DSDefaultCharacterPairMatcherUtil;
import com.huawei.mppdbide.view.utils.DSRegion;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLDoubleClickStrategy.
 *
 * @since 3.0.0
 */
public class SQLDoubleClickStrategy implements ITextDoubleClickStrategy {

    /**
     * The text.
     */
    protected ITextViewer fText;

    /**
     * The current position.
     */
    protected int currentPosition;

    /**
     * The start position.
     */
    protected int startPosition;

    /**
     * The end position.
     */
    protected int endPosition;

    /**
     * Instantiates a new SQL double click strategy.
     */
    public SQLDoubleClickStrategy() {
        super();
    }

    /**
     * Double clicked.
     *
     * @param viewer the viewer
     */
    @Override
    public void doubleClicked(ITextViewer viewer) {
        /* Get the viewer we are dealing with. */
        fText = viewer;

        /* Get the double-click location in the document. */
        currentPosition = viewer.getSelectedRange().x;

        if (currentPosition < 0 || currentPosition > fText.getDocument().getLength()) {
            return;
        }

        try {
            if (!selectBracketBlock()) {
                selectWord();
            }
        } catch (BadLocationException exception) {
            // do nothing
            MPPDBIDELoggerUtility.error("SQLDoubleClickStrategy.doubleClicked(): BadLocationException occurred.",
                    exception);
        }
    }

    /**
     * Match brackets at.
     *
     * @return true, if successful
     * @throws BadLocationException the bad location exception
     */
    protected boolean matchBracketsAt() throws BadLocationException {
        boolean isForward = true;
        CharPairsUtil fPairs = new CharPairsUtil(DSDefaultCharacterPairMatcherUtil.getMatchPunctuations());

        DSRegion region = (DSRegion) DSDefaultCharacterPairMatcherUtil.getRegion(fText.getDocument(), currentPosition,
                fPairs, isForward, DSDefaultCharacterPairMatcherUtil.SQL_PARTITIONING, true, false);
        if (null != region) {
            String partition = null != region.getPartitionType() ? region.getPartitionType() : "";
            if ((partition.equals(DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER)
                    || partition.equals(DSDefaultCharacterPairMatcherUtil.SQL_STRING)) && region.getForward()) {
                startPosition = region.getOffset() - 1;
                endPosition = region.getOffset() + region.getLength() - 1;
                return true;
            } else {
                startPosition = region.getOffset();
                endPosition = region.getOffset() + region.getLength() - 1;
                return true;
            }
        }

        return false;

    }

    /**
     * Match word.
     *
     * @return true, if successful
     */
    protected boolean matchWord() {
        IDocument doc = fText.getDocument();

        try {
            int position = currentPosition;
            char chr;

            // Scan back to get the beginning of the word.
            while (position >= 0) {
                chr = doc.getChar(position);
                if (!Character.isJavaIdentifierPart(chr)) {
                    break;
                }
                --position;
            }
            startPosition = position;

            // Scan forward for the end of the word.
            position = currentPosition;
            int length = doc.getLength();
            while (position < length) {
                chr = doc.getChar(position);
                if (!Character.isJavaIdentifierPart(chr)) {
                    break;
                }
                ++position;
            }
            endPosition = position;

            return true;
        } catch (BadLocationException exception) {
            // do nothing
            MPPDBIDELoggerUtility.error("SQLDoubleClickStrategy.matchWord(): BadLocationException occurred.",
                    exception);
        }

        return false;
    }

    /**
     * Select bracket block.
     *
     * @return true, if successful
     * @throws BadLocationException the bad location exception
     */
    protected boolean selectBracketBlock() throws BadLocationException {
        if (matchBracketsAt()) {
            if (startPosition == endPosition) {
                fText.setSelectedRange(startPosition, 0);
            } else {
                fText.setSelectedRange(startPosition + 1, endPosition - startPosition - 1);
            }

            return true;
        }
        return false;
    }

    /**
     * Select word.
     */
    protected void selectWord() {
        if (matchWord()) {
            if (startPosition == endPosition) {
                fText.setSelectedRange(startPosition, 0);
            } else {
                fText.setSelectedRange(startPosition + 1, endPosition - startPosition - 1);
            }
        }
    }

}
