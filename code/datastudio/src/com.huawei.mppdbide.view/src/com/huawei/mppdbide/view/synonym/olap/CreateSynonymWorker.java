/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.synonym.olap;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.synonym.olap.SynonymWrapper;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: Class
 * 
 * Description: The Class CreateSynonymWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author c00550043
 * @version
 * @since Mar 11, 2020
 */
public class CreateSynonymWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private SynonymWrapper synonymWrapper;

    public CreateSynonymWorker(String name, SynonymWrapper wrapper, String msg, IDialogWorkerInteraction dialog) {
        super(name, wrapper.getMetadata(), msg, dialog, MPPDBIDEConstants.CANCELABLEJOB);
        this.synonymWrapper = wrapper;
    }

    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return null;
    }

    @Override
    protected ServerObject getObjectBrowserRefreshItem() {
        return null;
    }

    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        synonymWrapper.executeComposeQuery(super.conn);
        synonymWrapper.refresh(super.conn);
        return null;
    }

}
