/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
