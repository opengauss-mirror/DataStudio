/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.SQLKeywords;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class PLSQLFormatter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PLSQLFormatter {
    private int beginCounter = 0;

    private static final int IGNORE_FULL_LINE = 1;
    private static final int VALID_LINE = 2;
    private static final int COMMENT_CLOSE_PENDING = 4;

    private static List<String> indentSQLWords = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private static List<String> newSQLWords = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    private static List<String> keywords = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    static {
        keywords.addAll(Arrays.asList(SQLKeywords.getRESERVEDWORDS()));
        keywords.add("INSERT");
        keywords.add("DELETE");
        keywords.add("UPDATE");

        indentSQLWords.add("begin");

        newSQLWords.add("select");
        newSQLWords.add("insert");
        newSQLWords.add("update");
        newSQLWords.add("delete");
        newSQLWords.add("return");
    }

    /**
     * Checks if is key word.
     *
     * @param currentKeyWord the current key word
     * @return true, if is key word
     */
    private boolean isKeyWord(String currentKeyWord) {
        if (keywords.contains(currentKeyWord.toUpperCase(Locale.ENGLISH))) {
            if (indentSQLWords.contains(currentKeyWord.toLowerCase(Locale.ENGLISH))
                    || currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end\\s*;*\\$$*")
                    || currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end\\s*;*\\$function\\$*")) {
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * Format.
     *
     * @param input the input
     * @return the string
     */
    public String format(String input) {

        StringBuilder formattedQuery = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        int cnt = 0;
        int start = 0;

        int indention = 0;
        String currentKeyword = null;

        int lastKeyLength = -1;
        int minKeyStart = 0;

        String[] everyLine = input.replaceAll("\r\n", "\n").split("\n");
        int[] startln = new int[everyLine.length];
        int rule = 0;
        boolean commentClosePending = false;

        int numSqlline = everyLine.length;
        while (cnt < numSqlline) {

            currentKeyword = everyLine[cnt];
            everyLine[cnt] = currentKeyword = currentKeyword.replaceAll("^\\s+", "");

            rule = identifyCommentingRule(currentKeyword, commentClosePending);
            commentClosePending = isCommentPending(rule);
            if (isIgnoreFullLine(rule)) {
                start = getStartLength(cnt, start, indention, startln);
                cnt++;
                continue;
            }
            currentKeyword = findEndOfKeyWord(currentKeyword);

            if (indention != 0) {
                startln[cnt] = start = indention;
                lastKeyLength = getLastKeyLengthForKeyword(currentKeyword, lastKeyLength);

            } else if (isKeyWordIndentEnd(currentKeyword)) {
                startln[cnt] = start = beginCounter * 4;
            } else if (isNewSqlOrKeywordIndent(currentKeyword)) {
                startln[cnt] = start = beginCounter * 4;
                lastKeyLength = currentKeyword.length();
            } else if (isKeyWord(currentKeyword)) {
                if (lastKeyLength == -1) {
                    minKeyStart = startln[cnt] = start;
                } else {
                    startln[cnt] = start = start + (lastKeyLength - currentKeyword.length());
                    minKeyStart = getMinKeyStart(start, minKeyStart);
                }
                lastKeyLength = currentKeyword.length();
            } else {
                startln[cnt] = start;
            }

            indention = getIndent(currentKeyword, indention);

            cnt++;
        }

        getStartIn(formattedQuery, numSqlline, minKeyStart, everyLine, startln);

        return formattedQuery.toString();
    }

    private int getStartLength(int cnt, int start, int indention, int[] startln) {
        if (indention != 0) {
            startln[cnt] = start = indention;
        } else {
            startln[cnt] = start;
        }
        return start;
    }

    private void getStartIn(StringBuilder formattedQuery, int numSqlline, int minKeyStart, String[] everyLine,
            int[] startln) {
        for (int index = 0; index < numSqlline; index++) {
            startln[index] -= minKeyStart;
        }

        concateSpaces(formattedQuery, numSqlline, everyLine, startln);
    }

    private int getMinKeyStart(int start, int minKeyStartParam) {
        int minKeyStart = minKeyStartParam;
        if (start < minKeyStart) {
            minKeyStart = start;
        }
        return minKeyStart;
    }

    private int getLastKeyLengthForKeyword(String currentKeyword, int lastKeyLengthParam) {
        int lastKeyLength = lastKeyLengthParam;
        if (isKeyWord(currentKeyword)) {
            lastKeyLength = currentKeyword.length();
        }
        return lastKeyLength;
    }

    /**
     * Concate spaces.
     *
     * @param formattedQuery the formatted query
     * @param numSqlline the num sqlline
     * @param everyLine the every line
     * @param startln the startln
     * @return the int
     */
    private int concateSpaces(StringBuilder formattedQuery, int numSqlline, String[] everyLine, int[] startln) {
        int index;
        String leadingSpaces = "";

        for (index = 0; index < numSqlline; index++) {
            leadingSpaces = "";
            for (int s = 0; s < startln[index]; s++) {
                leadingSpaces = leadingSpaces.concat(" ");
            }

            everyLine[index] = leadingSpaces.concat(everyLine[index]);
            formattedQuery.append(everyLine[index]);
            appendLineSeperator(formattedQuery, index, numSqlline);
        }
        return index;
    }

    /**
     * Checks if is ignore full line.
     *
     * @param rule the rule
     * @return true, if is ignore full line
     */
    private boolean isIgnoreFullLine(int rule) {
        return IGNORE_FULL_LINE == (rule & IGNORE_FULL_LINE);
    }

    /**
     * Append line seperator.
     *
     * @param formattedQuery the formatted query
     * @param index the i
     * @param numSqlline the num sqlline
     */
    private void appendLineSeperator(StringBuilder formattedQuery, int index, int numSqlline) {
        if (index < numSqlline - 1) {
            formattedQuery.append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
    }

    /**
     * Checks if is new sql or keyword indent.
     *
     * @param currentKeyword the current keyword
     * @return true, if is new sql or keyword indent
     */
    private boolean isNewSqlOrKeywordIndent(String currentKeyword) {
        return isNewSql(currentKeyword) || isKeyWordIndent(currentKeyword);
    }

    /**
     * Checks if is comment pending.
     *
     * @param rule the rule
     * @return true, if is comment pending
     */
    private boolean isCommentPending(int rule) {
        boolean commentClosePending = false;
        if (COMMENT_CLOSE_PENDING == (rule & COMMENT_CLOSE_PENDING)) {
            commentClosePending = true;
        } else {
            commentClosePending = false;
        }
        return commentClosePending;
    }

    /**
     * Find end of key word.
     *
     * @param currentKeyWordParam the current key word param
     * @return the string
     */
    private String findEndOfKeyWord(String currentKeyWordParam) {
        String currentKeyWord = currentKeyWordParam;
        int index = currentKeyWord.indexOf(' ');
        if (currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end loop[\\s*;*]")) {
            MPPDBIDELoggerUtility.none("End loop encountered");
        } else if (currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end if[\\s*;*]")) {
            MPPDBIDELoggerUtility.none("End if encountered");
        } else if (-1 < index) {
            currentKeyWord = currentKeyWord.substring(0, index);
        }
        return currentKeyWord;
    }

    /**
     * Identify commenting rule.
     *
     * @param currentKeyWord the current key word
     * @param commentClosePending the comment close pending
     * @return the int
     */
    private int identifyCommentingRule(String currentKeyWord, boolean commentClosePending) {
        int lastCommentStartPos = 0;
        int lastCommentClosePos = 0;
        int rule = 0;

        if (currentKeyWord.length() < 2) {
            if (commentClosePending) {
                return IGNORE_FULL_LINE | COMMENT_CLOSE_PENDING;
            }

            return IGNORE_FULL_LINE;
        }

        if (currentKeyWord.substring(0, 2).matches("--") || currentKeyWord.substring(0, 2).matches("//")) {
            // because the line starts with single-line comment start characters
            if (commentClosePending) {
                return IGNORE_FULL_LINE | COMMENT_CLOSE_PENDING;
            }

            return IGNORE_FULL_LINE;
        }

        lastCommentStartPos = currentKeyWord.lastIndexOf("/*");
        if (-1 == lastCommentStartPos && !commentClosePending) {
            return VALID_LINE;
        }

        if (0 == lastCommentStartPos) {
            // because the line starts with multi-line comment start characters
            rule = IGNORE_FULL_LINE;
        } else {
            /*
             * Two possibilities are possible: 1. The line is a valid line as no
             * comment at the beginning. 2. Line part of the comment that was
             * started earlier.
             */
            if (commentClosePending) {
                rule = IGNORE_FULL_LINE;
            } else {
                rule = VALID_LINE;
            }
        }

        // we need to check if there is any end multi line comment characters.
        lastCommentClosePos = currentKeyWord.lastIndexOf("*/");

        rule = checkForMultipleCommentEnd(commentClosePending, lastCommentStartPos, lastCommentClosePos, rule);

        return rule;

    }

    private int checkForMultipleCommentEnd(boolean commentClosePending, int lastCommentStartPos,
            int lastCommentClosePos, int rule) {
        if (-1 == lastCommentClosePos) {
            /*
             * We have no end comment characters.
             */
            if (-1 == lastCommentStartPos) {
                // we could not find both the start and end multi line comment
                // characters.
                if (commentClosePending) {
                    // we continue to say that end multi line is expected.
                    rule |= COMMENT_CLOSE_PENDING;
                }
            } else {
                // we found start multi line comment, but no end multi line
                // comment characters.
                // we continue to say that end multi line is expected.
                rule |= COMMENT_CLOSE_PENDING;
            }
        } else {
            if (-1 == lastCommentStartPos) {
                // may be some old comment is getting closed.
                rule = IGNORE_FULL_LINE;
            } else if (lastCommentClosePos < lastCommentStartPos) {
                // cases like /* xxxxx */ abc /*
                rule |= COMMENT_CLOSE_PENDING;
            }
        }
        return rule;
    }

    /**
     * Checks if is key word indent.
     *
     * @param currentKeyWord the current key word
     * @return true, if is key word indent
     */
    private boolean isKeyWordIndent(String currentKeyWord) {
        return indentSQLWords.contains(currentKeyWord.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Gets the indent.
     *
     * @param currentKeyWord the current key word
     * @param currentIndentation the current indentation
     * @return the indent
     */
    private int getIndent(String currentKeyWord, int currentIndentation) {
        if (indentSQLWords.contains(currentKeyWord.toLowerCase(Locale.ENGLISH))) {
            beginCounter++;
            return 4 * beginCounter;
        }
        return 0;
    }

    /**
     * Checks if is key word indent end.
     *
     * @param currentKeyWord the current key word
     * @return true, if is key word indent end
     */
    private boolean isKeyWordIndentEnd(String currentKeyWord) {
        if (currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end\\s*;*\\$*")
                || currentKeyWord.toLowerCase(Locale.ENGLISH).matches("end\\s*;*\\$function\\$*")) {
            if (!"end if;".equalsIgnoreCase(currentKeyWord) && !"end loop;".equalsIgnoreCase(currentKeyWord)) {
                beginCounter--;
                return true;
            }

        }

        return false;
    }

    /**
     * Checks if is new sql.
     *
     * @param currentKeyWord the current key word
     * @return true, if is new sql
     */
    private boolean isNewSql(String currentKeyWord) {
        if (newSQLWords.contains(currentKeyWord.toLowerCase(Locale.ENGLISH))) {
            return true;
        }

        return false;
    }

}
