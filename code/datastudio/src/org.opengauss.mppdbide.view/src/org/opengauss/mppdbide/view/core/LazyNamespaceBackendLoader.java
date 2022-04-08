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

package org.opengauss.mppdbide.view.core;

import java.util.List;

import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.ILazyLoadObject;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.handler.HandlerUtilities;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyNamespaceBackendLoader.
 *
 * @since 3.0.0
 */
public class LazyNamespaceBackendLoader extends LazyBackendLoader {
    private List<ILazyLoadObject> namespaces;
    private ILazyLoadObject currentNameSpace = null;
    private JobCancelStatus status = null;

    /**
     * Instantiates a new lazy namespace backend loader.
     *
     * @param name the name
     * @param toBeLoaded the to be loaded
     * @param statusMsg the status msg
     * @param objectName the object name
     * @param timer the timer
     */
    LazyNamespaceBackendLoader(String name, List<ILazyLoadObject> toBeLoaded, StatusMessage statusMsg,
            String objectName, IExecTimer timer) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, statusMsg, timer);
        this.namespaces = toBeLoaded;
        this.status = new JobCancelStatus();
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
        for (ILazyLoadObject namespace : namespaces) {
            currentNameSpace = namespace;
            setDb(namespace.getDatabase());
            setConn(getDb().getConnectionManager().getObjBrowserConn());
            getDb().setLoadingNamespaceInProgress(true);
            namespace.getAllObjects(getConn(), status);
            refreshSourceEditorUIAction(currentNameSpace);
        }
        // Namespace load flag set by getAllObjects(). No need to set again.
        return null;
    }

    /**
     * Refresh source editor UI action.
     *
     * @param object the object
     */
    public void refreshSourceEditorUIAction(final Object object) {
        Display.getDefault().asyncExec(new Runnable() {
            private PLSourceEditor editorUIObject;
            private IDebugObject editorObject;

            @Override
            public void run() {
                ILazyLoadObject ns = (ILazyLoadObject) object;
                try {
                    List<PLSourceEditor> plSourceEditors = UIElement.getInstance().getAllOpenedSourceViewer();
                    int noOfEditors = plSourceEditors.size();
                    int cnt = 0;
                    for (; cnt < noOfEditors; cnt++) {
                        editorUIObject = plSourceEditors.get(cnt);
                        editorObject = editorUIObject.getDebugObject();

                        if (null == editorObject || editorObject.getNamespace() == null
                                || editorObject.getNameSpaceId() != ns.getOid()) {
                            continue;
                        }

                        handleNamespace(getDb(), editorObject);

                    }
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("Database operation exception occured while plsourceviewer refresh  ..",
                            exception);
                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("Database critical exception occured while plsourceviewer refresh  ..",
                            exception);
                }
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(ns.getServer());
            }
        });

    }

    /**
     * Handle namespace.
     *
     * @param db the db
     * @param editorObject the editor object
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private boolean handleNamespace(Database db, IDebugObject editorObject)
            throws DatabaseOperationException, DatabaseCriticalException {

        if (null != editorObject) {
            IDebugObject newSqlObject = db.getDebugObjectById(editorObject.getOid(), editorObject.getNameSpaceId());

            if (newSqlObject != null) {
                HandlerUtilities.displaySourceCodeInEditorFromUI(newSqlObject, true);
            }
        }

        return true;
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        if (!status.getCancel()) {
            status.setCancel(true);
            DBConnection conn = getConn();
            if (null != conn) {
                try {
                    conn.cancelQuery();
                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("Database critical exception occured while cancel query...", exception);
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("Database operation exception occured while cancel query...",
                            exception);
                }
            }
        }
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    @Override
    String getErrorMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.NAMESPACE_RETRIVE_ERROR);
    }

    /**
     * Sets the load failed.
     */
    @Override
    void setLoadFailed() {
        currentNameSpace.setLoadFailed();
        super.setLoadFailed();
    }

    /**
     * Gets the message dialog title.
     *
     * @return the message dialog title
     */
    @Override
    String getMessageDialogTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ERR_TITLE_DB_CRITICAL_ERROR);
    }

    /**
     * Gets the message dialog message.
     *
     * @return the message dialog message
     */
    @Override
    String getMessageDialogMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.NAMESPACE_RETRIVE_ERROR, currentNameSpace.getName());
    }

}
