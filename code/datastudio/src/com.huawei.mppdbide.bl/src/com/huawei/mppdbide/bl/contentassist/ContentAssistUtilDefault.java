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
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import com.huawei.mppdbide.bl.contentassist.ContentAssistProcesserData.AutoSuggestComparator;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * Title: ContentAssistUtilDefault
 * 
 * Description:
 * 
 */

public class ContentAssistUtilDefault extends ContentAssistUtil {

    SortedMap<String, ServerObject> defaultMap = new TreeMap<String, ServerObject>(new AutoSuggestComparator());

    ArrayList<String> prefixParts = new ArrayList<String>();

    /**
     * Find matching namespace.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingNamespace(String prefix) {
        return defaultMap;
    }

    /**
     * Find matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingTables(String prefix) {
        return defaultMap;
    }

    /**
     * Find matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingDebugObjects(String prefix) {
        return defaultMap;
    }

    /**
     * Find matching views object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingViewsObject(String prefix) {
        return defaultMap;
    }

    /**
     * Find matching sequence object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingSequenceObject(String prefix) {
        return defaultMap;
    }

    /**
     * Find exact matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingSequences(String prefix) {
        return defaultMap;
    }

    /**
     * Find exact matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingDebugObjects(String prefix) {
        return defaultMap;
    }

    /**
     * Find exact matching views.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingViews(String prefix) {
        return defaultMap;
    }

    /**
     * Find exact matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingTables(String prefix) {
        return defaultMap;
    }

    /**
     * Find exact matching namespaces.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findExactMatchingNamespaces(String prefix) {
        return defaultMap;
    }

    /**
     * Find string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @return the string
     */
    @Override
    public String findString(String pretext, List<Character> workBreakCharList) {

        if (pretext.trim().isEmpty()) {
            return null;
        }
        int pos = pretext.length() - 1;
        char ch = '\0';

        while (pos >= 0) {
            ch = pretext.charAt(pos);

            if (Character.isWhitespace(ch)) {
                break;
            }

            --pos;
        }
        String retStr = null;
        retStr = pretext.substring(pos + 1);
        retStr = retStr.trim();

        return retStr;
    }

    /**
     * Remove literal escapes and quotes.
     *
     * @param str the str
     * @return the string
     */
    @Override
    public String removeLiteralEscapesAndQuotes(String str) {
        return str.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Split prefix by dots.
     *
     * @param prefix the prefix
     * @return the string[]
     */
    @Override
    public String[] splitPrefixByDots(String prefix) {
        prefixParts.add(prefix);
        return prefixParts.toArray(new String[prefixParts.size()]);
    }

    /**
     * Gets the prefix hyper link.
     *
     * @param prefix the prefix
     * @return the prefix hyper link
     */
    @Override
    public String[] getPrefixHyperLink(String prefix) {
        return prefixParts.toArray(new String[5]);
    }

    /**
     * Find matching keywords.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingKeywords(String prefix) {
        ContentAssistKeywords.getInstance().loadKeywords(null);
        return ContentAssistKeywords.getInstance().findMatchingDefaultKeyword(prefix);
    }

    /**
     * Find matching dataypes.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    @Override
    public SortedMap<String, ServerObject> findMatchingDataypes(String prefix) {
        return defaultMap;
    }

    /**
     * Find default string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @param oldPos the old pos
     * @param strList the str list
     * @param posForFindPrefix the pos for find prefix
     * @return the int
     */
    @Override
    public int findDefaultString(String pretext, List<Character> workBreakCharList, int oldPos,
            ArrayList<String> strList, int posForFindPrefix) {
        return 0;
    }

    @Override
    public SortedMap<String, ServerObject> getChildObject(SortedMap<String, ServerObject> found,
            boolean isParentDescNeeded) {
        return defaultMap;
    }

    @Override
    public SortedMap<String, ServerObject> findMatchingTriggerObject(String prefix) {
        return defaultMap;
    }

    @Override
    public SortedMap<String, ServerObject> findExactMatchingTriggers(String prefix) {
        return defaultMap;
    }

    @Override
    public SortedMap<String, ServerObject> findExactMatchingSynonyms(String prefix) {
        return defaultMap;
    }

    @Override
    public SortedMap<String, ServerObject> findMatchingSynonyms(String prefix) {
        return defaultMap;
    }

}
