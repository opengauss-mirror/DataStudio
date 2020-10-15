/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
