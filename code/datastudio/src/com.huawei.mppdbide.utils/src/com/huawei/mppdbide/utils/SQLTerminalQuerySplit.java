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

package com.huawei.mppdbide.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class SQLTerminalQuerySplit.
 *
 * @since 3.0.0
 */
public class SQLTerminalQuerySplit implements IQuerrySplitter {

    private static Map<String, String> allMatches = new HashMap<String, String>(1);

    @Override
    public void splitQuerries(ArrayList<String> queryArray, String query, boolean isOLAP)
            throws DatabaseOperationException {
        createSplitQueries(queryArray, query, isOLAP);

    }

    /**
     * Gets the replace tag.
     *
     * @param allMatchMap the all match map
     * @param substring the substring
     * @return the replace tag
     */
    private static String getReplaceTag(Map<String, String> allMatchMap, String substring) {
        String tag = "#S#" + (allMatchMap.size() + 1) + "#E#";
        allMatchMap.put(tag, substring);
        return tag;
    }

    private static String getInQuoteBack(String qryParam) {
        String qry = qryParam;
        String grp = null;
        Matcher tagMatcher = Pattern.compile("(#Q#\\d+#Q#)").matcher(qryParam);

        while (tagMatcher.find()) {
            grp = tagMatcher.group(0);
            qry = qry.replace(grp, allMatches.get(grp));
        }

        return qry;
    }

    /**
     * Gets the comments and quotes back.
     *
     * @param qryParam the qry param
     * @return the comments and quotes back
     */
    private static String getCommentsAndQuotesBack(String qryParam) {
        String qry = qryParam;
        Pattern commantTagPattern = null;
        Matcher tagMatcher = null;
        String grp = null;
        // Modified below taging that will have quotes tag also

        commantTagPattern = Pattern.compile("(#S#\\d+#E#|#Q#\\d+#Q#)");
        tagMatcher = commantTagPattern.matcher(qry);

        boolean isFound = tagMatcher.find();
        while (isFound) {
            grp = tagMatcher.group(0);
            qry = qry.replace(grp, allMatches.get(grp));
            isFound = tagMatcher.find();
        }

        // Pass 2: To handle Single-line comment within Multi-line comment
        tagMatcher = commantTagPattern.matcher(qry);
        isFound = tagMatcher.find();
        if (!isFound) {
            return qry;
        }
        // Still more items to match.
        // As per design only 2 level comments handling is possible.
        while (isFound) {
            grp = tagMatcher.group(0);
            qry = qry.replace(grp, allMatches.get(grp));
            isFound = tagMatcher.find();
        }
        // Pass 3: to hand nested quotes
        tagMatcher = commantTagPattern.matcher(qry);
        isFound = tagMatcher.find();
        // Still more item to match
        // As per design only 3 level quotes handling is possible.
        while (isFound) {
            grp = tagMatcher.group(0);
            qry = qry.replace(grp, allMatches.get(grp));
            isFound = tagMatcher.find();
        }

        return qry;
    }

    /**
     * Adds the token to qry arr.
     *
     * @param tokenStr the token str
     * @param queryArray the query array
     */
    private static void addTokenToQryArr(String tokenStr, ArrayList<String> queryArray) {
        if (!(MPPDBIDEConstants.LINE_SEPARATOR.equals(tokenStr))) {
            if (!("".equals(tokenStr))) {
                if (!(" ".equals(tokenStr))) {
                    queryArray.add(getCommentsAndQuotesBack(tokenStr));
                }
            }
        }
    }

    /**
     * Seperate SQL and PLSQL.
     *
     * @param queryArray the query array
     * @param splitArray the split array
     */
    private static void seperateSQLAndPLSQL(ArrayList<String> queryArray, String[] splitArray) {
        Pattern pattern1 = Pattern
                .compile("(?i)CREATE\\s+FUNCTION|CREATE\\s+OR\\s+REPLACE\\s+FUNCTION|CREATE\\s+PROCEDURE|"
                        + "CREATE\\s+PACKAGE|CREATE\\s+OR\\s+REPLACE\\s+PACKAGE|"
                        + "CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE|CREATE\\s+TRIGGER|"
                        + "CREATE\\s+OR\\s+REPLACE\\s+TRIGGER|(?<!\\w)DECLARE(?!\\w)|(?<!\\w)BEGIN(?!\\w)");
        Pattern validBeingPattern = Pattern
                .compile("(?i)VALID ([ ]*[" + MPPDBIDEConstants.LINE_SEPARATOR + "]*\t*)*BEGIN");
        String tokenStr = null;
        StringBuffer strBuffer = null;
        boolean matcher1 = false;
        boolean validBeingMatcher = false;
        boolean beginIncludeValidBeginMatcher = false;
        Pattern beginIncludeValidBeginPattern = Pattern
                .compile("(?i)BEGIN[\\s\\S]+VALID ([ ]*[" + MPPDBIDEConstants.LINE_SEPARATOR + "]*\t*)*BEGIN");
        for (int cnt = 0; cnt < splitArray.length; cnt++) {
            tokenStr = splitArray[cnt];
            // write method here if all the lines here contains ; and commented
            // then excluse it
            strBuffer = new StringBuffer();
            matcher1 = pattern1.matcher(tokenStr).find();
            validBeingMatcher = validBeingPattern.matcher(tokenStr).find();
            beginIncludeValidBeginMatcher = beginIncludeValidBeginPattern.matcher(tokenStr).find();
            boolean grantMatcher = tokenStr.trim().toLowerCase(Locale.ENGLISH).startsWith("grant");
            // consider ALTER ROLE VALID BEING case
            if (((matcher1 && !validBeingMatcher) || (matcher1 && validBeingMatcher && beginIncludeValidBeginMatcher))
                    && !grantMatcher) {
                strBuffer.append(tokenStr).append(";");
                tokenStr = "";
                cnt = handleQueryArray(queryArray, splitArray, strBuffer, cnt);
            } else {
                // write method here if all the lines here contains ; and
                // commented
                // then exclude it
                if (isAllTextCommented(tokenStr)) {
                    continue;
                }
            }
            addTokenToQryArr(tokenStr, queryArray);
        }
    }

    private static int handleQueryArray(ArrayList<String> queryArray, String[] splitArray, StringBuffer strBuffer,
            int cnt) {
        int count = cnt;
        int index = count;
        if (splitArray.length > 1) {
            do {
                count++;
                if (count != splitArray.length - 1 || !splitArray[splitArray.length - 1].trim().isEmpty()) {
                    strBuffer.append(splitArray[count]).append(";");
                }
                index++;

            } while (index < splitArray.length - 1);
        }
        if (strBuffer.toString().trim().endsWith(';' + MPPDBIDEConstants.LINE_SEPARATOR + ';')) {
            strBuffer.deleteCharAt(strBuffer.length() - 1);
        }
        queryArray.add(getCommentsAndQuotesBack(strBuffer.toString()));
        return count;
    }

    private static boolean isAllTextCommented(String tokenStr) {
        String[] eachLineArr = tokenStr.split(EnvirnmentVariableValidator.validateAndGetLineSeperator());

        boolean isAllCommented = true;

        String eachLineTrim = null;

        for (String eachLine : eachLineArr) {

            eachLineTrim = StringUtils.trimToEmpty(eachLine);

            if (!StringUtils.isBlank(eachLineTrim) && !eachLineTrim.startsWith("--")) {
                isAllCommented = false;
                break;
            }
        }
        return isAllCommented;
    }

    /**
     * Creates the split queries.
     *
     * @param queryArray the query array
     * @param query the query
     * @throws DatabaseOperationException the database operation exception
     */
    private static void createSplitQueries(ArrayList<String> queryArray, String query, boolean isOLAP)
            throws DatabaseOperationException {
        if (query.length() > MPPDBIDEConstants.SQL_TERMINAL_LOAD_MAXIMUM) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_LENGTH_TOO_BIG));
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_QUERY_LENGTH_TOO_BIG));
        }
        allMatches.clear();
        String[] splitBySlash = query.split("\\s*\\t*" + MPPDBIDEConstants.LINE_SEPARATOR + "\\/\\s*\\t*"
                + MPPDBIDEConstants.LINE_SEPARATOR + "(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        int length = splitBySlash.length;
        String replacedString = null;
        for (int jindex = 0; jindex < length; jindex++) {
            String[] splitArray = null;
            // Patter to check whether line starts from create function or
            // procedure
            Pattern pattern1 = Pattern
                    .compile("(?i)CREATE\\s+FUNCTION|CREATE\\s+OR\\s+REPLACE\\s+FUNCTION|CREATE\\s+PROCEDURE|"
                            + "CREATE\\s+PACKAGE|CREATE\\s+OR\\s+REPLACE\\s+PACKAGE|"
                            + "CREATE\\s+OR\\s+REPLACE\\s+PROCEDURE|CREATE\\s+TRIGGER|"
                            + "CREATE\\s+OR\\s+REPLACE\\s+TRIGGER|DECLARE|BEGIN");

            splitQueryForCommentsOLAP(splitBySlash, jindex);

            StringBuffer quotesBuffer = splitQueryForquotes(splitBySlash[jindex]);
            splitBySlash[jindex] = quotesBuffer.toString();

            // modified below code for quotes
            StringBuffer nestedBuffer = splitQueryForMultipleComments(splitBySlash, jindex);
            splitBySlash[jindex] = nestedBuffer.toString();
            replacedString = splitBySlash[jindex];
            if (replacedString.startsWith(pattern1.pattern())) {
                queryArray.add(getCommentsAndQuotesBack(splitBySlash[jindex]));
            } else {
                splitArray = replacedString.split(";");
                seperateSQLAndPLSQL(queryArray, splitArray);
            }
        }
    }

    private static StringBuffer splitQueryForComments(StringBuffer commentInQuoteBuffer) {
        /*
         * Modified to address below issue for comment regular expression -- --
         * Name: test123 Type: TABLE Schema: public Owner: - -- *
         */
        Matcher matcher = Pattern.compile("(?m)(--.*?(" + MPPDBIDEConstants.LINE_SEPARATOR + "|$)|\\/\\*.*?\\*\\/)")
                .matcher(commentInQuoteBuffer);
        StringBuffer commentBuffer = new StringBuffer(commentInQuoteBuffer.toString());
        int commentOffset = 0;
        while (matcher.find()) {
            String originalStr = commentInQuoteBuffer.substring(matcher.start(), matcher.end());
            String replaceTag = getReplaceTag(allMatches, originalStr);
            commentBuffer.replace(matcher.start() + commentOffset, matcher.end() + commentOffset, replaceTag);
            commentOffset = commentOffset + (replaceTag.length() - originalStr.length());
        }
        return commentBuffer;
    }

    private static void splitQueryForCommentsOLAP(String[] splitBySlash, int jindex) {

        /*
         * Added to address below issue for combating SQL injection through
         * column level comments COMMENT ON COLUMN emp0066.mgrno IS 'This column
         * ' '; drop table dsuser.tab4; --';
         */
        Matcher m1 = Pattern
                .compile("('(.*?)--(.*?)(?<!\')'(?:\\s|\\)|,|;|$))|(\"(.*?)--(.*?)(?<!\")\"(?:\\s|\\)|\\.|,|;|$))")
                .matcher(splitBySlash[jindex]);
        StringBuffer commentInQuoteBuffer = new StringBuffer(splitBySlash[jindex]);

        int quotesOffset = 0;

        while (m1.find()) {

            String originalStr = splitBySlash[jindex].substring(m1.start(), m1.end());
            String replaceTag = getReplaceQuotesTag(allMatches, originalStr);
            commentInQuoteBuffer.replace(m1.start() + quotesOffset, m1.end() + quotesOffset, replaceTag);
            quotesOffset = quotesOffset + (replaceTag.length() - originalStr.length());

        }
        commentInQuoteBuffer = splitQueryForComments(commentInQuoteBuffer);
        splitBySlash[jindex] = getInQuoteBack(commentInQuoteBuffer.toString());
    }

    /**
     * the splitQueryForquotes
     * 
     * @param splitBySlash the splitBySlash
     * @return string after replacing with Quotes
     */
    public static StringBuffer splitQueryForquotes(String splitBySlash) {
        Pattern patternQuote = Pattern.compile("(?ms)(?<!\\\\)'.*?'|(?<!\\\\)\".*?\"");
        Matcher matcherQuote = patternQuote.matcher(splitBySlash);
        StringBuffer quotesBuffer = new StringBuffer(splitBySlash);

        int quotesOffset = 0;

        while (matcherQuote.find()) {

            String originalStr = splitBySlash.substring(matcherQuote.start(), matcherQuote.end());
            String replaceTag = getReplaceQuotesTag(allMatches, originalStr);
            quotesBuffer.replace(matcherQuote.start() + quotesOffset, matcherQuote.end() + quotesOffset, replaceTag);
            quotesOffset = quotesOffset + (replaceTag.length() - originalStr.length());

        }
        return quotesBuffer;
    }

    private static StringBuffer splitQueryForMultipleComments(String[] splitBySlash, int jidx) {
        MultiLineComment commentParser = new MultiLineComment(splitBySlash[jidx]);
        StringBuffer nestedBuffer = new StringBuffer(splitBySlash[jidx]);
        int nestedOffset = 0;
        while (commentParser.find()) {
            String originalStr = splitBySlash[jidx].substring(commentParser.start(), commentParser.end());
            String replaceTag = getReplaceTag(allMatches, originalStr);
            nestedBuffer.replace(commentParser.start() + nestedOffset, commentParser.end() + nestedOffset, replaceTag);
            nestedOffset = nestedOffset + (replaceTag.length() - originalStr.length());
        }
        return nestedBuffer;
    }

    /**
     * Gets the replace quotes tag.
     *
     * @param allMatchMap the all match map
     * @param substring the substring
     * @return the replace quotes tag
     */
    private static String getReplaceQuotesTag(Map<String, String> allMatchMap, String substring) {
        String tag = "#Q#" + (allMatchMap.size() + 1) + "#Q#";
        allMatchMap.put(tag, substring);
        return tag;
    }
}
