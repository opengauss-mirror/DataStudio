/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseHelper;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.NamespaceUtils;
import com.huawei.mppdbide.bl.serverdatacache.NamespaceUtilsBase;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadataUtil;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.SystemNamespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.UserRoleManager;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SystemNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TriggerObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.NoNeedToRefreshException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.LoadLevel1Objects;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.GUISM;
import com.huawei.mppdbide.view.utils.IDEMemoryAnalyzer;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * Title: class Description: The Class RefreshObjectBrowserItem. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class RefreshObjectBrowserItem {

    private Job job;

    private StatusMessage statusMessage;

    private boolean isExceptionthrown;

    private PLSourceEditor editorUIObject;

    private IDebugObject editorObject;

    private IExecTimer timer;

    private boolean isNonLazzyLoadItem;

    private final Object INSTANCE_LOCK = new Object();

    /**
     * Execute.
     *
     * @param command the command
     * @param partService the part service
     */
    @Execute
    public void execute(@Optional @Named("objectbrowser.id") String command, final EPartService partService) {
        String cmd = command;

        MPPDBIDELoggerUtility.info("GUI: RefreshDbObject: Refresh clicked.");

        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();

        final Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        isExceptionthrown = false;

        job = new Job("Refresh connection objects") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                return refreshObjectBrowserItem(partService, bottomStatusBar, obj, cmd);
            }

        };

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_REFRESH_STATUSBAR));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        job.schedule();
    }

    /**
     * Refresh object browser item.
     *
     * @param partService the part service
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param cmd the cmd
     * @return the i status
     * @throws OutOfMemoryError the out of memory error
     */
    public IStatus refreshObjectBrowserItem(final EPartService partService, final BottomStatusBar bottomStatusBar,
            final Object obj, String cmd) throws OutOfMemoryError {
        StringBuilder objectName = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        Database db = null;
        // Bala issue List #2 start
        Server server = null;
        // Bala issue List #2 end

        int index = 0;
        ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
        if (obj instanceof ServerObject) {
            db = ((ServerObject) obj).getDatabase();
            if (db != null) {
                server = db.getServer();
            }
        } else if (obj instanceof ObjectGroup<?>) {
            db = ((ObjectGroup<?>) obj).getDatabase();
            if (db != null) {
                server = db.getServer();
            }
        } else if (obj instanceof Server) {
            server = (Server) obj;
        } else if (obj instanceof Database) {
            db = (Database) obj;
            server = db.getServer();
        }

        IStatus status = refresh(partService, bottomStatusBar, obj, objectName, db, server, index, objectBrowser, cmd);
        return status;
    }

    /**
     * Refresh.
     *
     * @param partService the part service
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param objectName the object name
     * @param dbParam the db param
     * @param serverParam the server param
     * @param cnt the cnt
     * @param objectBrowser the object browser
     * @param cmd the cmd
     * @return the i status
     * @throws OutOfMemoryError the out of memory error
     */
    private IStatus refresh(final EPartService partService, final BottomStatusBar bottomStatusBar, final Object obj,
            StringBuilder objectName, Database dbParam, Server serverParam, int cnt, ObjectBrowser objectBrowser,
            String cmd) throws OutOfMemoryError {
        Server server = serverParam;
        Database db = dbParam;
        if (null != objectBrowser) {
            try {
                validateForRefreshInProgress(bottomStatusBar, db, server);
                if (obj instanceof Server) {
                    server = refreshServer(bottomStatusBar, obj, objectName, objectBrowser);
                } else if (obj instanceof Database) {
                    db = (Database) obj;
                    objectName.append(db.getName());

                    server = refreshDatabaseOnCanNotBeConnected(db, server, objectBrowser);
                    List<String> namespaces = db.getSearchPathHelper().getSearchPath();
                    refreshDatabase(bottomStatusBar, db, objectBrowser, namespaces);
                } else if (obj instanceof ObjectGroup<?>) {

                    db = handleObjectGroupRefresh(obj, objectName, cnt, objectBrowser);
                } else if (obj instanceof ServerObject) {
                    handleServerObjRefresh(partService, bottomStatusBar, obj, objectName, cnt, objectBrowser);

                }
                handleObjSelAndMemUsage(objectName);
            } catch (NoNeedToRefreshException exception) {
                return Status.OK_STATUS;
            } catch (DatabaseOperationException exception) {
                return handleDatabaseException(bottomStatusBar, obj, objectName, db, exception, cmd);
            } catch (final DatabaseCriticalException exception) {
                return handleDatabaseCriticalException(bottomStatusBar, obj, db, server, exception, cmd);
            } catch (final Exception ex) {
                return handleGenericException(obj, db, ex, cmd);
            } finally {
                handleFinally(bottomStatusBar, obj, db);
            }

            handlePostRefreshOperations(db, server);
        }
        return Status.OK_STATUS;
    }

    /**
     * Handle object group refresh.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param cnt the cnt
     * @param objectBrowser the object browser
     * @return the database
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws Exception the exception
     */
    public Database handleObjectGroupRefresh(final Object obj, StringBuilder objectName, int cnt,
            ObjectBrowser objectBrowser) throws DatabaseOperationException, DatabaseCriticalException, Exception {
        Database db;
        ObjectGroup<?> objGroup = (ObjectGroup<?>) obj;
        objectName.append(objGroup.getName());
        db = objGroup.getDatabase();
        isNonLazzyLoadItem = true;
        refreshObjectGroup(obj, objectName, db, cnt, objectBrowser);
        return db;
    }

    /**
     * Handle server obj refresh.
     *
     * @param partService the part service
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param objectName the object name
     * @param plSourceEditors the pl source editors
     * @param noOfEditors the no of editors
     * @param cnt the cnt
     * @param objectBrowser the object browser
     * @throws NoNeedToRefreshException the no need to refresh exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    private void handleServerObjRefresh(final EPartService partService, final BottomStatusBar bottomStatusBar,
            final Object obj, StringBuilder objectName, int cnt, ObjectBrowser objectBrowser)
            throws NoNeedToRefreshException, DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException,
            Exception {
        if (obj instanceof Namespace) {
            refreshNamespace(obj, objectName, objectBrowser, bottomStatusBar);
        } else {
            refreshOlapServerObject(partService, obj, objectName, cnt);
        }
    }

    /**
     * Handle post refresh operations.
     *
     * @param db the db
     * @param server the server
     */
    private void handlePostRefreshOperations(Database db, Server server) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                UIElement.getInstance().refreshSQLTerminal();
            }
        });

        if (!isExceptionthrown) {
            MPPDBIDELoggerUtility.debug("GUI: RefreshDbObject: Refresh successful.");
            if (server != null) {
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(server);
            } else if (db != null) {
                UIElement.getInstance().updateTextEditorsIconAndConnButtons(db.getServer());
            }
        }
    }

    /**
     * Refresh olap server object.
     *
     * @param partService the part service
     * @param obj the obj
     * @param objectName the object name
     * @param plSourceEditors the pl source editors
     * @param noOfEditors the no of editors
     * @param cnt the cnt
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws Exception the exception
     */
    private void refreshOlapServerObject(final EPartService partService, final Object obj, StringBuilder objectName,
            int cnt) throws MPPDBIDEException, DatabaseOperationException, DatabaseCriticalException, Exception {
        ServerObject servObj = (ServerObject) obj;
        Database db = servObj.getDatabase();
        if (db == null) {
            throw new NoNeedToRefreshException("");
        }
        objectName.append(servObj.getName());
        isNonLazzyLoadItem = true;
        if (obj instanceof DebugObjects) {
            refreshDebugObject(partService, obj, db, cnt);
        } else if (obj instanceof TableMetaData) {
            refreshTableMetaData(obj, db);
        } else if (obj instanceof Tablespace) {
            Tablespace tablespace = (Tablespace) obj;
            tablespace.refresh();
        }

        else if (obj instanceof ViewMetaData) {
            refreshViewMetaData(obj, db);
        }

        else if (obj instanceof SequenceMetadata) {
            refreshSequenceMetaData(obj);
        }

        else if (obj instanceof SynonymMetaData) {
            Database zdb = servObj.getDatabase();
            ((SynonymMetaData) obj).refresh(zdb.getConnectionManager().getObjBrowserConn());
        }

        else if (obj instanceof UserRole) {
            refreshUserRole(obj);
        }
    }

    /**
     * Refresh table meta data.
     *
     * @param obj the obj
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshTableMetaData(final Object obj, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        TableMetaData table = (TableMetaData) obj;
        timer = new ExecTimer("Refresh Table : " + table.getName()).start();
        Namespace ns = table.getNamespace();
        db.setLoadingNamespaceInProgress(true);
        ns.refreshTable(table, db.getConnectionManager().getObjBrowserConn(), false);
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh view meta data.
     *
     * @param obj the obj
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshViewMetaData(final Object obj, Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        ViewMetaData view = (ViewMetaData) obj;
        timer = new ExecTimer("Refreshing View : " + view.getSearchName()).start();
        Namespace ns = view.getNamespace();
        isNonLazzyLoadItem = true;
        db.setLoadingNamespaceInProgress(true);
        ns.refreshView(view, ns.getDatabase().getConnectionManager().getObjBrowserConn(), false);
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh sequence meta data.
     *
     * @param obj the obj
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshSequenceMetaData(final Object obj)
            throws DatabaseCriticalException, DatabaseOperationException {
        SequenceMetadata sequence = (SequenceMetadata) obj;
        Namespace ns = sequence.getNamespace();
        SequenceMetadataUtil.refresh(sequence.getOid(), ns.getDatabase(), sequence);
    }

    /**
     * Refresh user role.
     *
     * @param obj the obj
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private void refreshUserRole(final Object obj) throws DatabaseOperationException, MPPDBIDEException {
        UserRole userRole = (UserRole) obj;
        try {
            UserRole freshUserRole = UserRoleManager.fetchUserRoleSimpleInfoByOid(userRole.getServer(),
                    userRole.getServer().findOneActiveDb().getConnectionManager().getObjBrowserConn(), userRole);
            userRole.setRolCanLogin(freshUserRole.getRolCanLogin());
            userRole.setName(freshUserRole.getName());
        } catch (MPPDBIDEException exception) {
            if (exception.getServerMessage() != null && exception.getServerMessage().equals(MessageConfigLoader
                    .getProperty(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST, String.valueOf(userRole.getOid())))) {
                userRole.getParent().remove(userRole);
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED), exception);
                throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
            }
            MPPDBIDELoggerUtility.error("user/role refresh fail", exception);
            throw exception;
        }
    }

    /**
     * Refresh debug object.
     *
     * @param partService the part service
     * @param obj the obj
     * @param db the db
     * @param plSourceEditors the pl source editors
     * @param noOfEditors the no of editors
     * @param index the i
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshDebugObject(final EPartService partService, final Object obj, Database db, int index)
            throws MPPDBIDEException, DatabaseOperationException, DatabaseCriticalException {
        int cnt = index;
        IDebugObject debugObject = (DebugObjects) obj;
        timer = new ExecTimer("Refresh debug Object : " + debugObject.getName()).start();
        db.setLoadingNamespaceInProgress(true);
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT, true);

        debugObject = ((Namespace) ((ServerObject) debugObject).getParent()).refreshDbObject(debugObject);

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT, false);
        List<PLSourceEditor> plSourceEditors = UIElement.getInstance().getAllOpenedSourceViewer();
        int noOfEditors = plSourceEditors.size();
        for (; cnt < noOfEditors; cnt++) {
            editorUIObject = plSourceEditors.get(cnt);
            editorObject = editorUIObject.getDebugObject();

            if (null == editorObject || (debugObject != null && debugObject.getOid() != editorObject.getOid())) {
                continue;
            }

            if (!handleDebugObject(db, partService, debugObject, editorObject)) {
                timer.stopAndLog();
                throw new NoNeedToRefreshException("");
            }
        }

        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh namespace.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param objectBrowser the object browser
     * @param bottomStatusBar the bottom status bar
     * @throws NoNeedToRefreshException the no need to refresh exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshNamespace(Object obj, StringBuilder objectName, ObjectBrowser objectBrowser,
            BottomStatusBar bottomStatusBar)
            throws NoNeedToRefreshException, DatabaseCriticalException, DatabaseOperationException {
        Namespace namespace = (Namespace) obj;
        Database db = namespace.getDatabase();
        if (db == null) {
            throw new NoNeedToRefreshException("");
        }
        if (!namespace.getDatabase().getSearchPathHelper().getSearchPath().contains(namespace.getName())) {
            namespace.getDatabase().getSearchPathHelper().getSearchPath().add(namespace.getName());
        }
        objectName.append("'").append(namespace.getName()).append("'").append(" namespace");
        if (obj instanceof UserNamespace) {
            if (db.getServer().isServerInProgress()
                    || db.getServer().getDatabaseGroup().isLoadingDatabaseGroupInProgress()
                    || db.isLoadingUserNamespaceInProgress()) {
                displayErrorLoadingInProgress(bottomStatusBar);
                throw new NoNeedToRefreshException(IMessagesConstants.NO_NEED_TO_REFRESH_EXCEPTION);
            }
            db.setLoadingUserNamespaceInProgress(true);
            db = DBConnProfCache.getInstance().getDbForProfileId(namespace.getDatabase().getProfileId());

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_NAMESPACE, true);

            NamespaceUtilsBase.refreshNamespace(namespace.getOid(), ((UserNamespace) namespace).isDrop(), db);

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_NAMESPACE, false);
        } else {
            if (db.getServer().isServerInProgress()
                    || db.getServer().getDatabaseGroup().isLoadingDatabaseGroupInProgress()
                    || db.isLoadingSystemNamespaceInProgress()) {
                displayErrorLoadingInProgress(bottomStatusBar);
                throw new NoNeedToRefreshException(IMessagesConstants.NO_NEED_TO_REFRESH_EXCEPTION);
            }
            isNonLazzyLoadItem = false;

            db.setLoadingSystemNamespaceInProgress(true);

            db = DBConnProfCache.getInstance().getDbForProfileId(namespace.getDatabase().getProfileId());

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_NAMESPACE, true);

            NamespaceUtilsBase.refreshNamespace(namespace.getOid(), false, db);
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_NAMESPACE, false);
        }
        objectBrowser.refreshObjectInUIThread(namespace);
        LoadLevel1Objects load = new LoadLevel1Objects(namespace, getStatusMessage());
        load.loadObjects();
    }

    /**
     * Validate for refresh in progress.
     *
     * @param bottomStatusBar the bottom status bar
     * @param db the db
     * @param server the server
     * @throws NoNeedToRefreshException the no need to refresh exception
     */
    private void validateForRefreshInProgress(final BottomStatusBar bottomStatusBar, Database db, Server server)
            throws NoNeedToRefreshException {
        if (null != server
                && (server.isServerInProgress() || server.getDatabaseGroup().isLoadingDatabaseGroupInProgress())
                || (db != null && db.isLoadingNamespaceInProgress())) {
            displayErrorLoadingInProgress(bottomStatusBar);
            MPPDBIDELoggerUtility.error("Refresh is in progress");
            throw new NoNeedToRefreshException("Refresh is in progress");
        }
    }

    /**
     * Refresh object group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @param plSourceEditors the pl source editors
     * @param noOfEditors the no of editors
     * @param cnt the cnt
     * @param objectBrowser the object browser
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws Exception the exception
     */
    private void refreshObjectGroup(final Object obj, StringBuilder objectName, Database db, int cnt,
            ObjectBrowser objectBrowser) throws DatabaseOperationException, DatabaseCriticalException, Exception {
        if (obj instanceof DatabaseObjectGroup) {
            refreshDatabaseObjGroup(obj, objectBrowser);
        } else if (obj instanceof UserNamespaceObjectGroup) {
            refreshUserNamespaceGroup(obj, db, objectBrowser);
        } else if (obj instanceof SystemNamespaceObjectGroup) {
            refreshSystemNamespaceGroup(obj, objectName, db, objectBrowser);
        } else if (obj instanceof DebugObjectGroup) {
            refreshDebugObjectGroup(obj, db, cnt);
        } else if (obj instanceof ForeignTableGroup) {
            refreshForeignTableGroup(obj, db);
        } else if (obj instanceof TableObjectGroup) {
            refreshTableObjectGroup(obj, db);
        } else if (obj instanceof TablespaceObjectGroup) {
            refreshTablespaceObjGroup(obj, objectName);
        } else if (obj instanceof ViewObjectGroup) {
            refreshViewObjGroup(obj, objectName, db);
        } else if (obj instanceof SequenceObjectGroup) {
            refreshSequenceObjGroup(obj, objectName, db);
        } else if (obj instanceof SynonymObjectGroup) {
            refreshSynonymObjGroup(obj, db);
        } else if (obj instanceof TriggerObjectGroup) {
            refreshTriggerGroup(obj, db);
        } else if (obj instanceof UserRoleObjectGroup) {
            refreshUserRoleObjGroup(obj, objectName);
        }
    }

    /**
     * Refresh user role obj group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshUserRoleObjGroup(final Object obj, StringBuilder objectName)
            throws DatabaseOperationException, DatabaseCriticalException {
        UserRoleObjectGroup userRoleObjectGroup = (UserRoleObjectGroup) obj;
        userRoleObjectGroup.getServer().refreshUserRoleObjectGroup();
    }

    /**
     * Refresh sequence obj group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshSequenceObjGroup(final Object obj, StringBuilder objectName, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        SequenceObjectGroup group = (SequenceObjectGroup) obj;
        Namespace ns = (Namespace) group.getParent();
        db.setLoadingNamespaceInProgress(true);
        timer = new ExecTimer("Refreshing sequences for Schema : " + ns.getDisplayName()).start();
        ns.refreshSequences(ns.getDatabase().getConnectionManager().getObjBrowserConn());
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh synonym obj group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshSynonymObjGroup(final Object obj, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        SynonymObjectGroup group = (SynonymObjectGroup) obj;
        Namespace ns = (Namespace) group.getParent();
        timer = new ExecTimer("Refreshing synonyms for Schema : " + ns.getDisplayName()).start();
        ns.loadSynonyms(db.getConnectionManager().getObjBrowserConn());
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh trigger obj group.
     *
     * @param Object the obj
     * @param Database the database
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshTriggerGroup(final Object obj, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        TriggerObjectGroup group = (TriggerObjectGroup) obj;
        Namespace ns = (Namespace) group.getParent();
        timer = new ExecTimer("Refreshing synonyms for Schema : " + ns.getDisplayName()).start();
        ns.loadTriggers(db.getConnectionManager().getObjBrowserConn());
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh view obj group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshViewObjGroup(final Object obj, StringBuilder objectName, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        ViewObjectGroup group = (ViewObjectGroup) obj;
        Namespace ns = (Namespace) group.getParent();

        db.setLoadingNamespaceInProgress(true);
        timer = new ExecTimer("Refreshing views for Schema : " + ns.getDisplayName()).start();
        ns.refreshAllViewsInNamespace(ns.getDatabase().getConnectionManager().getObjBrowserConn());
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh tablespace obj group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws Exception the exception
     */
    private void refreshTablespaceObjGroup(final Object obj, StringBuilder objectName)
            throws DatabaseOperationException, DatabaseCriticalException, Exception {
        TablespaceObjectGroup tablespaces = (TablespaceObjectGroup) obj;
        tablespaces.getServer().refresh();
    }

    /**
     * Refresh table object group.
     *
     * @param obj the obj
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshTableObjectGroup(final Object obj, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        TableObjectGroup tableGroup = (TableObjectGroup) obj;
        timer = new ExecTimer("Refresh table for Schema : " + tableGroup.getNamespace().getName()).start();
        db.setLoadingNamespaceInProgress(true);
        tableGroup.getNamespace().refreshTableHirarchy(db.getConnectionManager().getObjBrowserConn());
        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh foreign table group.
     *
     * @param obj the obj
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshForeignTableGroup(final Object obj, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        ForeignTableGroup foreignTableGroup = (ForeignTableGroup) obj;
        timer = new ExecTimer("Refresh table for Schema : " + foreignTableGroup.getNamespace().getName()).start();
        db.setLoadingNamespaceInProgress(true);
        foreignTableGroup.getNamespace()
                .refreshAllForeignTableMetadataInNamespace(db.getConnectionManager().getObjBrowserConn());

        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh debug object group.
     *
     * @param obj the obj
     * @param db the db
     * @param plSourceEditors the pl source editors
     * @param noOfEditors the no of editors
     * @param index the i
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws NoNeedToRefreshException the no need to refresh exception
     */
    private void refreshDebugObjectGroup(final Object obj, Database db, int index)
            throws DatabaseCriticalException, DatabaseOperationException, NoNeedToRefreshException {
        int cnt = index;
        List<PLSourceEditor> plSourceEditors = UIElement.getInstance().getAllOpenedSourceViewer();
        int noOfEditors = plSourceEditors.size();

        DebugObjectGroup objectGroup = (DebugObjectGroup) obj;
        timer = new ExecTimer("Refresh Debug object group : " + objectGroup.getName()).start();
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT_GROUP, true);

        db.setLoadingNamespaceInProgress(true);
        objectGroup.getNamespace().refreshDebugObjectGroup(objectGroup.getObjectGroupType());

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT_GROUP, false);
        for (; cnt < noOfEditors; cnt++) {
            editorUIObject = plSourceEditors.get(cnt);
            editorObject = editorUIObject.getDebugObject();

            if (null == editorObject || null == objectGroup.getObjectById(editorObject.getOid())) {
                continue;
            }

            if (!handleDebugObjectGroup(db, objectGroup, editorObject)) {
                timer.stopAndLog();
                MPPDBIDELoggerUtility.error("Exception handling debug object group");
                throw new NoNeedToRefreshException("Exception handling debug object group");
            }
        }

        db.setLoadingNamespaceInProgress(false);
        timer.stopAndLog();
    }

    /**
     * Refresh system namespace group.
     *
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @param objectBrowser the object browser
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshSystemNamespaceGroup(final Object obj, StringBuilder objectName, Database db,
            ObjectBrowser objectBrowser) throws DatabaseOperationException, DatabaseCriticalException {
        SystemNamespaceObjectGroup sysNsGroup = (SystemNamespaceObjectGroup) obj;

        db.setLoadingSystemNamespaceInProgress(true);
        isNonLazzyLoadItem = false;

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, true);
        NamespaceUtils.fetchAllSystemNamespaces(db);
        db.fetchAllDatatypes();

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, false);

        objectBrowser.refreshObjectInUIThread(sysNsGroup);
        LoadLevel1Objects load = new LoadLevel1Objects(sysNsGroup, getStatusMessage());
        load.loadObjects();
    }

    /**
     * Refresh user namespace group.
     *
     * @param obj the obj
     * @param db the db
     * @param objectBrowser the object browser
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshUserNamespaceGroup(final Object obj, Database db, ObjectBrowser objectBrowser)
            throws DatabaseOperationException, DatabaseCriticalException {
        UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) obj;

        db.setLoadingUserNamespaceInProgress(true);
        isNonLazzyLoadItem = false;

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, true);
        NamespaceUtils.fetchAllUserNamespaces(db);
        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, false);
        objectBrowser.refreshObjectInUIThread(userNsGroup);
        LoadLevel1Objects load = new LoadLevel1Objects(userNsGroup, getStatusMessage());
        load.loadObjects();
    }

    /**
     * Refresh database obj group.
     *
     * @param obj the obj
     * @param objectBrowser the object browser
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void refreshDatabaseObjGroup(final Object obj, ObjectBrowser objectBrowser)
            throws DatabaseOperationException, DatabaseCriticalException {
        DatabaseObjectGroup dbg = (DatabaseObjectGroup) obj;
        timer = new ExecTimer("Refresh All databases : " + dbg.getName()).start();
        dbg.setLoadingDatabaseGroupInProgress(true);
        isNonLazzyLoadItem = false;
        dbg.refresh();
        objectBrowser.refreshObjectInUIThread(dbg);
        LoadLevel1Objects load = new LoadLevel1Objects(dbg, getStatusMessage());
        load.loadObjects();
        timer.stopAndLog();
    }

    /**
     * Refresh database.
     *
     * @param bottomStatusBar the bottom status bar
     * @param db the db
     * @param objectBrowser the object browser
     * @param namespaces the namespaces
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws OutOfMemoryError the out of memory error
     * @throws NoNeedToRefreshException the no need to refresh exception
     */
    private void refreshDatabase(final BottomStatusBar bottomStatusBar, Database db, ObjectBrowser objectBrowser,
            List<String> namespaces)
            throws DatabaseOperationException, DatabaseCriticalException, OutOfMemoryError, NoNeedToRefreshException {
        Namespace uns = null;
        Namespace sns = null;
        List<String> toRemove = new ArrayList<String>();
        db.setLoadingNamespaceInProgress(true);
        isNonLazzyLoadItem = false;
        for (String searchPath : namespaces) {
            uns = db.getUserNamespaceGroup().get(searchPath);
            sns = db.getSystemNamespaceGroup().get(searchPath);

            if (null != uns) {
                if (uns.isLoadingInProgress()) {
                    displayErrorLoadingInProgress(bottomStatusBar);
                    MPPDBIDELoggerUtility.error("Namespace loading is in progress");
                    throw new NoNeedToRefreshException("Namespace loading is in progress");
                }

            } else if (null != sns) {
                if (sns.isLoadingInProgress()) {
                    displayErrorLoadingInProgress(bottomStatusBar);
                    throw new NoNeedToRefreshException("Namespace loading is in progress");
                }

            } else {
                toRemove.add(searchPath);
            }

        }
        namespaces.removeAll(toRemove);

        if (!db.isConnected()) {
            throw new NoNeedToRefreshException("Database is not connected");
        }

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, true);
        db.fetchSearchPathObjects(false);
        db.fetchDefaultDatatypes(true);
        DatabaseHelper.fetchTablespaceName(db);

        MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_CONNPROF, false);

        objectBrowser.refreshObjectInUIThread(db);
        LoadLevel1Objects load = new LoadLevel1Objects(db, getStatusMessage());
        load.loadObjects();
    }

    /**
     * Refresh database on can not be connected.
     *
     * @param db the db
     * @param server the server
     * @param objectBrowser the object browser
     * @return the server
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws Exception the exception
     */
    private Server refreshDatabaseOnCanNotBeConnected(Database db, Server serverParam, ObjectBrowser objectBrowser)
            throws DatabaseCriticalException, DatabaseOperationException, Exception {
        Server server = serverParam;
        if (db.getPrivilegeFlag() && !DatabaseHelper.canBeConnected(db)) {
            if (db.isConnected()) {
                DatabaseCriticalException exception = new DatabaseCriticalException(
                        IMessagesConstants.DATABASE_CONNECTION_ERR);
                exception.clearServerMessage();
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR));
                throw exception;
            }
            server = db.getServer();
            server.removeDatabase(db.getOid());
            server.refresh();
            objectBrowser.refreshObjectInUIThread(server);
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
            throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
        }
        return server;
    }

    /**
     * Handle finally.
     *
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param db the db
     */
    private void handleFinally(final BottomStatusBar bottomStatusBar, final Object obj, Database db) {
        if (isNonLazzyLoadItem) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    synchronized (INSTANCE_LOCK) {
                        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                        if (null != objectBrowserModel) {
                            objectBrowserModel.refreshObjectInUIThread(obj);
                            objectBrowserModel.refreshTablespaceGrpInUIThread(obj);
                            bottomStatusBar.hideStatusbar(getStatusMessage());

                        }
                    }
                }
            });

            if (null != db && db.isLoadingNamespaceInProgress()) {
                db.setLoadingNamespaceInProgress(false);
            }

        }
        resetObjectFilterWarningIcon(obj);
    }

    private void resetObjectFilterWarningIcon(final Object obj) {
        if (obj instanceof Server) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    synchronized (INSTANCE_LOCK) {
                        Server server = (Server) obj;
                        ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(server.getName());
                    }
                }
            });
        }
    }

    /**
     * Handle generic exception.
     *
     * @param obj the obj
     * @param db the db
     * @param ex the ex
     * @param cmd the cmd
     * @return the i status
     */
    private IStatus handleGenericException(final Object obj, Database db, final Exception ex, String cmd) {
        if (null != cmd && cmd.equals("objectbrowser")) {
            resetObjectFlags(obj, db);
            MPPDBIDELoggerUtility.error("Error while refreshing Object Browser item", ex);
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                            Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_REFRESHING)));
                    MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                            MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR), ex);
                    ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                    if (objectBrowserModel != null) {
                        objectBrowserModel.refreshObject(obj);
                    }
                    UIElement.getInstance().refreshSQLTerminal();
                    UIElement.getInstance().refreshBatchDeleteTerminal();
                }
            });
        }
        return Status.OK_STATUS;
    }

    /**
     * Handle database critical exception.
     *
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param db the db
     * @param server the server
     * @param exception the e
     * @param cmd the cmd
     * @return the i status
     */
    private IStatus handleDatabaseCriticalException(final BottomStatusBar bottomStatusBar, final Object obj,
            Database db, Server server, final DatabaseCriticalException exception, String cmd) {
        if (cmd != null && cmd.equals("objectbrowser")) {
            resetObjectFlags(obj, db);
            if (exception.getMessage()
                    .equalsIgnoreCase(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED))) {
                String elapsedTime = null;
                try {
                    timer.stop();
                    elapsedTime = timer.getElapsedTime();
                } catch (DatabaseOperationException exception1) {
                    MPPDBIDELoggerUtility.error("Exception while getting elapsed time", exception1);
                }
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED)
                                + "Time Elapsed in operation:" + elapsedTime, exception);
            }
            final Database dbConnProfile = db;
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    synchronized (INSTANCE_LOCK) {
                        bottomStatusBar.hideStatusbar(getStatusMessage());
                        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                                .getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_REFRESHING)));
                    }
                }
            });

            // Bala issue List #2 start
            if (null == db) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleServerCriticalError(exception, server);
            } else {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, dbConnProfile);
            }
            // Bala issue List #2 end
        }
        return Status.OK_STATUS;
    }

    /**
     * Handle database exception.
     *
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param objectName the object name
     * @param db the db
     * @param exception the e
     * @param cmd the cmd
     * @return the i status
     */
    private IStatus handleDatabaseException(final BottomStatusBar bottomStatusBar, final Object obj,
            StringBuilder objectName, Database db, DatabaseOperationException exception, String cmd) {
        if (cmd != null && cmd.equals("objectbrowser")) {
            resetObjectFlags(obj, db);
            isExceptionthrown = true;
            boolean noResultFlag = false;

            if (exception.getDBErrorMessage().contains("Access denied")
                    || exception.getDBErrorMessage().contains("No connection")
                    || exception.getDBErrorMessage().contains("may have been dropped")) {
                noResultFlag = true;
            }
            handleRefreshException(objectName.toString(), editorUIObject, editorObject, obj, noResultFlag, exception);
            if (null != bottomStatusBar) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (INSTANCE_LOCK) {
                            bottomStatusBar.hideStatusbar(getStatusMessage());
                        }
                    }
                });
            }
        }
        return Status.OK_STATUS;

    }

    /**
     * Refresh server.
     *
     * @param bottomStatusBar the bottom status bar
     * @param obj the obj
     * @param objectName the object name
     * @param objectBrowser the object browser
     * @return the server
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws NoNeedToRefreshException the no need to refresh exception
     */
    private Server refreshServer(final BottomStatusBar bottomStatusBar, final Object obj, StringBuilder objectName,
            ObjectBrowser objectBrowser)
            throws DatabaseOperationException, DatabaseCriticalException, NoNeedToRefreshException {
        Server server = (Server) obj;
        objectName.append(server.getName());
        isNonLazzyLoadItem = false;
        server.fetchServerObjects();
        objectBrowser.refreshObjectInUIThread(server);
        server.setServerInProgress(true);
        objectBrowser.refreshTablespaceGrpInUIThread(server);
        LoadLevel1Objects load = new LoadLevel1Objects(obj, getStatusMessage());
        load.loadObjects();
        /*
         * do not call refresh tablespace before db objects are refreshed and
         * loaded.
         */
        server.refreshTablespace();
        server.refreshUserRoleObjectGroup();
        return server;
    }

    /**
     * Reset object flags.
     *
     * @param obj the obj
     * @param db the db
     */
    private void resetObjectFlags(final Object obj, Database db) {
        if (null != db && db.isLoadingNamespaceInProgress()) {
            db.setLoadingNamespaceInProgress(false);
        }
        if (null != db && db.isLoadingUserNamespaceInProgress()) {
            db.setLoadingUserNamespaceInProgress(false);
        }
        if (null != db && db.isLoadingSystemNamespaceInProgress()) {
            db.setLoadingSystemNamespaceInProgress(false);
        }
        if (obj instanceof DatabaseObjectGroup) {
            ((DatabaseObjectGroup) obj).setLoadingDatabaseGroupInProgress(false);
        }
        if (obj instanceof Server) {
            ((Server) obj).setServerInProgress(false);
        }
    }

    /**
     * Handle no result exception.
     *
     * @param obj the obj
     */
    private static void handleNoResultException(final Object obj) {
        try {
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {
                objectBrowserModel.getTreeViewer().setExpandedState(obj, false);
                objectBrowserModel.setSelection(StructuredSelection.EMPTY);
                objectBrowserModel.refreshObjectInUIThread(((ServerObject) obj).getParent());
                objectBrowserModel.refreshTablespaceGrpInUIThread(obj);
            }
            if (obj instanceof Database) {
                Database db = (Database) obj;
                db.setLoadingNamespaceInProgress(false);
            }
            if (obj instanceof UserNamespace) {
                UserNamespace uns = (UserNamespace) obj;
                uns.getDatabase().setLoadingUserNamespaceInProgress(false);
            }
            if (obj instanceof SystemNamespace) {
                SystemNamespace sns = (SystemNamespace) obj;
                sns.getDatabase().setLoadingSystemNamespaceInProgress(false);
            }
            if (obj instanceof DebugObjects) {
                Database db = ((DebugObjects) obj).getDatabase();
                db.setLoadingNamespaceInProgress(false);
            }
            if (obj instanceof TableMetaData) {
                Database db = ((TableMetaData) obj).getNamespace().getDatabase();
                db.setLoadingNamespaceInProgress(false);
            }
            if (obj instanceof ViewMetaData) {
                Database db = ((ViewMetaData) obj).getNamespace().getDatabase();
                db.setLoadingNamespaceInProgress(false);
            }
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error("RefreshObjectBrowserItem: Error while refreshing object browser item.",
                    exception);
        }

    }

    /**
     * Display error loading in progress.
     *
     * @param bottomStatusBar the bottom status bar
     */
    public void displayErrorLoadingInProgress(final BottomStatusBar bottomStatusBar) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                bottomStatusBar.hideStatusbar(getStatusMessage());
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, GUISM.REFRESH));
            }
        });

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
     * Handle obj sel and mem usage.
     *
     * @param objectName the object name
     */
    private void handleObjSelAndMemUsage(StringBuilder objectName) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(
                objectName + "  " + MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_ITEM_REFRESHED)));

        Display.getDefault().asyncExec(new HandleObjSel());
    }

    /**
     * Title: class Description: The Class HandleObjSel. Copyright (c) Huawei
     * Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class HandleObjSel implements Runnable {
        @Override
        public void run() {
            PLSourceEditor sourceEditor = UIElement.getInstance().getVisibleSourceViewer();
            if (null != sourceEditor && null != sourceEditor.getDebugObject()) {
                HandlerUtilities.selectVisibleDebugObject(sourceEditor.getDebugObject(), sourceEditor);
                if (!IDEMemoryAnalyzer.is90PercentReached() && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() >= 90) {
                    IDEMemoryAnalyzer.setIs90PercentReached(true);
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MOM_USAGE),
                            MessageConfigLoader.getProperty(IMessagesConstants.DB_CONN_DIA_MOM_USAGE_MSG));
                } else if (IDEMemoryAnalyzer.is90PercentReached()
                        && IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() < 90) {
                    IDEMemoryAnalyzer.setIs90PercentReached(false);
                }
            }
        }
    }

    /**
     * Handle refresh debugobject group.
     *
     * @param db the db
     * @param objectGroup the object group
     * @param editorObj the editor obj
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private boolean handleDebugObjectGroup(Database db, DebugObjectGroup objectGroup, IDebugObject editorObj)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (null != editorObj && editorObj.getObjectType() == NamespaceUtilsBase
                .getDebugObjectTypeByGroupType(objectGroup.getObjectGroupType())) {
            IDebugObject debugObjectById = db.getDebugObjectById(editorObj.getOid(), editorObj.getNameSpaceId());
            DebugObjects newSqlObject = null;
            if (debugObjectById instanceof DebugObjects) {
                newSqlObject = (DebugObjects) debugObjectById;
            }
            if (null == newSqlObject) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getErrorFromConst(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED,
                                editorObj.getDatabase().getServer().getServerConnectionInfo().getConectionName(),
                                editorObj.getName())));
                MPPDBIDELoggerUtility.info("Object may have been dropped");
                return false;
            }
            // DTS2016010408984 Starts
            Display.getDefault().asyncExec(new DisplaySourceCodeInEditor(newSqlObject));
            // DTS2016010408984 Ends
        }

        return true;
    }

    /**
     * Handle debug object.
     *
     * @param db the db
     * @param partService the part service
     * @param debugObject the debug object
     * @param editorObj the editor obj
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private boolean handleDebugObject(Database db, EPartService partService, IDebugObject debugObject,

            IDebugObject editorObj) throws DatabaseOperationException, DatabaseCriticalException {
        DebugObjects newSqlObject = null;

        if (null != editorObj && (debugObject != null && editorObj.getOid() == debugObject.getOid())) {
            IDebugObject debugObjectById = db.getDebugObjectById(editorObj.getOid(), editorObj.getNameSpaceId());
            if (debugObjectById instanceof DebugObjects) {
                newSqlObject = (DebugObjects) debugObjectById;
            }
            if (null == newSqlObject) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getErrorFromConst(
                        MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED,
                                editorObj.getDatabase().getServer().getServerConnectionInfo().getConectionName(),
                                editorObj.getName())));
                MPPDBIDELoggerUtility.warn("Object may have been dropped");

            }
            // DTS2016010408984 Starts
            Display.getDefault().asyncExec(new DisplaySourceCodeInEditor(newSqlObject));
            // DTS2016010408984 Ends
        }

        return (null == newSqlObject) ? false : true;
    }

    /**
     * Title: class Description: The Class DisplaySourceCodeInEditor. Copyright
     * (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class DisplaySourceCodeInEditor implements Runnable {
        private final DebugObjects sqlDebugObject;

        /**
         * Instantiates a new display source code in editor.
         *
         * @param sqlObject the sql object
         */
        private DisplaySourceCodeInEditor(DebugObjects sqlObject) {
            this.sqlDebugObject = sqlObject;
        }

        @Override
        public void run() {
            try {
                HandlerUtilities.displaySourceCodeInEditorFromUI(sqlDebugObject, true);
            } catch (DatabaseOperationException exception) {
                if (!exception.getMessage()
                        .contentEquals(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_INVALID_STATE))) {
                    IHandlerUtilities.handleGetSrcCodeException(sqlDebugObject);
                }

                return;
            } catch (DatabaseCriticalException exception) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                        sqlDebugObject.getDatabase());
                return;
            }
        }
    }

    /**
     * Added for findbugs Static inner class creation check.
     *
     * @param objectName the object name
     * @param editorUIObj the editor UI obj
     * @param editorObj the editor obj
     * @param obj the obj
     * @param noResultFlag the no result flag
     * @param exception the exception
     */
    private void handleRefreshException(String objectName, final PLSourceEditor editorUIObj,
            final IDebugObject editorObj, final Object obj, boolean noResultFlag,
            DatabaseOperationException exception) {

        Display.getDefault().asyncExec(new RefreshDisplayHandler(obj, editorObj, noResultFlag));

        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                .getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DURING_REFRESHING, objectName)));

        MPPDBIDELoggerUtility.error("Error while refreshing Object Browser item", exception);

        MPPDBIDELoggerUtility.info("GUI: RefreshDbObject: Error during refreshing.");
    }

    /**
     * Title: class Description: The Class RefreshDisplayHandler. Copyright (c)
     * Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class RefreshDisplayHandler implements Runnable {
        private final Object obj;

        private final IDebugObject editorObject;

        private final boolean noResultFlag;

        /**
         * Instantiates a new refresh display handler.
         *
         * @param obj the obj
         * @param editorObject the editor object
         * @param noResultFlag the no result flag
         */
        private RefreshDisplayHandler(Object obj, IDebugObject editorObject, boolean noResultFlag) {
            this.obj = obj;
            this.editorObject = editorObject;
            this.noResultFlag = noResultFlag;
        }

        /**
         * Show editor clear error.
         *
         * @param propertyFlag the property flag
         */
        private void showEditorClearError(boolean propertyFlag) {
            UIElement.getInstance().closeSourceViewerById(editorObject.getPLSourceEditorElmId());
            if (propertyFlag) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getErrorFromConst(IMessagesConstants.MSG_CLEAR_EDITOR_CONTENT_ON_ERROR));
            } else {
                ObjectBrowserStatusBarProvider.getStatusBar()
                        .displayMessage(Message.getError(IMessagesConstants.MSG_CLEAR_EDITOR_CONTENT_ON_ERROR));
            }
        }

        @Override
        public void run() {
            if (null != editorObject) {
                if (obj instanceof DebugObjects) {
                    DebugObjects debugObject = (DebugObjects) obj;
                    if (editorObject.getOid() == debugObject.getOid()) {
                        showEditorClearError(true);
                    }
                } else if (obj instanceof DebugObjectGroup) {
                    DebugObjectGroup objectGroup = (DebugObjectGroup) obj;
                    DebugObjects newObject = objectGroup.getObjectById(editorObject.getOid());
                    if (null == newObject) {
                        showEditorClearError(false);
                    }
                } else if (obj instanceof Namespace) {
                    Namespace namespace = (Namespace) obj;
                    IDebugObject debugObjectById = namespace.getDebugObjectById(editorObject.getOid());
                    DebugObjects newObject = null;
                    if (debugObjectById instanceof DebugObjects) {
                        newObject = (DebugObjects) debugObjectById;
                    }
                    if (null == newObject) {
                        showEditorClearError(false);
                    }
                } else if (obj instanceof Database) {

                    DebugObjects newObject = DatabaseUtils.getDebugObjects((Database) obj, editorObject.getOid());
                    if (null == newObject) {
                        showEditorClearError(true);
                    }
                } else if (obj instanceof Server) {
                    Server server = (Server) obj;
                    server.setServerInProgress(false);
                    ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getErrorFromConst(
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_REFRESHING)));
                }
            }

            if (noResultFlag) {
                handleNoResultException(obj);
            }
            refreshObjectBrowser();
            UIElement.getInstance().refreshSQLTerminal();
        }

        private void refreshObjectBrowser() {
            /*
             * Don't move it to finally as it is applicable only for exception,
             * DatabaseOperationException and not required for
             * DatabaseCriticalException.
             */

            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshTablespaceGrpInUIThread(obj);
                if (!noResultFlag) {
                    objectBrowserModel.refreshObject(obj);
                }
            }
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object object = UIElement.getInstance().getActivePartObject();
        if (object instanceof SearchWindow) {
            return false;
        }
        return isRefreshPossible();

    }

    /**
     * Checks if is refresh possible.
     *
     * @return true, if is refresh possible
     */
    private boolean isRefreshPossible() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        // DTS2016072611558 Fix Starts
        if (obj instanceof Server) {
            return findActiveDb(obj);
        }
        if (obj instanceof DatabaseObjectGroup) {
            return isDatabaseConnected(obj);
        }
        if (obj instanceof Database && ((Database) obj).isConnected()) {
            return true;
        }
        if (obj instanceof TablespaceObjectGroup || obj instanceof Tablespace) {
            return validateForActiveDB(obj);
        }
        if (isNamespaceGroup(obj)) {
            return validateNamespaceListSize(obj);
        }
        return validateServerObject(obj);
    }

    /**
     * Validate server object.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean validateServerObject(Object obj) {
        if (obj instanceof PartitionTable) {
            if (obj instanceof ForeignPartitionTable) {
                return false;
            }
        }

        // DTS2016072611558 Fix Ends
        boolean isInstanceTrueCheck = validateForNamespaceChild(obj);

        boolean isInstanceTrue = validateForNamespaceChildGroup(obj, isInstanceTrueCheck);

        if (validateNamespaceObjects(obj, isInstanceTrue)) {
            return true;
        }

        if (obj instanceof OLAPObjectGroup<?>
                && OBJECTTYPE.NODEGROUP_GROUP == ((ObjectGroup<?>) obj).getObjectGroupType()) {
            return true;
        }

        if (obj instanceof UserRoleObjectGroup) {
            UserRoleObjectGroup role = (UserRoleObjectGroup) obj;
            if (IHandlerUtilities.getActiveDB(role.getServer())) {
                return true;
            }
        }

        if (obj instanceof UserRole) {
            UserRole role = (UserRole) obj;
            if (IHandlerUtilities.getActiveDB(role.getServer())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate namespace objects.
     *
     * @param obj the obj
     * @param isInstanceTrue the is instance true
     * @return true, if successful
     */
    private boolean validateNamespaceObjects(Object obj, boolean isInstanceTrue) {
        return obj instanceof Namespace || obj instanceof DebugObjectGroup || obj instanceof DebugObjects
                || obj instanceof ForeignTableGroup || isInstanceTrue;
    }

    /**
     * Validate for namespace child group.
     *
     * @param obj the obj
     * @param isInstanceTrueCheck the is instance true check
     * @return true, if successful
     */
    private boolean validateForNamespaceChildGroup(Object obj, boolean isInstanceTrueCheck) {
        return obj instanceof ForeignTable || obj instanceof TableObjectGroup || obj instanceof TableMetaData
                || obj instanceof ViewObjectGroup || isInstanceTrueCheck;
    }

    /**
     * Validate for namespace child.
     *
     * @param Object the obj
     * @return boolean true if successful
     */
    private boolean validateForNamespaceChild(Object obj) {
        return obj instanceof ViewMetaData || obj instanceof SequenceMetadata || obj instanceof SequenceObjectGroup
                || obj instanceof SynonymMetaData || obj instanceof SynonymObjectGroup
                || obj instanceof TriggerMetaData || obj instanceof TriggerObjectGroup;
    }

    /**
     * Validate namespace list size.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean validateNamespaceListSize(Object obj) {
        OLAPObjectGroup<UserNamespace> group = (OLAPObjectGroup<UserNamespace>) obj;
        if (group.getSize() > 0) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is namespace group.
     *
     * @param obj the obj
     * @return true, if is namespace group
     */
    private boolean isNamespaceGroup(Object obj) {
        return obj instanceof UserNamespaceObjectGroup || obj instanceof SystemNamespaceObjectGroup;
    }

    /**
     * Validate for active DB.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean validateForActiveDB(Object obj) {
        if (IHandlerUtilities.getActiveDB(getServer(obj))) {
            return true;
        }
        return false;
    }

    /**
     * Gets the server.
     *
     * @param obj the obj
     * @return the server
     */
    private Server getServer(Object obj) {
        Server server = null;
        if (obj instanceof TablespaceObjectGroup) {
            TablespaceObjectGroup tablespaces = (TablespaceObjectGroup) obj;
            server = tablespaces.getServer();
        } else {
            Tablespace tablespace = (Tablespace) obj;
            server = tablespace.getServer();
        }
        return server;
    }

    /**
     * Checks if is database connected.
     *
     * @param obj the obj
     * @return true, if is database connected
     */
    private boolean isDatabaseConnected(Object obj) {
        DatabaseObjectGroup dbg = (DatabaseObjectGroup) obj;
        if (dbg.getServer().isAleastOneDbConnected()) {
            return true;
        }
        return false;
    }

    /**
     * Find active db.
     *
     * @param obj the obj
     * @return true, if successful
     */
    private boolean findActiveDb(Object obj) {
        Server server = (Server) obj;
        try {
            server.findOneActiveDb();
            return true;
        } catch (DatabaseOperationException exception) {
            return false;
        }
    }

}
