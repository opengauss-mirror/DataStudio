/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.sqlparser.parser;

import java.util.ListIterator;
import java.util.Set;

import com.huawei.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * Title: AbstractASTNodeParser
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version [DataStudio 6.5.1, Nov 30, 2019]
 * @param <E> the element type
 * @since Nov 30, 2019
 */
public abstract class AbstractASTNodeParser<E> {

    private Set<String> keywordList = null;

    /**
     * Prepare AST stmt object.
     *
     * @param listIterator the list iterator
     * @return the e
     */
    public abstract E prepareASTStmtObject(ListIterator<ISQLTokenData> listIterator);

    /**
     * Gets the keyword list.
     *
     * @return the keyword list
     */
    public Set<String> getKeywordList() {
        return keywordList;
    }

    /**
     * Sets the keyword list.
     *
     * @param keywordList the new keyword list
     */
    public void setKeywordList(Set<String> keywordList) {
        this.keywordList = keywordList;
    }

}
