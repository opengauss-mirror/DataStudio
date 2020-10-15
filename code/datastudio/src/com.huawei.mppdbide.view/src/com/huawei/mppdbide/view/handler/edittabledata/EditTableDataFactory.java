/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.edittabledata;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.EditTableDataCore;
import com.huawei.mppdbide.presentation.IEditTableDataCore;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: EditTableDataFactory
 * 
 * Description:A factory for creating EditTableData objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class EditTableDataFactory {

    /**
     * Gets the edits the table data core.
     *
     * @param serverObject the server object
     * @return the edits the table data core
     */
    public static IEditTableDataCore getEditTableDataCore(ServerObject serverObject) {
        switch (serverObject.getType()) {
            case TABLEMETADATA:
            default: {
                return new EditTableDataCore();
            }
        }

    }

    /**
     * Gets the edits the table data UI initializer.
     *
     * @param object the object
     * @return the edits the table data UI initializer
     */
    public static void getEditTableDataUIInitializer(ServerObject object) {
        if (UIElement.getInstance().isWindowLimitReached()) {
            UIElement.getInstance().openMaxSourceViewerDialog();
            return;
        }
        ISelection selection = null;
        if (null == object) {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {

                selection = objectBrowserModel.getTreeViewer().getSelection();
            }
            if (null != selection) {
                if (selection instanceof IStructuredSelection) {
                    object = (ServerObject) ((IStructuredSelection) selection).getFirstElement();
                }
            }
        }
        if (object == null) {
            return;
        }
        IEditTableDataCore core = EditTableDataFactory.getEditTableDataCore(object);
        core.init(object);
        Object editTableDataWindow = UIElement.getInstance().findWindowAndActivate(core.getWindowDetails());
        if (editTableDataWindow != null) {
            // The window is already open so do nothing.
            return;
        }
        if (object instanceof TableMetaData) {
            EditTableData editTableData = new EditTableData();
            editTableData.excuteEdit(object, core);
        }
    }
}
