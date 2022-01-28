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

package com.huawei.mppdbide.view.component.grid.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;

import com.huawei.mppdbide.presentation.objectproperties.PropertiesConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class CustomComboBoxCellEditor.
 *
 * @since 3.0.0
 */
public class CustomComboBoxCellEditor extends ComboBoxCellEditor implements ICustomComboBoxCellEditor {

    /**
     * Instantiates a new custom combo box cell editor.
     *
     * @param comboBoxDataProvider the combo box data provider
     * @param maxVisibleItems the max visible items
     */
    public CustomComboBoxCellEditor(IComboBoxDataProvider comboBoxDataProvider, int maxVisibleItems) {
        super(comboBoxDataProvider, maxVisibleItems);
    }

    /**
     * Gets the canonical value.
     *
     * @return the canonical value
     */
    @Override
    public Object getCanonicalValue() {
        Object canonicalValue = super.getCanonicalValue();
        if (canonicalValue instanceof List<?>) {
            List<?> list = (List<?>) canonicalValue;
            if (1 == list.size() && null == list.get(0)) {
                return "";
            }
        }
        return canonicalValue == null ? null : canonicalValue.toString();
    }

    /**
     * Sets the canonical value.
     *
     * @param canonicalValue the new canonical value
     */
    @Override
    public void setCanonicalValue(Object canonicalValue) {
        List<String> list = new ArrayList<>();
        String valueStr = "";
        if (canonicalValue instanceof String) {
            valueStr = (String) canonicalValue;
            if (valueStr.contains("[") && valueStr.contains("]")) {
                valueStr = valueStr.substring(1, valueStr.length() - 1);
                String[] splitStr = valueStr.split(PropertiesConstants.SPLIT_STR);
                for (int i = 0; i < splitStr.length; i++) {
                    list.add(splitStr[i].trim());
                }
            }
        }
        super.setCanonicalValue(list.isEmpty() ? valueStr.trim() : list);
    }

}
