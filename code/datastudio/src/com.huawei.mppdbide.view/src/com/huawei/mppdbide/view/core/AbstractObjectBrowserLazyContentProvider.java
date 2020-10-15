/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
