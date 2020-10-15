/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.search;

import org.eclipse.jface.viewers.TreeViewer;

import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.search.SearchObjCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SearchWorkerJob extends UIWorkerJob {
    private SearchObjCore searchCore;
    private IExecTimer timer;
    private StatusMessage statusMesg;
    private SearchWindow searchWindow;

    /**
     * Instantiates a new search worker job.
     *
     * @param jobName the job name
     * @param core the core
     * @param statMssage the stat mssage
     * @param searchWindow the search window
     */
    public SearchWorkerJob(String jobName, SearchObjCore core, StatusMessage statMssage, SearchWindow searchWindow) {
        super(jobName, MPPDBIDEConstants.CANCELABLEJOB);
        this.searchCore = core;
        this.statusMesg = statMssage;
        this.searchWindow = searchWindow;
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
        timer = new ExecTimer("Search OB");
        timer.start();
        searchCore.search();

        timer.stop();
        searchCore.setExecutionTime(timer.getElapsedTime());
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        // check result window is open if Yes then Close it
        TreeViewer resultViewer = searchWindow.getResultViewer();
        if (!resultViewer.getControl().isDisposed()) {
            resultViewer.setContentProvider(new SearchResultContentProvider());
            resultViewer.setInput(searchCore.getSearchedDatabase().getAllSearchNameSpaces());
            resultViewer.setExpandedState(searchCore.getSearchNamespace(), true);
            resultViewer.refresh();
            searchWindow.printStatus();
            searchCore.setSearchStatus(SearchObjectEnum.SEARCH_END);
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {

        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(exception.getServerMessage());

        msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(exception.getDBErrorMessage());
        UIElement.getInstance().bringConsoleWindowOntop();
        searchWindow.setErrorMsg(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OBJ_ERROR));
        MPPDBIDELoggerUtility.error(
                MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OBJ_ERROR, MPPDBIDEConstants.LINE_SEPARATOR),
                exception);
        // Delete the file on exception

        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, searchCore.getSelectedDb());

    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        UIElement.getInstance().bringConsoleWindowOntop();
        searchWindow.setErrorMsg(exception.getServerMessage());
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OBJ_ERROR), exception);
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {

        super.canceling();
        try {
            searchCore.cancelQuery();
            searchWindow.getResultViewer().setAutoExpandLevel(0);
            searchCore.setSearchStatus(SearchObjectEnum.SEARCH_END);
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("SearchWorkerJob: search operation cancel failed.", exception);
            UIElement.getInstance().bringConsoleWindowOntop();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OBJ_CANCEL_MSG)));
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("SearchWorkerJob: search operation cancel failed.", exception);
            UIElement.getInstance().bringConsoleWindowOntop();
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_OBJ_CANCEL_MSG)));
        }
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        searchCore.cleanUpSearch();

    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        searchCore.setSearchStatus(SearchObjectEnum.SEARCH_END);
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bttmStatusBar != null) {
            bttmStatusBar.hideStatusbar(this.statusMesg);
        }
        // stop Progress bar
    }

}
