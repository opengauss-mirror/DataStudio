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

package org.opengauss.mppdbide.view.search;

import org.eclipse.jface.viewers.TreeViewer;

import org.opengauss.mppdbide.bl.search.SearchObjectEnum;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.search.SearchObjCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SearchWorkerJob.
 *
 * @since 3.0.0
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
