/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.convert;

/**
 * 
 * Title: class
 * 
 * Description: The Class DisplayConverterFactoryProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class DisplayConverterFactoryProvider {

    /**
     * Gets the display converter factory.
     *
     * @return the display converter factory
     */
    public static DisplayConverterFactoryIf getDisplayConverterFactory() {
        return new CustomDisplayConverterFactoryImpl();
    }
}
