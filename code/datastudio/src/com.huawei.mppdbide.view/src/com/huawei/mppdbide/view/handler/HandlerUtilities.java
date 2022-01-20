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

package com.huawei.mppdbide.view.handler;

import org.eclipse.jface.text.Document;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ISourceCode;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.common.SourceViewerUtil;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class HandlerUtilities.
 *
 * @since 3.0.0
 */
public class HandlerUtilities {
    private static IDebugObject oldDebugObject = null;
    private static int formatSign;
    private static ServerConnectionInfo serverInfo; // Debug
    private static IQueryResult resultHandler; // Connection
    private static boolean canExport = true;
    private static final Object LOCK = new Object();

    /**
     * Display source code in editor.
     *
     * @param sqlObject the sql object
     * @param isReloadAgain the is reload again
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public static void displaySourceCodeInEditor(IDebugObject sqlObject, boolean isReloadAgain)
            throws DatabaseOperationException, DatabaseCriticalException {

        if (null == sqlObject) {
            IHandlerUtilities.refreshObjectBrowserTree();
            return;
        }
        PLSourceEditor editorObject = UIElement.getInstance().createEditor(sqlObject);
        try {
            HandlerUtilities.codeForHandler(sqlObject, isReloadAgain);
        } catch (DatabaseCriticalException exp) {
            SourceViewerUtil.removeSourceViewerId(sqlObject.getPLSourceEditorElmId(), sqlObject.getObjectType());
            throw exp;
        }
        if (null != editorObject) {
            selectVisibleDebugObject(sqlObject, editorObject);
            editorObject.displaySourceForDebugObject(sqlObject);
            editorObject.registerModifyListener();
        }
    }

    /**
     * Display refresh source code in editor.
     *
     * @param sqlObject the sql object
     * @param editor the editor
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void displayRefreshSourceCodeInEditor(IDebugObject sqlObject, PLSourceEditor editor)
            throws DatabaseCriticalException {
        if (null == sqlObject) {
            IHandlerUtilities.refreshObjectBrowserTree();
            return;
        }

        // Setting back Breakpoints
        Document doc = new Document(sqlObject.getSourceCode().getCode());
        selectVisibleDebugObject(sqlObject, editor);
        editor.displaySourceForDebugObject(sqlObject);
        editor.registerModifyListener();
    }

    /**
     * Display source code while refresh.
     *
     * @param sqlObject the sql object
     * @param isAskUserBeforeRefresh the is ask user before refresh
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void displaySourceCodeWhileRefresh(IDebugObject sqlObject, boolean isAskUserBeforeRefresh)
            throws DatabaseOperationException, DatabaseCriticalException {

        if (null == sqlObject) {
            IHandlerUtilities.refreshObjectBrowserTree();
            return;
        }
        PLSourceEditor editorObject = UIElement.getInstance().getEditorById(sqlObject, false);
        HandlerUtilities.codeForHandler(sqlObject);

        if (null != editorObject) {
            editorObject.registerModifyListener();
            if (isAskUserBeforeRefresh && editorObject.isObjDirty()) {
                // Ask user with yes or no
                int res = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_DBG_OBJ_TITLE), MessageConfigLoader
                                .getProperty(IMessagesConstants.UPDATE_DBG_OBJ_QUESTION, sqlObject.getDisplayName()));
                if (UIConstants.OK_ID == res) {
                    editorObject.displaySourceForDebugObject(sqlObject);
                    editorObject.setModified(false);
                    editorObject.setModifiedAfterCreate(false);
                    if (editorObject.getSourceEditorCore() != null) {
                        editorObject.getSourceEditorCore()
                                .setDocument(new Document(sqlObject.getSourceCode().getCode()), 0);
                        editorObject.registerModifyListener();
                    }
                } else {
                    editorObject.setDebugObject(sqlObject);
                }
            } else {
                editorObject.displaySourceForDebugObject(sqlObject);
                editorObject.setModified(false);
                editorObject.setModifiedAfterCreate(false);
            }
        }
    }

    /**
     * Code for handler.
     *
     * @param sqlObject the sql object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void codeForHandler(IDebugObject sqlObject)
            throws DatabaseOperationException, DatabaseCriticalException {
        codeForHandler(sqlObject, false);
    }

    /**
     * Code for handler.
     *
     * @param sqlObject the sql object
     * @param isReloadAgain the is reload again
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void codeForHandler(IDebugObject sqlObject, boolean isReloadAgain)
            throws DatabaseOperationException, DatabaseCriticalException {
        getSourceForDbgObj(sqlObject, isReloadAgain);
      
        ObjectBrowser browser = UIElement.getInstance().getObjectBrowserModel();
        if (null != browser && sqlObject != null) {
            browser.refreshObject(sqlObject.getNamespace().getFunctions());
            ;
        }
    }

    /**
     * Select visible debug object.
     *
     * @param sqlObject the sql object
     * @param editorObject the editor object
     */
    public static void selectVisibleDebugObject(IDebugObject sqlObject, PLSourceEditor editorObject) {
        synchronized (LOCK) {
            if (null != editorObject) {
                if ((null == oldDebugObject || sqlObject.getOid() != oldDebugObject.getOid())
                        && UIElement.getInstance().isObjectBrowserPartOpen()) {
                    ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
                    if (null != objectBrowser) {
                        objectBrowser.findAndSelectConnectionProfile(sqlObject);
                        oldDebugObject = sqlObject;
                    }
                }
            }
        }
    }

    /**
     * Gets the source for dbg obj.
     *
     * @param sqlObject the sql object
     * @return the source for dbg obj
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static ISourceCode getSourceForDbgObj(IDebugObject sqlObject)
            throws DatabaseOperationException, DatabaseCriticalException {
        return getSourceForDbgObj(sqlObject, false);
    }

    /**
     * Gets the source for dbg obj.
     *
     * @param sqlObject the sql object
     * @param isReloadAgain the is reload again
     * @return the source for dbg obj
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static ISourceCode getSourceForDbgObj(IDebugObject sqlObject, boolean isReloadAgain)
            throws DatabaseOperationException, DatabaseCriticalException {
        synchronized (LOCK) {
            if (null != sqlObject) {
                ISourceCode code = sqlObject.getSourceCode();
                /*
                 * isReloadAgain - For ViewSource,if editor is not open, reload
                 * the source code from server and display the latest code when
                 * clicked on ViewSource instead of the cache copy
                 */
                if (isReloadAgain || null == code || null == code.getCode()) {
                    MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_FETCH_SRC_CODE, true);

                    try {
                        sqlObject.refreshSourceCode();
                    } catch (DatabaseOperationException e) {
                        if (e.getMessage().equalsIgnoreCase(
                                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE))) {
                            // We expect the debug object is deleted.
                            ObjectBrowserStatusBarProvider.getStatusBar()
                                    .displayMessage(Message.getError(MessageConfigLoader.getProperty(
                                            IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE_HANDLER,
                                            sqlObject.getDisplayName(false))));

                        }

                        throw e;
                    } finally {
                        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_FETCH_SRC_CODE, false);
                    }
                }

                return sqlObject.getSourceCode();
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the server info.
     *
     * @return the server info
     */
    public static ServerConnectionInfo getServerInfo() {
        return serverInfo;
    }

    /**
     * Sets the server info.
     *
     * @param serverInfo the new server info
     */
    public static void setServerInfo(ServerConnectionInfo serverInfo) {
        HandlerUtilities.serverInfo = serverInfo;
    }

    /**
     * Display source code in editor from UI.
     *
     * @param sqlObject the sql object
     * @param isAskUserBeforeRefresh the is ask user before refresh
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void displaySourceCodeInEditorFromUI(final IDebugObject sqlObject, boolean isAskUserBeforeRefresh)
            throws DatabaseOperationException, DatabaseCriticalException {
        try {
            HandlerUtilities.getSourceForDbgObj(sqlObject);
        } catch (DatabaseOperationException e) {
            IHandlerUtilities.handleGetSrcCodeException(sqlObject);
            return;
        } catch (DatabaseCriticalException e) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e, sqlObject.getDatabase());
            return;
        }

        HandlerUtilities.displaySourceCodeWhileRefresh(sqlObject, isAskUserBeforeRefresh);
    }

}
