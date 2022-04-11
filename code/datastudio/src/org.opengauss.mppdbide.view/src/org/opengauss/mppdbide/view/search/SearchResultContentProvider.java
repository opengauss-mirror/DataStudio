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

package org.opengauss.mppdbide.view.search;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import org.opengauss.mppdbide.view.core.ObjectBrowserContentProvider;

/**
 * Title: class Description: The Class SearchResultContentProvider.
 *
 * @since 3.0.0
 */
public class SearchResultContentProvider extends ObjectBrowserContentProvider {

    /**
     * Checks for children.
     *
     * @param element the element
     * @return true, if successful
     */
    @Override
    public boolean hasChildren(Object element) {
        boolean isInstanceOfElement = element instanceof TableObjectGroup || element instanceof ViewObjectGroup
                || element instanceof ForeignTableGroup || element instanceof SequenceObjectGroup
                || element instanceof SynonymObjectGroup;

        if (element instanceof DebugObjectGroup || element instanceof Namespace || element instanceof OLAPObjectList<?>
                || isInstanceOfElement || element instanceof OLAPObjectGroup<?>) {
            return true;
        }

        return false;
    }

    /**
     * to get the Child Object List of Object Group
     * 
     * @param groupObject object group whose child object need to be get
     * @return return object group child
     */
    @Override
    protected Object[] getObjectGroupChildList(ObjectGroup<?> groupObject) {
        return groupObject.getChildrenWithoutFilter();
    }
}
