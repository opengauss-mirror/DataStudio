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

import java.util.Collection;

import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDisplayConverter;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomComboxDefaultDisplayConverter.
 *
 * @since 3.0.0
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
