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

package com.huawei.mppdbide.view.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartSashContainerImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartStackImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledMenuItemImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.MenuImpl;
import org.eclipse.e4.ui.model.application.ui.menu.impl.ToolControlImpl;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.autorefresh.RefreshObjectDetails;
import com.huawei.mppdbide.presentation.erd.AbstractERPresentation;
import com.huawei.mppdbide.presentation.search.SearchObjCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.autorefresh.RefreshObjects;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.BatchDropUIWindow;
import com.huawei.mppdbide.view.ui.ConsoleWindow;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.ui.DefaultSchemaControl;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.autosave.AutoSaveTerminalStatus;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.ui.erd.ERPart;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualPlanWindowShutdownListener;
import com.huawei.mppdbide.view.utils.common.SourceViewerUtil;
import com.huawei.mppdbide.view.utils.common.UICommonUtil;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class UIElement.
 *
 * @since 3.0.0
 */
public final class UIElement {

    private static final String STATUS_MSG_BAR = "com.huawei.mppdbide.view.statusbar.statusmsg";

    private static final String STATUS_BAR_DRAG = "Drag Placerholder";

    private static volatile UIElement instance = null;

    private static final Object INSTANCE_LOCK = new Object();

    private static final String UI_PART_EDITWINDOWTERMINAL_ID = "com.huawei.mppdbide.command.id.editresultdata";

    private static final String UI_PART_EDITWINDOWVIEWER_ID = "com.huawei.mppdbide.part.id.editresultdata";

    private static final String UI_TOOLBAR_DB_LIST_CTRL = "com.huawei.mppdbide.view.toolcontrol.databaselist";

    private static final String UI_TOOLBAR_DEFAULT_SCHEMA_CTRL = "com.huawei.mppdbide.view.toolcontrol.defaultschema";

    private static final int MAX_TABS_ALLOWED = 100;

    private static final String RELEASE_NAME = "Data Studio";

    private EPartService partService;

    private EModelService modelService;

    private MApplication application;

    private MToolControl progressBarElement;

    private Map<String, Integer> sqlTerminalIds;

    private int resultWindowCounter = 0;

    private final Object resultWindowCounterLock = new Object();

    private static UIElementUtils uiUtils;

    /**
     * Default constructor made private to make this class singleton.
     */
    private UIElement(EPartService partService, EModelService modelService, MApplication application) {
        // Making this class singleton.

        this.partService = partService;
        this.modelService = modelService;
        this.application = application;
        this.sqlTerminalIds = new HashMap<String, Integer>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    /**
     * Gets the single instance of UIElement.
     *
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     * @return single instance of UIElement
     */
    public static UIElement getInstance(EPartService partService, EModelService modelService,
            MApplication application) {
        if (null == instance) {
            synchronized (INSTANCE_LOCK) {
                if (null == instance) {
                    instance = new UIElement(partService, modelService, application);
                    EclipseInjections.getInstance().setPartService(partService);
                    EclipseInjections.getInstance().setModelService(modelService);
                    EclipseInjections.getInstance().setApplication(application);
                    DSViewDataManager.getInstance()
                            .setSourceViewerId(new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE));
                    uiUtils = new UIElementUtils(partService, modelService, application);
                }
            }
        }
        return instance;
    }

    /**
     * Gets the application.
     *
     * @return the application
     */
    public MApplication getApplication() {
        return application;
    }

    /**
     * Gets the part service.
     *
     * @return the part service
     */
    public EPartService getPartService() {
        return partService;
    }

    /**
     * Gets the model service.
     *
     * @return the model service
     */
    public EModelService getModelService() {
        return modelService;
    }

    /**
     * Gets the single instance of UIElement.
     *
     * @return single instance of UIElement
     */
    public static UIElement getInstance() {
        return instance;
    }

    /**
     * Removes the part from stack.
     *
     * @param id the id
     */
    public void removePartFromStack(String id) {
        UICommonUtil.removePartFromStack(id);
    }

    /**
     * Disable part in stack.
     *
     * @param db the db
     */
    public void disablePartInStack(Database db) {
        Collection<MPart> parts = partService.getParts();
        if (parts != null) {
            for (Iterator<MPart> iterator = parts.iterator(); iterator.hasNext();) {
                MPart mPart = (MPart) iterator.next();

                if (mPart.getObject() instanceof SQLTerminal) {
                    SQLTerminal terminal = (SQLTerminal) mPart.getObject();
                    if (terminal.getSelectedDatabase() == db) {
                        terminal.resetConnButtons(false);
                    }
                }
            }
        }
    }

    /**
     * Gets the total source viewer count.
     *
     * @return the total source viewer count
     */
    public int getTotalSourceViewerCount() {

        return uiUtils.getTotalSourceViewerCount();
    }

    /**
     * Gets the part specific node window.
     *
     * @param stack the stack
     * @param title the title
     * @return the part specific node window
     */
    public MPart getPartSpecificNodeWindow(MPartStack stack, String title) {
        MPart part;
        List<MStackElement> eles = stack.getChildren();
        if (null != eles) {
            for (MStackElement elements : eles) {
                if (elements instanceof MPart) {
                    part = (MPart) elements;
                    if (title.equalsIgnoreCase(part.getLabel())) {
                        return part;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Open max source viewer dialog.
     */
    public void openMaxSourceViewerDialog() {
        uiUtils.openMaxSourceViewerDialog();
    }

    /**
     * Open max source viewer dialog startup.
     */
    public void openMaxSourceViewerDialogStartup() {
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.MAX_SOURCE_VIEWER),
                MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_STARTUP_MAX_TAB_REACHED));

    }

    /**
     * Creates the editor.
     *
     * @param debugObject the debug object
     * @return the PL source editor
     */
    public PLSourceEditor createEditor(IDebugObject debugObject) {
        MPart part = null;
        Collection<MPart> parts = partService.getParts();
        if (null != parts) {
            for (Iterator<MPart> iterator = parts.iterator(); iterator.hasNext();) {
                MPart mPart = (MPart) iterator.next();

                String mpartId = mPart.getElementId();
                if (null != mpartId && mpartId.equals(debugObject.getPLSourceEditorElmId())) {
                    String objectType = mPart.getProperties().get(OBJECTTYPE.class.getSimpleName());
                    if (objectType != null && objectType.equals(debugObject.getObjectType().name())) {
                        part = mPart;
                        part.setTooltip(debugObject.getPLSourceEditorElmTooltip());
                        break;
                    }
                }
            }
        }
        if (null != part) {
            if (null == part.getObject()) {
                partService.activate(part);
                part.setTooltip(debugObject.getPLSourceEditorElmTooltip());
            }
            partService.bringToTop(part);
            return (PLSourceEditor) part.getObject();

        } else {
            if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
                openMaxSourceViewerDialog();
                return null;
            } else {
                return uiUtils.createNewEditor(debugObject);
            }
        }
    }

    /**
     * Creates the editor.
     *
     * @param debugObject the debug object
     * @param id the id
     * @param label the label
     * @param toolTip the tool tip
     * @param isDirtyFlag the is dirty flag
     * @return the PL source editor
     */
    public PLSourceEditor createEditor(IDebugObject debugObject, String id, String label, String toolTip,
            boolean isDirtyFlag) {
        MPart newpart = MBasicFactory.INSTANCE.createPart();

        PLSourceEditor editor = new PLSourceEditor();
        editor.setSyntax(debugObject.getDatabase() != null ? debugObject.getDatabase().getSqlSyntax() : null);
        String label1 = uiUtils.setNewPartProperties(debugObject, id, label, toolTip, isDirtyFlag, newpart, editor);

        switch (debugObject.getObjectType()) {
            case PLSQLFUNCTION: {
                newpart.setIconURI(
                        IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONPLSQL_DISCONNECTED, this.getClass()));
                break;
            }
            case SQLFUNCTION: {
                newpart.setIconURI(
                        IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONSQL_DISCONNECTED, this.getClass()));
                break;
            }
            case CFUNCTION: {
                newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONC_DISCONNECTED, this.getClass()));
                break;
            }
            case PROCEDURE: {
                newpart.setIconURI(
                        IconUtility.getIconImageUri(IiconPath.ICON_PROCEDURE_PL_DISCONNECTED, this.getClass()));
                break;
            }
            default: {
                return null;
            }
        }

        SourceViewerUtil.addSourceViewerId(id);

        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        uiUtils.addNewPartIntoStackList(newpart, stacks);

        PLSourceEditor srcEditor = (PLSourceEditor) newpart.getObject();
        srcEditor.setDebugObject(debugObject);
        srcEditor.setElementID(id);
        srcEditor.setTabLabel(label1);
        srcEditor.setTabToolTip(toolTip);
        return srcEditor;
    }

    /**
     * Gets the terminal.
     *
     * @param id the id
     * @return the terminal
     */
    public SQLTerminal getTerminal(String id) {
        MPart part = partService.findPart(id);

        if (null != part) {
            if (null == part.getObject()) {
                partService.activate(part);
            }
            if (part.getObject() instanceof SQLTerminal) {
                return (SQLTerminal) part.getObject();
            }
        }
        return null;
    }

    /**
     * Creates the new terminal.
     *
     * @param db the db
     * @return the SQL terminal
     */
    public SQLTerminal createNewTerminal(Database db) {

        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();
            return null;

        } else {
            SQLTerminal terminal2 = new SQLTerminal();
            terminal2.setExecuteDB(db);

            MPart newpart = MBasicFactory.INSTANCE.createPart();
            newpart.setToBeRendered(true);
            newpart.setVisible(true);
            newpart.setContributionURI(
                    "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.terminal.SQLTerminal");
            newpart.setCloseable(true);
            newpart.setOnTop(true);

            String dbName = null;
            dbName = db.getName() + '@' + db.getServerName();
            String id = getTerminalID(dbName);
            terminal2.setUiID(id);

            String label = id.substring(0,
                    id.length() > MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                            ? MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                            : id.length());
            newpart.setLabel(label);
            newpart.setElementId(id);
            newpart.setObject(terminal2);

            List<String> tags = newpart.getTags();
            if (null != tags) {
                tags.add(IPresentationEngine.NO_MOVE);
                tags.add(EPartService.REMOVE_ON_HIDE_TAG);
            }
            newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_SQL_TERMINAL, this.getClass()));
            newpart.setTooltip(id);
            terminal2.setTabToolTip(id);

            uiUtils.activateEditorPart(newpart);
            SQLTerminal terminal = (SQLTerminal) newpart.getObject();
            terminal2.setPartLabel(label);
            terminal.setPartLabel(label);
            terminal.setTabToolTip(id);
            terminal.registerModifyListener();
            terminal.updateStatus(AutoSaveTerminalStatus.LOAD_FINISHED);
            terminal.setDefLabelId(dbName);
            return terminal;
        }
    }

    /**
     * Creates the new terminal.
     *
     * @param userrole the userrole
     * @return the SQL terminal
     */
    public SQLTerminal createNewTerminal(UserRole userrole) {

        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();
            return null;

        } else {
            SQLTerminal terminalTablSpa = new SQLTerminal();
            terminalTablSpa.setExecuteDB(userrole.getDatabase());

            MPart newpart = MBasicFactory.INSTANCE.createPart();
            newpart.setToBeRendered(true);
            newpart.setVisible(true);
            newpart.setContributionURI(
                    "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.terminal.SQLTerminal");
            newpart.setCloseable(true);
            newpart.setOnTop(true);

            String dbName = null;

            dbName = userrole.getName() + '@' + userrole.getServer().getName();
            String id = getTerminalID(dbName);
            terminalTablSpa.setUiID(id);
            newpart.setLabel(id);
            newpart.setElementId(id);
            newpart.setObject(terminalTablSpa);

            newpart.getTags().add(IPresentationEngine.NO_MOVE);
            newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_SQL_TERMINAL, this.getClass()));
            newpart.setTooltip(id);

            uiUtils.activateEditorPart(newpart);
            SQLTerminal terminal = (SQLTerminal) newpart.getObject();
            Database db = null;
            if (userrole.getServer().isAleastOneDbConnected()) {
                Iterator<Database> dbItr = userrole.getServer().getAllDatabases().iterator();
                boolean hasNext = dbItr.hasNext();

                while (hasNext) {
                    db = dbItr.next();
                    if (db.isConnected()) {
                        break;
                    }
                    hasNext = dbItr.hasNext();
                }
            }
            terminal.setExecuteDB(db);
            terminal.setUiID(id);
            terminal.setPartLabel(id);
            terminal.setDefLabelId(dbName);
            return terminal;
        }
    }

    /**
     * Creates the new terminal.
     *
     * @param tSpace the t space
     * @return the SQL terminal
     */
    public SQLTerminal createNewTerminal(Tablespace tSpace) {

        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();
            return null;

        } else {
            SQLTerminal terminalTablSpa = new SQLTerminal();
            terminalTablSpa.setExecuteDB(tSpace.getDatabase());
            String dbName = null;
            dbName = tSpace.getName() + '@' + tSpace.getServer().getName();
            String id = getTerminalID(dbName);
            terminalTablSpa.setUiID(id);
            terminalTablSpa.setTabToolTip(id);
            String label = id.substring(0,
                    id.length() > MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                            ? MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                            : id.length());
            MPart newpart = uiUtils.createNewPart(terminalTablSpa, id, label);

            List<String> tags = newpart.getTags();
            if (null != tags) {
                tags.add(IPresentationEngine.NO_MOVE);
                tags.add(EPartService.REMOVE_ON_HIDE_TAG);
            }
            newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_SQL_TERMINAL, this.getClass()));
            newpart.setTooltip(id);

            uiUtils.activateEditorPart(newpart);
            SQLTerminal terminal = uiUtils.setTerminalData(tSpace, newpart, dbName, id, label);
            return terminal;
        }
    }

    /**
     * Creates the new terminal.
     *
     * @param dbNameParam the db name param
     * @param serverName the server name
     * @param id the id
     * @param label the label
     * @param toolTip the tool tip
     * @return the SQL terminal
     */
    public SQLTerminal createNewTerminal(String dbNameParam, String serverName, String id, String label,
            String toolTip) {
        SQLTerminal terminal2 = new SQLTerminal();
        String dbName = dbNameParam;
        MPart newpart = MBasicFactory.INSTANCE.createPart();
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI(
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.terminal.SQLTerminal");
        newpart.setCloseable(true);
        newpart.setOnTop(true);

        String label1 = label.substring(0,
                label.length() > MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        ? MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        : label.length());
        newpart.setLabel(label1);
        dbName = dbName + '@' + serverName;
        getTerminalID(dbName);
        newpart.setElementId(id);
        newpart.setObject(terminal2);

        List<String> tags = newpart.getTags();
        if (null != tags) {
            tags.add(IPresentationEngine.NO_MOVE);
            tags.add(EPartService.REMOVE_ON_HIDE_TAG);
        }
        newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_SQL_TERMINAL_DISCONNECTED, this.getClass()));
        newpart.setTooltip(toolTip);

        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        uiUtils.addNewPartIntoStackList(newpart, stacks);

        SQLTerminal terminal = (SQLTerminal) newpart.getObject();
        terminal.setExecuteDB(null);
        terminal.setUiID(id);
        terminal.setTabToolTip(toolTip);
        terminal.setPartLabel(label1);
        terminal.registerModifyListener();
        terminal.setDefLabelId(dbName);
        return terminal;
    }

    /**
     * Gets the terminal ID.
     *
     * @param key the key
     * @return the terminal ID
     */
    private String getTerminalID(String key) {
        String newName = "";
        if (sqlTerminalIds.containsKey(key)) {
            newName = generateNewName(key);
        } else {
            sqlTerminalIds.put(key, 1);
            newName = key + " (1)";
        }

        for (;;) {
            if (!isNameDuplicate(newName)) {
                break;
            }

            newName = generateNewName(key);
        }

        return newName;
    }

    /**
     * Generate new name.
     *
     * @param key the key
     * @return the string
     */
    private String generateNewName(String key) {
        int count = sqlTerminalIds.get(key);
        count++;
        sqlTerminalIds.put(key, count);
        return key + " (" + sqlTerminalIds.get(key) + ')';
    }

    /**
     * Checks if is name duplicate.
     *
     * @param userInput the user input
     * @return true, if is name duplicate
     */
    private boolean isNameDuplicate(String userInput) {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> eles = stacks.get(0).getChildren();
            if (null != eles) {
                for (MStackElement elements : eles) {
                    if (elements instanceof MPart) {
                        MPart part = (MPart) elements;
                        if (userInput.equalsIgnoreCase(part.getLabel())) {
                            return true;
                        } else if (userInput.equalsIgnoreCase(part.getElementId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the main window.
     *
     * @return the main window
     */
    public Object getMainWindow() {
        return modelService.find(UIConstants.UI_MAIN_WINDOW_ID, application);
    }

    /**
     * Checks if is main window.
     *
     * @param shell the shell
     * @return true, if is main window
     */
    public boolean isMainWindow(Shell shell) {
        if (null != shell && !shell.isDisposed()) {
            Object obj = shell.getData("modelElement");
            if (obj instanceof MTrimmedWindow && obj instanceof TrimmedWindowImpl) {
                TrimmedWindowImpl trimWindow = (TrimmedWindowImpl) obj;
                if (UIConstants.UI_MAIN_WINDOW_ID.equals(trimWindow.getElementId())) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Gets the editor by id.
     *
     * @param debugObject the debug object
     * @param bringOnTop the bring on top
     * @return the editor by id
     */
    public PLSourceEditor getEditorById(IDebugObject debugObject, boolean bringOnTop) {
        MPart part = partService.findPart(debugObject.getPLSourceEditorElmId());

        if (null != part) {
            if (null == part.getObject()) {
                partService.activate(part);
            }
            if (bringOnTop) {
                partService.bringToTop(part);
            }
            return (PLSourceEditor) part.getObject();
        }
        return null;

    }

    /**
     * Gets the editor model by id.
     *
     * @param debugObject the debug object
     * @return the editor model by id
     */
    public PLSourceEditor getEditorModelById(IDebugObject debugObject) {
        if (debugObject != null) {
            MPart part = partService.findPart(debugObject.getPLSourceEditorElmId());
            if (null == part) {
                createEditor(debugObject);
                part = partService.findPart(debugObject.getPLSourceEditorElmId());
                if (null == part) {
                    return null;
                }

            }
            if (null == part.getObject()) {
                partService.activate(part);
            }

            if (!(part.getObject() instanceof PLSourceEditor)) {
                return null;
            }

            return (PLSourceEditor) part.getObject();
        }
        return null;
    }

    /**
     * Gets the editor model by id and activate.
     *
     * @param debugObject the debug object
     * @return the editor model by id and activate
     */
    public PLSourceEditor getEditorModelByIdAndActivate(IDebugObject debugObject) {
        MPart part = partService.findPart(debugObject.getPLSourceEditorElmId());
        if (null == part) {
            createEditor(debugObject);
            part = partService.findPart(debugObject.getPLSourceEditorElmId());
            if (null == part) {
                return null;
            }
        }

        if (!(part.getObject() instanceof PLSourceEditor)) {
            return null;
        }
        partService.activate(part);

        return (PLSourceEditor) part.getObject();
    }

    /**
     * Checks if is editor exist by dbg obj.
     *
     * @param debugObject the debug object
     * @return true, if is editor exist by dbg obj
     */
    public boolean isEditorExistByDbgObj(IDebugObject debugObject) {
        MPart part = partService.findPart(debugObject.getPLSourceEditorElmId());
        if (null != part) {
            return true;
        }
        return false;
    }

    /**
     * Out of memory catch.
     *
     * @param elapsedTime the elapsed time
     * 
     * @param errorMessage the error message
     */
    public void outOfMemoryCatch(String elapsedTime, String errorMessage) {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED)
                + "Time Elapsed in operation:" + elapsedTime);
        StringBuilder errMsg = new StringBuilder(
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));

        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getErrorFromConst(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY), errMsg.toString());
    }

    /**
     * Gets the visible source viewer.
     *
     * @return the visible source viewer
     */
    public PLSourceEditor getVisibleSourceViewer() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersItr = children.iterator();

                cleanupSourceViewerIds();
                MStackElement stackElement = null;
                MPart mPart = null;
                TrimmedWindowImpl obj = (TrimmedWindowImpl) UIElement.getInstance().getMainWindow();
                boolean hasNext = sourceViewersItr.hasNext();
                while (hasNext) {
                    stackElement = sourceViewersItr.next();
                    String elementId = stackElement.getElementId();
                    if (null != elementId && elementId.equalsIgnoreCase(STATUS_BAR_DRAG)) {
                        return null;
                    }
                    mPart = (MPart) stackElement;

                    if (partService.isPartVisible(mPart)) {
                        setDisplayLabel(mPart, obj);
                        if (mPart.getObject() instanceof PLSourceEditor) {
                            PLSourceEditor sourceEditor = (PLSourceEditor) mPart.getObject();
                            if (null == sourceEditor.getDebugObject() || sourceEditor.getDebugObject().getOid() < 1) {
                                hasNext = sourceViewersItr.hasNext();
                                continue;
                            } else {
                                return sourceEditor;
                            }

                        }
                    }
                    hasNext = sourceViewersItr.hasNext();
                }
            }
        }
        return null;
    }

    /**
     * set the tab name in application window.
     *
     * @return NA
     */
    private void setDisplayLabel(MPart mPart, TrimmedWindowImpl obj) {
        obj.setLabel(RELEASE_NAME + " : " + mPart.getLabel());
    }

    /**
     * Gets the visible terminal.
     *
     * @return the visible terminal
     */
    public SQLTerminal getVisibleTerminal() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks) && stacks.get(0).getChildren() != null) {
            Iterator<MStackElement> sourceViewersItr = stacks.get(0).getChildren().iterator();

            cleanupSourceViewerIds();
            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersItr.hasNext();
            TrimmedWindowImpl obj = (TrimmedWindowImpl) UIElement.getInstance().getMainWindow();
            while (hasNext) {
                stackElement = sourceViewersItr.next();
                String elementId = stackElement.getElementId();
                if (elementId != null && elementId.equalsIgnoreCase(STATUS_BAR_DRAG)) {
                    return null;
                }
                mPart = (MPart) stackElement;

                if (partService.isPartVisible(mPart)) {
                    setDisplayLabel(mPart, obj);
                    if (mPart.getObject() instanceof SQLTerminal) {
                        SQLTerminal sourceEditor = (SQLTerminal) mPart.getObject();
                        return sourceEditor;

                    }
                }
                hasNext = sourceViewersItr.hasNext();
            }
        }
        return null;
    }

    /**
     * Cleanup source viewer ids.
     */
    public void cleanupSourceViewerIds() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersItr = children.iterator();
                Iterator<String> itrSVIds = DSViewDataManager.getInstance().getSourceViewerId().iterator();

                boolean hasSourceViewer = false;
                String svId = null;
                MStackElement stackElement = null;
                MPart mPart = null;
                boolean hasNextSVIds = itrSVIds.hasNext();
                boolean hasNext = sourceViewersItr.hasNext();
                while (hasNextSVIds) {
                    svId = itrSVIds.next();

                    while (hasNext) {
                        stackElement = sourceViewersItr.next();
                        mPart = (MPart) stackElement;

                        if (mPart.getObject() instanceof PLSourceEditor) {
                            if (svId.equalsIgnoreCase(mPart.getElementId())) {
                                hasSourceViewer = true;
                                hasNext = sourceViewersItr.hasNext();
                                break;
                            }
                        }
                        hasNext = sourceViewersItr.hasNext();
                    }

                    if (!hasSourceViewer) {
                        itrSVIds.remove();

                    }
                    hasSourceViewer = false;
                    List<MStackElement> children2 = stacks.get(0).getChildren();
                    if (null != children2) {
                        sourceViewersItr = children2.iterator();
                        hasNextSVIds = itrSVIds.hasNext();
                    }
                }
            }
        }
    }

    /**
     * Close all source viewer.
     */
    public void closeAllSourceViewer() {
        List<MPartStack> stack = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stack)) {
            List<MStackElement> children = stack.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersIterator = children.iterator();
                MStackElement stackElementVal = null;
                MPart mPartVal = null;
                boolean hasNextRcrd = sourceViewersIterator.hasNext();
                while (hasNextRcrd) {
                    stackElementVal = sourceViewersIterator.next();
                    mPartVal = (MPart) stackElementVal;

                    if (mPartVal.getObject() instanceof PLSourceEditor) {
                        PLSourceEditor sourceEditor = (PLSourceEditor) mPartVal.getObject();
                        if (!sourceEditor.isObjDirty()) {
                            sourceEditor.destroy();
                            SourceViewerUtil.removeSourceViewerId(mPartVal.getElementId());
                            sourceViewersIterator = stack.get(0).getChildren().iterator();
                        }
                    }

                    hasNextRcrd = sourceViewersIterator.hasNext();
                }
            }
        }
    }

    /**
     * Close source viewer by id.
     *
     * @param id the id
     */
    public void closeSourceViewerById(String id) {
        List<MPartStack> stacksList = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacksList)) {
            List<MStackElement> children = stacksList.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> srceViewersIt = children.iterator();
                MStackElement stackElement = null;
                MPart mPart = null;
                boolean hasNextRc = srceViewersIt.hasNext();
                while (hasNextRc) {
                    stackElement = srceViewersIt.next();
                    mPart = (MPart) stackElement;
                    String elementId = mPart.getElementId();
                    if ((mPart.getObject() instanceof PLSourceEditor) && null != elementId && elementId.equals(id)) {
                        PLSourceEditor sourceEditor = (PLSourceEditor) mPart.getObject();
                        // added condition for protection
                        if (null != sourceEditor.getDebugObject()) {
                            SourceViewerUtil.removeSourceViewerId(id, sourceEditor.getDebugObject().getType());
                            sourceEditor.destroy();
                        }
                        break;
                    }
                    hasNextRc = srceViewersIt.hasNext();
                }
            }
        }
    }

    /**
     * Reset source viewer editable.
     */
    public void resetSourceViewerEditable() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> chldren = stacks.get(0).getChildren();
            if (null != chldren) {
                Iterator<MStackElement> sourceViwersItr = chldren.iterator();
                MStackElement stackElemnt = null;
                MPart mPart = null;
                boolean hasNext = sourceViwersItr.hasNext();
                while (hasNext) {
                    stackElemnt = sourceViwersItr.next();
                    mPart = (MPart) stackElemnt;

                    if (null != mPart) {
                        if (mPart.getObject() instanceof PLSourceEditor) {
                            PLSourceEditor srcEditr = (PLSourceEditor) mPart.getObject();
                            if (null == srcEditr.getDebugObject() || srcEditr.getDebugObject().getOid() < 1) {
                                hasNext = sourceViwersItr.hasNext();
                                continue;
                            }
                            if (srcEditr.getSourceEditorCore() != null) {
                                srcEditr.getSourceEditorCore().setEditable(true);
                            }

                        }
                    }
                    hasNext = sourceViwersItr.hasNext();
                }
            }
        }
    }

    /**
     * Gets the all opened source viewer.
     *
     * @return the all opened source viewer
     */
    public List<PLSourceEditor> getAllOpenedSourceViewer() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        List<PLSourceEditor> lstSourceViewers = new ArrayList<PLSourceEditor>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersItr = children.iterator();
                MStackElement stackElement = null;
                MPart mPart = null;
                boolean hasNext = sourceViewersItr.hasNext();
                while (hasNext) {
                    stackElement = sourceViewersItr.next();
                    mPart = (MPart) stackElement;

                    if (null != mPart) {
                        if (mPart.getObject() instanceof PLSourceEditor) {
                            PLSourceEditor sourceEditor = (PLSourceEditor) mPart.getObject();
                            if (null == sourceEditor.getDebugObject() || sourceEditor.getDebugObject().getOid() < 1) {
                                hasNext = sourceViewersItr.hasNext();
                                continue;
                            }
                            lstSourceViewers.add(sourceEditor);
                        }
                    }
                    hasNext = sourceViewersItr.hasNext();
                }
            }
        }
        return lstSourceViewers;
    }

    /**
     * Gets the all open tabs.
     *
     * @return the all open tabs
     */
    public List<IAutoSaveObject> getAllOpenTabs() {
        List<MPart> openPartList = uiUtils.getAllOpenTabsPart();
        Iterator<MPart> partItr = openPartList.iterator();
        List<IAutoSaveObject> lstSourceViewers = new ArrayList<IAutoSaveObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        boolean hasNext = partItr.hasNext();
        while (hasNext) {
            MPart mPart = partItr.next();
            IAutoSaveObject obj = (IAutoSaveObject) mPart.getObject();
            if (obj != null) {
                lstSourceViewers.add(obj);
            }
            hasNext = partItr.hasNext();
        }
        return lstSourceViewers;
    }

    /**
     * Gets the all open terminals.
     *
     * @return the all open terminals
     */
    public List<IAutoSaveObject> getAllOpenTerminals() {
        List<MPart> openPartList = uiUtils.getAllOpenTabsPart();
        Iterator<MPart> partItr = openPartList.iterator();
        List<IAutoSaveObject> listOpenTerminals = new ArrayList<IAutoSaveObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        boolean hasNext = partItr.hasNext();
        while (hasNext) {
            MPart mPart = partItr.next();
            IAutoSaveObject obj = (IAutoSaveObject) mPart.getObject();
            if (obj != null && obj instanceof SQLTerminal) {
                listOpenTerminals.add(obj);
            }
            hasNext = partItr.hasNext();
        }
        return listOpenTerminals;
    }

    /**
     * Checks if is terminal group open.
     *
     * @param termDefaultLabel the term default label
     * @return true, if is terminal group open
     */
    public boolean isTerminalGroupOpen(String termDefaultLabel) {
        List<MPart> openPartList = uiUtils.getAllOpenTabsPart();
        Iterator<MPart> partItr = openPartList.iterator();
        boolean hasNext = partItr.hasNext();
        while (hasNext) {
            MPart mPart = partItr.next();
            IAutoSaveObject obj = (IAutoSaveObject) mPart.getObject();
            if (obj != null && obj instanceof SQLTerminal && termDefaultLabel.equals(obj.getDefLabelId())) {
                return true;
            }
            hasNext = partItr.hasNext();
        }
        return false;
    }


    /**
     * Bring on top view table data window.
     *
     * @param details the details
     */
    public void bringOnTopViewTableDataWindow(IWindowDetail details) {
        MPart part = partService.findPart(details.getUniqueID());

        if (null == part) {
            return;
        }

        partService.activate(part, true);

    }

    /**
     * Gets the specific window.
     *
     * @param details the details
     * @param uiManager the ui manager
     * @param partClassURI the part class URI
     * @return the specific window
     */
    private Object getSpecificWindow(IWindowDetail details, Object uiManager, String partClassURI) {
        MPart part = partService.findPart(details.getUniqueID());

        if (null == part) {
            part = uiUtils.createNewViewTableData(uiManager, details.getTitle(), details.getUniqueID(),
                    details.getIcon(), partClassURI);
        }

        if (part == null) {
            return null;
        }

        part.setLabel(details.getTitle());
        part.setTooltip(details.getTitle());

        if (null == part.getObject()) {
            partService.activate(part);
        }

        return part.getObject();
    }

    /**
     * Checks if is window exists.
     *
     * @param details the details
     * @return true, if is window exists
     */
    public boolean isWindowExists(IWindowDetail details) {
        MPart part = partService.findPart(details.getUniqueID());

        return !(null == part);
    }

    /**
     * Gets the batch drop window.
     *
     * @param details the details
     * @param batchDropUIManager the batch drop UI manager
     * @return the batch drop window
     */
    public Object getBatchDropWindow(IWindowDetail details, AbstractResultDisplayUIManager batchDropUIManager) {
        return getSpecificWindow(details, batchDropUIManager,
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.BatchDropUIWindow");
    }

    /**
     * Gets the view table data window.
     *
     * @param details the details
     * @param viewTableDataResultDisplayUIManager the view table data result
     * display UI manager
     * @return the view table data window
     */
    public Object getViewTableDataWindow(IWindowDetail details,
            AbstractResultDisplayUIManager viewTableDataResultDisplayUIManager) {
        return getSpecificWindow(details, viewTableDataResultDisplayUIManager,
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.ViewEditTableDataUIWindow");
    }

    /**
     * Gets the edits the table data window.
     *
     * @param details the details
     * @param editTableDataResultDisplayUIManager the edit table data result
     * display UI manager
     * @return the edits the table data window
     */
    public Object getEditTableDataWindow(IWindowDetail details,
            AbstractResultDisplayUIManager editTableDataResultDisplayUIManager) {
        return getSpecificWindow(details, editTableDataResultDisplayUIManager,
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.EditTableDataUIWindow");
    }

    /**
     * Gets the view object partition window.
     *
     * @param details the details
     * @param viewTableDataResultDisplayUIManager the view table data result
     * display UI manager
     * @return the view object partition window
     */
    public Object getViewObjectPartitionWindow(IWindowDetail details,
            AbstractResultDisplayUIManager viewTableDataResultDisplayUIManager) {
        return getSpecificWindow(details, viewTableDataResultDisplayUIManager,
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow");
    }

    /**
     * Gets the search object window.
     *
     * @param details the details
     * @param core the core
     * @return the search object window
     */
    public Object getSearchObjectWindow(IWindowDetail details, SearchObjCore core) {
        return getSpecificWindow(details, core,
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.search.SearchWindow");
    }

    /**
     * Gets the object browser model.
     *
     * @return the object browser model
     */
    public ObjectBrowser getObjectBrowserModel() {
        MPart part = partService.findPart(UIConstants.UI_PART_OBJECT_BROWSER_ID);
        if (part == null) {
            return null;
        }
        if (null == part.getObject()) {
            partService.activate(part);
        }

        if (!(part.getObject() instanceof ObjectBrowser)) {
            return null;
        }

        return (ObjectBrowser) part.getObject();
    }

    /**
     * Refresh object browser part.
     */
    public void refreshObjectBrowserPart() {
        if (!isObjectBrowserPartOpen()) {
            return;
        }

        MPart part = partService.findPart(UIConstants.UI_PART_OBJECT_BROWSER_ID);
        if (part != null) {
            if (null == part.getObject()) {
                partService.activate(part);
            }

            if (!(part.getObject() instanceof ObjectBrowser)) {
                return;
            }

            ((ObjectBrowser) part.getObject()).refresh();
        }
    }

    /**
     * Refresh SQL terminal.
     *
     * @return the SQL terminal
     */
    public SQLTerminal refreshSQLTerminal() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);

        if (uiUtils.isAtleastOnePartIsOpen(stacks) && stacks.get(0).getChildren() != null) {
            Iterator<MStackElement> sourceViewersItr = stacks.get(0).getChildren().iterator();

            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersItr.hasNext();
            while (hasNext) {
                stackElement = sourceViewersItr.next();
                mPart = (MPart) stackElement;

                if (mPart.getObject() instanceof SQLTerminal) {
                    SQLTerminal terminal = (SQLTerminal) mPart.getObject();
                    terminal.resetButtons();
                    if (terminal.getSelectedDatabase() != null) {
                        resetTabIcon(mPart, terminal.getSelectedDatabase().isConnected());
                    } else {
                        resetTabIcon(mPart, false);
                    }
                }
                hasNext = sourceViewersItr.hasNext();
            }
        }
        return null;
    }

    /**
     * Reset all SQL terminal connections.
     */
    public void resetAllSQLTerminalConnections() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            Iterator<MStackElement> srcViwersItr = stacks.get(0).getChildren().iterator();

            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = srcViwersItr.hasNext();
            while (hasNext) {
                stackElement = srcViwersItr.next();
                mPart = (MPart) stackElement;

                if (mPart.getObject() instanceof SQLTerminal) {
                    SQLTerminal terminal = (SQLTerminal) mPart.getObject();
                    terminal.resetButtons();
                    terminal.resetConnection();
                    if (terminal.getSelectedDatabase() != null) {
                        resetTabIcon(mPart, terminal.getSelectedDatabase().isConnected());
                    } else {
                        resetTabIcon(mPart, false);
                    }
                }
                hasNext = srcViwersItr.hasNext();
            }
        }
    }

    /**
     * Reset SQL terminal connections.
     *
     * @param server the server
     */
    public void resetSQLTerminalConnections(Server server) {
        if (server != null) {
            List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                    MPartStack.class, null);
            if (!uiUtils.isAtleastOnePartIsOpen(stacks)) {
                return;
            }
            Iterator<MStackElement> sourceViewersItr = stacks.get(0).getChildren().iterator();
            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersItr.hasNext();
            while (hasNext) {
                stackElement = sourceViewersItr.next();
                mPart = (MPart) stackElement;
                if (mPart.getObject() instanceof SQLTerminal) {
                    SQLTerminal terminal = (SQLTerminal) mPart.getObject();
                    if ((server.getServerConnectionInfo().getConectionName()).equals(terminal.getConnectionName())) {
                        terminal.resetButtons();
                        terminal.resetConnection();
                        if (terminal.getSelectedDatabase() != null) {
                            resetTabIcon(mPart, terminal.getSelectedDatabase().isConnected());
                        } else {
                            resetTabIcon(mPart, false);
                        }

                    }

                }
                hasNext = sourceViewersItr.hasNext();
            }
        }
    }

    /**
     * Reset connection related buttons.
     *
     * @param isConnectionPresent the is connection present
     */
    public void resetConnectionRelatedButtons(boolean isConnectionPresent) {
        List<MPartStack> elementStacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(elementStacks)) {
            Iterator<MStackElement> sourceViewersIterator = elementStacks.get(0).getChildren().iterator();

            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersIterator.hasNext();
            while (hasNext) {
                stackElement = sourceViewersIterator.next();
                mPart = (MPart) stackElement;

                if (mPart.getObject() instanceof IAutoSaveObject) {
                    IAutoSaveObject terminal = (IAutoSaveObject) mPart.getObject();
                    terminal.resetConnButtons(isConnectionPresent);
                }
                hasNext = sourceViewersIterator.hasNext();
            }
        }
    }

    /**
     * Refresh batch delete terminal.
     */
    public void refreshBatchDeleteTerminal() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            Iterator<MStackElement> sourceViewersItr = stacks.get(0).getChildren().iterator();

            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersItr.hasNext();
            while (hasNext) {
                stackElement = sourceViewersItr.next();
                mPart = (MPart) stackElement;

                if (mPart.getObject() instanceof BatchDropUIWindow) {
                    BatchDropUIWindow batchDeleteUIWindow = (BatchDropUIWindow) mPart.getObject();
                    if (batchDeleteUIWindow != null) {
                        batchDeleteUIWindow.disableButtons();
                    }

                }

                hasNext = sourceViewersItr.hasNext();
            }
        }
        return;
    }

    /**
     * Gets the sql terminal model.
     *
     * @return the sql terminal model
     */
    public SQLTerminal getSqlTerminalModel() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);

        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> sourceViewersItr = children.iterator();

                MStackElement stackElemnt = null;
                MPart mPart = null;
                boolean hasNext = sourceViewersItr.hasNext();
                while (hasNext) {
                    stackElemnt = sourceViewersItr.next();
                    mPart = (MPart) stackElemnt;

                    if (partService.isPartVisible(mPart)) {
                        if (mPart.getObject() instanceof SQLTerminal) {
                            SQLTerminal terminal = (SQLTerminal) mPart.getObject();
                            return terminal;

                        }
                    }
                    hasNext = sourceViewersItr.hasNext();
                }
            }
        }
        return null;
    }

    /**
     * Checks if is database connected.
     *
     * @return true, if is database connected
     */
    public boolean isDatabaseConnected() {

        SQLTerminal lSQLTerminal = getSqlTerminalModel();
        return null != lSQLTerminal && null != lSQLTerminal.getSelectedDatabase()
                && lSQLTerminal.getSelectedDatabase().isConnected();

    }

    /**
     * Gets the search window part object.
     *
     * @return the search window part object
     */
    public Object getSearchWindowPartObject() {
        MPart part = partService.findPart(UIConstants.UI_PART_SEARCHWINDOW_ID);
        if (null == part) {
            return null;
        }
        if (null == part.getObject()) {
            partService.activate(part);
            return null;
        }
        return part.getObject();
    }

    /**
     * Checks if is object browser part open.
     *
     * @return true, if is object browser part open
     */
    public boolean isObjectBrowserPartOpen() {
        MPart objectBrowserPart = partService.findPart(UIConstants.UI_PART_OBJECT_BROWSER_ID);
        if (objectBrowserPart != null) {
            return objectBrowserPart.isToBeRendered();
        }
        return false;
    }

    /**
     * Bring console window ontop.
     */
    public void bringConsoleWindowOntop() {
        bringPartOnTop(UIConstants.UI_PART_CONSOLE_ID);
    }

    /**
     * Bring search window on top.
     */
    public void bringSearchWindowOnTop() {
        bringPartOnTop(UIConstants.UI_PART_SEARCHWINDOW_ID);
    }

    /**
     * Bring part on top.
     *
     * @param partName the part name
     */
    public void bringPartOnTop(String partName) {
        MPart part = partService.findPart(partName);

        if (part == null) {
            return;
        }

        if (null == part.getObject()) {
            partService.activate(part);
            return;
        }

        if (!part.isVisible()) {
            part.setVisible(true);
        }

        part.setOnTop(true);
        partService.bringToTop(part);

    }

    /**
     * Gets the progress bar control.
     *
     * @return the progress bar control
     */
    public MToolControl getProgressBarControl() {
        if (null == progressBarElement) {
            this.progressBarElement = (MToolControl) modelService.find(UIElement.STATUS_MSG_BAR, application);
        }

        return this.progressBarElement;
    }

    /**
     * Gets the object browser part.
     *
     * @return the object browser part
     */
    public MPart getObjectBrowserPart() {
        MPart part = partService.findPart(UIConstants.UI_PART_OBJECT_BROWSER_ID);
        if (part != null && null == part.getObject()) {
            partService.activate(part);
        }
        return part;
    }

    /**
     * Gets the progress bar on top.
     *
     * @return the progress bar on top
     */
    public BottomStatusBar getProgressBarOnTop() {
        getProgressBarControl().setOnTop(true);

        if (!(progressBarElement.getObject() instanceof BottomStatusBar)) {
            return null;
        }

        return (BottomStatusBar) progressBarElement.getObject();
    }

    /**
     * Hide progress bar.
     */
    public void hideProgressBar() {
        // Now showing and hiding of the status bar is done as part of a job.
    }

    /**
     * Sets the status bar message.
     *
     * @param status the new status bar message
     */
    public void setStatusBarMessage(String status) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(status));
    }

    /**
     * Gets the database list control.
     *
     * @return the database list control
     */
    public DatabaseListControl getDatabaseListControl() {
        DatabaseListControl dbListControl = null;
        ToolControlImpl getNextRecordToolControl = (ToolControlImpl) modelService
                .find(UIElement.UI_TOOLBAR_DB_LIST_CTRL, application);
        if (getNextRecordToolControl != null) {

            dbListControl = (DatabaseListControl) getNextRecordToolControl.getObject();
        }

        return dbListControl;
    }

    /**
     * Gets the default schema control.
     *
     * @return the default schema control
     */
    public DefaultSchemaControl getDefaultSchemaControl() {
        DefaultSchemaControl defaultSchemaControl = null;
        ToolControlImpl defaultSchemaToolControl = (ToolControlImpl) modelService
                .find(UIElement.UI_TOOLBAR_DEFAULT_SCHEMA_CTRL, application);
        if (defaultSchemaToolControl != null) {

            defaultSchemaControl = (DefaultSchemaControl) defaultSchemaToolControl.getObject();
        }

        return defaultSchemaControl;
    }

    /**
     * Checks if is editor on top by id.
     *
     * @return true, if is editor on top by id
     */
    public boolean isEditorOnTopById() {

        if (null == UIElement.getInstance().getVisibleSourceViewer()) {
            return false;
        }

        IDebugObject debugObject = UIElement.getInstance().getVisibleSourceViewer().getDebugObject();

        if (null == debugObject) {
            return false;
        } else if (debugObject.getDatabase() == null) {
            return false;
        }
        MPart part = (MPart) modelService.find(debugObject.getPLSourceEditorElmId() + "", application);

        return partService.isPartVisible(part);
    }

    /**
     * Checks if is new editor on top.
     *
     * @return true, if is new editor on top
     */
    public boolean isNewEditorOnTop() {
        PLSourceEditor srcEditor = getEditorOnTop();
        if (null != srcEditor) {
            return true;
        }
        return false;
    }

    /**
     * Gets the editor on top.
     *
     * @return the editor on top
     */
    public PLSourceEditor getEditorOnTop() {
        List<MPartStack> mPartstack = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(mPartstack)) {
            List<MStackElement> children = mPartstack.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> viewersItr = children.iterator();
                boolean hasNext = viewersItr.hasNext();
                while (hasNext) {
                    MStackElement stackEle = viewersItr.next();

                    MPart part = (MPart) stackEle;

                    if (partService.isPartVisible(part) && part.isVisible()) {
                        if (part.getObject() instanceof PLSourceEditor) {
                            PLSourceEditor srcEditor = (PLSourceEditor) part.getObject();
                            if (null == srcEditor.getDebugObject() || srcEditor.getDebugObject().getOid() < 1) {
                                return srcEditor;
                            }
                        }
                    }
                    hasNext = viewersItr.hasNext();
                }
            }
        }
        return null;
    }

    /**
     * Gets the editor on top existing.
     *
     * @param existingEditor the existing editor
     * @return the editor on top existing
     */
    public PLSourceEditor getEditorOnTopExisting(boolean existingEditor) {
        List<MPartStack> mPartstackObj = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(mPartstackObj)) {
            List<MStackElement> children = mPartstackObj.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> viewersItr = children.iterator();
                boolean hasNext = viewersItr.hasNext();
                while (hasNext) {
                    MStackElement stackEle = viewersItr.next();

                    MPart partObj = (MPart) stackEle;

                    if (partService.isPartVisible(partObj) && partObj.isVisible()
                            && partObj.getObject() instanceof PLSourceEditor) {
                        PLSourceEditor srcEditor = (PLSourceEditor) partObj.getObject();

                        if (existingEditor) {
                            if (null != srcEditor.getDebugObject() && srcEditor.getDebugObject().getOid() > 0) {
                                return srcEditor;
                            }
                        } else {
                            if (null == srcEditor.getDebugObject() || srcEditor.getDebugObject().getOid() < 1) {
                                return srcEditor;
                            }
                        }
                    }
                    hasNext = viewersItr.hasNext();
                }
            }
        }
        return null;
    }

    /**
     * Checks if is sql terminal on top.
     *
     * @return true, if is sql terminal on top
     */
    public boolean isSqlTerminalOnTop() {
        SQLTerminal terminal = UIElement.getInstance().getSqlTerminalModel();
        if (null == terminal || null == terminal.getUiID()) {
            return false;
        }

        MPart part = (MPart) modelService.find(terminal.getUiID(), application);

        return partService.isPartVisible(part);
    }

    /**
     * Checks if is part on top.
     *
     * @param id the id
     * @return true, if is part on top
     */
    public boolean isPartOnTop(String id) {

        MPart part = (MPart) modelService.find(id, application);

        return part != null && partService.isPartVisible(part);
    }

    /**
     * Gets the active part object.
     *
     * @return the active part object
     */
    public Object getActivePartObject() {
        MPart activePart = partService.getActivePart();
        if (null == activePart) {
            return null;
        }
        return activePart.getObject();
    }

    /**
     * Gets the active part.
     *
     * @return the active part
     */
    public MPart getActivePart() {
        return partService.getActivePart();
    }

    /**
     * Toggle set column not null check.
     *
     * @param setChecked the set checked
     */
    public void toggleSetColumnNotNullCheck(boolean setChecked) {
        MPart mPart = getObjectBrowserPart();

        if (null == mPart) {
            return;
        }

        List<MMenu> menus = mPart.getMenus();

        int menuSize = menus.size();

        MMenuElement menuElement = null;
        MMenuElement childElement = null;
        MenuImpl menuImpl = null;
        List<MMenuElement> childElements = null;
        HandledMenuItemImpl menuItemImpl = null;
        int childSize = 0;
        // Loop through all the main menu elements till we get view menu option
        for (int i = 0; i < menuSize; i++) {
            menuElement = menus.get(i);

            menuImpl = (MenuImpl) menuElement;

            childElements = menuImpl.getChildren();
            childSize = childElements.size();
            for (int m = 0; m < childSize; m++) {
                childElement = childElements.get(m);
                if (childElement instanceof HandledMenuItemImpl) {
                    menuItemImpl = (HandledMenuItemImpl) childElement;

                    if (UIConstants.UI_OBJ_BROWSER_MENU_COLUMN_NOT_NULL.equalsIgnoreCase(menuItemImpl.getElementId())) {
                        if (setChecked) {
                            menuItemImpl.setIconURI("platform:/plugin/com.huawei.mppdbide.view/icons/checked.png");
                        } else {
                            menuItemImpl.setIconURI(null);
                        }

                    }
                }
            }
        }
    }

    /**
     * Creates the progress bar window.
     */
    public void createProgressBarWindow() {
        final MPart part = partService.findPart(UIConstants.UI_PART_PROGRESSBAR_ID);

        if (null != part) {
            if (!part.isVisible()) {
                part.setVisible(true);
            }
            if (null == part.getObject()) {
                partService.activate(part);
            }
            final ProgressMonitorControl propWindow = (ProgressMonitorControl) part.getObject();

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    propWindow.refreshJobs();
                    part.setLabel(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_WINDOW_TITLE));
                    partService.bringToTop(part);
                }
            });

        } else {
            createNewStatusBarWindow();
        }
    }

    /**
     * Creates the new status bar window.
     *
     * @return the m part
     */
    private MPart createNewStatusBarWindow() {
        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();
            return null;

        } else {
            MPart newpart = MBasicFactory.INSTANCE.createPart();
            newpart.setToBeRendered(true);
            newpart.setVisible(true);
            newpart.setContributionURI(
                    "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.utils.ProgressMonitorControl");
            newpart.setCloseable(true);
            newpart.setOnTop(true);
            newpart.setLabel(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_WINDOW_TITLE));
            newpart.setTooltip(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_WINDOW_TITLE));
            newpart.setElementId(UIConstants.UI_PART_PROGRESSBAR_ID);
            List<String> tags = newpart.getTags();
            if (null != tags) {
                tags.add(IPresentationEngine.NO_MOVE);
            }
            newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_BAR_WINDOW, this.getClass()));
            List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                    MPartStack.class, null);
            if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
                List<MStackElement> children = stacks.get(0).getChildren();
                if (null != children) {
                    children.add(newpart);
                }
            }
            partService.activate(newpart);
            if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
                MElementContainer<MUIElement> parent = stacks.get(0).getParent();
                if (null != parent) {
                    parent.setVisible(true);
                }
                ((ProgressMonitorControl) newpart.getObject()).refreshJobs();
            }

            return newpart;
        }

    }

    /**
     * Hide status bar window.
     */
    public void hideStatusBarWindow() {
        if (!DSViewDataManager.getInstance().isWbGoingToClose()) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    MPart part = partService.findPart(UIConstants.UI_PART_PROGRESSBAR_ID);
                    if (null != part) {
                        List<MPartStack> stacks = modelService.findElements(application,
                                UIConstants.PARTSTACK_ID_EDITOR, MPartStack.class, null);
                        if (uiUtils.isAtleastOnePartIsOpen(stacks) && stacks.get(0).getChildren() != null) {
                            stacks.get(0).getChildren().remove(part);
                            if (part.getObject() instanceof ProgressMonitorControl) {
                                ProgressMonitorControl propWindow = (ProgressMonitorControl) part.getObject();
                                propWindow.removeJobListener();
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Gets the selected window.
     *
     * @return the selected window
     */
    public MWindow getSelectedWindow() {
        return application.getSelectedElement();
    }

    /**
     * Toggle part.
     *
     * @param partid the partid
     * @param isVisible the is visible
     * @param toggleMenuOption the toggle menu option
     */
    public void togglePart(String partid, boolean isVisible, String toggleMenuOption) {
        MPart part = partService.findPart(partid);
        if (part != null) {
            MPartStack partStack = null;
            if (UIConstants.UI_TOGGLE_CONSOLE_MENU.equalsIgnoreCase(toggleMenuOption)) {
                partStack = (MPartStack) modelService.find("com.huawei.mppdbide.partstack.id.console", application);

            } else if (UIConstants.UI_TOGGLE_OBJECT_BROWSER_MENU.equalsIgnoreCase(toggleMenuOption)) {
                partStack = (MPartStack) modelService.find("com.huawei.mppdbide.partstack.id.objectbrowser",
                        application);
            } else {
                return;
            }
            uiUtils.addOrRemoveStack(isVisible, partStack);
        }
    }

    /**
     * View all parts.
     */
    public void viewAllParts() {
        uiUtils.viewAllParts();
    }

    /**
     * Removes the all parts from editor stack.
     */
    public void removeAllPartsFromEditorStack() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                Iterator<MStackElement> itr = children.iterator();
                while (itr.hasNext()) {
                    Object obj = itr.next();
                    if (obj instanceof PartImpl
                            && MPPDBIDEConstants.DO_NOT_DELETE_PART_LABEL.equals(((PartImpl) obj).getLabel())) {
                        continue;
                    }
                    stacks.get(0).getChildren().remove(obj);
                    itr = stacks.get(0).getChildren().iterator();
                }
            }

        }
    }

    /**
     * Force focus SQL terminal.
     */
    public void forceFocusSQLTerminal() {
        uiUtils.forceFocusSQLTerminal();
    }

    /**
     * Force focus local console.
     */
    public void forceFocusLocalConsole() {
        SQLTerminal terminal = getVisibleTerminal();

        if (null != terminal) {
            terminal.forceFocusLocalConsole();
        }

    }

    /**
     * Checks if is window limit reached.this method need to be protected and
     * synchronised
     *
     * @return true, if is window limit reached
     */
    public boolean isWindowLimitReached() {

        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is object browser active.
     *
     * @return true, if is object browser active
     */
    public boolean isObjectBrowserActive() {
        MPart part2 = partService.getActivePart();
        if (part2 == null) {
            return false;
        }
        String iD = part2.getElementId();

        if (UIConstants.UI_PART_OBJECT_BROWSER_ID.equalsIgnoreCase(iD)
                || UIConstants.UI_PART_SEARCHWINDOW_ID.equalsIgnoreCase(iD)) {
            return true;
        }

        return false;

    }

    /**
     * Checks if is more result window allowed.
     *
     * @return true, if is more result window allowed
     */
    public boolean isMoreResultWindowAllowed() {
        synchronized (resultWindowCounterLock) {
            if (resultWindowCounter >= PreferenceWrapper.getInstance().getPreferenceStore()
                    .getInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT)) {
                return false;
            }
            return true;
        }
    }

    /**
     * Update result window counter.
     */
    public void updateResultWindowCounter() {
        synchronized (resultWindowCounterLock) {
            resultWindowCounter++;
        }
    }

    /**
     * Update result window counter on close.
     */
    public void updateResultWindowCounterOnClose() {
        synchronized (resultWindowCounterLock) {
            resultWindowCounter--;
        }
    }

    /**
     * Update result window counter on close.
     *
     * @param count the count
     */
    public void updateResultWindowCounterOnClose(int count) {
        synchronized (resultWindowCounterLock) {
            resultWindowCounter -= count;
        }
    }

    /**
     * Find window and activate.
     *
     * @param windowDetails the window details
     * @return the object
     */
    public Object findWindowAndActivate(IWindowDetail windowDetails) {
        MPart part = partService.findPart(windowDetails.getUniqueID());
        Object object = null;
        if (part != null) {
            partService.activate(part);
            object = part.getObject();
        }
        return object;
    }

    /**
     * Exec command.
     *
     * @param command the command
     * @return true, if successful
     */
    public boolean execCommand(String command) {
        Command cmd = EclipseInjections.getInstance().getCommandService().getCommand(command);
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
        if (EclipseInjections.getInstance().getHandlerService().canExecute(parameterizedCmd)) {
            // execute the command
            EclipseInjections.getInstance().getHandlerService().executeHandler(parameterizedCmd);

            return true;

        }

        return false;
    }

    /**
     * Removes the all parts from stack.
     *
     * @param partstackid the partstackid
     */
    public void removeAllPartsFromStack(String partstackid) {
        List<MPartStack> stacks = modelService.findElements(application, partstackid, MPartStack.class, null);
        MPart part = null;

        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            Iterator<MStackElement> itr = null;
            if (null != children) {
                itr = children.iterator();
            }
            while (null != itr && itr.hasNext()) {
                MStackElement elements = itr.next();
                if (elements instanceof MPart) {
                    part = (MPart) elements;
                    if (part.getElementId() != null
                            && !(part.getElementId().equals("com.huawei.mppdbide.view.part.donotdeleteme.0"))) {
                        stacks.get(0).getChildren().remove(elements);
                    }
                }
                itr = stacks.get(0).getChildren().iterator();
            }
        }
    }

    /**
     * Check if visual window visible.
     *
     * @param object the object
     * @return true, if successful
     */
    public boolean checkIfVisualWindowVisible(Object object) {
        if (object instanceof String) {
            String windowPartId = (String) object;
            // Find the Default-Window to place the Part
            MWindow window = (MWindow) modelService.find(windowPartId, this.getApplication());
            if (window == null) {
                return false;
            } else {
                return window.isVisible() && window.isToBeRendered();
            }
        }

        return false;
    }

    /**
     * Gets the object browser debug obj.
     *
     * @param dbgObj the dbg obj
     * @return the object browser debug obj
     */
    public IDebugObject getObjectBrowserDebugObj(IDebugObject dbgObj) {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.UI_PARTSTACK_EDITOR,
                MPartStack.class, null);
        if (uiUtils.isAtleastOnePartIsOpen(stacks)) {
            Iterator<MStackElement> partsItr = stacks.get(0).getChildren().iterator();
            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = partsItr.hasNext();
            while (hasNext) {
                stackElement = partsItr.next();
                mPart = (MPart) stackElement;

                if (null != mPart && mPart.getObject() != null && mPart.getObject() instanceof PLSourceEditor) {
                    IDebugObject debugObject = ((PLSourceEditor) mPart.getObject()).getDebugObject();
                    if (debugObject.equals(dbgObj)) {
                        return debugObject;
                    }
                }
                hasNext = partsItr.hasNext();
            }
        }
        return null;
    }

    /**
     * Reset tab icon.
     *
     * @param mPart the m part
     * @param reattachSuccess the reattach success
     */
    public void resetTabIcon(MPart mPart, boolean reattachSuccess) {
        IAutoSaveObject obj = (IAutoSaveObject) mPart.getObject();
        Map<String, Object> transientData = mPart.getTransientData();
        if (null != transientData) {
            if (obj instanceof SQLTerminal) {
                SQLTerminal sqlTerminal = (SQLTerminal) obj;

                mPart.getTransientData().remove("IconUriForPart");
                mPart.setIconURI(IconUtility.getIconImageUri(reattachSuccess
                        ? (sqlTerminal.isFileTerminalFlag() ? IiconPath.ICO_FILE_TERMINAL : IiconPath.ICO_SQL_TERMINAL)
                        : (sqlTerminal.isFileTerminalFlag() ? IiconPath.ICO_FILE_TERMINAL_DISCONNECTED
                                : IiconPath.ICO_SQL_TERMINAL_DISCONNECTED),
                        this.getClass()));
            } else {
                uiUtils.resetTabIconForPlSourceEditor(mPart, reattachSuccess, obj, transientData);
            }
        }
    }

    /**
     * Update text editors icon and conn buttons.
     *
     * @param server the server
     */
    public void updateTextEditorsIconAndConnButtons(Server server) {
        String profileId = server.getName();
        Collection<Database> dbs = server.getAllDatabases();
        List<MPart> openWindows = new ArrayList<MPart>(uiUtils.getAllOpenTabsPart());
        for (MPart obj : openWindows) {
            IAutoSaveObject partObj = (IAutoSaveObject) obj.getObject();
            if (partObj != null && partObj.getConnectionName() != null
                    && partObj.getConnectionName().equals(profileId)) {
                for (Database db : dbs) {
                    String dbName = db.getName();
                    if (partObj.getDatabaseName().equals(dbName)) {
                        boolean reattachSuccess = partObj.updateEditorWindow(db);
                        resetTabIcon(obj, reattachSuccess);
                        partObj.resetConnButtons(true);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Gets the max tabs allowed.
     *
     * @return the max tabs allowed
     */
    public static int getMaxTabsAllowed() {
        return MAX_TABS_ALLOWED;
    }

    /**
     * Clear SQL terminal counter.
     */
    public void clearSQLTerminalCounter() {
        this.sqlTerminalIds.clear();
    }

    /**
     * Removes the SQL terminal id from map.
     *
     * @param key the key
     */
    public void removeSQLTerminalIdFromMap(Object key) {
        this.sqlTerminalIds.remove(key);
    }

    /**
     * Gets the object browser on focus.
     *
     * @return the object browser on focus
     */
    public void getObjectBrowserOnFocus() {
        if (isObjectBrowserPartOpen() && getObjectBrowserModel() != null) {
            getObjectBrowserModel().onFocus();
        }
    }

    /**
     * Auto refresh.
     *
     * @param objectsToBeRefreshed the objects to be refreshed
     */
    public void autoRefresh(HashSet<Object> objectsToBeRefreshed) {
        if (objectsToBeRefreshed == null) {
            return;
        }
        HashSet<Object> listOfObjects = objectsToBeRefreshed;
        TreeViewer viewer = null;
        if (UIElement.getInstance().getObjectBrowserModel() != null) {
            viewer = UIElement.getInstance().getObjectBrowserModel().getTreeViewer();
        }

        for (Object obj : listOfObjects) {
            RefreshObjectDetails refreshObj = (RefreshObjectDetails) obj;
            if (null != refreshObj.getObjToBeRefreshed()) {
                RefreshObjects.refreshObjectsInTreeViewer(refreshObj, viewer);

            }
        }
    }

    /**
     * Creates the ER part.
     *
     * @param presentation the presentation
     * @return Object
     *
     * @Title: createERPart
     * @Description: create ERPart
     */
    public Object createERPart(AbstractERPresentation presentation) {
        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();
            return null;
        }

        MPart newpart = MBasicFactory.INSTANCE.createPart();
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI("bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.erd.ERPart");
        newpart.setCloseable(true);
        newpart.setOnTop(true);
        newpart.setLabel(presentation.getWindowTitle());
        newpart.setTooltip(presentation.getWindowTitle());
        newpart.setObject(presentation);

        List<String> tags = newpart.getTags();
        if (null != tags) {
            tags.add(IPresentationEngine.NO_MOVE);
            tags.add(EPartService.REMOVE_ON_HIDE_TAG);
        }
        newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.VIEW_ER_ICON, this.getClass()));

        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        uiUtils.addNewPartIntoStackList(newpart, stacks);

        uiUtils.activateEditorPart(newpart);
        return newpart.getObject();
    }

    /**
     * Gets the all open ER part.
     *
     * @return the all open ER part
     */
    public List<MPart> getAllOpenERPart() {
        List<MPart> parts = new ArrayList<MPart>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        for (MPart mPart : uiUtils.getAllParts()) {
            if (mPart.getObject() instanceof ERPart) {
                parts.add(mPart);
            }
        }
        return parts;
    }

    /**
     * Gets the visual explain plan window.
     *
     * @param windowId the window id
     * @return the visual explain plan window
     */
    public Shell getVisualExplainPlanWindow(String windowId) {
        MWindow window = (MWindow) modelService.find(windowId, this.getApplication());
        final Shell shell = (Shell) window.getWidget();
        return shell;

    }

    /**
     * Clear visual plan.
     *
     * @param compositeList the composite list
     */
    public void clearVisualPlan(List<MStackElement> compositeList) {
        Iterator<MStackElement> compositeItr = compositeList.iterator();

        while (compositeItr.hasNext()) {
            MStackElement eleStack = compositeItr.next();
            if (eleStack instanceof MCompositePart) {
                MCompositePart compositePart = (MCompositePart) eleStack;
                List<MPartSashContainerElement> mainSashList = compositePart.getChildren();
                if (mainSashList.size() > 0) {
                    MPartSashContainer mainSash = (MPartSashContainer) mainSashList.get(0);
                    Iterator<MPartSashContainerElement> sashChild = mainSash.getChildren().iterator();

                    while (sashChild.hasNext()) {
                        MUIElement el = sashChild.next();
                        if (el instanceof PartImpl) {
                            MPart part = (MPart) el;
                            partService.hidePart(part);
                            break;
                        }
                        if (el instanceof PartStackImpl) {
                            uiUtils.cleanUpPartStack(el);
                            break;
                        }
                        if (el instanceof PartSashContainerImpl) {
                            MPartSashContainer childSash = (MPartSashContainer) el;
                            Iterator<MPartSashContainerElement> child = childSash.getChildren().iterator();
                            uiUtils.cleanUpPartSashContainer(child);
                        }

                    }
                }
            }
        }
    }

    /**
     * Rendering within a window to create parts.
     *
     * @param windowId the window id
     * @param stackId the stack id
     * @param compositeLabel the composite label
     * @param bundleClassName the bundle class name
     * @param partDetails the part details
     * @param presentationData the presentation data
     * @param partID the part ID
     * @return the object
     */
    public Object newRenderInWindow(String windowId, String stackId, String compositeLabel, String bundleClassName,
            IWindowDetail partDetails, Object presentationData, String partID) {
        MPartStack stack = createWindowAndMainPartStackForVisualExplainPlan(windowId, stackId);
        MCompositePart snippetCompPart = uiUtils.createCompositePartForWindow(compositeLabel, stack);
        stack.setSelectedElement(snippetCompPart);
        stack.setOnTop(true);
        MPart part = null;

        MUIElement element = modelService.find(partID, snippetCompPart);
        if (element == null) {
            part = modelService.createModelElement(MPart.class);
            snippetCompPart.getChildren().add(part);
            snippetCompPart.setSelectedElement(part);
        }
        if (element instanceof PartStackImpl) {
            MPartStack propsStack = (MPartStack) element;
            propsStack.setOnTop(true);
            part = getPartSpecificNodeWindow(propsStack, partDetails.getTitle());
            if (part == null) {
                part = modelService.createModelElement(MPart.class);
                propsStack.getChildren().add(part);
                propsStack.setSelectedElement(part);
                part.setToBeRendered(false);
            }
        } else {
            part = (MPart) modelService.find(partID, snippetCompPart);
            if (part == null) {
                part = modelService.createModelElement(MPart.class);
                snippetCompPart.getChildren().add(part);
                snippetCompPart.setSelectedElement(part);
            }
            snippetCompPart.setSelectedElement(part);
        }
        uiUtils.activatePartObject(bundleClassName, partDetails, presentationData, partID, part);
        return part.getObject();

    }

    private MPartStack createWindowAndMainPartStackForVisualExplainPlan(String windowId, String stackId) {
        MPartStack stack = (MPartStack) modelService.find(stackId, this.getApplication());
        if (stack == null) {
            // and create it if not
            stack = modelService.createModelElement(MPartStack.class);
            stack.setElementId(stackId);

            // Find the Default-Window to place the Part
            MWindow window = (MWindow) modelService.find(windowId, this.getApplication());
            if (window == null) {
                // and create it if it doesn't exist (expected)
                window = modelService.createModelElement(MTrimmedWindow.class);
                window.setElementId(windowId);
                window.setLabel(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_MAN_WINDOW_TITLE));
                // Add window to application
                this.getApplication().getChildren().add(window);
            } else {
                window.getChildren().add(stack); // Add stack to the window
            }
        }

        MWindow window = (MWindow) modelService.find(windowId, this.getApplication());
        if (null != window) {
            window.setLabel(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_MAN_WINDOW_TITLE));
            window.getChildren().add(stack);
        }
        return stack;
    }

    /**
     * Create new tab for each query in window.
     *
     * @param windowId the window id
     * @param stackId the stack id
     * @param compositeLabel the composite label
     * @return the object
     */
    public Object newTabInWindow(String windowId, String stackId, String compositeLabel) {

        MPartStack stack = createWindowAndMainPartStackForVisualExplainPlan(windowId, stackId);
        return uiUtils.createCompositePartForWindow(compositeLabel, stack);

    }

    /**
     * getVisualPlanWindow
     * 
     * @param windowId id
     * @return window obj
     */
    public TrimmedWindowImpl getVisualPlanWindow(String windowId) {
        return (TrimmedWindowImpl) modelService.find(windowId, this.getApplication());
    }

    /**
     * validateFileSize validate size
     * 
     * @param filePath filepath
     * @throws FileOperationException
     */
    public void validateFileSize(String filePath) throws FileOperationException {
        File file = new File(filePath);
        double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
        double fileLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_SQL);
        if (fileLimit != 0 && fileSizeInMB > fileLimit) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_HEADER),
                            MessageConfigLoader.getProperty(IMessagesConstants.FILE_SIZE_EXCEED_WARNING_MSG));
                    MPPDBIDELoggerUtility
                            .debug(MessageConfigLoader.getProperty(IMessagesConstants.FILE_SIZE_EXCEED_WARNING_MSG));
                }

            });
            throw new FileOperationException(IMessagesConstants.ERR_IO_ERROR_EXPORT);
        }
    }
}
