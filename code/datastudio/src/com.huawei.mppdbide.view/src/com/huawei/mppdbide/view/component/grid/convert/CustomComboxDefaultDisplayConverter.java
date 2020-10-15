/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.convert;

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomComboxDefaultDisplayConverter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CustomComboxDefaultDisplayConverter extends DefaultDisplayConverter {

    /**
     * Instantiates a new custom combox default display converter.
     */
    public CustomComboxDefaultDisplayConverter() {
        super();
    }

    /**
     * Canonical to display value.
     *
     * @param canonicalValue the canonical value
     * @return the object
     */
    @Override
    public Object canonicalToDisplayValue(Object canonicalValue) {
        if (canonicalValue instanceof Collection) {
            String result = canonicalValue.toString();
            if (result.length() > 1) {
                result = result.substring(1, result.length() - 1);
            }
            return result;
        }
        if (canonicalValue instanceof String) {
            String result = (String) canonicalValue;
            if (result.contains("[") && result.contains("]")) {
                if (result.length() > 1) {
                    return result.substring(1, result.length() - 1);
                }
            }
        }
        return super.canonicalToDisplayValue(canonicalValue);
    }

}
