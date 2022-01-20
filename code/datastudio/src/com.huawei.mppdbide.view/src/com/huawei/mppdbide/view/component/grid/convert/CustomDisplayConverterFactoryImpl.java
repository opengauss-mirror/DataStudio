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

package com.huawei.mppdbide.view.component.grid.convert;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomDisplayConverterFactoryImpl.
 *
 * @since 3.0.0
 */
public class CustomDisplayConverterFactoryImpl implements DisplayConverterFactoryIf {

    /**
     * Gets the custom default date display converter.
     *
     * @param format the format
     * @return the custom default date display converter
     */
    public DefaultDateDisplayConverter getCustomDefaultDateDisplayConverter(String format) {
        return new CustomDefaultDateDisplayConverter(format);

    }

    /**
     * Gets the custom combox default display converter.
     *
     * @return the custom combox default display converter
     */
    public DefaultDisplayConverter getCustomComboxDefaultDisplayConverter() {
        return new CustomComboxDefaultDisplayConverter();

    }

}
