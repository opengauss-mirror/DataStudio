/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.IndexList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewColumnList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;

/**
 * Title: class Description: The Class ObjectBrowserContentProvider. Copyright
 * (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ObjectBrowserContentProvider implements ITreeContentProvider {
    private TreeViewer viewer = null;

    /**
     * Dispose.
     */
    @Override
    public void dispose() {
        // No Cleanup activity as of now. May have to add.
    }

    /**
     * Input changed.
     *
     * @param treeViewer the tree viewer
     * @param oldInput the old input
     * @param newInput the new input
     */
    @Override
    public void inputChanged(Viewer treeViewer, Object oldInput, Object newInput) {

        this.viewer = (TreeViewer) treeViewer;

    }

    /**
     * Gets the elements.
     *
     * @param connectionProfiles the connection profiles
     * @return the elements
     */
    @Override
    public Object[] getElements(Object connectionProfiles) {
        return ((Collection<?>) connectionProfiles).toArray();
    }

    /**
     * Gets the children.
     *
     * @param parentElement the parent element
     * @return the children
     */
    @Override
    public Object[] getChildren(Object parentElement) {
        if (!loadNextLevelData(parentElement, viewer)) {
            return new Object[] {new LoadingUIElement()};
        }

        if (parentElement instanceof ObjectGroup<?>) {
            ObjectGroup<?> groupObject = (ObjectGroup<?>) parentElement;
            return getObjectGroupChildList(groupObject);
        }

        if (parentElement instanceof ServerObject) {
            ServerObject obj = (ServerObject) parentElement;
            if (obj.isLoadingInProgress()) {
                return new Object[] {new LoadingUIElement()};
            }
            if (obj instanceof Namespace) {
                Namespace ns = (Namespace) obj;
                if (ns.isNotLoaded()) {
                    return new Object[0];
                }
            }

            return obj.getChildren();
        }

        else if (parentElement instanceof ObjectList<?>) {
            return ((ObjectList<?>) parentElement).getList().toArray();
        }

        if (parentElement instanceof ViewColumnList) {
            ViewColumnList colList = (ViewColumnList) parentElement;
            return colList.getList().toArray();
        }

        if (parentElement instanceof Server) {
            Server node = (Server) parentElement;
            return node.getChildren();
        }

        return new Object[0];
    }

    /**
     * to get the Child Object List of Object Group
     * 
     * @param groupObject object group whose child object need to be get
     * @return return object group child
     */
    protected Object[] getObjectGroupChildList(ObjectGroup<?> groupObject) {
        return groupObject.getChildren();
    }

    /**
     * Gets the parent.
     *
     * @param element the element
     * @return the parent
     */
    @Override
    public Object getParent(Object element) {
        return null;
    }

    /**
     * Checks for children.
     *
     * @param element the element
     * @return true, if successful
     */
    @Override
    public boolean hasChildren(Object element) {

        boolean isElmntHasChildren = checkElementHasChildren(element);

        boolean isElementHasChildren = element instanceof DebugObjectGroup || element instanceof Namespace
                || element instanceof TableMetaData || element instanceof IndexList
                || element instanceof TableObjectGroup || isElmntHasChildren;

        if (element instanceof Server || element instanceof Database || element instanceof ObjectList<?>
                || isElementHasChildren || element instanceof ObjectGroup<?>) {
            return true;
        }
        return false;
    }

    private boolean checkElementHasChildren(Object element) {
        return element instanceof PartitionTable || element instanceof TablespaceObjectGroup
                || element instanceof ViewObjectGroup || element instanceof ForeignTableGroup
                || element instanceof ViewMetaData || element instanceof SequenceObjectGroup;
    }

    /**
     * loadNextLevelData - Load Next level data like Level 2, Level 3 etc.
     *
     * @param obj the obj
     * @param currViewer the curr viewer
     * @return true, if successful
     */
    private boolean loadNextLevelData(final Object obj, TreeViewer currViewer) {
        if (obj instanceof ColumnMetaData && !((ColumnMetaData) obj).isLoaded()) {
            return false;
        } else if (obj instanceof ConstraintMetaData && !((ConstraintMetaData) obj).isLoaded()) {
            return false;
        } else if (obj instanceof IndexMetaData && !((IndexMetaData) obj).isLoaded()) {
            return false;
        }
        return true;
    }
}
