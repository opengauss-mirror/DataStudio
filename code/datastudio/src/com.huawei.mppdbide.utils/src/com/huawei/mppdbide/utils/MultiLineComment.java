/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.utils;

/**
 * 
 * Title: class
 * 
 * Description: The Class MultiLineComment.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class MultiLineComment {
    private String stringToParse;

    /**
     * The start sequence.
     */
    protected char[] startSequence = "/*".toCharArray();

    /**
     * The end sequence.
     */
    protected char[] endSequence = "*/".toCharArray();

    private int from;
    private int to;

    private int startOffset;
    private int endOffset;

    /**
     * Instantiates a new multi line comment.
     *
     * @param stringToParse1 the string to parse 1
     */
    public MultiLineComment(String stringToParse1) {
        stringToParse = stringToParse1;
        from = 0;
        to = stringToParse1.length();
        startOffset = -1;
        endOffset = -1;
    }

    /**
     * Sequence detected.
     *
     * @param startIdx the start idx
     * @param sequence the sequence
     * @param eofAllowed the eof allowed
     * @return true, if successful
     */
    protected boolean sequenceDetected(int startIdx, char[] sequence, boolean eofAllowed) {
        for (int cnt = 1; cnt < sequence.length; cnt++) {
            if (startIdx + cnt < to) {
                int val = stringToParse.charAt(startIdx + cnt);
                if (val != sequence[cnt]) {
                    return false;
                }
            } else {
                return eofAllowed;
            }
        }

        return true;
    }

    /**
     * Find.
     *
     * @return true, if successful
     */
    public boolean find() {
        char character;
        int loop = from;
        int commentNestingDepth = 0;
        while (loop < to) {
            character = stringToParse.charAt(loop);

            if (character == startSequence[0]) {
                // Check if the nested start sequence has been found.
                if (sequenceDetected(loop, startSequence, false)) {
                    if (commentNestingDepth == 0) {
                        startOffset = loop;
                    }

                    commentNestingDepth++;
                }
            } else if (character == endSequence[0]) {
                // Check if the specified end sequence has been found.
                if (sequenceDetected(loop, endSequence, true)) {
                    commentNestingDepth--;

                    if (commentNestingDepth == 0) {
                        endOffset = loop + endSequence.length;
                        from = endOffset;
                        return true;
                    } else if (commentNestingDepth < 1) {
                        from = loop + endSequence.length;
                        startOffset = -1;
                        endOffset = -1;
                        return false;
                    }
                }
            }

            loop++;
        }

        from = loop;
        startOffset = -1;
        endOffset = -1;
        return false;
    }

    /**
     * Start.
     *
     * @return the int
     */
    public int start() {
        return startOffset;
    }

    /**
     * End.
     *
     * @return the int
     */
    public int end() {
        return endOffset;
    }
}
