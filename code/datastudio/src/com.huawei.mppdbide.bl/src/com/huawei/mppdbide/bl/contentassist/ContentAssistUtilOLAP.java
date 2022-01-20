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

package com.huawei.mppdbide.bl.contentassist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ContentAssistUtilOLAP.
 * 
 */

public class ContentAssistUtilOLAP extends ContentAssistUtil {

    private Database database;

    private static final char DOT = '.';

    private static final char DOUBLE_QUOTE = '\"';

    private static final char ESCAPE_CHAR = '\"';

    private static final char NEW_LINE_CHAR = MPPDBIDEConstants.LINE_SEPARATOR.length() > 1
            ? MPPDBIDEConstants.LINE_SEPARATOR.charAt(1)
            : MPPDBIDEConstants.LINE_SEPARATOR.charAt(0);

    /**
     * Instantiates a new content assist util OLAP.
     *
     * @param servObj the serv obj
     */
    public ContentAssistUtilOLAP(ServerObject servObj) {
        if (servObj instanceof Database) {
            database = (Database) servObj;
        }
    }

    /**
     * Find matching namespace.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingNamespace(String prefix) {
        SortedMap<String, ServerObject> matchedNamespaces = new TreeMap<String, ServerObject>();
        matchedNamespaces.putAll(database.getUserNamespaces().getMatching(prefix));
        matchedNamespaces.putAll(database.getSystemNamespaces().getMatching(prefix));
        return matchedNamespaces;
    }

    /**
     * Find matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingTables(String prefix) {
        return getMatching(database.getSearchPoolManager().getTableTrie(), prefix);
    }

    /**
     * Find matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingDebugObjects(String prefix) {
        return getMatching(database.getSearchPoolManager().getDebugObjectTrie(), prefix);
    }

    /**
     * Find matching views object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingViewsObject(String prefix) {
        return getMatching(database.getSearchPoolManager().getViewTrie(), prefix);
    }

    /**
     * Find matching sequence object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingSequenceObject(String prefix) {
        return getMatching(database.getSearchPoolManager().getSequenceTrie(), prefix);
    }

    /**
     * Find matching sequence object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingSynonyms(String prefix) {
        return getMatching(database.getSearchPoolManager().getSynonymTrie(), prefix);
    }

    /**
     * Find exact matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingSequences(String prefix) {
        SortedMap<String, SequenceMetadata> map = database.getSearchPoolManager().getSequenceTrie().prefixMap(prefix);
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();
        for (Entry<String, SequenceMetadata> entry : map.entrySet()) {
            SequenceMetadata object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;
    }

    /**
     * Find exact matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingDebugObjects(String prefix) {

        SortedMap<String, DebugObjects> map = database.getSearchPoolManager().getDebugObjectTrie().prefixMap(prefix);
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();
        for (Entry<String, DebugObjects> entry : map.entrySet()) {
            DebugObjects object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;
    }

    /**
     * Find exact matching views.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingViews(String prefix) {
        SortedMap<String, ViewMetaData> map = database.getSearchPoolManager().getViewTrie().prefixMap(prefix);
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();
        for (Entry<String, ViewMetaData> entry : map.entrySet()) {
            ViewMetaData object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;
    }

    /**
     * Find exact matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingTables(String prefix) {
        SortedMap<String, TableMetaData> map = database.getSearchPoolManager().getTableTrie().prefixMap(prefix);
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();

        for (Entry<String, TableMetaData> entry : map.entrySet()) {
            TableMetaData object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;
    }

    /**
     * Find exact matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingSynonyms(String prefix) {
        SortedMap<String, SynonymMetaData> map = database.getSearchPoolManager().getSynonymTrie().prefixMap(prefix);
        SortedMap<String, ServerObject> retMap = new TreeMap<String, ServerObject>();
        for (Entry<String, SynonymMetaData> entry : map.entrySet()) {
            SynonymMetaData object = entry.getValue();
            if (prefix.equals(object.getName())) {
                retMap.put(entry.getKey(), object);
            }
        }
        return retMap;
    }

    /**
     * Find exact matching namespaces.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingNamespaces(String prefix) {
        SortedMap<String, ServerObject> exactMatchedNamespaces = new TreeMap<String, ServerObject>();
        exactMatchedNamespaces.putAll(database.getUserNamespaces().getMatchingHyperLink(prefix));
        exactMatchedNamespaces.putAll(database.getSystemNamespaces().getMatchingHyperLink(prefix));
        return exactMatchedNamespaces;
    }

    /**
     * Checks if is escaped.
     *
     * @param pretext the pretext
     * @param pos the pos
     * @return true, if is escaped
     */
    private static boolean isEscaped(String pretext, int pos) {
        if (0 == pos) {
            return false;
        }
        char ch = pretext.charAt(pos - 1);
        return ESCAPE_CHAR == ch;
    }

    /**
     * Checks if is escaped literal.
     *
     * @param pretext the pretext
     * @param pos the pos
     * @return true, if is escaped literal
     */
    private boolean isEscapedLiteral(String pretext, int pos) {
        if (pretext.length() == (pos + 1)) {
            return false;
        }

        return ESCAPE_CHAR == pretext.charAt(pos) && DOUBLE_QUOTE == pretext.charAt(pos + 1);
    }

    @Override
    public int findDefaultString(String pretext, List<Character> workBreakCharList, int oldPos,
            ArrayList<String> strList, int posForFindPrefix) {
        char ch = '\0';
        boolean isLastCharDot = false;
        boolean isQuotedString = false;

        while (posForFindPrefix >= 0) {
            ch = pretext.charAt(posForFindPrefix);

            if (DOT == ch) {
                isLastCharDot = true;
            } else if (NEW_LINE_CHAR == ch) {
                break;
            } else if (isDoubleQuoteAndEscaped(pretext, posForFindPrefix, ch)) {
                --posForFindPrefix;
            } else if (isLastCharDotOrNotQuotedString(isLastCharDot, isQuotedString) && DOUBLE_QUOTE == ch
                    && !isEscaped(pretext, posForFindPrefix)) {
                isQuotedString = true;
                isLastCharDot = false;
            } else if (isQuotedString) {
                isLastCharDot = false;
                if (DOUBLE_QUOTE == ch && !isEscaped(pretext, posForFindPrefix)) {
                    isQuotedString = false;
                }
            } else if (isWhitespaceOrContainsWordBreakChar(workBreakCharList, ch)) {
                break;
            } else {
                isLastCharDot = false;
            }

            --posForFindPrefix;
        }

        String retStr = pretext.substring(posForFindPrefix + 1, oldPos + 1);
        retStr = retStr.trim();
        strList.add(retStr);
        return posForFindPrefix;
    }

    /**
     * Checks if is whitespace or contains word break char.
     *
     * @param workBreakCharList the work break char list
     * @param ch the ch
     * @return true, if is whitespace or contains word break char
     */
    private boolean isWhitespaceOrContainsWordBreakChar(List<Character> workBreakCharList, char ch) {
        return Character.isWhitespace(ch) || (!workBreakCharList.isEmpty() && workBreakCharList.contains(ch));
    }

    /**
     * Checks if is double quote and escaped.
     *
     * @param pretext the pretext
     * @param pos the pos
     * @param ch the ch
     * @return true, if is double quote and escaped
     */
    private boolean isDoubleQuoteAndEscaped(String pretext, int pos, char ch) {
        return DOUBLE_QUOTE == ch && isEscaped(pretext, pos);
    }

    /**
     * Checks if is last char dot or not quoted string.
     *
     * @param isLastCharDot the is last char dot
     * @param isQuotedString the is quoted string
     * @return true, if is last char dot or not quoted string
     */
    private boolean isLastCharDotOrNotQuotedString(boolean isLastCharDot, boolean isQuotedString) {
        return isLastCharDot || !isQuotedString;
    }

    /**
     * Remove literal escapes and quotes.
     *
     * @param str the str
     * @return the string
     */
    @Override
    public String removeLiteralEscapesAndQuotes(String str) {
        int strLen = str.length();
        StringBuilder blr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        char ch = '\0';
        int pos = 0;
        boolean isLower = false;
        boolean isUpper = false;

        if (strLen > 1 && str.charAt(pos) == DOUBLE_QUOTE && str.charAt(strLen - 1) == DOUBLE_QUOTE) {
            // Skip the first and last quotes (for quoted strings only)
            pos++;
            strLen--;
        } else {
            // Non Quoted strings will be handled as lower case by Gauss, then
            // lets convert to lower
            isLower = database.isLowerCase();
            isUpper = database.isUpperCase();

        }

        for (; pos < strLen; pos++) {
            ch = str.charAt(pos);
            if (ESCAPE_CHAR == ch && isEscapedLiteral(str, pos)) {
                // Remove the escape char
                pos++;
                ch = str.charAt(pos);
            }
            blr.append(isLower ? Character.toLowerCase(ch) : isUpper ? Character.toUpperCase(ch) : ch);
        }

        return blr.toString();
    }

    /**
     * Split prefix by dots.
     *
     * @param prefix the prefix
     * @return the string[]
     */
    @Override
    public String[] splitPrefixByDots(String prefix) {
        int prefixLen = prefix.length();
        ArrayList<String> prefixParts = new ArrayList<String>();
        boolean isStartNewPart = true;
        boolean isQuotedString = false;
        boolean isEscapedByLastChar = false;
        StringBuilder blr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        char ch = '\0';

        for (int pos = 0; pos < prefixLen; pos++) {
            ch = prefix.charAt(pos);

            if (DOT == ch && !isQuotedString) {
                // If its not new part already
                prefixParts.add(blr.toString());
                // If start of new word then create new string build.
                blr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                isStartNewPart = true;
            } else if (ESCAPE_CHAR == ch && isEscapedLiteral(prefix, pos)) {
                blr.append(ch);

                // consume next EscapeChar char, no need of maintaining the
                // state
                blr.append(prefix.charAt(pos));
                pos++;
            } else if (isQuotedString) {
                if (DOUBLE_QUOTE == ch && !isEscapedByLastChar) {
                    blr.append(ch);
                    // Finish of quoted word
                    prefixParts.add(blr.toString());
                    // If start of new word then create new string build.
                    blr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                    isQuotedString = false;
                    isStartNewPart = true;

                    // consume next DOT char
                    if ((pos + 1) < prefixLen && DOT == prefix.charAt(pos + 1)) {
                        pos++;
                    }
                } else {
                    // Consume the quoted string content
                    blr.append(ch);
                    isEscapedByLastChar = false;
                    isStartNewPart = false;
                }
            } else if (DOUBLE_QUOTE == ch && !isEscapedByLastChar) {
                // Start of a quoted string
                isQuotedString = true;
                isStartNewPart = true;
                blr.append(ch);
            } else {
                // Consume the unquoted string content
                blr.append(ch);
                isEscapedByLastChar = false;
                isStartNewPart = false;
            }
        }

        if (!isStartNewPart) {
            // If any unfinished content found, added to list.
            prefixParts.add(blr.toString());
        }

        return prefixParts.toArray(new String[prefixParts.size()]);
    }

    /**
     * Gets the prefix hyper link.
     *
     * @param searchPrefix the search prefix
     * @return the prefix hyper link
     */
    @Override
    public String[] getPrefixHyperLink(String searchPrefix) {
        String[] prefixes = searchPrefix.split("\\.");
        String quoteRmvdstr = null;

        List<String> arrListolap = new ArrayList<String>(5);
        String str = null;
        if (searchPrefix.contains("\"")) {
            for (int i = 0; i < prefixes.length; i++) {
                str = prefixes[i].trim();
                if (str.endsWith("\"")) {
                    if (str.contains("\"\"")) {
                        quoteRmvdstr = str.replaceAll("^\"|\"$", "");
                        quoteRmvdstr = quoteRmvdstr.replace("\"\"", "\"");
                        arrListolap.add(quoteRmvdstr);
                    } else {
                        if (str.contains("\"")) {
                            quoteRmvdstr = str.replaceAll("^\"|\"$", "");
                            arrListolap.add(quoteRmvdstr);
                        }
                    }
                } else {
                    quoteRmvdstr = str;
                    arrListolap.add(quoteRmvdstr);
                }
            }
            prefixes = arrListolap.toArray(new String[arrListolap.size()]);
        }
        return prefixes;
    }

    /**
     * Find matching keywords.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingKeywords(String prefix) {
        ContentAssistKeywords.getInstance().loadKeywords(database);
        return ContentAssistKeywords.getInstance().findMatchingOLAPKeyword(prefix);
    }

    /**
     * Find matching dataypes.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingDataypes(String prefix) {
        return database.findMatchingDatatype(prefix);
    }

    @Override
    public SortedMap<String, ServerObject> getChildObject(SortedMap<String, ServerObject> found,
            boolean isParentDescNeeded) {
        SortedMap<String, ServerObject> resultMap = new TreeMap<String, ServerObject>();
        for (ServerObject obj : found.values()) {
            if (obj instanceof ColumnMetaData) {
                ColumnMetaData clm = (ColumnMetaData) obj;
                resultMap.put(clm.getClmNameWithDatatype(isParentDescNeeded), obj);
            } else if (obj instanceof ViewColumnMetaData) {
                ViewColumnMetaData clm = (ViewColumnMetaData) obj;
                resultMap.put(clm.getClmNameWithDatatype(isParentDescNeeded), obj);
            }
        }
        return resultMap;
    }

    /**
     * Find matching trigger object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingTriggerObject(String prefix) {
        return new TreeMap<String, ServerObject>();
    }

    /**
     * Find exact matching triggers.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingTriggers(String prefix) {
        return new TreeMap<String, ServerObject>();
    }

}
