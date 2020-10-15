/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.objectbrowser;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserObjectRefreshPresentation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ObjectBrowserObjectRefreshPresentation {

    /**
     * Refresh sever object.
     *
     * @param obj the obj
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void refreshSeverObject(ServerObject obj)
            throws DatabaseCriticalException, DatabaseOperationException {
        if (obj instanceof TableMetaData) {
            TableMetaData tbl = (TableMetaData) obj;
            Namespace ns = tbl.getNamespace();
            tbl.getDatabase().setLoadingNamespaceInProgress(true);
            ns.refreshTable(tbl, tbl.getDatabase().getConnectionManager().getObjBrowserConn(), false);
            tbl.getDatabase().setLoadingNamespaceInProgress(false);
        }
        if (obj instanceof ViewMetaData) {
            ViewMetaData view = (ViewMetaData) obj;
            Namespace ns = view.getNamespace();
            view.getDatabase().setLoadingNamespaceInProgress(true);
            ns.refreshView(view, view.getDatabase().getConnectionManager().getObjBrowserConn(), false);
            view.getDatabase().setLoadingNamespaceInProgress(false);
        }

    }
}
