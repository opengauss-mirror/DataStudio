/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.uidisplay;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.contentassist.ContentAssistKeywords;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerUtil;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.UIVersionHandler;
import com.huawei.mppdbide.view.handler.connection.PasswordDialog;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import com.huawei.mppdbide.view.utils.TerminalStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIDisplayState.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class UIDisplayState implements UIDisplayStateIf {
    @Inject
    private ECommandService commandService;
    @Inject
    private EHandlerService handlerService;

    private static volatile UIDisplayState instance = null;

    private ConnectionProfileId connectedProfileId;
    private StatusMessage statusMessage;
    private boolean isDisclaimerReq = true;
    private boolean isSSLcheck = true;

    private static final Object LOCK = new Object();
    private boolean isReturnFromServer = true;
    private boolean debugInProgressOnNewEditor;
    private TerminalStatusBar prevStatusBar;

    /**
     * Clear sql object.
     */
    public void clearSqlObject() {
        UIElement.getInstance().resetSourceViewerEditable();
        setPlViewerConfig();
    }

    private void setPlViewerConfig() {
        PLSourceEditor plEditor = UIElement.getInstance().getVisibleSourceViewer();
        if (null != plEditor) {
            plEditor.setSourceViewerConfiguration();
        }

    }

    /**
     * Private constructor because this class is a singleton.
     */
    private UIDisplayState() {
    }

    /**
     * Gets the insta display state.
     *
     * @return the insta display state
     */
    public static UIDisplayState getInstaDisplayState() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new UIDisplayState();
                }
            }
        }
        return instance;
    }

    /**
     * Checks if is version compatibile.
     *
     * @return true, if is version compatibile
     */
    public boolean isVersionCompatibile() {
        return UIVersionHandler.isVersionCompatible();
    }

    /**
     * Sets the version compatibile.
     *
     * @param modelService the model service
     * @param application the application
     */
    public void setVersionCompatibile(EModelService modelService, MApplication application) {
        hideAllPartStacks(modelService, application);

    }

    /**
     * Gets the checks if is return from server.
     *
     * @return the checks if is return from server
     */
    public boolean getIsReturnFromServer() {
        return isReturnFromServer;
    }

    /**
     * Sets the return from server.
     *
     * @param returnFromServer the new return from server
     */
    public void setReturnFromServer(boolean returnFromServer) {
        this.isReturnFromServer = returnFromServer;
    }

    private void hideAllPartStacks(EModelService modelService, MApplication application) {
        String[] uiPartNames = new String[] {UIConstants.UI_PARTSTACK_OBJECTBROWSER, UIConstants.UI_PARTSTACK_EDITOR};
        int partsLength = uiPartNames.length;
        int cnt = 0;
        MUIElement uiElement = null;
        for (; cnt < partsLength; cnt++) {
            uiElement = modelService.find(uiPartNames[cnt], application);
            if (null != uiElement) {
                uiElement.setVisible(false);
            }
        }
    }

    /**
    /**
     * Handle DB critical error.
     *
     * @param exception the exception
     * @param db the db
     */
    public void handleDBCriticalError(final MPPDBIDEException exception, final Database db) {

        StringBuilder msgStr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if ("Java heap space".equals(exception.getServerMessage())) {
            msgStr.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        } else if ((MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED))
                .equalsIgnoreCase(exception.getDBErrorMessage())) {
            msgStr.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        } else {
            if (exception.getMessage() != null && !exception.getMessage().trim().isEmpty()
                    && !MessageConfigLoader.UNKNOWN_ERROR.equals(exception.getMessage())) {
                msgStr.append(exception.getMessage()).append(System.lineSeparator());
            }

            msgStr.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR))
                    .append(System.lineSeparator()).append(System.lineSeparator());

            if (exception.getServerMessage() != null && !exception.getServerMessage().trim().isEmpty()) {
                msgStr.append(exception.getServerMessage()).append(System.lineSeparator());
            }

        }
        final String message = msgStr.toString();

        handleUiActivitiesOnDbCriticalException(exception, db, message);
    }

    private void handleUiActivitiesOnDbCriticalException(final MPPDBIDEException exception, final Database db,
            final String message) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                displayErrorMsgOnCriticalException(exception, message);

                MPPDBIDELoggerUtility.fatal("GUI: UIDisplayState: Closing all the connection profiles.", exception);

                DBConnProfCache.getInstance().destroyConnection(db);

                resetConnectedProfileId();
                refreshServerOnCriticalException(db);

                cleanupUIItems();
                doRefreshObjectBroswer(db);
            }

        });
    }

    private void refreshServerOnCriticalException(final Database db) {
        if (null != db) {
            if (!db.getServer().isAleastOneDbConnected()) {
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                disconnectAllDBCleanup(db.getServer());
                if (objectBrowserModel != null) {
                    objectBrowserModel.refreshObject(db.getServer());
                }
            }
            ConsoleCoreWindow.getInstance()
                    .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                            db.getServer().getServerConnectionInfo().getConectionName(), db.getName()));
        }
    }

    /**
     * Display error msg on critical exception.
     *
     * @param exception the exception
     * @param message the message
     */
    private void displayErrorMsgOnCriticalException(final MPPDBIDEException exception, final String message) {
        if ((MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED))
                .equalsIgnoreCase(exception.getDBErrorMessage())) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        } else {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_TITLE_DB_CRITICAL_ERROR), message);
        }
    }

    private void disconnectAllDBCleanup(Server server) {
        server.close();
        ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(server.getName());
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                .getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_ALL_DB, server.getName())));
        MPPDBIDELoggerUtility.info("Disconnected all dbs.");
    }

    /**
     * Handle server critical error.
     *
     * @param mppException the mpp exception
     * @param server the server
     */
    public void handleServerCriticalError(final MPPDBIDEException mppException, final Server server) {
        StringBuilder msgString = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        if ("Java heap space".equals(mppException.getServerMessage())) {
            msgString.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        } else {
            msgString.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR));
        }
        final String msg = msgString.toString();

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_TITLE_DB_CRITICAL_ERROR), msg);

                MPPDBIDELoggerUtility.fatal("GUI: UIDisplayState: Closing all the connection profiles.", mppException);

                Iterator<Database> dbItr = null;

                if (server != null) {

                    dbItr = server.getAllDatabases().iterator();

                } else {

                    return;
                }

                boolean hasNextDB = dbItr.hasNext();
                Database db = null;
                while (hasNextDB) {
                    db = dbItr.next();
                    DBConnProfCache.getInstance().destroyConnection(db);
                    hasNextDB = dbItr.hasNext();
                }

                resetConnectedProfileId();
                if (null != db) {
                    ConsoleCoreWindow.getInstance()
                            .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECT_ALL_DB,
                                    db.getServer().getServerConnectionInfo().getConectionName()));
                }
                cleanupUIItems();
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(server);
                ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                if (objectBrowserModel != null) {
                    objectBrowserModel.refreshObject(server);
                }
            }
        });
    }

    /**
     * Cleanup on database disconnect.
     *
     * @param database the database
     * @param server the server
     * @return true, if successful
     */
    public void cleanupOnDatabaseDisconnect(final Database database, Server server) {
        ContentAssistKeywords.getInstance().clear();
        closeAllDBRelatedPLSourceViewers(database, server);
    }

    private void closeAllDBRelatedPLSourceViewers(final Database database, Server server) {
        List<PLSourceEditor> openPLs = UIElement.getInstance().getAllOpenedSourceViewer();
        if (openPLs.size() != 0) {
            for (final PLSourceEditor pl : openPLs) {
                if (pl.getDebugObject().belongsTo(database, server) && !pl.isObjDirty()) {
                    clearSqlObject();
                    pl.refreshAnnotations();
                    pl.destroySourceViewer();
                }
            }
        }
    }


    /**
     * Cleanup on server removal.
     *
     * @param server the server
     * @return true, if successful
     */
    public boolean cleanupOnServerRemoval(Server server) {
        Iterator<Database> databases = null;
        Database database = null;
        boolean dbHasNext = false;
        databases = server.getAllDatabases().iterator();
        dbHasNext = databases.hasNext();
        while (dbHasNext) {
            database = databases.next();
            if (database.isConnected()) {
                cleanupOnDatabaseDisconnect(database, server);

                IHandlerUtilities.cleanupAllJobsInDB(database);
            }
            UIElement.getInstance().disablePartInStack(database);
            dbHasNext = databases.hasNext();
        }
        return true;
    }

    /**
     * deleteSecurityFolderFromProfile
     */
    public void deleteSecurityFolderFromProfile(Server server) {
        IServerConnectionInfo info = server.getServerConnectionInfo();
        if (info.getSavePrdOption() != SavePrdOptions.PERMANENTLY) {
            SecureUtil sec = new SecureUtil();
            String profilePath = ConnectionProfileManagerImpl.getInstance().getProfilePath(info);
            sec.setPackagePath(profilePath);
            sec.deleteSecurityFolder();
        }
    }

    /**
     * Cleanup UI items.
     */
    public void cleanupUIItems() {
        Server server = null;
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_UIDISPLAYSTATE_CLEANING_UP_UI_ELEMENTS));

        // Dont remove this. This is checked for deciding
        // Critical exception handling.

        boolean isOneDBConnected = false;
        Iterator<Database> databases = null;
        Database database = null;
        boolean dbHasNext = false;

        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        boolean serverHasNext = servers.hasNext();

        while (serverHasNext) {
            server = servers.next();
            databases = server.getAllDatabases().iterator();
            dbHasNext = databases.hasNext();
            while (dbHasNext) {
                database = databases.next();
                if (database.isConnected()) {
                    isOneDBConnected = true;
                    break;
                }
                dbHasNext = databases.hasNext();
            }
            if (isOneDBConnected) {
                break;
            }
            serverHasNext = servers.hasNext();
        }

        UIElement.getInstance().resetAllSQLTerminalConnections();
        UIElement.getInstance().closeAllSourceViewer();

        cleanupDebugItems();

    }

    // DTS2014102908502 end
    /**
     * Cleanup debug items.
     */
    public void cleanupDebugItems() {
        setPlViewerConfig();
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Sets the connected profile id.
     *
     * @param profileId the new connected profile id
     */
    public void setConnectedProfileId(ConnectionProfileId profileId) {
        this.connectedProfileId = profileId;
    }

    /**
     * Reset connected profile id.
     */
    public void resetConnectedProfileId() {
        this.connectedProfileId = null;
    }

    /**
     * Gets the connected profile id.
     *
     * @return the connected profile id
     */
    public ConnectionProfileId getConnectedProfileId() {
        return this.connectedProfileId;
    }

    /**
     * Checks if is disclaimer req.
     *
     * @return true, if is disclaimer req
     */
    public boolean isDisclaimerReq() {
        return isDisclaimerReq;
    }

    /**
     * Sets the disclaimer req.
     *
     * @param isDisclaimrReq the new disclaimer req
     */
    public void setDisclaimerReq(boolean isDisclaimrReq) {
        this.isDisclaimerReq = isDisclaimrReq;
    }

    /**
     * Sets the SS loff.
     *
     * @param isSSLon the new SS loff
     */
    public void setSSLoff(boolean isSSLon) {
        this.isSSLcheck = isSSLon;
    }

    /**
     * Gets the SS loff.
     *
     * @return the SS loff
     */
    public boolean getSSLoff() {
        return isSSLcheck;
    }

    /**
     * Reconnect on critical exception for debug.
     *
     * @param debugObject the debug object
     */
    public void reconnectOnCriticalExceptionForDebug(final IDebugObject debugObject) {
        try {

            if (null == debugObject) {
                MPPDBIDELoggerUtility.error("UIDisplayState: Reconnect operation failed [Null Object].");
                return;
            }

            Database database = debugObject.getDatabase();
            if (!database.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
                database.connectToServer();
            } else {
                Shell currentActiveShell = (Display.getCurrent() != null) ? Display.getCurrent().getActiveShell()
                        : null;
                PasswordDialog helper = new PasswordDialog(currentActiveShell, database);
                int returnValue = helper.open();
                if (returnValue == 0) {
                    database.connectToServer();
                } else {
                    onCancelClickDisconnectDB(debugObject);

                }
            }
            Command cmd = EclipseInjections.getInstance().getCommandService()
                    .getCommand("com.huawei.mppdbide.command.id.debugeditoritem");
            ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
            EclipseInjections.getInstance().getHandlerService().executeHandler(parameterizedCmd);

        } catch (MPPDBIDEException exception) {
            handleMPPDBIDEException(debugObject, exception);
        }
    }

    private void handleMPPDBIDEException(final IDebugObject debugObject, MPPDBIDEException exception) {
        final String errMsg = exception.getServerMessage();
        if (null != errMsg && (UIDisplayFactoryProvider.getUIDisplayStateIf().needPromptPasswordError(errMsg))) {

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED), errMsg);
            onCancelClickDisconnectDB(debugObject);

        } else {
            Display.getDefault().asyncExec(new Runnable() {

                /**
                 * run
                 */
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                            MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR)
                                    + MPPDBIDEConstants.LINE_SEPARATOR + errMsg);
                    onCancelClickDisconnectDB(debugObject);
                }
            });
        }
        MPPDBIDELoggerUtility.error("UIDisplayState: Reconnect operation failed.", exception);
    }

    /**
     * Reset connection on critical error.
     *
     * @param debugObj the debug obj
     */
    public void resetConnectionOnCriticalError(final IDebugObject debugObj) {
        IServerConnectionInfo connInfo = null;
        try {
            Database db = debugObj.getDatabase();
            String dbName = db.getDbName();
            connInfo = db.getServer().getServerConnectionInfo(dbName);
            db.getExecutor().disconnect();

            if (!db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
                db.getExecutor().connectToServer((ServerConnectionInfo) connInfo,
                        db.getConnectionManager().getConnectionDriver());
                return;
            }
            Shell currentActiveShell = (Display.getCurrent() != null) ? Display.getCurrent().getActiveShell() : null;
            PasswordDialog helper = new PasswordDialog(currentActiveShell, db);
            int returnValue = helper.open();
            if (returnValue == 0) {
                connInfo = db.getServer().getServerConnectionInfo(dbName);
                db.getExecutor().connectToServer((ServerConnectionInfo) connInfo,
                        db.getConnectionManager().getConnectionDriver());
            } else {
                onCancelClickDisconnectDB(debugObj);

            }

        } catch (MPPDBIDEException ex) {
            handleMppDbIdeException(debugObj, ex);

        } finally {
            if (connInfo != null) {
                ServerUtil.clearConnectionInfo(connInfo);
            }
        }
    }

    private void handleMppDbIdeException(final IDebugObject debugObj, MPPDBIDEException ex) {
        final String errMsg = ex.getServerMessage();

        if (null != errMsg && (UIDisplayFactoryProvider.getUIDisplayStateIf().needPromptPasswordError(errMsg))) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SERVER_CONNECTION_FAILED), errMsg);
            onCancelClickDisconnectDB(debugObj);
        } else {
            Display.getDefault().asyncExec(new Runnable() {

                /**
                 * run
                 */
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                            MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_DATABASE_CRITICAL_ERROR)
                                    + MPPDBIDEConstants.LINE_SEPARATOR + errMsg);
                    onCancelClickDisconnectDB(debugObj);
                }
            });
        }
        MPPDBIDELoggerUtility.error("UIdisplayState: Reconnect operation failed.", ex);
    }

    /**
     * Gets the reconnect pop up on critical error.
     *
     * @param debugObj the debug obj
     * @return the reconnect pop up on critical error
     */
    public int getReconnectPopUpOnCriticalError(IDebugObject debugObj) {
        String termName = debugObj.getDisplayLabel();
        int btnPressed = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_DEBUG_CONNECTION_ERROR) + " : " + termName,
                MessageConfigLoader.getProperty(IMessagesConstants.RECONNECT_FOR_EXECUTION_PLAN_VISUAL_EXPLAIN));
        return btnPressed;
    }

    /**
     * On cancel click disconnect DB.
     *
     * @param debugObj the debug obj
     */
    public void onCancelClickDisconnectDB(final IDebugObject debugObj) {
        Database db = debugObj.getDatabase();

        PLSourceEditor editor = UIElement.getInstance().getEditorModelById(debugObj);
        if (null != editor) {
            editor.refreshAnnotations();
            editor.destroySourceViewer();
        }
        DBConnProfCache.getInstance().destroyConnection(db);

        ConsoleCoreWindow.getInstance()
                .logFatal(MessageConfigLoader.getProperty(IMessagesConstants.DISCONNECTED_FROM_SERVER,
                        db.getServer().getServerConnectionInfo().getConectionName(), db.getName()));

        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (null != objectBrowserModel) {

            objectBrowserModel.refreshObject(db);
        }
        UIElement.getInstance().resetAllSQLTerminalConnections();
    }

    /**
     * Handle critical exception for reconnect.
     *
     * @param dbgObj the dbg obj
     * @return true, if successful
     */
    public boolean handleCriticalExceptionForReconnect(IDebugObject dbgObj) {
        boolean restartJob = false;
        if (dbgObj != null) {
            int btnPressed = getReconnectPopUpOnCriticalError(dbgObj);
            if (btnPressed == IDialogConstants.OK_ID) {
                restartJob = true;
            } else if (btnPressed == IDialogConstants.CANCEL_ID) {
                onCancelClickDisconnectDB(dbgObj);
            }
        }
        return restartJob;
    }

    /**
     * Clean upon window close.
     */
    public void cleanUponWindowClose() {
        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        boolean serverHasNextRcrd = servers.hasNext();
        Server serv = null;
        while (serverHasNextRcrd) {
            serv = servers.next();
            if (cleanupOnServerRemoval(serv)) {
                deleteSecurityFolderFromProfile(serv);
                serv.close();
                ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(serv.getName());
                MPPDBIDELoggerUtility.info("Disconnected all databases");
            }
            serverHasNextRcrd = servers.hasNext();
        }
    }
    
    /**
     * Clean up security on window close.
     */
    public void cleanUpSecurityOnWindowClose() {
        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        boolean serverHasNextRcrd = servers.hasNext();
        Server server = null;
        while (serverHasNextRcrd) {
            server = servers.next();
            deleteSecurityFolderFromProfile(server);
            serverHasNextRcrd = servers.hasNext();
        }
    }

    private void doRefreshObjectBroswer(final Database db) {
        if (db != null) {
            UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(db);
            }
        }
    }

    /**
     * Handle new editor stat bar.
     *
     * @param prevObjId the prev obj id
     */
    public void handleNewEditorStatBar(long prevObjId) {
        this.debugInProgressOnNewEditor = true;
        UIElement.getInstance().getAllOpenedSourceViewer().forEach(editor -> {
            if (editor.getDebugObject().getOid() == prevObjId) {
                this.prevStatusBar = editor.getSourceEditorCore().getExecStatusBar();
            }
        });
    }

    /**
     * Checks if is debug in progres on new editor.
     *
     * @return true, if is debug in progres on new editor
     */
    public boolean isDebugInProgresOnNewEditor() {
        return this.debugInProgressOnNewEditor;
    }

    /**
     * Gets the prev status bar.
     *
     * @return the prev status bar
     */
    public TerminalStatusBar getPrevStatusBar() {
        return this.prevStatusBar;
    }

    /**
     * Sets the debug in progres new ed.
     *
     * @param isDebuginProgres the new debug in progres new ed
     */
    public void setDebugInProgresNewEd(boolean isDebuginProgres) {
        this.debugInProgressOnNewEditor = isDebuginProgres;
        this.prevStatusBar = null;
    }

    /**
     * Need prompt password error.
     *
     * @param errMsg the err msg
     * @return true, if successful
     */
    public boolean needPromptPasswordError(String errMsg) {
        return errMsg.contains("Invalid username/password") || errMsg.contains("Incorrect user or password");
    }

}
