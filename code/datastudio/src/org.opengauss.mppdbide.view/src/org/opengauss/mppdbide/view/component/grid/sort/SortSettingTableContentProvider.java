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

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortSettingTableContentProvider.
 *
 * @since 3.0.0
 */
public class SortSettingTableContentProvider implements IStructuredContentProvider {

    /**
     * Dispose.
     */
    @Override
    public void dispose() {

    }

    /**
     * Input changed.
     *
     * @param viewer the viewer
     * @param oldInput the old input
     * @param newInput the new input
     */
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /**
     * Gets the elements.
     *
     * @param inputElement the input element
     * @return the elements
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
        return ((List<SortColumnSetting>) inputElement).toArray();
    }

}
