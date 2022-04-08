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

package org.opengauss.mppdbide.view.core.sourceeditor.templates;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * 
 * Title: class
 * 
 * Description: The Class TemplateContentProvider.
 *
 * @since 3.0.0
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
