/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.TreeViewer;

import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CollapseOpenObjectHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class CollapseOpenObjectHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowser != null) {
            TreeViewer viewer = objectBrowser.getTreeViewer();
            viewer.collapseAll();
            objectBrowser.refresh();
        }
    }

}
