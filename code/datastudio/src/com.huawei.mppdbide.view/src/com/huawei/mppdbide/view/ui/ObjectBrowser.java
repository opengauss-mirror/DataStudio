/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.huawei.mppdbide.bl.search.SearchObjectEnum;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.NamespaceUtilsBase;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ShowMoreObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.SystemNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserNamespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.eclipse.dependent.EclipseContextDSKeys;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.LoadLevel1Objects;
import com.huawei.mppdbide.view.core.LoadingUIElement;
import com.huawei.mppdbide.view.core.ObjectBrowserContentProvider;
import com.huawei.mppdbide.view.core.ObjectBrowserLabelProvider;
import com.huawei.mppdbide.view.core.ObjectBrowserLazyContentProvider;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.handler.ConnectToDB;
import com.huawei.mppdbide.view.handler.DisconnectDatabase;
import com.huawei.mppdbide.view.handler.ExecuteObjectBrowserItem;
import com.huawei.mppdbide.view.handler.RefreshObjectBrowserItem;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.uiif.ObjectBrowserIf;
import com.huawei.mppdbide.view.utils.GUISM;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class ObjectBrowser. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ObjectBrowser implements Observer, ObjectBrowserIf {
    private TreeViewer viewer;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private EHandlerService handlerService;

    @Inject
    private ECommandService commandService;

    private static final int TREE_AUTO_EXPAND_LEVEL = 0;

    private UIElement uiElementInstance;

    private ToolItem searchItem;

    private ToolItem refreshIteam;

    private ObjectBrowserFilterTree tree;

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param menuService the menu service
     * @param modelService the model service
     * @param application the application
     */

    @PostConstruct
    public void createPartControl(Composite parent, EPartService partService, EMenuService menuService,
            EModelService modelService, MApplication application) {
        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        IDEStartup.getInstance().init(partService, modelService, application);
        MPart mpart = partService.findPart("com.huawei.mppdbide.part.id.objectbrowser");
        MUIElement ele = modelService.cloneSnippet(application, "com.huawei.mppdbide.view.part.common", null);
        MPart part = (MPart) ele;
        addAllMenus(mpart, part);

        uiElementInstance = UIElement.getInstance();

        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());

        addObjectBrowserToolbar(mainComposite);

        addObViewer(menuService, mainComposite);

        addHandler(UIConstants.UI_COMMAND_DISCONNECT_DBOBJECT, new DisconnectDatabase());
        addHandler(UIConstants.UI_COMMAND_REFRESH_DBOBJECT, new RefreshObjectBrowserItem());
        addHandler(UIConstants.UI_COMMAND_EXECUTE_DBOBJECT, new ExecuteObjectBrowserItem(viewer));

    }

    @SuppressWarnings("deprecation")
    private void addObViewer(EMenuService menuService, Composite mainComposite) {
        tree = new ObjectBrowserFilterTree(mainComposite, getTreeOptions(), true);
        tree.setInitialText(MessageConfigLoader.getProperty(IMessagesConstants.FILTER_INTIAL_TEXT));
        viewer = tree.getViewer();
        viewer.setSorter(new ViewerSorter() {

            /**
             * Compare.
             *
             * @param iviewer the iviewer
             * @param e1 the e 1
             * @param e2 the e 2
             * @return the int
             */
            public int compare(Viewer iviewer, Object e1, Object e2) {
                if (e1 instanceof ServerObject && e2 instanceof ServerObject) {
                    if (((ServerObject) e1).getName() != null && ((ServerObject) e2).getName() != null) {
                        return ((ServerObject) e1).getName().compareToIgnoreCase(((ServerObject) e2).getName());
                    }

                }
                return 0;
            }
        });
        // create tree content
        viewer.setContentProvider(getContentProvider());
        viewer.setUseHashlookup(true);
        // create tree label
        viewer.setLabelProvider(new ObjectBrowserLabelProvider());
        viewer.setInput(DBConnProfCache.getInstance().getServersList());
        viewer.getTree().setItemCount(DBConnProfCache.getInstance().getServersList().size());
        viewer.setAutoExpandLevel(TREE_AUTO_EXPAND_LEVEL);
        viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTree().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TRE_OBJECTBROWSER_TREE_001");
        // create right click context menus
        menuService.registerContextMenu(viewer.getControl(), UIConstants.UI_MENUITEM_CONNECTIONPROFILE_ID);
        viewer.getControl().addKeyListener(new ViewerControlKeyListener());

        viewer.addSelectionChangedListener(new OBSelectionChangedListener());

        // Defect fix for DTS2013012908852 Start
        // DTS2013012908852: Code is removed as part of <Batch Drop>
        // Defect fix for DTS2013012908852 End

        viewer.addTreeListener(new TreeviwerHelper());

        viewer.addSelectionChangedListener(new ViewerSelectionChangeListener());

        viewer.addDoubleClickListener(new ViewerDoubleClickListener());
        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.RECREATE);
    }

    private IContentProvider getContentProvider() {
        readTreeRenderPolicy();
        if ("LAZY".equalsIgnoreCase(DSViewDataManager.getInstance().getTreeRenderPolicy())) {
            return new ObjectBrowserLazyContentProvider(viewer);
        }
        return new ObjectBrowserContentProvider();
    }

    private void readTreeRenderPolicy() {
        if (!StringUtils.isEmpty(DSViewDataManager.getInstance().getTreeRenderPolicy())) {
            return;
        }
        String[] args = Platform.getApplicationArgs();
        int len = args.length;
        for (int i = 0; i < len; i++) {
            if (args[i] != null && args[i].startsWith("-TreeRenderPolicy")) {
                DSViewDataManager.getInstance().setTreeRenderPolicy(getCLArgumentValue(args[i]));
            }
        }
    }

    private String getCLArgumentValue(String clArg) {
        String val = "";
        try {
            val = clArg.split("=")[1].trim();
        } catch (ArrayIndexOutOfBoundsException e) {
            val = "";
        }
        return val;
    }

    private int getTreeOptions() {
        readTreeRenderPolicy();
        int options = SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI;
        if ("LAZY".equalsIgnoreCase(DSViewDataManager.getInstance().getTreeRenderPolicy())) {
            return options | SWT.VIRTUAL;
        }
        return options;
    }

    private void addObjectBrowserToolbar(Composite mainComposite) {
        ToolBar toolBar = new ToolBar(mainComposite, SWT.FLAT | SWT.FOCUSED);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        toolBar.pack();

        searchItem = new ToolItem(toolBar, SWT.NONE);
        searchItem.setImage(IconUtility.getIconImage(IiconPath.ICO_SEARCH, getClass()));
        searchItem.setEnabled(false);
        searchItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.KEY_BINDING_SEARCH));
        searchItem.addSelectionListener(new SearchItemSelectionListener());

        refreshIteam = new ToolItem(toolBar, SWT.NONE);
        refreshIteam.setImage(IconUtility.getIconImage(IiconPath.ICO_REFRESH, getClass()));
        refreshIteam.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OB_REFRESH_TOOL));
        refreshIteam.setEnabled(false);
        refreshIteam.addSelectionListener(new RefreshItemSelectionListener());
    }

    /**
     * The listener interface for receiving viewerDoubleClick events. The class
     * that is interested in processing a viewerDoubleClick event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addViewerDoubleClickListener<code> method. When the
     * viewerDoubleClick event occurs, that object's appropriate method is
     * invoked. ViewerDoubleClickEvent
     */
    private class ViewerDoubleClickListener implements IDoubleClickListener {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            MPPDBIDELoggerUtility.debug("GUI: ObjectBrowser: Func/Proc/Trigger have selected.");
            performLoadingObjects(event);
        }
    }

    /**
     * The listener interface for receiving viewerSelectionChange events. The
     * class that is interested in processing a viewerSelectionChange event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addViewerSelectionChangeListener<code> method. When the
     * viewerSelectionChange event occurs, that object's appropriate method is
     * invoked. ViewerSelectionChangeEvent
     */
    private class ViewerSelectionChangeListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            selectionService.setSelection(selection);

            if (selection instanceof IStructuredSelection) {
                Object object = ((IStructuredSelection) selection).getFirstElement();

                if (object instanceof Server) {
                    Server sobj = (Server) object;
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, sobj);
                } else if (object instanceof ServerObject) {
                    ServerObject sobj = (ServerObject) object;
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, sobj);
                } else if (object instanceof ObjectGroup<?>) {

                    ObjectGroup<?> objGroup = (ObjectGroup<?>) object;
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, objGroup);
                } else if (object instanceof ObjectList<?>) {

                    ObjectList<?> objGroup = (ObjectList<?>) object;
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    eclipseContext.set(EclipseContextDSKeys.SERVER_OBJECT, objGroup);
                }
            }
        }
    }

    /**
     * The listener interface for receiving viewerControlKey events. The class
     * that is interested in processing a viewerControlKey event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addViewerControlKeyListener<code>
     * method. When the viewerControlKey event occurs, that object's appropriate
     * method is invoked. ViewerControlKeyEvent
     */
    private class ViewerControlKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent event) {

        }

        @Override
        public void keyPressed(KeyEvent event) {
            if (isSelectAllKeyPress(event)) {
                Tree viewer1 = (Tree) event.getSource();
                viewer1.selectAll();
                selectionService.setSelection(viewer.getSelection());
            }
        }

        private boolean isSelectAllKeyPress(KeyEvent event) {
            // CTRL+a or CTRL+A
            return ((event.stateMask & SWT.CONTROL) != 0) && ((event.keyCode == 'a') || (event.keyCode == 'A'));
        }
    }

    /**
     * The listener interface for receiving refreshItemSelection events. The
     * class that is interested in processing a refreshItemSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addRefreshItemSelectionListener<code> method. When the
     * refreshItemSelection event occurs, that object's appropriate method is
     * invoked. RefreshItemSelectionEvent
     */
    private class RefreshItemSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            Command command = commandService.getCommand("com.huawei.mppdbide.command.id.refreshserverobject");
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
            handlerService.executeHandler(parameterizedCommand);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving searchItemSelection events. The
     * class that is interested in processing a searchItemSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSearchItemSelectionListener<code> method. When the
     * searchItemSelection event occurs, that object's appropriate method is
     * invoked. SearchItemSelectionEvent
     */
    private class SearchItemSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {

            Command command = commandService.getCommand("com.huawei.mppdbide.view.command.search");
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
            handlerService.executeHandler(parameterizedCommand);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    private void addAllMenus(MPart mpart, MPart part) {
        if (mpart != null && part != null) {
            mpart.getMenus().addAll(part.getMenus());
        }
    }

    private boolean isValidObj(Object object) {
        boolean isValidObject = object instanceof DebugObjectGroup || object instanceof Database
                || object instanceof TableObjectGroup || object instanceof ForeignTableGroup;

        return object instanceof Server || isValidObject || object instanceof ObjectGroup<?>
                || object instanceof ObjectList<?> || object instanceof ViewMetaData
                || object instanceof SequenceMetadata;
    }

    private void checkIfObjectValid(Object obj) {
        if (viewer.getExpandedState(obj)) {
            viewer.collapseToLevel(obj, 1);
        } else {
            viewer.expandToLevel(obj, 1);
        }
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        ObjectBrowserStatusBarProvider.getStatusBar().destroy();

    }

    private void addHandler(final String id, final Object handlerObject) {
        viewer.getControl().addListener(SWT.Activate, new OBListener(id, handlerObject));
    }

    /**
     * The listener interface for receiving OB events. The class that is
     * interested in processing a OB event implements this interface, and the
     * object created with that class is registered with a component using the
     * component's <code>addOBListener<code> method. When the OB event occurs,
     * that object's appropriate method is invoked. OBEvent
     */
    private class OBListener implements Listener {
        private String id;

        private Object object;

        protected OBListener(String id, Object object) {
            this.id = id;
            this.object = object;
        }

        @Override
        public void handleEvent(Event event) {
            handlerService.activateHandler(id, object);
        }
    }

    /**
     * Refresh.
     *
     * @param profileId the profile id
     */
    public void refresh(ConnectionProfileId profileId) {
        MPPDBIDELoggerUtility.debug("start to refresh connection prof...");
        if (uiElementInstance.isObjectBrowserPartOpen()) {
            MPPDBIDELoggerUtility.debug("OB part open..start refresh");
            refresh();
            MPPDBIDELoggerUtility.debug("end refresh...start viewer refresh");
            viewer.refresh();
            MPPDBIDELoggerUtility.debug("end viewer refresh");
            viewer.setAutoExpandLevel(TREE_AUTO_EXPAND_LEVEL);
            MPPDBIDELoggerUtility.debug("GUI: ObjectBrowser: Tree refreshed.");
            DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
            if (null != databaseListControl) {
                databaseListControl.refreshConnectionComboItems();
                MPPDBIDELoggerUtility.debug("connection combos refreshed");
                if (profileId.getDatabase() != null) {
                    databaseListControl.setSelectedDatabase(profileId.getDatabase());
                }
                MPPDBIDELoggerUtility.debug("updated db in list control");
            }
        }
        MPPDBIDELoggerUtility.debug("refresh complete");
    }

    /**
     * Refresh object.
     *
     * @param obj the obj
     */
    public void refreshObject(Object obj) {
        if (!viewer.getControl().isDisposed()) {
            viewer.refresh(obj);
            if (obj instanceof ObjectGroup<?>) {
                ObjectGroup<?> group = (ObjectGroup<?>) obj;
                viewer.update(group.getParent(), null);
            }
            if (obj instanceof DatabaseObjectGroup) {
                Server server = ((DatabaseObjectGroup) obj).getServer();
                viewer.refresh(server.getTablespaceGroup());
                viewer.refresh(server.getUserRoleObjectGroup());
            }

            if (obj instanceof Server || obj instanceof DatabaseObjectGroup || obj instanceof Database) {
                /*
                 * To update databse list combo when critical exception occurs
                 * while doing some operation in SQLTerminal and SourceViewer
                 */
                DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
                if (null != databaseListControl) {
                    databaseListControl.refreshConnectionComboItems();
                }
            } else if ((obj instanceof UserNamespaceObjectGroup || obj instanceof SystemNamespaceObjectGroup)) {
                DefaultSchemaControl defaultSchemaControl = UIElement.getInstance().getDefaultSchemaControl();
                if (null != defaultSchemaControl) {
                    defaultSchemaControl.refreshDefaultSchemaComboItems();
                }
            }
        }
    }

    /**
     * Refresh tablespace grp.
     *
     * @param obj the obj
     */
    public void refreshTablespaceGrp(Object obj) {
        if (obj instanceof TablespaceObjectGroup) {
            Server server = ((TablespaceObjectGroup) obj).getServer();
            try {
                server.refreshTablespace();
            } catch (DatabaseOperationException event) {
                MPPDBIDELoggerUtility.error("ObjectBrowser: Refresh tablespace failed.", event);
            } catch (DatabaseCriticalException event) {
                MPPDBIDELoggerUtility.error("Objectbrowser: Refresh tablespace failed.", event);
            }
            viewer.refresh(server.getTablespaceGroup());
        }
    }

    /**
     * Refresh user role grp.
     *
     * @param obj the obj
     */
    public void refreshUserRoleGrp(Object obj) {
        if (obj instanceof UserRoleObjectGroup) {
            Server server = ((UserRoleObjectGroup) obj).getServer();
            try {
                server.refreshUserRoleObjectGroup();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.debug("ObjectBrowser: Refresh users/roles group failed.");
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.debug("Objectbrowser: Refresh users/roles group failed.");
            }
            viewer.refresh(server.getUserRoleObjectGroup());
        }
    }

    /**
     * Updat object.
     *
     * @param obj the obj
     */
    public void updatObject(Object obj) {
        viewer.update(obj, null);

        /* Refresh Default Schema Combo entries */
        if (obj instanceof UserNamespace) {
            DefaultSchemaControl defaultSchemaControl = UIElement.getInstance().getDefaultSchemaControl();
            if (null != defaultSchemaControl) {
                defaultSchemaControl.refreshDefaultSchemaComboItems();
            }
        }
    }

    /**
     * Refresh object in UI thread.
     *
     * @param obj the obj
     */
    public void refreshObjectInUIThread(final Object obj) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                refreshObject(obj);
            }
        });
    }

    /**
     * Refresh tablespace grp in UI thread.
     *
     * @param obj the obj
     */
    public void refreshTablespaceGrpInUIThread(final Object obj) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                refreshTablespaceGrp(obj);
            }
        });
    }

    /**
     * Gets the tree viewer.
     *
     * @return the tree viewer
     */
    public TreeViewer getTreeViewer() {
        return viewer;
    }

    /**
     * Clear.
     */
    public void clear() {
        viewer.setInput(null);
        refresh();
    }

    /**
     * Refresh.
     */
    public void refresh() {
        // DTS2014102803122 start
        viewer.getTree().setRedraw(false);

        viewer.setInput(DBConnProfCache.getInstance().getServersList());
        viewer.refresh();
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.refreshConnectionComboItems();
        }

        viewer.getTree().setRedraw(true);
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {
        viewer.getControl().setFocus();
    }

    /**
     * Find and select connection profile.
     *
     * @param debugObject the debug object
     */
    public void findAndSelectConnectionProfile(IDebugObject debugObject) {
        TreeItem[] items = viewer.getTree().getItems();
        int itemSize = items.length;
        if (itemSize > 0) {
            TreeItem item = null;
            for (int cnt = 0; cnt < itemSize; cnt++) {
                item = items[cnt];
                if (item.getData() instanceof Database) {
                    Database db = (Database) item.getData();
                    if (null != debugObject.getDatabase()
                            && db.getProfileId() == debugObject.getDatabase().getProfileId()) {
                        viewer.setExpandedState(item.getData(), true);
                        findAndSelectNameSpace(item, debugObject);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Find and expand namespace element under the connection profile.
     *
     * @param parentItem the parent item
     * @param debugObject the debug object
     */
    private void findAndSelectNameSpace(TreeItem parentItem, IDebugObject debugObject) {
        TreeItem[] items = parentItem.getItems();
        int itemSize = items.length;
        if (itemSize > 0) {
            TreeItem item = null;
            for (int index = 0; index < itemSize; index++) {
                item = items[index];
                if (item.getData() instanceof Namespace) {
                    Namespace namespace = (Namespace) item.getData();
                    if (namespace.getOid() == debugObject.getNameSpaceId()) {
                        viewer.setExpandedState(item.getData(), true);
                        findAndSelectDebugObjectGroup(item, debugObject);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Find and expand object group element under namespace.
     *
     * @param parentItem the parent item
     * @param debugObject the debug object
     */
    private void findAndSelectDebugObjectGroup(TreeItem parentItem, IDebugObject debugObject) {
        TreeItem[] items = parentItem.getItems();
        int itemSize = items.length;
        if (itemSize > 0) {
            TreeItem item = null;
            for (int index = 0; index < itemSize; index++) {
                item = items[index];
                if (item.getData() instanceof DebugObjectGroup) {
                    DebugObjectGroup objectGroup = (DebugObjectGroup) item.getData();

                    if (NamespaceUtilsBase.getDebugObjectTypeByGroupType(objectGroup.getObjectGroupType())
                            .equals(debugObject.getObjectType())) {
                        viewer.setExpandedState(item.getData(), true);
                        findAndSelectDebugObject(item, debugObject);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Sets the selection.
     *
     * @param selectObject the new selection
     */
    public void setSelection(Object selectObject) {
        viewer.setSelection(new StructuredSelection(selectObject), true);
    }

    /**
     * Find and select debug object under the group.
     *
     * @param parentItem the parent item
     * @param debugObject the debug object
     */
    private void findAndSelectDebugObject(TreeItem parentItem, IDebugObject debugObject) {
        TreeItem[] items = parentItem.getItems();

        int itemSize = items.length;
        if (itemSize > 0) {
            TreeItem item = null;
            for (int index = 0; index < itemSize; index++) {
                item = items[index];
                if (!item.isDisposed() && item.getData() instanceof DebugObjects) {
                    DebugObjects curDebugObject = (DebugObjects) item.getData();
                    if (curDebugObject.getOid() == debugObject.getOid()) {
                        viewer.setExpandedState(item.getData(), true);
                        viewer.setSelection(new StructuredSelection(item.getData()), true);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }

    }

    /**
     * The listener interface for receiving OBSelectionChanged events. The class
     * that is interested in processing a OBSelectionChanged event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addOBSelectionChangedListener<code> method. When the
     * OBSelectionChanged event occurs, that object's appropriate method is
     * invoked. OBSelectionChangedEvent
     */
    private class OBSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            selectionService.setSelection(selection);

            // When elements selected have to be handled, use this.

            if (selection instanceof IStructuredSelection) {
                Object obj = ((IStructuredSelection) selection).getFirstElement();

                if (null != obj && obj instanceof ColumnMetaData) {
                    UIElement.getInstance().toggleSetColumnNotNullCheck(((ColumnMetaData) obj).isNotNull());
                }
            }

            MPPDBIDELoggerUtility.debug("GUI: ObjectBrowser: Tree item selected.");
        }
    }

    /**
     * Title: class Description: The Class TreeviwerHelper. Copyright (c) Huawei
     * Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class TreeviwerHelper implements ITreeViewerListener {

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {

        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            Object obj = event.getElement();
            if (obj instanceof Namespace) {
                final Namespace namespace = (Namespace) obj;
                loadNamespaceObject(namespace);
            } else if (obj instanceof TableMetaData) {
                TableMetaData tbl = (TableMetaData) obj;
                /*
                 * this is a special case where the table is not loaded; and
                 * this case happens when the number of objects in the group is
                 * more than a certain threshold.Refer namespace.java for
                 * threshold details. Now user has expanded a table which is not
                 * loaded fully. DS must now fetch the data.
                 */
                if (!tbl.isLoaded()) {
                    loadTableObject(tbl);
                }
            } else if (obj instanceof ViewMetaData) {
                ViewMetaData view = (ViewMetaData) obj;
                if (!view.isLoaded()) {
                    loadViewObject(view);
                }
            }
        }

    }

    /**
     * Load namespace object.
     *
     * @param namespace the namespace
     */
    public static void loadNamespaceObject(final Namespace namespace) {
        Database db = namespace.getDatabase();
        if (namespace.isNotLoaded()) {
            if (db.isLoadingNamespaceInProgress()) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, GUISM.REFRESH));
                return;
            }
            if (!namespace.getDatabase().getSearchPathHelper().getSearchPath().contains(namespace.getName())) {
                namespace.getDatabase().getSearchPathHelper().getSearchPath().add(namespace.getName());
            }
            db.setLoadingNamespaceInProgress(true);
            LoadLevel1Objects load = new LoadLevel1Objects(namespace, null);
            try {
                load.loadObjects();
            } catch (DatabaseCriticalException event) {
                MPPDBIDELoggerUtility.error("ObjectBrowser: Loading objects failed.", event);
            }
        }
    }

    private static void loadTableObject(final TableMetaData table) {
        try {
            loadSeverObject(table, table.getDatabase());
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("ObjectBrowser: Loading table objects failed.", exception);
        }
    }

    private static void loadViewObject(final ViewMetaData view) {
        try {
            loadSeverObject(view, view.getDatabase());
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("ObjectBrowser: Loading view objects failed.", exception);
        }
    }

    private static void loadSeverObject(final ServerObject table, Database db) throws DatabaseCriticalException {
        if (db.isLoadingNamespaceInProgress()) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXECTION_IN_PROGRESS, GUISM.REFRESH));
            return;
        }
        db.setLoadingNamespaceInProgress(true);
        LoadLevel1Objects load = new LoadLevel1Objects(table, null);
        load.loadObjects();
    }

    /**
     * Update.
     *
     * @param obj the obj
     * @param searchStus the search stus
     */
    @Override
    public void update(Observable obj, Object searchStus) {
        SearchObjectEnum searchStatus = (SearchObjectEnum) searchStus;
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        int dbCount = null != databaseListControl ? databaseListControl.getConnectedDbCount() : 0;
        Text filterText = tree.getFilterText();
        boolean args = false;
        if (searchStatus.equals(SearchObjectEnum.SEARCH_START)) {
            args = true;
        }

        boolean isEnable = (dbCount > 0) && !args;
        filterText.setEnabled(isEnable);
        searchItem.setEnabled(isEnable);
        if (searchStatus.equals(SearchObjectEnum.DATABASELIST_UPDATE)) {
            refreshIteam.setEnabled(dbCount > 0);
        }

    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(Object obj) {
        viewer.remove(obj);
    }

    /**
     * Gets the selection.
     *
     * @return the selection
     */
    public Object getSelection() {
        return selectionService.getSelection();
    }

    private void doubleClickConnectToDB(Object obj, Shell shell) {
        Database db;
        db = (Database) obj;
        if (!db.isConnected()) {
            ConnectToDB connectDb = new ConnectToDB();
            connectDb.connect(db, shell);
        }
    }

    private void initDebugObjects() {
        Command command = commandService.getCommand("com.huawei.mppdbide.command.id.viewsourceobjectbrowseritem");
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
        handlerService.executeHandler(parameterizedCommand);
    }

    private void performLoadingObjects(DoubleClickEvent event) {
        ISelection selection = event.getSelection();
        if (selection instanceof IStructuredSelection) {
            Object obj = ((IStructuredSelection) selection).getFirstElement();
            if (null != obj) {
                if (obj instanceof Database) {
                    doubleClickConnectToDB(obj, null);
                }
                if (obj instanceof LoadingUIElement) {
                    return;
                }
                if (obj instanceof ShowMoreObject) {
                    ShowMoreObject loadObj = (ShowMoreObject) obj;
                    loadObj.showNextBatch();
                    viewer.refresh(((ShowMoreObject) obj).getParent());
                    return;
                }
                if (obj instanceof IDebugObject) {
                    initDebugObjects();
                } else if (obj instanceof TriggerMetaData) {
                    TriggerMetaData trigger = (TriggerMetaData) obj;
                    SQLTerminal terminal = UIElement.getInstance().createNewTerminal(trigger.getDatabase());
                    if (null != terminal) {
                        Document doc = new Document(trigger.getHeader() + trigger.getDdlMsg());
                        terminal.getTerminalCore().setDocument(doc, 0);
                        terminal.resetSQLTerminalButton();
                        terminal.resetAutoCommitButton();
                        terminal.setModified(true);
                        terminal.setModifiedAfterCreate(true);
                        terminal.registerModifyListener();
                    }
                } else if (isValidObj(obj)) {
                    checkIfObjectValid(obj);
                } else if (((ServerObject) obj).isLoaded()) {
                    viewer.setExpandedState(obj, isExpand(obj));
                } else if (obj instanceof Namespace) {
                    Namespace ns = (Namespace) obj;
                    loadNamespaceObject(ns);
                } else if (obj instanceof TableMetaData) {
                    TableMetaData table = (TableMetaData) obj;
                    loadTableObject(table);
                } else if (obj instanceof ViewMetaData) {
                    ViewMetaData view = (ViewMetaData) obj;
                    loadViewObject(view);
                }
            }
        }
    }

    private boolean isExpand(Object obj) {
        return !viewer.getExpandedState(obj);
    }

}
