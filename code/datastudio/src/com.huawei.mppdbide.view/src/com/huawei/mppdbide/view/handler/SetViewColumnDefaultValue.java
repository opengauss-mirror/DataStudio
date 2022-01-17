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

package com.huawei.mppdbide.view.handler;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.view.ui.AlterViewColumnDefaultDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetViewColumnDefaultValue.
 *
 * @since 3.0.0
 */
public class SetViewColumnDefaultValue {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof ViewColumnMetaData) {
            ViewColumnMetaData column = (ViewColumnMetaData) obj;
            AlterViewColumnDefaultDialog dialog = new AlterViewColumnDefaultDialog(
                    Display.getDefault().getActiveShell(), column);
            dialog.open();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        ViewColumnMetaData view = IHandlerUtilities.getSelectedViewColumnObject();
        if (null != view) {
            Namespace ns = (Namespace) view.getParent().getNamespace();
            if (null != ns && ns.getDatabase().isConnected()) {
                return true;
            }
        }
        return false;
    }

}
