/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor.templates;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateContentProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class TemplateContentProvider implements IStructuredContentProvider {

    /**
     * The store.
     */
    private TemplateStore store;

    /**
     * Gets the elements.
     *
     * @param input the input
     * @return the elements
     */
    public Object[] getElements(Object input) {
        return store.getTemplateData(false);
    }

    /**
     * Input changed.
     *
     * @param viewer the viewer
     * @param oldInput the old input
     * @param newInput the new input
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        store = (TemplateStore) newInput;
    }

    /**
     * Dispose.
     */
    public void dispose() {
        store = null;
    }
}
