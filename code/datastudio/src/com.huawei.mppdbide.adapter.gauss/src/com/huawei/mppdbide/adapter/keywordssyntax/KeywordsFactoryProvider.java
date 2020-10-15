/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.adapter.keywordssyntax;

/**
 * 
 * Title: class
 * 
 * Description: The Class KeywordsFactoryProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class KeywordsFactoryProvider {

    /**
     * Gets the keywords factory.
     *
     * @return the keywords factory
     */
    public static KeywordsFactoryIf getKeywordsFactory() {
        return new KeywordsFactoryImpl();
    }

}
