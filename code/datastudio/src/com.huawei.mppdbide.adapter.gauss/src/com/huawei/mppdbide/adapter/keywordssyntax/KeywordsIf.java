/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface KeywordsIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
