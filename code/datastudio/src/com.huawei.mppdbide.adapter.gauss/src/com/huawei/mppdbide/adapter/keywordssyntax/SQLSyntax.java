/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

import org.apache.commons.collections4.trie.PatriciaTrie;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLSyntax.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLSyntax {

    private PatriciaTrie<String> reservedkrywords;
    private PatriciaTrie<String> unreservedkrywords;
    private PatriciaTrie<String> types;
    private PatriciaTrie<String> constants;
    private PatriciaTrie<String> predicates;
    private PatriciaTrie<String> datatypes;

    /**
     * Instantiates a new SQL syntax.
     */
    public SQLSyntax() {
        reservedkrywords = new PatriciaTrie<String>();
        unreservedkrywords = new PatriciaTrie<String>();
        types = new PatriciaTrie<String>();
        constants = new PatriciaTrie<String>();
        predicates = new PatriciaTrie<String>();
        datatypes = new PatriciaTrie<String>();
    }

    /**
     * Gets the reservedkrywords.
     *
     * @return the reservedkrywords
     */
    public PatriciaTrie<String> getReservedkrywords() {
        return reservedkrywords;
    }

    /**
     * Gets the unreservedkrywords.
     *
     * @return the unreservedkrywords
     */
    public PatriciaTrie<String> getUnreservedkrywords() {
        return this.unreservedkrywords;
    }

    /**
     * Gets the types.
     *
     * @return the types
     */
    public PatriciaTrie<String> getTypes() {
        return this.types;
    }

    /**
     * Gets the constants.
     *
     * @return the constants
     */
    public PatriciaTrie<String> getConstants() {
        return this.constants;
    }

    /**
     * Gets the predicates.
     *
     * @return the predicates
     */
    public PatriciaTrie<String> getPredicates() {
        return this.predicates;
    }

    /**
     * Clear.
     */
    public void clear() {
        if (reservedkrywords != null) {
            reservedkrywords.clear();
        }
        if (unreservedkrywords != null) {
            unreservedkrywords.clear();
        }
        if (types != null) {
            types.clear();
        }
        if (constants != null) {
            constants.clear();
        }
        if (predicates != null) {
            predicates.clear();
        }
    }

    private PatriciaTrie<String> getDatatypes() {
        return this.datatypes;
    }

    /**
     * addKeywordListInTrie
     * 
     * @param keywords parameter
     */
    public void addKeywordListInTrie(KeywordsIf keywords) {
        convert(keywords.getReservedKeywords(), getReservedkrywords());
        convert(keywords.getUnReservedKeywords(), getUnreservedkrywords());
        convert(keywords.getUnRetentionKeywords(), getUnreservedkrywords());
        convert(keywords.getConstants(), getConstants());
        convert(keywords.getTypes(), getTypes());
        convert(keywords.getPredicates(), getPredicates());
        convert(keywords.getDataTypes(), getDatatypes());
    }

    private static void convert(String[] keywords, PatriciaTrie<String> keywordTrie) {
        for (int index = 0; index < keywords.length; index++) {
            keywordTrie.put(keywords[index].trim(), "1");
        }
    }
}
