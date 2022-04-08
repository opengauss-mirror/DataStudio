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

package org.opengauss.mppdbide.gauss.sqlparser.parser;

import java.util.ListIterator;
import java.util.Set;

import org.opengauss.mppdbide.gauss.sqlparser.bean.tokendata.ISQLTokenData;

/**
 * Title: AbstractASTNodeParser
 * 
 * @param <E> the element type
 * @since 3.0.0
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
