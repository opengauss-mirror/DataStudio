/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.synonym;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;

/**
 * 
 * Title: Class
 * 
 * Description: The Class SynonymCommonUtil.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author s00428892
 * @version
 * @since Mar 18, 2020
 */
public class SynonymCommonUtil {
    public SynonymCommonUtil() {
    }

    /**
     * gets the GroupContainer
     * 
     * @param tabFolder the tab folder
     * @return container the group container
     */
    public Group getGroupContainer(TabFolder tabFolder) {
        Group container = new Group(tabFolder, SWT.SHADOW_IN);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);
        return container;
    }

    /**
     * gets the InnerGroupComposite
     * 
     * @param container the container
     * @return innerGroup the inner group
     */
    public Composite getInnerGroupComposite(Group container) {
        Composite innerGroup = new Composite(container, SWT.NONE);
        GridLayout innerGLayout = new GridLayout(2, false);
        innerGroup.setLayout(innerGLayout);
        GridData innerGData = new GridData(SWT.FILL, SWT.FILL, true, false);
        innerGroup.setLayoutData(innerGData);
        return innerGroup;
    }

    /**
     * the gridComponentStyle
     * 
     * @param gd the grid
     * @param heightHint the heightHint
     * @param widthHint the widthHint
     * @param horizontalIndent the horizontalIndent
     * @param verticalIndent the verticalIndent
     */
    public void gridComponentStyle(GridData gd, int heightHint, int widthHint, int horizontalIndent,
            int verticalIndent) {
        gd.heightHint = heightHint;
        gd.widthHint = widthHint;
        gd.horizontalIndent = horizontalIndent;
        gd.verticalIndent = verticalIndent;
    }
}
