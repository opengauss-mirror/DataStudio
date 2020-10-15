/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.Collection;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.huawei.mppdbide.bl.serverdatacache.GaussOLAPDBMSObject;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserLazyContentProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ObjectBrowserLazyContentProvider implements ILazyTreeContentProvider {
    private TreeViewer viewer;
    private ObjectBrowserLazyContentProviderForGaussOLAP gaussOlapCp = null;

    /**
     * Instantiates a new object browser lazy content provider.
     *
     * @param viewer the viewer
     */
    public ObjectBrowserLazyContentProvider(TreeViewer viewer) {
        this.viewer = viewer;
        gaussOlapCp = new ObjectBrowserLazyContentProviderForGaussOLAP(viewer);
    }

    /**
     * Dispose.
     */
    @Override
    public void dispose() {

    }

    /**
     * Input changed.
     *
     * @param viewerInput the viewer input
     * @param oldInput the old input
     * @param newInput the new input
     */
    @Override
    public void inputChanged(Viewer viewerInput, Object oldInput, Object newInput) {

    }

    /**
     * Update element.
     *
     * @param parent the parent
     * @param index the index
     */
    @Override
    public void updateElement(Object parent, int index) {
        Object element = null;

        if (parent instanceof Collection<?>) {
            Collection<?> node = (Collection<?>) parent;
            element = node.toArray()[index];
            if (element != null) {
                viewer.replace(parent, index, element);
                updateChildCount(element, -1);
            }
        } else if (parent instanceof GaussOLAPDBMSObject) {
            if (gaussOlapCp != null) {
                gaussOlapCp.updateElement(parent, index);
            }
            return;
        }

    }

    /**
     * Update child count.
     *
     * @param element the element
     * @param currentChildCount the current child count
     */
    @Override
    public void updateChildCount(Object element, int currentChildCount) {
        int length = 0;

        if (element instanceof Collection<?>) {
            Collection<?> node = (Collection<?>) element;
            length = node.size();
            viewer.setChildCount(element, length);
        } else if (element instanceof GaussOLAPDBMSObject) {
            if (gaussOlapCp != null) {
                gaussOlapCp.updateChildCount(element, currentChildCount);
            }
            return;
        }
    }

    /**
     * Gets the parent.
     *
     * @param element the element
     * @return the parent
     */
    @Override
    public Object getParent(Object element) {
        if (element instanceof GaussOLAPDBMSObject) {
            if (gaussOlapCp != null) {
                return gaussOlapCp.getParent(element);
            }
        }
        return null;
    }
}
