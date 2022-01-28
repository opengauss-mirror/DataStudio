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

import org.eclipse.jface.viewers.TreeViewer;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserLazyContentProviderForGaussOLAP.
 * 
 * @since 3.0.0
 */
public class ObjectBrowserLazyContentProviderForGaussOLAP extends AbstractObjectBrowserLazyContentProvider {

    /**
     * The viewer.
     */
    protected TreeViewer viewer;

    /**
     * Update element.
     *
     * @param parent the parent
     * @param index the index
     */
    @Override
    public void updateElement(Object parent, int index) {
        try {
            Object element = null;

            if (parent instanceof ServerObject) {
                ServerObject objct = (ServerObject) parent;
                element = updateServerObject(objct, index);
                if (objct instanceof Namespace) {
                    Namespace ns = (Namespace) objct;
                    if (ns.isNotLoaded()) {
                        element = new LoadingUIElement();
                    }
                }

            } else if (parent instanceof OLAPObjectGroup<?>) {
                element = ((OLAPObjectGroup<?>) parent).getChildren()[index];
            } else if (parent instanceof Server) {
                Server node = (Server) parent;
                element = node.getChildren()[index];
            } else if (parent instanceof OLAPObjectList<?>) {
                element = ((OLAPObjectList<?>) parent).getList().get(index);
            }

            if (element != null) {
                viewer.replace(parent, index, element);
                updateChildCount(element, -1);
            }

        } catch (ArrayIndexOutOfBoundsException exception) {
            MPPDBIDELoggerUtility.error("Index Out Of Bound Exception, Error while Refreshing Tablespace", exception);
        }

    }

    /**
     * Instantiates a new object browser lazy content provider for gauss OLAP.
     *
     * @param viewer the viewer
     */
    public ObjectBrowserLazyContentProviderForGaussOLAP(TreeViewer viewer) {
        this.viewer = viewer;
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

        if (element instanceof OLAPObjectGroup<?>) {
            OLAPObjectGroup<?> groupObject = (OLAPObjectGroup<?>) element;
            length = groupObject.getChildren().length;
        } else if (element instanceof ServerObject) {
            ServerObject obj = (ServerObject) element;
            if (obj.isLoadingInProgress()) {
                length = 1; // loading UI
            } else {
                length = obj.getChildren().length;
            }
            if (obj instanceof Namespace) {
                Namespace ns = (Namespace) obj;
                if (ns.isNotLoaded()) {
                    length = -1; // loading UI
                }
            } else if (obj instanceof Database) {
                if (!((Database) obj).isConnected()) {
                    length = 0;
                }
            }
        } else if (element instanceof Server) {
            Server node = (Server) element;
            length = node.getChildren().length;

        } else if (element instanceof OLAPObjectList<?>) {
            length = ((OLAPObjectList<?>) element).getList().size();
        }

        viewer.setChildCount(element, length);

    }

    /**
     * Gets the parent.
     *
     * @param element the element
     * @return the parent
     */
    @Override
    public Object getParent(Object element) {
        if (element instanceof ServerObject) {
            ServerObject obj = (ServerObject) element;
            return obj.getParent();
        } else if (element instanceof OLAPObjectList<?>) {
            return ((OLAPObjectList<?>) element).getParent();
        }
        return null;
    }
}
