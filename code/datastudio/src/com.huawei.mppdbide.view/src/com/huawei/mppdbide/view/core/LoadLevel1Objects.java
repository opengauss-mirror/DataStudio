/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.AccessMethod;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ILazyLoadObject;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SystemNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class LoadLevel1Objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class LoadLevel1Objects {
    private Object obj;
    private StatusMessage statusMsg;
    private IExecTimer timer;
    private static final Object LOCK = new Object();
    private static final String OLAP_PG_CATALOG = "pg_catalog";

    /**
     * Instantiates a new load level 1 objects.
     *
     * @param object the object
     * @param statusMessege the status messege
     */
    public LoadLevel1Objects(Object object, StatusMessage statusMessege) {
        this.obj = object;
        this.statusMsg = statusMessege;
        this.timer = null;
    }

    /**
     * Load objects.
     *
     * @throws DatabaseCriticalException the database critical exception
     */
    public void loadObjects() throws DatabaseCriticalException {
        loadTableAndViewObjects();

        if (obj instanceof Server) {
            loadServerObjects();
        } else if (obj instanceof DatabaseObjectGroup) {
            loadDatabaseObjectGroup();
        } else if (obj instanceof Database) {
            loadDatabaseObjects();
        } else if (obj instanceof Namespace) {
            loadNamespaceObjects();
        } else if (obj instanceof UserNamespaceObjectGroup) {
            loadUserNamespaceObjectGroup();
        } else if (obj instanceof SystemNamespaceObjectGroup) {
            loadSystemNamespaceObjectGroup();
        }

    }

    /**
     * Load database objects.
     */
    private void loadDatabaseObjects() {
        Database db = (Database) obj;
        timer = new ExecTimer("Refresh DB:" + db.getName()).start();
        loadDataBasesObjects(db, db.getName());
        db.setLoadingNamespaceInProgress(false);
    }

    /**
     * Load system namespace object group.
     */
    private void loadSystemNamespaceObjectGroup() {
        List<ILazyLoadObject> nsList = new ArrayList<ILazyLoadObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        SystemNamespaceObjectGroup sysNsGroup = (SystemNamespaceObjectGroup) obj;
        timer = new ExecTimer("Refresh User Namespace Group: " + sysNsGroup.getName()).start();
        Namespace ns = null;
        Set<String> inclusion = this.getFastLoadNamespaceList(sysNsGroup.getDatabase());
        for (String searchPath : inclusion) {
            ns = sysNsGroup.get(searchPath);
            if (null != ns) {
                ns.setNotLoaded();
                nsList.add(ns);
            }
        }
        loadNameSpaceObjects(nsList, sysNsGroup.getDatabase().getName(), false, false);
        sysNsGroup.getDatabase().setLoadingSystemNamespaceInProgress(false);
    }

    /**
     * Load user namespace object group.
     */
    private void loadUserNamespaceObjectGroup() {
        List<ILazyLoadObject> nsList = new ArrayList<ILazyLoadObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        UserNamespaceObjectGroup userNsGroup = (UserNamespaceObjectGroup) obj;
        timer = new ExecTimer("Refresh User Namespace Group: " + userNsGroup.getName()).start();
        Namespace ns = null;
        Set<String> inclusion = this.getFastLoadNamespaceList(userNsGroup.getDatabase());
        for (String searchPath : inclusion) {
            ns = userNsGroup.get(searchPath);
            if (null != ns) {
                ns.setNotLoaded();
                nsList.add(ns);
            }
        }

        loadNameSpaceObjects(nsList, userNsGroup.getDatabase().getName(), false, true);
        userNsGroup.getDatabase().setLoadingUserNamespaceInProgress(false);
    }

    /**
     * Load namespace objects.
     */
    private void loadNamespaceObjects() {
        /*
         * On refresh of namespace, if that selected namespace is there in
         * exclusion list, it should be removed from it
         */
        updateSchemaExclusionList();

        List<ILazyLoadObject> nsList = new ArrayList<ILazyLoadObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        Namespace ns = (Namespace) obj;
        timer = new ExecTimer("Refresh Namespace : " + ns.getName()).start();
        ns.setNotLoaded();
        nsList.add(ns);
        if (obj instanceof UserNamespace) {
            loadNameSpaceObjects(nsList, ns.getDatabaseName(), true, true);
            ns.getDatabase().setLoadingUserNamespaceInProgress(false);
        } else {
            loadNameSpaceObjects(nsList, ns.getDatabaseName(), true, false);
            ns.getDatabase().setLoadingSystemNamespaceInProgress(false);
        }
    }

    /**
     * Load database object group.
     *
     * @throws DatabaseCriticalException the database critical exception
     */
    private void loadDatabaseObjectGroup() throws DatabaseCriticalException {
        /*
         * There is lot of duplication between this block and above
         * block(Server) But This is a much cleaner setting because in future
         * load logic of server will change
         */
        boolean dbHasNext = false;
        DatabaseObjectGroup dbg = (DatabaseObjectGroup) obj;
        timer = new ExecTimer("Refresh Database Group : " + dbg.getName()).start();
        Database database = null;
        Iterator<Database> databases = dbg.getServer().getAllDatabases().iterator();
        dbHasNext = databases.hasNext();
        int dbCount = 0;
        while (dbHasNext) {
            database = databases.next();
            if (database.isConnected() && !database.isLoadingNamespaceInProgress()) {
                statusBarManager(dbCount);
                loadDataBasesObjects(database, database.getName());
                dbCount++;
            }
            dbHasNext = databases.hasNext();
        }
        dbg.setLoadingDatabaseGroupInProgress(false);
        if (dbCount == 0) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE));
            throw new DatabaseCriticalException(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE));
        }
    }

    /**
     * Load server objects.
     *
     * @throws DatabaseCriticalException the database critical exception
     */
    private void loadServerObjects() throws DatabaseCriticalException {
        boolean dbHasNext = false;
        Server server = (Server) obj;
        timer = new ExecTimer("Refresh Server : " + server.getName()).start();
        Database database = null;
        Iterator<Database> databases = server.getAllDatabases().iterator();
        dbHasNext = databases.hasNext();
        int dbCount = 0;
        while (dbHasNext) {
            database = databases.next();
            if (database.isConnected() && !database.isLoadingNamespaceInProgress()) {
                statusBarManager(dbCount);
                loadDataBasesObjects(database, database.getName());
                dbCount++;
            }
            dbHasNext = databases.hasNext();
        }
        server.setServerInProgress(false);
        if (dbCount == 0) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE));
            throw new DatabaseCriticalException(IMessagesConstants.ERR_NO_CONNECTION_AVAILABLE);
        }
    }

    /**
     * Load table and view objects.
     */
    private void loadTableAndViewObjects() {
        if (obj instanceof TableMetaData || obj instanceof ViewMetaData) {
            timer = new ExecTimer("Refresh Object Details : " + ((ServerObject) obj).getName()).start();
            startServerObjectLoadUIJob();
        }
    }

    /**
     * Update schema exclusion list.
     */
    private void updateSchemaExclusionList() {
        Database db = ((Namespace) obj).getDatabase();
        Server server = db.getServer();
        IServerConnectionInfo serverConnectionInfo = server.getServerConnectionInfo();
        Set<String> newExcludeList = serverConnectionInfo.getModifiedSchemaExclusionList();
        newExcludeList.remove(((Namespace) obj).getName());
        serverConnectionInfo.setModifiedSchemaExclusionList(newExcludeList);
    }

    /**
     * Start server object load UI job.
     */
    private void startServerObjectLoadUIJob() {
        synchronized (LOCK) {
            ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowser) {

                LazyLoadServerObjectUIJob loadNameSpace = new LazyLoadServerObjectUIJob(Display.getDefault(),
                        "Server object Loading", obj, objectBrowser.getTreeViewer(), statusMsg, timer);
                loadNameSpace.schedule();
            }
        }
    }

    /**
     * Load name space objects.
     *
     * @param namespaceList the namespace list
     * @param objectName the object name
     * @param isDBLoad the is DB load
     * @param isUserSchema the is user schema
     */
    private void loadNameSpaceObjects(List<ILazyLoadObject> namespaceList, String objectName, boolean isDBLoad,
            boolean isUserSchema) {
        ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
        String jobName = "Loading NameSpace objects of Database: " + objectName;
        if (objectBrowser != null) {
            LazyLoadNamespace loadNameSpace = new LazyLoadNamespace(Display.getDefault(), jobName, namespaceList,
                    objectBrowser.getTreeViewer(), statusMsg, objectName, timer, isDBLoad, isUserSchema);
            loadNameSpace.schedule();
        }
    }

    /**
     * Gets the fast load namespace list.
     *
     * @param db the db
     * @return the fast load namespace list
     */
    private Set<String> getFastLoadNamespaceList(Database db) {
        List<String> namespaces = db.getSearchPathHelper().getSearchPath();
        Set<String> inclusions = new HashSet<String>(namespaces);
        List<String> defaultSchemasToLoad = getDefaultSchemasToLoad();
        Server server = db.getServer();
        IServerConnectionInfo serverConnectionInfo = server.getServerConnectionInfo();
        inclusions.addAll(defaultSchemasToLoad);
        inclusions.addAll(serverConnectionInfo.getModifiedSchemaInclusionList());
        inclusions.removeAll(serverConnectionInfo.getModifiedSchemaExclusionList());

        return inclusions;
    }

    /**
     * Gets the default schemas to load.
     *
     * @return the default schemas to load
     */
    private List<String> getDefaultSchemasToLoad() {
        List<String> schema = new ArrayList<String>();
            schema.add(OLAP_PG_CATALOG);
        return schema;
    }

    /**
     * Load data bases objects.
     *
     * @param db the db
     * @param objectName the object name
     */
    private void loadDataBasesObjects(final Database db, final String objectName) {
        final List<ILazyLoadObject> userNsList = new ArrayList<ILazyLoadObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        final List<ILazyLoadObject> systemNsList = new ArrayList<ILazyLoadObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        Set<String> inclusions = this.getFastLoadNamespaceList(db);
        Namespace userNameSapces = null;
        Namespace systemNameSpaces = null;
        for (String searchPath : inclusions) {
            if (db.getDBType() == DBTYPE.OPENGAUSS) {
                userNameSapces = db.getUserNamespaceGroup().get(searchPath);
                if (null != userNameSapces) {
                    userNameSapces.setNotLoaded();
                    userNsList.add(userNameSapces);
                }
                systemNameSpaces = db.getSystemNamespaceGroup().get(searchPath);
                if (null != systemNameSpaces) {
                    systemNameSpaces.setNotLoaded();
                    systemNsList.add(systemNameSpaces);
                }
            }
        }
        final boolean expandObjectInViewer = false;
        String jobName = "Load Database Objects: " + db.getDbName();
        Job job = loadDatabaseObjects(db, objectName, userNsList, systemNsList, expandObjectInViewer, jobName);
        job.schedule();
        if (userNsList.size() == 0 && systemNsList.size() == 0) {
            try {
                timer.stopAndLog();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Exception while getting elapsed time", exception);
            }
        }
    }

    private Job loadDatabaseObjects(final Database db, final String objectName, final List<ILazyLoadObject> userNsList,
            final List<ILazyLoadObject> SystemNsList, final boolean expandObjectInViewer, String jobName) {
        Job job = new Job(jobName) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {

                try {
                    db.fetchAllTablespace();
                    AccessMethod.fetchAllAccessMethods(db);
                    db.fetchAllDatatypes();

                } catch (DatabaseCriticalException exception) {
                    MPPDBIDELoggerUtility.error("Loading TableSpace/AccessMethod/Datatype failed.", exception);
                } catch (DatabaseOperationException exception) {
                    MPPDBIDELoggerUtility.error("Loading TableSpace/AccessMethod/Datatype failed.", exception);
                }

                if (db.getDBType() == DBTYPE.OPENGAUSS) {
                    loadNameSpaceObjects(userNsList, objectName, expandObjectInViewer, true);
                    loadNameSpaceObjects(SystemNsList, objectName, expandObjectInViewer, false);
                }
                return Status.OK_STATUS;
            }
        };
        return job;
    }

    /**
     * Status bar manager.
     *
     * @param dbCount the db count
     */
    /*
     * To add status message to statusBarif server have more than one database
     * connected initialy messasge would be added inRefreshObjectBrowserItem
     */
    private void statusBarManager(int dbCount) {
        statusMsg = new StatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_REFRESH_STATUSBAR));
        if (dbCount > 0) {
            StatusMessageList.getInstance().push(statusMsg);

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
        }
    }

}
