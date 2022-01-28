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
 * @since 3.0.0
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
            case "CREATE_VIEW":
            case "CREATE_TRIGGER": {
                String text = FilterObject.getInstance().getFilterText();
                if ((text == null) || (objectDetail.getObjToBeRefreshed().getName().toLowerCase(Locale.ENGLISH)
                        .contains(text.toLowerCase(Locale.ENGLISH)))) {
                    viewer.add(objectDetail.getParent(), objectDetail.getObjToBeRefreshed());
                    viewer.update(objectDetail.getParent(), null);
                    if ("CREATE_TRIGGER".equals(operationType) || "CREATE_VIEW".equals(operationType)) {
                        viewer.refresh();
                    }
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
