/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSdefaultBooleanDisplayConverter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
