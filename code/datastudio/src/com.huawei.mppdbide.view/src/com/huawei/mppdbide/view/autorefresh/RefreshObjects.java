/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.autorefresh;

import java.util.Locale;

import org.eclipse.jface.viewers.TreeViewer;

import com.huawei.mppdbide.bl.serverdatacache.groups.FilterObject;
import com.huawei.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;

/**
 * Title: RefreshObjects
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 03-Feb-2020]
 * @since 03-Feb-2020
 */

public class RefreshObjects {
    /**
     * Refresh objects in tree viewer.
     *
     * @param objectDetail the object detail
     * @param viewer the viewer
     */
    public static void refreshObjectsInTreeViewer(RefreshObjectDetails objectDetail, TreeViewer viewer) {
        String operationType = objectDetail.getOperationType();
        if (null == operationType) {
            return;
        }
        switch (operationType) {
            case "CREATE_TABLE":
            case "CREATE_FUNC_PROC":
            case "CREATE_VIEW": {
                String text = FilterObject.getInstance().getFilterText();
                if ((text == null) || (objectDetail.getObjToBeRefreshed().getName().toLowerCase(Locale.ENGLISH)
                        .contains(text.toLowerCase(Locale.ENGLISH)))) {
                    viewer.add(objectDetail.getParent(), objectDetail.getObjToBeRefreshed());
                    viewer.update(objectDetail.getParent(), null);
                }
                break;
            }
            case "ALTER_TABLE":
                // fall through
            case "ALTER_VIEW": {
                viewer.refresh(objectDetail.getObjToBeRefreshed());
                break;
            }
            case "DROP_TABLE":
                // fall through
            case "DROP_VIEW": {
                viewer.remove(objectDetail.getObjToBeRefreshed());
                viewer.update(objectDetail.getParent(), null);
                break;
            }
            default: {
                break;
            }
        }
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.AUTO_REFRESH_SUCCESS_MSG)));
    }
}
