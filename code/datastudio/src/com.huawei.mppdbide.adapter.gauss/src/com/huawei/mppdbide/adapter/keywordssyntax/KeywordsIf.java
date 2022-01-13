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

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface KeywordsIf.
 * 
 * @since 3.0.0
 */
public interface KeywordsIf {

    /**
     * Gets the reserved keywords.
     *
     * @return the reserved keywords
     */
    String[] getReservedKeywords();

    /**
     * Gets the un reserved keywords.
     *
     * @return the un reserved keywords
     */
    String[] getUnReservedKeywords();

    /**
     * Gets the un retention keywords.
     *
     * @return the un retention keywords
     */
    String[] getUnRetentionKeywords();

    /**
     * Gets the types.
     *
     * @return the types
     */
    String[] getTypes();

    /**
     * Gets the constants.
     *
     * @return the constants
     */
    String[] getConstants();

    /**
     * Gets the predicates.
     *
     * @return the predicates
     */
    String[] getPredicates();

    /**
     * Gets the data types.
     *
     * @return the data types
     */
    String[] getDataTypes();
}
