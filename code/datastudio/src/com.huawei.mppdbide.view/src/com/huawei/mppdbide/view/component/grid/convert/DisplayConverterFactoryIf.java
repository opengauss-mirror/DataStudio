/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.convert;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface DisplayConverterFactoryIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface DisplayConverterFactoryIf {

    /**
     * Gets the custom default date display converter.
     *
     * @param format the format
     * @return the custom default date display converter
     */
    public DefaultDateDisplayConverter getCustomDefaultDateDisplayConverter(String format);

    /**
     * Gets the custom combox default display converter.
     *
     * @return the custom combox default display converter
     */
    public DefaultDisplayConverter getCustomComboxDefaultDisplayConverter();
}
