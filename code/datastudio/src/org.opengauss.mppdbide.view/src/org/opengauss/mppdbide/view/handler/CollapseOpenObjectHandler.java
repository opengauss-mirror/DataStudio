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

package org.opengauss.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.viewers.TreeViewer;

import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class CollapseOpenObjectHandler.
 *
 * @since 3.0.0
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
