/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;

import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.CharPairsUtil;
import com.huawei.mppdbide.view.utils.DSDefaultCharacterPairMatcherUtil;
import com.huawei.mppdbide.view.utils.DSRegion;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSDefaultCharacterPairMatcher.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSDefaultCharacterPairMatcher extends DefaultCharacterPairMatcher {
    private CharPairsUtil fPairs;
    private int fAnchor = -1;
    private String fPartitioning;
    private boolean fCaretEitherSideOfBracket;

    /**
     * Instantiates a new DS default character pair matcher.
     *
     * @param chars the chars
     * @param partitioning the partitioning
     */
    public DSDefaultCharacterPairMatcher(char[] chars, String partitioning) {
        this(chars, partitioning, false);
    }

    /**
     * Instantiates a new DS default character pair matcher.
     *
     * @param chars the chars
     * @param partitioning the partitioning
     * @param caretEitherSideOfBracket the caret either side of bracket
     */
    public DSDefaultCharacterPairMatcher(char[] chars, String partitioning, boolean caretEitherSideOfBracket) {
        super(chars, partitioning, caretEitherSideOfBracket);
        Assert.isLegal(chars.length % 2 == 0);
        Assert.isNotNull(partitioning);
        fPairs = new CharPairsUtil(chars);
        fPartitioning = partitioning;
        fCaretEitherSideOfBracket = caretEitherSideOfBracket;
    }

    /**
     * Instantiates a new DS default character pair matcher.
     *
     * @param chars the chars
     */
    public DSDefaultCharacterPairMatcher(char[] chars) {
        this(chars, IDocumentExtension3.DEFAULT_PARTITIONING);
    }

    /**
     * Match.
     *
     * @param document the document
     * @param offset the offset
     * @param length the length
     * @return the i region
     */
    @Override
    public IRegion match(IDocument document, int offset, int length) {
        if (document == null || offset < 0 || offset > document.getLength() || Math.abs(length) > 1) {
            return null;
        }

        try {
            int sourceCaretOffset = offset + length;
            if (Math.abs(length) == 1) {
                char ch = length > 0 ? document.getChar(offset) : document.getChar(sourceCaretOffset);
                if (!fPairs.contains(ch) && !DSDefaultCharacterPairMatcherUtil.isQuote(ch)) {
                    return null;
                }
            }
            int adjustment = getOffSetAdjustment(document, sourceCaretOffset, length);
            sourceCaretOffset += adjustment;
            return match(document, sourceCaretOffset);
        } catch (BadLocationException e) {
            return null;
        }
    }

    /**
     * Match.
     *
     * @param doc the doc
     * @param offset the offset
     * @return the i region
     */
    @Override
    public IRegion match(IDocument doc, int offset) {
        if (doc == null || offset < 0 || offset > doc.getLength()) {
            return null;
        }
        try {
            return performDsMatch(doc, offset);
        } catch (BadLocationException ble) {
            return null;
        }
    }

    /**
     * Gets the offset adjustment.
     *
     * @param document the document
     * @param offset the offset
     * @param length the length
     * @return the offset adjustment
     */
    private int getOffSetAdjustment(IDocument document, int offset, int length) {
        if (length == 0 || Math.abs(length) > 1 || offset >= document.getLength()) {
            return 0;
        }
        try {
            if (length < 0) {
                if (fPairs.isStartPunctuation(document.getChar(offset))
                        || (!fCaretEitherSideOfBracket && fPairs.isEndPunctuation(document.getChar(offset)))) {
                    return 1;
                }
            } else {
                if (fCaretEitherSideOfBracket && fPairs.isEndPunctuation(document.getChar(offset - 1))) {
                    return -1;
                }
            }
        } catch (BadLocationException exception) {
            // do nothing
            MPPDBIDELoggerUtility.error(
                    "DSDefaultCharacterPairMatcher.getOffsetAdjustment(): BadLocationException occurred.", exception);
        }
        return 0;
    }

    /**
     * Perform match.
     *
     * @param doc the doc
     * @param caretOffset the caret offset
     * @return the i region
     * @throws BadLocationException the bad location exception
     */
    public IRegion performDsMatch(IDocument doc, int caretOffset) throws BadLocationException {

        boolean isForward = true;
        DSRegion retRegion = (DSRegion) DSDefaultCharacterPairMatcherUtil.getRegion(doc, caretOffset, fPairs, isForward,
                fPartitioning, fCaretEitherSideOfBracket, false);
        if (null != retRegion) {
            fAnchor = retRegion.getForward() ? ICharacterPairMatcher.LEFT : ICharacterPairMatcher.RIGHT;
        }
        return retRegion;
    }

    /**
     * Checks if is matched char.
     *
     * @param ch the ch
     * @return true, if is matched char
     */
    @Override
    public boolean isMatchedChar(char ch) {
        return fPairs.contains(ch);
    }

    /**
     * Checks if is matched char.
     *
     * @param ch the ch
     * @param document the document
     * @param offset the offset
     * @return true, if is matched char
     */
    @Override
    public boolean isMatchedChar(char ch, IDocument document, int offset) {
        return isMatchedChar(ch);
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        fAnchor = -1;
    }

    /**
     * Gets the anchor.
     *
     * @return the anchor
     */
    @Override
    public int getAnchor() {
        return fAnchor;
    }

}
