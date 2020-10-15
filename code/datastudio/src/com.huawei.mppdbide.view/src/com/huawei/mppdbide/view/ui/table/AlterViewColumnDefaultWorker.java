/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AlterViewColumnDefaultWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class AlterViewColumnDefaultWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private ViewColumnMetaData column;
    private String expression;

    /**
     * Instantiates a new alter view column default worker.
     *
     * @param name the name
     * @param view the view
     * @param exp the exp
     * @param msg the msg
     * @param dialogWorkerInteraction the dialog worker interaction
     */
    public AlterViewColumnDefaultWorker(String name, ViewColumnMetaData view, String exp, String msg,
            IDialogWorkerInteraction dialogWorkerInteraction) {
        super(name, view, msg, dialogWorkerInteraction, MPPDBIDEConstants.CANCELABLEJOB);
        this.column = view;
        this.expression = exp;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        Namespace ns = column.getParent().getNamespace();
        column.setDefaultValue(expression, conn);
        ns.refreshView(this.column.getParent(), conn, false);
        return null;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {

        return null;
    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {

        return null;
    }

}
