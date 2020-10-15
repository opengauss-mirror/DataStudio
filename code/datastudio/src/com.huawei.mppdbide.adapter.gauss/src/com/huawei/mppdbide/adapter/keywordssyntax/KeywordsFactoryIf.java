/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface KeywordsFactoryIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface KeywordsFactoryIf {

    /**
     * Gets the keywords.
     *
     * @return the keywords
     */
    Keywords getKeywords();

    /**
     * Gets the OLAP keywords.
     *
     * @return the OLAP keywords
     */
    Keywords getOLAPKeywords();

}
