/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.contentassist;

import java.util.List;
import java.util.SortedMap;

import org.apache.commons.collections4.trie.PatriciaTrie;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ContentAssistUtilIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface ContentAssistUtilIf {

    /**
     * Find matching namespace.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingNamespace(String prefix);

    /**
     * Find matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingTables(String prefix);

    /**
     * Find matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingDebugObjects(String prefix);

    /**
     * Find matching views object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingViewsObject(String prefix);

    /**
     * Find matching sequence object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingSequenceObject(String prefix);

    /**
     * Find exact matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingSequences(String prefix);

    /**
     * Find exact matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingDebugObjects(String prefix);

    /**
     * Find exact matching views.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingViews(String prefix);

    /**
     * Find exact matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingTables(String prefix);

    /**
     * Find exact matching namespaces.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingNamespaces(String prefix);

    /**
     * Find string.
     *
     * @param pretext the pretext
     * @param workBreakCharList the work break char list
     * @return the string
     */
    String findString(String pretext, List<Character> workBreakCharList);

    /**
     * Removes the literal escapes and quotes.
     *
     * @param str the str
     * @return the string
     */
    String removeLiteralEscapesAndQuotes(String str);

    /**
     * Split prefix by dots.
     *
     * @param prefix the prefix
     * @return the string[]
     */
    String[] splitPrefixByDots(String prefix);

    /**
     * Gets the prefix hyper link.
     *
     * @param prefix the prefix
     * @return the prefix hyper link
     */
    String[] getPrefixHyperLink(String prefix);

    /**
     * Gets the matching.
     *
     * @param trie the trie
     * @param prefix the prefix
     * @return the matching
     */
    SortedMap<String, ServerObject> getMatching(PatriciaTrie trie, String prefix);

    /**
     * Gets the all object with prefix case insensitive.
     *
     * @param trie the trie
     * @param text the text
     * @return the all object with prefix case insensitive
     */
    SortedMap<String, ServerObject> getAllObjectWithPrefixCaseInsensitive(PatriciaTrie trie, String text);

    /**
     * Find matching keywords.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingKeywords(String prefix);

    /**
     * Find matching dataypes.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingDataypes(String prefix);

    /**
     * Checks if is insert.
     *
     * @return true, if is insert
     */
    boolean isInsert();

    /**
     * Gets the child object.
     *
     * @param found the found
     * @param isParentDescNeeded the is parent desc needed
     * @return the child object
     */
    SortedMap<String, ServerObject> getChildObject(SortedMap<String, ServerObject> found, boolean isParentDescNeeded);

    /**
     * Find matching trigger object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findMatchingTriggerObject(String prefix);

    /**
     * Find exact matching triggers.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    SortedMap<String, ServerObject> findExactMatchingTriggers(String prefix);

    /**
     * Find matching synonyms.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    default SortedMap<String, ServerObject> findMatchingSynonyms(String prefix) {
        return null;
    }

    /**
     * Find exact matching synonyms.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    default SortedMap<String, ServerObject> findExactMatchingSynonyms(String prefix) {
        return null;
    }
}
