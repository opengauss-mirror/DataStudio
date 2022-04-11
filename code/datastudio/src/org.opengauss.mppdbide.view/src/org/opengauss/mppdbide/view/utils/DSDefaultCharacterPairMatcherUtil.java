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

import java.util.Arrays;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.TextUtilities;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSDefaultCharacterPairMatcherUtil.
 *
 * @since 3.0.0
 */
public abstract class DSDefaultCharacterPairMatcherUtil {

    /**
     * The Constant MATCH_PUNCTUATIONS.
     */
    static final char[] MATCH_PUNCTUATIONS = {'(', ')', '{', '}', '[', ']'};

    /**
     * The Constant MATCH_PUNCTUATIONS_QUOTES.
     */
    static final char[] MATCH_PUNCTUATIONS_QUOTES = {'\'', '\'', '"', '"'};

    /**
     * The Constant EDITOR_MATCHING_BRACKETS.
     */
    public static final String EDITOR_MATCHING_BRACKETS = "matchingBrackets";

    /**
     * The Constant EDITOR_MATCHING_BRACKETS_COLOR.
     */
    public static final String EDITOR_MATCHING_BRACKETS_COLOR = "matchingBracketsColor";

    /**
     * The Constant SQL_PARTITIONING.
     */
    public static final String SQL_PARTITIONING = "___sql_partitioning";

    /**
     * The Constant SQL_DOUBLE_QUOTES_IDENTIFIER.
     */
    public static final String SQL_DOUBLE_QUOTES_IDENTIFIER = "sql_double_quotes_identifier";

    /**
     * The Constant SQL_STRING.
     */
    public static final String SQL_STRING = "sql_character";

    /**
     * The Constant SINGLE_QUOTE.
     */
    public static final char SINGLE_QUOTE = '\'';

    /**
     * The Constant DOUBLE_QUOTE.
     */
    public static final char DOUBLE_QUOTE = '\"';

    /**
     * The Constant SQL_COMMENT.
     */
    public static final String SQL_COMMENT = "sql_comment";

    /**
     * The Constant SQL_MULTILINE_COMMENT.
     */
    public static final String SQL_MULTILINE_COMMENT = "sql_multiline_comment";

    /**
     * The Constant SINGLE_LINE_COMMENT.
     */
    public static final String SINGLE_LINE_COMMENT = "sql_singleline_comment";

    /**
     * Gets the region.
     *
     * @param doc the doc
     * @param caretOffsetParam the caret offset param
     * @param fPairs the f pairs
     * @param isForwardParam the is forward param
     * @param fPartitioning the f partitioning
     * @param fCaretEitherSideOfBracket the f caret either side of bracket
     * @param isContentAssist the is content assist
     * @return the region
     * @throws BadLocationException the bad location exception
     */
    public static IRegion getRegion(IDocument doc, int caretOffsetParam, CharPairsUtil fPairs, boolean isForwardParam,
            String fPartitioning, boolean fCaretEitherSideOfBracket, boolean isContentAssist)
            throws BadLocationException {
        int caretOffset = caretOffsetParam;
        boolean isForward = isForwardParam;
        char prevChar = getPreChar(doc, caretOffset);
        char ch = Character.MIN_VALUE;
        boolean quotesFlag = false;
        if (isQuote(prevChar)) {
            caretOffset = getCharOffset(doc, caretOffset, fPairs, fPartitioning);
        }
        final ITypedRegion region1 = getPartition(doc, caretOffset, fPartitioning);
        String partition1 = "";
        int partOffset1 = 0;
        if (null != region1) {
            partition1 = region1.getType();
            partOffset1 = region1.getOffset();
            int partLength1 = region1.getLength();

            if (isDoubleQuotes(partition1)) {
                if (isOffsetFlag(caretOffset, partOffset1, partLength1)) {

                    return null;
                } else if (isQuotesFlag(caretOffset, partOffset1, partLength1)) {
                    quotesFlag = true;
                }

            }
        }
        return validateAndGetRegin(doc, fPairs, fPartitioning, fCaretEitherSideOfBracket, isContentAssist, caretOffset,
                isForward, prevChar, ch, quotesFlag, partition1);
    }

    private static IRegion validateAndGetRegin(IDocument doc, CharPairsUtil fPairs, String fPartitioning,
            boolean fCaretEitherSideOfBracket, boolean isContentAssist, int caretOffsetParam, boolean isForwardParam,
            char prevCharParam, char chParam, boolean quotesFlag, String partition1) throws BadLocationException {
        int caretOffset = caretOffsetParam;
        char prevChar = prevCharParam;
        boolean isForward = isForwardParam;
        char ch = chParam;
        if (isCaretEitherSideOfBracketAndNotQuotes(fCaretEitherSideOfBracket, quotesFlag)) {
            char currChar = getCurrChar(doc, caretOffset);
            if (isEndPunctuation(fPairs, prevChar, currChar)) {
                caretOffset--;
                currChar = prevChar;
                prevChar = doc.getChar(Math.max(caretOffset - 1, 0));
            } else if (isStartPunctuation(fPairs, prevChar, currChar)) {
                caretOffset++;
                prevChar = currChar;
                currChar = doc.getChar(caretOffset);
            } else if (fpairContainsPreOrNext(fPairs, prevChar, currChar)) {
                if (isQuote(prevChar)) {
                    caretOffset--;
                    currChar = prevChar;
                    prevChar = doc.getChar(Math.max(caretOffset - 1, 0));
                }
            }
            isForward = getForward(fPairs, prevChar);
            boolean isBackward = getBackward(fPairs, currChar);
            if (isNotForwardBackward(isForward, prevChar, currChar, isBackward)) {
                return null;
            }
            ch = isForward ? prevChar : currChar;
        } else if (isNotCaretEitherSideOfBracketAndQuotesFlag(fCaretEitherSideOfBracket, quotesFlag)) {
            if (isNotContentAssistAndQuoteFlag(fPairs, isContentAssist, prevChar, quotesFlag)) {
                return null;
            }
            isForward = fPairs.isStartPunctuation(prevChar);
            ch = prevChar;
        }
        return getRegin1(doc, fPairs, fPartitioning, fCaretEitherSideOfBracket, isContentAssist, caretOffset, isForward,
                prevChar, ch, partition1);
    }

    private static IRegion getRegin1(IDocument doc, CharPairsUtil fPairs, String fPartitioning,
            boolean fCaretEitherSideOfBracket, boolean isContentAssist, int caretOffset, boolean isForward,
            char prevChar, char ch, String partition1) throws BadLocationException {
        char prevCharQuote = Character.MIN_VALUE;
        char currCharQuote = Character.MIN_VALUE;

        if (isDoubleQuotesAndEndPunctuation(fPairs, ch, partition1)) {
            prevCharQuote = getPreChar(doc, caretOffset);
            currCharQuote = getCurrChar(doc, caretOffset);
        }
        final ITypedRegion region = getPartitionForRegion(doc, caretOffset, isForward, fPartitioning,
                fCaretEitherSideOfBracket);

        if (isContentAssist) {
            return (ITypedRegion) region;
        }

        return validateDouleQuoteAndGetRegin(doc, fPairs, fPartitioning, fCaretEitherSideOfBracket, caretOffset,
                isForward, prevChar, ch, partition1, prevCharQuote, currCharQuote, region);
    }

    private static IRegion validateDouleQuoteAndGetRegin(IDocument doc, CharPairsUtil fPairs, String fPartitioning,
            boolean fCaretEitherSideOfBracket, int caretOffset, boolean isForwardParam, char prevChar, char ch,
            String partition1, char prevCharQuote, char currCharQuote, final ITypedRegion region)
            throws BadLocationException {
        boolean isForward = isForwardParam;
        if (null == region) {
            return null;
        }
        char chQuote = Character.MIN_VALUE;
        final String partition = region.getType();
        int partOffset = region.getOffset();
        int partLength = region.getLength();
        if (DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(partition)) {
            chQuote = getChQuote1(prevCharQuote, currCharQuote);
        } else if (DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(partition)) {
            chQuote = getChQuote2(prevCharQuote, currCharQuote);
        }
        if (isDoubleQuotesAndNotSingleQuote(chQuote, partition)) {
            return null;
        }

        if (isDoubleQuoteAndSingleQuote(chQuote, partition)) {
            if (isDoubleQuotesAndCaretOffsetPartoffsetEqual(caretOffset, fPairs, prevChar, partition1, partOffset)) {
                if (caretOffsetPartoffsetCon1(caretOffset, partOffset)) {
                    isForward = true;
                } else if (caretOffsetPartoffsetCon2(caretOffset, partOffset, partLength)) {
                    isForward = false;
                } else {
                    return null;
                }
            }
        } else if (validateComments(partition) || validateChQuote(fPairs, chQuote)
                || validateQuotes(chQuote, partition)) {
            return null;
        }

        final int searchStartPosition = getSearchStartPosition(caretOffset, isForward, fCaretEitherSideOfBracket);

        final DocumentPartitionAccessor partDoc = new DocumentPartitionAccessor(doc, fPartitioning, partition);

        int endOffset = findMatchingPeer(partDoc, getStartChar(fPairs, ch, chQuote), getEndChar(fPairs, ch, chQuote),
                isForward, getBoundary(doc, isForward), searchStartPosition);
        if (isEndOffSetMinusOne(region, endOffset)) {
            return null;
        }
        return validateQuoteAndGetRegin(doc, fPairs, fCaretEitherSideOfBracket, caretOffset, isForward, prevChar,
                partition1, chQuote, region, partOffset, partLength, endOffset);
    }

    private static boolean validateComments(final String partition) {
        return SQL_COMMENT.equals(partition) || SQL_MULTILINE_COMMENT.equals(partition)
                || SINGLE_LINE_COMMENT.equals(partition);
    }

    private static boolean validateChQuote(CharPairsUtil fPairs, char chQuote) {
        return chQuote != Character.MIN_VALUE && !fPairs.contains(chQuote)
                && !DSDefaultCharacterPairMatcherUtil.isQuote(chQuote);
    }

    private static boolean validateQuotes(char chQuote, final String partition) {
        return chQuote != Character.MIN_VALUE
                && (DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(partition)
                        && DSDefaultCharacterPairMatcherUtil.SINGLE_QUOTE == chQuote)
                || (DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(partition)
                        && DSDefaultCharacterPairMatcherUtil.DOUBLE_QUOTE == chQuote);
    }

    private static IRegion validateQuoteAndGetRegin(IDocument doc, CharPairsUtil fPairs,
            boolean fCaretEitherSideOfBracket, int caretOffset, boolean isForward, char prevChar, String partition1,
            char chQuote, final ITypedRegion region, int partOffset, int partLength, int endOffset)
            throws BadLocationException {
        final int adjustedEndOffset;
        final int adjustedOffset;
        if (isSQLDoubleQuotesOrSingleQuote(chQuote, region)) {
            if (isDoubleQuotesAndCatetOffsetPartoffsetEqual(caretOffset, fPairs, prevChar, partition1, partOffset)) {
                adjustedOffset = getAdjustedOffset(caretOffset, isForward, fCaretEitherSideOfBracket);
                adjustedEndOffset = adjustedEndOffset1(isForward, endOffset);
            } else {
                adjustedOffset = getAdjustedOffset1(isForward, partOffset, partLength);
                adjustedEndOffset = adjustedEndOffset2(isForward, partOffset, partLength);
            }

            // If first char(cursor position) of the partition is quote and last
            // char is not null return null
            char endChar = doc.getChar(adjustedEndOffset - 1);
            if (isForwardAndQuote(isForward, chQuote, endChar)) {
                return null;
            }

            // If last char(cursor position) of the partition is quote and first
            // char is not null return null
            endChar = doc.getChar(adjustedEndOffset);
            if (isNotForwardAndQuote(isForward, chQuote, endChar)) {
                return null;
            }

        } else {
            adjustedOffset = getAdjustedOffset(caretOffset, isForward, fCaretEitherSideOfBracket);
            adjustedEndOffset = adjustedEndOffset1(isForward, endOffset);
        }
        if (adjustedEndOffset == adjustedOffset) {
            return null;
        }
        return getUserDefinedRegion(isForward, region, adjustedOffset, adjustedEndOffset);
    }

    private static int getAdjustedOffset1(boolean isForward, int partOffset, int partLength) {
        return isForward ? partOffset + 1 : partOffset + partLength;
    }

    private static int getBoundary(IDocument doc, boolean isForward) {
        return isForward ? doc.getLength() : -1;
    }

    private static char getEndChar(CharPairsUtil fPairs, char ch, char chQuote) {
        return fPairs.contains(ch) ? fPairs.getMatching(ch) : chQuote;
    }

    private static char getStartChar(CharPairsUtil fPairs, char ch, char chQuote) {
        return fPairs.contains(ch) ? ch : chQuote;
    }

    private static boolean isDoubleQuotesAndCatetOffsetPartoffsetEqual(int caretOffset, CharPairsUtil fPairs,
            char prevChar, String partition1, int partOffset) {
        return isDoubleQuotes(partition1) && caretOffset == partOffset && fPairs.contains(prevChar);
    }

    private static boolean isEndOffSetMinusOne(final ITypedRegion region, int endOffset) {
        return endOffset == -1
                && !DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(region.getType())
                && !DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(region.getType());
    }

    private static int getSearchStartPosition(int caretOffset, boolean isForward, boolean fCaretEitherSideOfBracket) {
        return isForward ? caretOffset : (fCaretEitherSideOfBracket ? caretOffset - 1 : caretOffset - 2);
    }

    private static boolean caretOffsetPartoffsetCon2(int caretOffset, int partOffset, int partLength) {
        return caretOffset == partOffset + partLength || caretOffset == partOffset + partLength - 1;
    }

    private static boolean caretOffsetPartoffsetCon1(int caretOffset, int partOffset) {
        return caretOffset == partOffset || caretOffset == partOffset + 1;
    }

    private static boolean isDoubleQuotesAndCaretOffsetPartoffsetEqual(int caretOffset, CharPairsUtil fPairs,
            char prevChar, String partition1, int partOffset) {
        return !isDoubleQuotesAndCatetOffsetPartoffsetEqual(caretOffset, fPairs, prevChar, partition1, partOffset);
    }

    private static boolean isDoubleQuoteAndSingleQuote(char chQuote, final String partition) {
        return (DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(partition)
                && DSDefaultCharacterPairMatcherUtil.DOUBLE_QUOTE == chQuote)
                || (DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(partition)
                        && DSDefaultCharacterPairMatcherUtil.SINGLE_QUOTE == chQuote);
    }

    private static boolean isDoubleQuotesAndNotSingleQuote(char chQuote, final String partition) {
        return (DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(partition)
                && DSDefaultCharacterPairMatcherUtil.DOUBLE_QUOTE != chQuote)
                || (DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(partition)
                        && DSDefaultCharacterPairMatcherUtil.SINGLE_QUOTE != chQuote);
    }

    private static char getChQuote2(char prevCharQuote, char currCharQuote) {
        return currCharQuote == DSDefaultCharacterPairMatcherUtil.SINGLE_QUOTE ? currCharQuote : prevCharQuote;
    }

    private static char getChQuote1(char prevCharQuote, char currCharQuote) {
        return currCharQuote == DSDefaultCharacterPairMatcherUtil.DOUBLE_QUOTE ? currCharQuote : prevCharQuote;
    }

    private static boolean isSQLDoubleQuotesOrSingleQuote(char chQuote, final ITypedRegion region) {
        return (DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(region.getType())
                && DSDefaultCharacterPairMatcherUtil.DOUBLE_QUOTE == chQuote)
                || (DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(region.getType())
                        && DSDefaultCharacterPairMatcherUtil.SINGLE_QUOTE == chQuote);
    }

    private static boolean isNotForwardAndQuote(boolean isForward, char chQuote, char endChar) {
        return !isForward && isQuote(chQuote) && !isQuote(endChar);
    }

    private static boolean isForwardAndQuote(boolean isForward, char chQuote, char endChar) {
        return isForward && isQuote(chQuote) && !isQuote(endChar);
    }

    private static int adjustedEndOffset2(boolean isForward, int partOffset, int partLength) {
        return isForward ? partOffset + partLength : partOffset;
    }

    private static int adjustedEndOffset1(boolean isForward, int endOffset) {
        return isForward ? endOffset + 1 : endOffset;
    }

    private static int getAdjustedOffset(int caretOffset, boolean isForward, boolean fCaretEitherSideOfBracket) {
        return isForward ? caretOffset - 1 : adjustedEndOffset1(fCaretEitherSideOfBracket, caretOffset);
    }

    private static boolean isDoubleQuotesAndEndPunctuation(CharPairsUtil fPairs, char ch, String partition1) {
        return !fPairs.contains(ch) || (isDoubleQuotes(partition1) && !fPairs.isEndPunctuation(ch));
    }

    private static ITypedRegion getPartitionForRegion(IDocument doc, int caretOffset, boolean isForward,
            String fPartitioning, boolean fCaretEitherSideOfBracket) throws BadLocationException {
        return TextUtilities.getPartition(doc, fPartitioning,
                (!isForward && fCaretEitherSideOfBracket) ? caretOffset : Math.max(caretOffset - 1, 0), false);
    }

    private static boolean isNotContentAssistAndQuoteFlag(CharPairsUtil fPairs, boolean isContentAssist, char prevChar,
            boolean quotesFlag) {
        return !isContentAssist && !fPairs.contains(prevChar) && !quotesFlag;
    }

    private static boolean isNotCaretEitherSideOfBracketAndQuotesFlag(boolean fCaretEitherSideOfBracket,
            boolean quotesFlag) {
        return !fCaretEitherSideOfBracket && !quotesFlag;
    }

    private static boolean isNotForwardBackward(boolean isForward, char prevChar, char currChar, boolean isBackward) {
        return !isForward && !isBackward && !DSDefaultCharacterPairMatcherUtil.isQuote(prevChar)
                && !DSDefaultCharacterPairMatcherUtil.isQuote(currChar);
    }

    private static boolean getBackward(CharPairsUtil fPairs, char currChar) {
        return fPairs.contains(currChar) && !fPairs.isStartPunctuation(currChar);
    }

    private static boolean getForward(CharPairsUtil fPairs, char prevChar) {
        return fPairs.contains(prevChar) && fPairs.isStartPunctuation(prevChar);
    }

    private static boolean fpairContainsPreOrNext(CharPairsUtil fPairs, char prevChar, char currChar) {
        return !fPairs.contains(prevChar) && !fPairs.contains(currChar);
    }

    private static boolean isStartPunctuation(CharPairsUtil fPairs, char prevChar, char currChar) {
        return fPairs.isStartPunctuation(currChar) && !fPairs.contains(prevChar);
    }

    private static boolean isEndPunctuation(CharPairsUtil fPairs, char prevChar, char currChar) {
        return fPairs.isEndPunctuation(prevChar) && !fPairs.isEndPunctuation(currChar);
    }

    private static char getCurrChar(IDocument doc, int caretOffset) throws BadLocationException {
        return (caretOffset != doc.getLength()) ? doc.getChar(caretOffset) : Character.MIN_VALUE;
    }

    private static boolean isCaretEitherSideOfBracketAndNotQuotes(boolean fCaretEitherSideOfBracket,
            boolean quotesFlag) {
        return fCaretEitherSideOfBracket && !quotesFlag;
    }

    private static boolean isQuotesFlag(int caretOffset, int partOffset1, int partLength1) {
        return partOffset1 + 1 == caretOffset || partOffset1 + partLength1 - 1 == caretOffset;
    }

    private static boolean isOffsetFlag(int caretOffset, int partOffset1, int partLength1) {
        return caretOffset > partOffset1 + 1 && caretOffset < partOffset1 + partLength1 - 1;
    }

    private static boolean isDoubleQuotes(String partition1) {
        return DSDefaultCharacterPairMatcherUtil.SQL_DOUBLE_QUOTES_IDENTIFIER.equals(partition1)
                || DSDefaultCharacterPairMatcherUtil.SQL_STRING.equals(partition1);
    }

    private static ITypedRegion getPartition(IDocument doc, int caretOffset, String fPartitioning)
            throws BadLocationException {
        return TextUtilities.getPartition(doc, fPartitioning,
                caretOffset >= doc.getLength() ? Math.max(caretOffset - 1, 0) : caretOffset, false);
    }

    private static char getPreChar(IDocument doc, int caretOffset) throws BadLocationException {
        return (caretOffset - 1 >= 0) ? doc.getChar(caretOffset - 1) : Character.MIN_VALUE;
    }

    /**
     * Gets the user defined region.
     *
     * @param isForward the is forward
     * @param region the region
     * @param adjustedOffset the adjusted offset
     * @param adjustedEndOffset the adjusted end offset
     * @return the user defined region
     */
    private static IRegion getUserDefinedRegion(boolean isForward, final ITypedRegion region, final int adjustedOffset,
            final int adjustedEndOffset) {
        DSRegion dsRegion = new DSRegion(Math.min(adjustedOffset, adjustedEndOffset),
                Math.abs(adjustedEndOffset - adjustedOffset));
        dsRegion.setForward(isForward);
        dsRegion.setPartitionType(region.getType());
        return dsRegion;
    }

    /**
     * Gets the char offset.
     *
     * @param doc the doc
     * @param caretOffsetParam the caret offset
     * @param fPairs the f pairs
     * @param fPartitioning the f partitioning
     * @return the char offset
     * @throws BadLocationException the bad location exception
     */
    private static int getCharOffset(IDocument doc, int caretOffsetParam, CharPairsUtil fPairs, String fPartitioning)
            throws BadLocationException {
        int caretOffset = caretOffsetParam;
        final char caretChar = caretOffset < doc.getLength() ? doc.getChar(caretOffset) : Character.MIN_VALUE;
        final ITypedRegion region = TextUtilities.getPartition(doc, fPartitioning, caretOffset - 1, false);
        if (null != region) {
            String part = region.getType();
            int pOffset = region.getOffset();
            int pLength = region.getLength();
            if (isDoubleQuotes(part) && caretOffset == pOffset + pLength && !fPairs.isEndPunctuation(caretChar)) {
                caretOffset--;
            }
        }
        return caretOffset;
    }

    /**
     * Find matching peer.
     *
     * @param doc the doc
     * @param start the start
     * @param end the end
     * @param searchForward the search forward
     * @param boundary the boundary
     * @param startPosition the start position
     * @return the int
     * @throws BadLocationException the bad location exception
     */
    private static int findMatchingPeer(DocumentPartitionAccessor doc, char start, char end, boolean searchForward,
            int boundary, int startPosition) throws BadLocationException {
        int position = startPosition;
        int nestingLevel = 0;
        while (position != boundary) {
            final char ch = doc.getChar(position);

            if (ch == end && doc.inPartition(position)) {
                if (nestingLevel == 0) {
                    return position;
                }
                nestingLevel--;
            } else if (ch == start && doc.inPartition(position)) {
                nestingLevel++;
            }
            position = doc.getNextPosition(position, searchForward);
        }
        return -1;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DocumentPartitionAccessor.
     */
    private static class DocumentPartitionAccessor {

        private final IDocument fDoc;
        private final String fPartitioning;
        private final String fPart;
        private ITypedRegion fCachedPart;
        private int fLen;

        /**
         * Creates new partitioned document for the specified document.
         *
         * @param doc the document to wrap
         * @param partitioning the partitioning used
         * @param part the partition managed by this document
         */
        public DocumentPartitionAccessor(IDocument doc, String partitioning, String partition) {
            fDoc = doc;
            fPartitioning = partitioning;
            fPart = partition;
            fLen = doc.getLength();
        }

        /**
         * Returns character at specified position in this document.
         *
         * @param pos an offset within the document
         * @return character at the offset
         * @throws BadLocationException if the offset is invalid in this
         * document
         */
        public char getChar(int pos) throws BadLocationException {
            return fDoc.getChar(pos);
        }

        /**
         * Returns true if the specified offset is within the partition managed
         * by this document.
         *
         * @param pos an offset within this document
         * @return true if the offset is within this document's partition
         */
        public boolean inPartition(int pos) {
            final ITypedRegion partition = getPartition(pos);
            return partition != null && partition.getType().equals(fPart);
        }

        /**
         * Returns next position to query in the search. The position is not
         * guaranteed to be in this document's partition.
         *
         * @param pos an offset within the document
         * @param searchForward the direction of the search
         * @return the next position to query
         */
        public int getNextPosition(int pos, boolean searchForward) {
            final ITypedRegion partition = getPartition(pos);
            if (partition == null || fPart.equals(partition.getType())) {
                return simpleIncrement(pos, searchForward);
            }
            if (searchForward) {
                int end = partition.getOffset() + partition.getLength();
                if (pos < end) {
                    return end;
                }
            } else {
                int offset = partition.getOffset();
                if (pos > offset) {
                    return offset - 1;
                }
            }
            return simpleIncrement(pos, searchForward);
        }

        /**
         * Simple increment.
         *
         * @param pos the pos
         * @param searchForward the search forward
         * @return the int
         */
        private int simpleIncrement(int pos, boolean searchForward) {
            return pos + (searchForward ? 1 : -1);
        }

        /**
         * Returns partition information about the region containing the
         * specified position.
         *
         * @param pos a position within this document.
         * @return positioning information about the region containing the
         * position
         */
        private ITypedRegion getPartition(int pos) {
            if (fCachedPart == null || !contains(fCachedPart, pos)) {
                if (pos >= 0 && pos <= fLen) {
                    try {
                        fCachedPart = TextUtilities.getPartition(fDoc, fPartitioning, pos, false);
                    } catch (BadLocationException e) {
                        fCachedPart = null;
                    }
                } else {
                    fCachedPart = null;
                }
            }
            return fCachedPart;
        }

        /**
         * Contains.
         *
         * @param region the region
         * @param pos the pos
         * @return true, if successful
         */
        private boolean contains(IRegion region, int pos) {
            int offset = region.getOffset();
            return offset <= pos && pos < offset + region.getLength();
        }

    }

    /**
     * Gets the match punctuations.
     *
     * @return the match punctuations
     */
    public static char[] getMatchPunctuations() {
        return Arrays.copyOf(MATCH_PUNCTUATIONS, MATCH_PUNCTUATIONS.length);
    }

    /**
     * Checks if is quote.
     *
     * @param ch the ch
     * @return true, if is quote
     */
    public static boolean isQuote(char ch) {
        char[] pairs = MATCH_PUNCTUATIONS_QUOTES;
        for (int i = 0, n = pairs.length; i < n; i++) {
            if (ch == pairs[i]) {
                return true;
            }
        }
        return false;
    }

}
