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

package com.huawei.mppdbide.view.core;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractObjectBrowserLazyContentProvider.
 *
 * @since 3.0.0
 */
public abstract class AbstractObjectBrowserLazyContentProvider implements ILazyTreeContentProvider {

    /**
     * Dispose.
     */
    @Override
    public void dispose() {

    }

    /**
     * Input changed.
     *
     * @param viewr the viewr
     * @param oldInput the old input
     * @param newInput the new input
     */
    @Override
    public void inputChanged(Viewer viewr, Object oldInput, Object newInput) {

    }

    /**
     * Update element.
     *
     * @param parent the parent
     * @param index the index
     */
    @Override
    public abstract void updateElement(Object parent, int index);

    /**
     * Update server object.
     *
     * @param obj the obj
     * @param index the index
     * @return the object
     */
    protected Object updateServerObject(ServerObject obj, int index) {
        if (obj.isLoadingInProgress()) {
            return new LoadingUIElement();
        } else {
            return obj.getChildren()[index];
        }
    }

    /**
     * Update child count.
     *
     * @param element the element
     * @param currentChildCount the current child count
     */
    @Override
    public abstract void updateChildCount(Object element, int currentChildCount);

    /**
     * Gets the parent.
     *
     * @param element the element
     * @return the parent
     */
    @Override
    public abstract Object getParent(Object element);

}
