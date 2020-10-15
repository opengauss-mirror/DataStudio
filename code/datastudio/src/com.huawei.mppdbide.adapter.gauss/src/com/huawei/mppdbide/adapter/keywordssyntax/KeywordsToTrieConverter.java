/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class KeywordsToTrieConverter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class KeywordsToTrieConverter {

    /**
     * Convert keywordsto trie.
     *
     * @param syntax the syntax
     * @param keywords the keywords
     * @return the SQL syntax
     */
    public static SQLSyntax convertKeywordstoTrie(SQLSyntax syntax, KeywordsIf keywords) {
        syntax.addKeywordListInTrie(keywords);
        return syntax;
    }
}
