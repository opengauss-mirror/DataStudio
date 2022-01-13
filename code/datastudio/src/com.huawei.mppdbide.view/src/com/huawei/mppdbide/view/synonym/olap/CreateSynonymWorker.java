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
 * @since 3.0.0
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
