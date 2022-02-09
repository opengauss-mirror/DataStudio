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

package org.opengauss.mppdbide.view.component.grid.sort;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Item;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortColumnSettingModifier.
 *
 * @since 3.0.0
 */
public class SortColumnSettingModifier implements ICellModifier {
    private Viewer viewer;
    private String[] cols = null;
    private String[] datatypes = null;
    private static final String[] COLUMN_HEADERS = {MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_PRIORITY),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_COLUMN_NAME),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_DATATYPE),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_SORT_ORDER)};

    /**
     * Instantiates a new sort column setting modifier.
     *
     * @param view the view
     * @param cols the cols
     * @param datatypes the datatypes
     */
    public SortColumnSettingModifier(Viewer view, String[] cols, String[] datatypes) {
        this.viewer = view;
        this.cols = cols.clone();
        this.datatypes = datatypes.clone();
    }

    /**
     * Can modify.
     *
     * @param element the element
     * @param property the property
     * @return true, if successful
     */
    @Override
    public boolean canModify(Object element, String property) {
        return COLUMN_HEADERS[MulticolumnSortConstants.COLUMN_INDEX].equals(property)
                || COLUMN_HEADERS[MulticolumnSortConstants.ORDER_INDEX].equals(property);
    }

    /**
     * Gets the value.
     *
     * @param element the element
     * @param property the property
     * @return the value
     */
    @Override
    public Object getValue(Object element, String property) {
        SortColumnSetting data = (SortColumnSetting) element;
        if (COLUMN_HEADERS[MulticolumnSortConstants.COLUMN_INDEX].equals(property)) {
            return data.getColumnName();
        } else if (COLUMN_HEADERS[MulticolumnSortConstants.ORDER_INDEX].equals(property)) {
            return data.getSortOrder();
        } else if (COLUMN_HEADERS[MulticolumnSortConstants.DATATYPE_INDEX].equals(property)) {
            return data.getDataType();
        } else if (COLUMN_HEADERS[MulticolumnSortConstants.PRIORITY_INDEX].equals(property)) {
            return data.getPriority();
        } else {
            return null;
        }
    }

    /**
     * Modify.
     *
     * @param elementParam the element param
     * @param property the property
     * @param value the value
     */
    @Override
    public void modify(Object elementParam, String property, Object value) {
        Object element = elementParam;
        if (element instanceof Item) {
            element = ((Item) element).getData();
        }
        SortColumnSetting data = (SortColumnSetting) element;
        if (COLUMN_HEADERS[MulticolumnSortConstants.COLUMN_INDEX].equals(property)) {
            data.setColumnName((String) value, cols, datatypes);
        } else if (COLUMN_HEADERS[MulticolumnSortConstants.ORDER_INDEX].equals(property)) {
            data.setSortOrder((String) value);
        } else if (COLUMN_HEADERS[MulticolumnSortConstants.PRIORITY_INDEX].equals(property)) {
            data.setPriority((String) value);
        }
        viewer.refresh();
    }

}
