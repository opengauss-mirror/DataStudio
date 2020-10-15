/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.sort;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortSettingTableContentProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
