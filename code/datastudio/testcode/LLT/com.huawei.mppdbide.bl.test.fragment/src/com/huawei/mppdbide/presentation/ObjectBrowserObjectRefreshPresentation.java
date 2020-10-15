package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

public class ObjectBrowserObjectRefreshPresentation
{
    public static void refreshSeverObject(ServerObject obj)
            throws DatabaseCriticalException, DatabaseOperationException
    {
        if (obj instanceof TableMetaData)
        {
            TableMetaData tbl = (TableMetaData) obj;
            Namespace ns = tbl.getNamespace();
            tbl.getDatabase().setLoadingNamespaceInProgress(true);
            ns.refreshTable(tbl, tbl.getDatabase().getConnectionManager().getObjBrowserConn(), false);
            tbl.getDatabase().setLoadingNamespaceInProgress(false);
        }
        else if (obj instanceof ViewMetaData)
        {
            ViewMetaData view = (ViewMetaData) obj;
            Namespace ns = view.getNamespace();
            view.getDatabase().setLoadingNamespaceInProgress(true);
            ns.refreshView(view, view.getDatabase().getConnectionManager().getObjBrowserConn(), false);
            view.getDatabase().setLoadingNamespaceInProgress(false);
        }
    }
}
