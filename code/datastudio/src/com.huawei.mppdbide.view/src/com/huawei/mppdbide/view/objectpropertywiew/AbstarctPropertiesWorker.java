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

package com.huawei.mppdbide.view.objectpropertywiew;

import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstarctPropertiesWorker.
 *
 * @since 3.0.0
 */
public abstract class AbstarctPropertiesWorker extends PromptPasswordUIWorkerJob {

    /**
     * The data provider.
     */
    protected IDSGridDataProvider dataProvider;

    /**
     * Instantiates a new abstarct properties worker.
     *
     * @param name the name
     * @param family the family
     * @param errorWindowTitle the error window title
     * @param dataProvider the data provider
     */
    public AbstarctPropertiesWorker(String name, Object family, String errorWindowTitle,
            IDSGridDataProvider dataProvider) {
        super(name, family, errorWindowTitle);
        this.dataProvider = dataProvider;
    }

    /**
     * Exception event call.
     *
     * @param exception the e
     */
    protected abstract void exceptionEventCall(MPPDBIDEException exception);

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
        exceptionEventCall(e);

    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException e) {
        exceptionEventCall(e);
    }

    /**
     * On MPPDBIDE exception.
     *
     * @param exception the e
     */
    @Override
    public void onMPPDBIDEException(MPPDBIDEException exception) {
        exceptionEventCall(exception);
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        try {
            ((IDSEditGridDataProvider) dataProvider).setCancel(true);

            ((IDSEditGridDataProvider) dataProvider).cancelCommit();
        } catch (DatabaseCriticalException e) {

            exceptionEventCall(e);
        } catch (DatabaseOperationException e) {
            exceptionEventCall(e);
        }

    }

}
