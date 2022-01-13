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

package com.huawei.mppdbide.view.workerjob;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.presentation.IUIWorkerJobNotifier;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.data.DSViewApplicationObjectManager;
import com.huawei.mppdbide.view.data.DSViewFactoryManager;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.progressmonitorif.ProgressMonitorControlIf;

/**
 * 
 * Title: UIWorkerJob
 * 
 * Description: A template for all UI worker job template. This abstract class
 * is aimed to improve the code readability and avoid complex coding of worker
 * thread.
 *
 * @since 3.0.0
 */
public abstract class UIWorkerJob extends Job implements IUIWorkerJobNotifier {
    private final Object familyObject;
    private boolean isCancel = false;
    private boolean isNotified;
    private Database taskDB;

    /**
     * The Constant SQL_TERMINAL_THREAD_SLEEP_TIME.in ms
     */
    protected static final int SQL_TERMINAL_THREAD_SLEEP_TIME = 10;

    /**
     * Instantiates a new UI worker job.
     *
     * @param name the name
     * @param family the family
     */
    public UIWorkerJob(String name, Object family) {
        super(name);
        familyObject = family;
        refreshProgressBar();
        this.setNotified(false);
    }

    /**
     * Run.
     *
     * @param monitor the monitor
     * @return the i status
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            UIPreHandler preHandler = new UIPreHandler(this, this.familyObject);
            Display.getDefault().syncExec(preHandler);
            if (!preHandler.isContinue()) {
                return Status.CANCEL_STATUS;
            }
            Object resultObj = doJob();
            Display.getDefault().syncExec(new UIHandler(this, resultObj));

        } catch (final DatabaseOperationException dbOperationException) {
            onOperationalExceptionAction(dbOperationException);
            UIExceptionHandler handler = new UIExceptionHandler(this, dbOperationException);
            Display.getDefault().syncExec(handler);
        } catch (final DatabaseCriticalException dbCriticalException) {
            onCriticalExceptionAction(dbCriticalException);
            UIExceptionHandler handler = new UIExceptionHandler(this, dbCriticalException);
            Display.getDefault().syncExec(handler);
        } catch (final MPPDBIDEException mppDbException) {
            onMPPDBIDEException(mppDbException);
            UIExceptionHandler handler = new UIExceptionHandler(this, mppDbException);
            Display.getDefault().syncExec(handler);
        } catch (final OutOfMemoryError error) {
            onOutOfMemoryError(error);
            onOutOfMemoryUIError(error);
        } catch (final Exception exception) {
            onException(exception);
            UIExceptionHandler handler = new UIExceptionHandler(this, exception);
            Display.getDefault().syncExec(handler);
        }

        finally {
            try {
                preFinalCleanup();
            } catch (MPPDBIDEException mppdbException) {
                MPPDBIDELoggerUtility.none("Nothing to do here");
            }
            try {
                finalCleanup();
            } catch (MPPDBIDEException mppdbException) {
                MPPDBIDELoggerUtility.none("Nothing to do here");
            }
            Display.getDefault().syncExec(new FinalizerUI(this));
        }

        return Status.OK_STATUS;
    }

    /**
     * On exception.
     *
     * @param exception the exception
     */
    public void onException(Exception exception) {

    }

    /**
     * On exception UI action.
     *
     * @param exception the exception
     */
    public void onExceptionUIAction(Exception exception) {

    }

    /**
     * On MPPDBIDE exception UI action.
     *
     * @param mppDbException the mpp db exception
     */
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException mppDbException) {

    }

    /**
     * On MPPDBIDE exception.
     *
     * @param mppDbException the mpp db exception
     */
    public void onMPPDBIDEException(MPPDBIDEException mppDbException) {

    }

    /**
     * Pre final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void preFinalCleanup() throws MPPDBIDEException {

    }

    /**
     * On out of memory UI error.
     *
     * @param error the error
     */
    public void onOutOfMemoryUIError(OutOfMemoryError error) {

    }

    /**
     * On out of memory error.
     *
     * @param error the error
     */
    public void onOutOfMemoryError(OutOfMemoryError error) {

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
    public abstract Object doJob()
            throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception;

    /**
     * On success UI action.
     *
     * @param object the object
     */
    public void onSuccessUIAction(Object object) {

    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the db critical exception
     */
    public abstract void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException);

    /**
     * On critical exception action.
     *
     * @param dbCriticalException the db critical exception
     */
    public void onCriticalExceptionAction(DatabaseCriticalException dbCriticalException) {
        // Do nothing... To be overridden by child on need basis
    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    public abstract void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException);

    /**
     * On operational exception action.
     *
     * @param dbOperationException the db operation exception
     */
    public void onOperationalExceptionAction(DatabaseOperationException dbOperationException) {
        // Do nothing... To be overridden by child on need basis
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void finalCleanup() throws MPPDBIDEException {

    }

    /**
     * Final cleanup UI.
     */
    public void finalCleanupUI() {

    }

    /**
     * Refresh progress bar.
     */
    protected void refreshProgressBar() {
        Display.getDefault().asyncExec(new RefreshProgressWindow());
    }

    /**
     * Belongs to.
     *
     * @param family the family
     * @return true, if successful
     */
    @Override
    public boolean belongsTo(Object family) {
        return family == familyObject;
    }

    /**
     * Cancel job.
     *
     * @return true, if successful
     */
    public boolean cancelJob() {
        this.isCancel = true;
        super.cancel();
        return this.isCancel;
    }

    /**
     * Checks if is cancel.
     *
     * @return true, if is cancel
     */
    public boolean isCancel() {
        return this.isCancel;
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        cancelJob();
    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    public boolean preUISetup(Object preHandlerObject) {
        return true;
    }

    /**
     * Pre setup on UI has to be done on this thread.
     *
     */
    private static final class UIPreHandler implements Runnable {
        private UIWorkerJob uiWorkerJob;
        private Object preHandlerObject;
        private boolean isContinue;

        private UIPreHandler(UIWorkerJob workerJob, Object preHandlerObj) {
            this.uiWorkerJob = workerJob;
            this.preHandlerObject = preHandlerObj;
            // By default continue
            this.isContinue = true;
        }

        @Override
        public void run() {
            this.isContinue = this.uiWorkerJob.preUISetup(this.preHandlerObject);
        }

        /**
         * Checks if is continue.
         *
         * @return true, if is continue
         */
        public boolean isContinue() {
            return isContinue;
        }
    }

    private static final class UIHandler implements Runnable {
        private UIWorkerJob uiHandlerJob;
        private Object resultObj;

        private UIHandler(UIWorkerJob uiHandlerJob, Object resultObj) {
            this.uiHandlerJob = uiHandlerJob;
            this.resultObj = resultObj;
        }

        @Override
        public void run() {
            uiHandlerJob.onSuccessUIAction(resultObj);
        }
    }

    private static final class FinalizerUI implements Runnable {
        private UIWorkerJob finalizerJob;

        private FinalizerUI(UIWorkerJob finalizerJob) {
            this.finalizerJob = finalizerJob;
        }

        @Override
        public void run() {
            finalizerJob.finalCleanupUI();
        }
    }

    private static final class UIExceptionHandler implements Runnable {
        private Exception exception;
        private UIWorkerJob uiExcepJob;

        private UIExceptionHandler(UIWorkerJob job, Exception ex) {
            this.uiExcepJob = job;
            this.exception = ex;
        }

        @Override
        public void run() {
            if (exception instanceof DatabaseOperationException) {
                DatabaseOperationException excep = (DatabaseOperationException) exception;
                uiExcepJob.onOperationalExceptionUIAction(excep);
            } else if (exception instanceof DatabaseCriticalException) {
                DatabaseCriticalException excep = (DatabaseCriticalException) exception;
                uiExcepJob.onCriticalExceptionUIAction(excep);
            } else if (exception instanceof MPPDBIDEException) {
                MPPDBIDEException excep = (MPPDBIDEException) exception;
                uiExcepJob.onMPPDBIDEExceptionUIAction(excep);
            } else {
                Exception excep = exception;
                uiExcepJob.onExceptionUIAction(excep);
            }
        }
    }

    private static final class RefreshProgressWindow implements Runnable {
        @Override
        public void run() {
            ProgressMonitorControlIf monitorControl = getprogressBarModel();
            if (monitorControl != null) {
                monitorControl.refreshJobs();
            }

        }
    }

    private static ProgressMonitorControlIf getprogressBarModel() {
        MPart part = DSViewFactoryManager.getDSViewApplicationObjectManager().getPartService()
                .findPart(UIConstants.UI_PART_PROGRESSBAR_ID);
        if (null == part) {
            return null;
        }
        if (null == part.getObject()) {
            DSViewApplicationObjectManager.getInstance().getPartService().activate(part);
            part.setVisible(false);
        }

        if (!(part.getObject() instanceof ProgressMonitorControlIf)) {
            return null;
        }
        return (ProgressMonitorControlIf) part.getObject();
    }

    /**
     * Sets the task DB.
     *
     * @param taskDB the new task DB
     */
    public void setTaskDB(Database taskDB) {
        this.taskDB = taskDB;
    }

    /**
     * Gets the task DB.
     *
     * @return the task DB
     */
    public Database getTaskDB() {
        return taskDB;
    }

    /**
     * Checks if is notified.
     *
     * @return true, if is notified
     */
    public boolean isNotified() {
        return isNotified;
    }

    /**
     * Sets the notified.
     *
     * @param notified the new notified
     */
    @Override
    public void setNotified(boolean notified) {
        this.isNotified = notified;
    }

    /**
     * Sets the cancelled.
     *
     * @param cancel the new cancelled
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelJob();
    }
}
