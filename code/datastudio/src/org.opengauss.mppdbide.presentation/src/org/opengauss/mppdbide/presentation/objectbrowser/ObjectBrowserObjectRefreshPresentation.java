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

package org.opengauss.mppdbide.presentation.objectbrowser;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectBrowserObjectRefreshPresentation.
 * 
 * @since 3.0.0
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
