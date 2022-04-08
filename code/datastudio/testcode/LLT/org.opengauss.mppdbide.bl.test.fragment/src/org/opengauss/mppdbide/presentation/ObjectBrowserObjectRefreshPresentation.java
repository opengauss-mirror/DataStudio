package org.opengauss.mppdbide.presentation;

import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

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
