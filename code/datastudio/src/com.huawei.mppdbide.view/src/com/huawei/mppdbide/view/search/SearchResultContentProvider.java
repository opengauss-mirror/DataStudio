/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.search;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.view.core.ObjectBrowserContentProvider;

/**
 * Title: class Description: The Class SearchResultContentProvider. Copyright
 * (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
