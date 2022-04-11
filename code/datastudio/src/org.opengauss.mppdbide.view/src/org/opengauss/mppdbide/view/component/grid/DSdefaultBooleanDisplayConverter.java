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

package org.opengauss.mppdbide.view.component.grid;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSdefaultBooleanDisplayConverter.
 *
 * @since 3.0.0
 */
public class DSdefaultBooleanDisplayConverter extends DefaultBooleanDisplayConverter {

    private static final String BIT_ONE = "1";
    private static final String BIT_ZERO = "0";
    private static final String BOOLEAN_TRUE = "true";
    private static final String BOOLEAN_FALSE = "false";

    /**
     * Canonical to display value.
     *
     * @param cell the cell
     * @param configRegistry the config registry
     * @param canonicalValue the canonical value
     * @return the object
     */
    @Override
    public Object canonicalToDisplayValue(ILayerCell cell, IConfigRegistry configRegistry, Object canonicalValue) {

        if (canonicalValue == null) {
            return null;
        } else {
            if (BIT_ONE.equals(canonicalValue) || BOOLEAN_TRUE.equals(canonicalValue)) {
                return true;
            }
            if (BIT_ZERO.equals(canonicalValue) || BOOLEAN_FALSE.equals(canonicalValue)) {
                return false;
            }
            return canonicalValue.toString();
        }
    }

}
