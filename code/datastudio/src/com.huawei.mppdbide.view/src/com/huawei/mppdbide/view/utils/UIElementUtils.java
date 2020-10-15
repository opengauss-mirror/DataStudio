/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.e4.core.contexts.IEclipseContext;
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
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartStackImpl;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Widget;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.erd.AbstractERPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.objectpropertywiew.PropertiesWindow;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.autosave.AutoSaveTerminalStatus;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveDbgObject;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.common.SourceViewerUtil;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIElementUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
 */
public class UIElementUtils {

    private EPartService partService;

    private EModelService modelService;

    private MApplication application;

    /**
     * maximum tabs allowed
     */
    public static final int MAX_TABS_ALLOWED = 100;

    private static final String SNIPPET_STACK_ID = "com.huawei.mppdbide.view.partstack.visualsnippet";

    private static final String CONSOLE_PARTSTACK_ID = "com.huawei.mppdbide.partstack.id.console";

    private static final String OBJECTBROWSER_PARTSTACK_ID = "com.huawei.mppdbide.partstack.id.objectbrowser";

    public UIElementUtils(EPartService partService2, EModelService modelService2, MApplication application2) {
        this.partService = partService2;
        this.modelService = modelService2;
        this.application = application2;
    }

    /**
     * return true, if is at least One Part is Open 
     * 
     * @param stacks the stacks
     * @return return true, if is at least One Part is Open 
     */
    public boolean isAtleastOnePartIsOpen(List<MPartStack> stacks) {
        return null != stacks && stacks.size() > 0;
    }

    /**
     * Adds the or remove stack.
     *
     * @param isVisible the is visible
     * @param partStack the part stack
     */
    public void addOrRemoveStack(boolean isVisible, MPartStack partStack) {
        IEclipseContext activeWindowContext = application.getContext().getActiveChild();
        if (activeWindowContext != null) {
            EPartService activeWindowPartService = activeWindowContext.get(EPartService.class);
            if (partStack != null && activeWindowPartService == partService) {
                List<String> tags = partStack.getTags();
                if (null != tags) {
                    if (!isVisible) {
                        tags.add(IPresentationEngine.MINIMIZED);
                    } else {
                        tags.remove(IPresentationEngine.MINIMIZED);
                    }
                }
            }
        }
    }

    /**
     * View all parts.
     */
    public void viewAllParts() {

        MPartStack partStack = null;

        partStack = (MPartStack) modelService.find(CONSOLE_PARTSTACK_ID, application);

        if (partStack != null && null != partStack.getTags()) {
            partStack.getTags().remove(IPresentationEngine.MINIMIZED);
        }

        partStack = (MPartStack) modelService.find(OBJECTBROWSER_PARTSTACK_ID, application);

        if (partStack != null && null != partStack.getTags()) {
            partStack.getTags().remove(IPresentationEngine.MINIMIZED);
        }
    }

    /**
     * Force focus SQL terminal.
     */
    public void forceFocusSQLTerminal() {

        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (isAtleastOnePartIsOpen(stacks)) {
            Iterator<MStackElement> sourceViewersItr = stacks.get(0).getChildren().iterator();
            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = sourceViewersItr.hasNext();
            int index = 0;
            while (hasNext) {
                stackElement = sourceViewersItr.next();

                mPart = (MPart) stackElement;

                if (mPart != null && mPart.getObject() != null) {
                    if (stacks.get(0).getWidget() instanceof CTabFolder) {
                        ((CTabFolder) stacks.get(0).getWidget()).setSelection(index - 1);
                        ((CTabFolder) stacks.get(0).getWidget()).forceFocus();
                        return;
                    }

                }
                hasNext = sourceViewersItr.hasNext();
                index++;
            }
        }
    }

    /**
     * Creates the new view table data.
     *
     * @param obj the obj
     * @param label the label
     * @param id the id
     * @param icon the icon
     * @param partClassURI the part class URI
     * @return the m part
     */
    public MPart createNewViewTableData(Object obj, String label, String id, String icon, String partClassURI) {
        if (getTotalSourceViewerCount() >= MAX_TABS_ALLOWED) {
            openMaxSourceViewerDialog();

            return null;

        } else {
            MPart newpart = getNewPartObject(obj, label, partClassURI);
            if (icon == null) {
                if (id.contains("EDIT")) {
                    newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_EDIT_TABLE, this.getClass()));
                } else {
                    newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_TABLE, this.getClass()));
                }
            } else {
                if (icon.contains("Partition")) {
                    if (id.contains("EDIT")) {
                        newpart.setIconURI(
                                IconUtility.getIconImageUri(IiconPath.ICO_EDIT_PARTITION_TABLE, this.getClass()));
                    } else {
                        newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.PARTITION_TABLE, this.getClass()));
                    }
                } else {
                    newpart.setIconURI(icon);
                }
            }

            newpart.setElementId(id);
            if (newpart.getTags() != null) {
                newpart.getTags().add(IPresentationEngine.NO_MOVE);
            }
            List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                    MPartStack.class, null);

            addNewPartIntoStackList(newpart, stacks);
            if (partService != null) {
                partService.activate(newpart);
            }

            return newpart;
        }

    }

    private MPart getNewPartObject(Object obj, String label, String partClassURI) {
        MPart newpart = MBasicFactory.INSTANCE.createPart();
        newpart.setObject(obj);
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI(partClassURI);
        newpart.setCloseable(true);
        newpart.setOnTop(true);
        newpart.setLabel(label);
        return newpart;
    }

    /**
     * adds NewPart Into StackList
     * 
     * @param newpart the newpart
     * @param stacks the stacks
     */
    public void addNewPartIntoStackList(MPart newpart, List<MPartStack> stacks) {
        if (isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                children.add(newpart);
            }
            MElementContainer<MUIElement> parent = stacks.get(0).getParent();
            if (null != parent) {
                parent.setVisible(true);
            }
        }
    }

    /**
     * Sets the new part properties.
     *
     * @param debugObject the debug object
     * @param id the id
     * @param label the label
     * @param toolTip the tool tip
     * @param isDirtyFlag the is dirty flag
     * @param newpart the newpart
     * @param editor the editor
     * @return the string
     */
    public String setNewPartProperties(IDebugObject debugObject, String id, String label, String toolTip,
            boolean isDirtyFlag, MPart newpart, PLSourceEditor editor) {
        newpart.setObject(editor);
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI("bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.PLSourceEditor");
        newpart.setCloseable(true);
        newpart.setOnTop(true);

        String label1 = label.substring(0,
                label.length() > MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        ? MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        : label.length());
        newpart.setLabel(label1);
        newpart.setElementId(id);
        List<String> tags = newpart.getTags();
        if (null != tags) {
            tags.add(IPresentationEngine.NO_MOVE);
            tags.add(EPartService.REMOVE_ON_HIDE_TAG);
        }
        newpart.setTooltip(toolTip);
        newpart.setDirty(isDirtyFlag);

        newpart.getProperties().put(OBJECTTYPE.class.getSimpleName(), debugObject.getObjectType().name());
        return label1;
    }

    /**
     * Gets the total source viewer count.
     *
     * @return the total source viewer count
     */
    public int getTotalSourceViewerCount() {

        // Maximum allowed tabs are 100 only, including view table data and sql
        // terminal.
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);

        // Added a part as dummy (invisible/not rendered) as if no part inside
        // the stack, stack will also be removed. But the size is always the
        // number of tabs user is seeing in GUI.i.e., 1 less than actual in the
        // stack.
        if (isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            if (null != children) {
                return children.size() - 1;
            }
            return 0;
        }
        return 0;
    }

    /**
     * Open max source viewer dialog.
     */
    public void openMaxSourceViewerDialog() {

        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.MAX_SOURCE_VIEWER),
                MessageConfigLoader.getProperty(IMessagesConstants.INFO_MAX_SOURCE_VIEWER));

    }

    /**
     * Sets the debug part icon.
     *
     * @param debugObject the debug object
     * @param editorPart the editor part
     */
    public void setDebugPartIcon(IDebugObject debugObject, MPart editorPart) {
        if (null != editorPart) {
            Map<String, Object> transientData = editorPart.getTransientData();
            if (null != transientData) {
                OBJECTTYPE type = debugObject.getObjectType();
                switch (type) {
                    case PLSQLFUNCTION:
                    case FUNCTION_GROUP: {
                        transientData.remove("IconUriForPart");
                        editorPart
                                .setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONPLSQL, this.getClass()));
                        break;
                    }
                    case SQLFUNCTION: {
                        transientData.remove("IconUriForPart");
                        editorPart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONSQL, this.getClass()));
                        break;
                    }
                    case PROCEDURE: {
                        transientData.remove("IconUriForPart");
                        editorPart
                                .setIconURI(IconUtility.getIconImageUri(IiconPath.ICON_PROCEDURE_PL, this.getClass()));
                        break;
                    }
                    case CFUNCTION: {
                        transientData.remove("IconUriForPart");
                        editorPart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONC, this.getClass()));
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
    }

    /**
     * resets the TabIcon For PlSourceEditor
     * 
     * @param mPart the mPart
     * @param reattachSuccess the reattachSuccess
     * @param obj the obj
     * @param transientData the transientData
     */
    public void resetTabIconForPlSourceEditor(MPart mPart, boolean reattachSuccess, IAutoSaveObject obj,
            Map<String, Object> transientData) {
        // PLSourceEditor
        IDebugObject debugObject = null;
        if (obj instanceof PLSourceEditor) {
            debugObject = ((PLSourceEditor) obj).getDebugObject();
            if (reattachSuccess) {
                setDebugPartIcon(debugObject, mPart);
            } else {
                IAutoSaveDbgObject dbgObj = (IAutoSaveDbgObject) obj;

                switch (dbgObj.getDbgObjType()) {
                    case PLSQLFUNCTION: {
                        transientData.remove("IconUriForPart");
                        mPart.setIconURI(
                                IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONPLSQL_DISCONNECTED, this.getClass()));
                        break;
                    }
                    case SQLFUNCTION: {
                        transientData.remove("IconUriForPart");
                        mPart.setIconURI(
                                IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONSQL_DISCONNECTED, this.getClass()));
                        break;
                    }
                    case CFUNCTION: {
                        transientData.remove("IconUriForPart");
                        mPart.setIconURI(
                                IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONC_DISCONNECTED, this.getClass()));
                        break;
                    }
                    case PROCEDURE: {
                        transientData.remove("IconUriForPart");
                        mPart.setIconURI(
                                IconUtility.getIconImageUri(IiconPath.ICON_PROCEDURE_PL_DISCONNECTED, this.getClass()));
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Creates the new buffer.
     *
     * @param obj the obj
     * @param debugObject the debug object
     * @return the m part
     */
    public MPart createNewBuffer(Object obj, IDebugObject debugObject) {
        MPart newpart = MBasicFactory.INSTANCE.createPart();
        newpart.setObject(obj);
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI("bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.PLSourceEditor");
        newpart.setCloseable(true);
        newpart.setOnTop(true);
        String label = debugObject.getWindowTitleName();
        newpart.setLabel(label.substring(0,
                label.length() > MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        ? MPPDBIDEConstants.RENAME_TERMINAL_MAX_LENGTH
                        : label.length()));
        newpart.setElementId(debugObject.getPLSourceEditorElmId());
        newpart.getTags().add(IPresentationEngine.NO_MOVE);
        newpart.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
        newpart.setTooltip(debugObject.getPLSourceEditorElmTooltip());

        newpart.getProperties().put(OBJECTTYPE.class.getSimpleName(), debugObject.getObjectType().name());
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        if (isAtleastOnePartIsOpen(stacks)) {
            List<MStackElement> children = stacks.get(0).getChildren();
            MElementContainer<MUIElement> parent = stacks.get(0).getParent();
            if (children != null) {
                children.add(newpart);
            }
            if (parent != null) {
                parent.setVisible(true);
            }
        }
        partService.activate(newpart);

        return newpart;
    }

    /**
     * Creates the new editor.
     *
     * @param debugObject the debug object
     * @return the PL source editor
     */
    public PLSourceEditor createNewEditor(IDebugObject debugObject) {
        PLSourceEditor editor = new PLSourceEditor();
        editor.setSyntax(debugObject.getDatabase() != null ? debugObject.getDatabase().getSqlSyntax() : null);
        MPart newpart = createNewBuffer(editor, debugObject);

        switch (debugObject.getObjectType()) {
            case PLSQLFUNCTION: {
                newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONPLSQL, this.getClass()));
                break;
            }
            case SQLFUNCTION: {
                newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONSQL, this.getClass()));
                break;
            }
            case CFUNCTION: {
                newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICO_FUNCTIONC, this.getClass()));
                break;
            }
            case PROCEDURE: {
                newpart.setIconURI(IconUtility.getIconImageUri(IiconPath.ICON_PROCEDURE_PL, this.getClass()));
                break;
            }
            default: {
                return null;
            }
        }

        SourceViewerUtil.addSourceViewerId(debugObject.getPLSourceEditorElmId());
        ((PLSourceEditor) newpart.getObject()).updateStatus(AutoSaveTerminalStatus.LOAD_FINISHED);
        return (PLSourceEditor) newpart.getObject();
    }

    /**
     * Creates the new part.
     *
     * @param terminalTablSpa the terminal tabl spa
     * @param id the id
     * @param label the label
     * @return the m part
     */
    public MPart createNewPart(SQLTerminal terminalTablSpa, String id, String label) {
        MPart newpart = MBasicFactory.INSTANCE.createPart();
        newpart.setToBeRendered(true);
        newpart.setVisible(true);
        newpart.setContributionURI(
                "bundleclass://com.huawei.mppdbide.view/com.huawei.mppdbide.view.ui.terminal.SQLTerminal");
        newpart.setCloseable(true);
        newpart.setOnTop(true);

        newpart.setLabel(label);
        newpart.setElementId(id);
        newpart.setObject(terminalTablSpa);
        return newpart;
    }

    /**
     * Sets the terminal data.
     *
     * @param tSpace the t space
     * @param newpart the newpart
     * @param dbName the db name
     * @param id the id
     * @param label the label
     * @return the SQL terminal
     */
    public SQLTerminal setTerminalData(Tablespace tSpace, MPart newpart, String dbName, String id, String label) {
        SQLTerminal terminal = (SQLTerminal) newpart.getObject();
        Database db = null;
        if (tSpace.getServer().isAleastOneDbConnected()) {
            Iterator<Database> dbItr = tSpace.getServer().getAllDatabases().iterator();
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
        terminal.setTabToolTip(id);
        terminal.setPartLabel(label);
        terminal.registerModifyListener();
        terminal.updateStatus(AutoSaveTerminalStatus.LOAD_FINISHED);
        terminal.setDefLabelId(dbName);
        return terminal;
    }

    /**
     * Activate editor part.
     *
     * @param newpart the newpart
     */
    public void activateEditorPart(MPart newpart) {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        addNewPartIntoStackList(newpart, stacks);
        partService.activate(newpart);
        if (isAtleastOnePartIsOpen(stacks)) {
            stacks.get(0).setOnTop(true);
        }
    }

    /**
     * Gets the all open tabs part.
     *
     * @return the all open tabs part
     */
    public List<MPart> getAllOpenTabsPart() {
        List<MPart> parts = new ArrayList<MPart>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        for (MPart mPart : getAllParts()) {
            if (mPart.getObject() instanceof IAutoSaveObject) {
                parts.add(mPart);
            }
        }
        return parts;
    }

    /**
     * Gets the all open part.
     *
     * @return the all open part
     */
    public List<MPart> getAllParts() {
        List<MPartStack> stacks = modelService.findElements(application, UIConstants.PARTSTACK_ID_EDITOR,
                MPartStack.class, null);
        List<MPart> parts = new ArrayList<MPart>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (isAtleastOnePartIsOpen(stacks)) {
            Iterator<MStackElement> partsItr = stacks.get(0).getChildren().iterator();
            MStackElement stackElement = null;
            MPart mPart = null;
            boolean hasNext = partsItr.hasNext();
            while (hasNext) {
                stackElement = partsItr.next();
                mPart = (MPart) stackElement;

                if (null != mPart && mPart.getObject() != null) {
                    parts.add(mPart);
                }
                hasNext = partsItr.hasNext();
            }
        }
        return parts;
    }

    /**
     * cleans Up the PartSashContainer
     * 
     * @param child the child element
     */
    public void cleanUpPartSashContainer(Iterator<MPartSashContainerElement> child) {
        while (child.hasNext()) {
            MUIElement ele = child.next();
            if (ele instanceof PartImpl) {
                MPart part = (MPart) ele;
                partService.hidePart(part);
                break;
            }
            if (ele instanceof PartStackImpl) {
                MPartStack partStack = (MPartStack) ele;
                List<MStackElement> e1 = partStack.getChildren();
                MPart partStChild = (MPart) e1.get(0);
                partService.hidePart(partStChild);
                break;
            }
            if (ele instanceof MPartSashContainer) {
                MPartSashContainer container = (MPartSashContainer) ele;
                if (container.getChildren().isEmpty()) {
                    continue;
                }
                MPartStack partStack = (MPartStack) container.getChildren().get(0);
                if (!partStack.getChildren().isEmpty()) {
                    cleanUpPartStackchild(partStack);
                }
            }
        }
    }

    /**
     * cleans UpPartStack
     * 
     * @param el the element
     */
    public void cleanUpPartStack(MUIElement el) {
        if (el instanceof MPartStack) {
            MPartStack partStack = (MPartStack) el;
            if (!partStack.getChildren().isEmpty()) {
                MPart partStChild = (MPart) partStack.getChildren().get(0);
                partService.hidePart(partStChild);
            }
        }
    }

    /**
     * cleans UpPartStackchild
     * 
     * @param partStack the partStack
     */
    public void cleanUpPartStackchild(MPartStack partStack) {
        Iterator<MStackElement> partStChild = partStack.getChildren().iterator();
        while (partStChild.hasNext()) {
            MUIElement ele1 = partStChild.next();
            if (ele1 instanceof PartImpl) {
                MPart part = (MPart) ele1;
                partService.hidePart(part);
            }
        }
    }

    /**
     * activate the PartObject
     * 
     * @param bundleClassName the bundleClassName
     * @param partDetails the partDetails
     * @param presentationData the presentationData
     * @param partID the partID
     * @param part the part
     */
    public void activatePartObject(String bundleClassName, IWindowDetail partDetails, Object presentationData,
            String partID, MPart part) {
        if (part.getObject() == null) {
            part.setElementId(partID);
            part.setContributionURI(bundleClassName);
            part.setCloseable(partDetails.isCloseable());
            part.setLabel(partDetails.getTitle());
            part.setIconURI(IconUtility.getIconImageUri(partDetails.getIcon(), this.getClass()));
            part.setObject(presentationData);
            part.setToBeRendered(true);
            part.setVisible(true);
            part.setOnTop(true);
            List<String> tags = part.getTags();
            if (null != tags) {
                tags.remove(IPresentationEngine.MINIMIZED);
            }
            partService.activate(part);
        }
        partService.bringToTop(part);
        disableMinMaxButtons(part);
    }

    private void disableMinMaxButtons(MPart part) {
        Object widg = part.getParent().getWidget();
        if (widg instanceof Widget) {
            Widget tab = (Widget) widg;
            if (tab instanceof CTabFolder) {
                CTabFolder tab1 = (CTabFolder) tab;
                tab1.setMaximizeVisible(false);
                tab1.setMinimizeVisible(false);
                return;
            }
        }
    }

    /**
     * creates the CompositePart For Window
     * 
     * @param compositeLabel the compositeLabel
     * @param stack the stack
     * @return snippetCompPart the snippetCompPart
     */
    public MCompositePart createCompositePartForWindow(String compositeLabel, MPartStack stack) {
        MCompositePart snippetCompPart = null;
        List<MStackElement> stackChildren = stack.getChildren();
        if (!stackChildren.isEmpty()) {

            for (MStackElement ele : stackChildren) {
                MCompositePart compositePart = (MCompositePart) ele;
                compositePart.getObject();
                compositePart.setCloseable(true);
                if (compositeLabel.equals(compositePart.getLabel())) {
                    snippetCompPart = compositePart;
                    break;
                }
            }
            if (snippetCompPart == null) {

                MPartStack snippetPartStack = (MPartStack) modelService.cloneSnippet(application, SNIPPET_STACK_ID,
                        null);

                snippetCompPart = (MCompositePart) snippetPartStack.getChildren().get(0);
                snippetCompPart.setLabel(compositeLabel);
                snippetCompPart.setCloseable(true);
                stack.getChildren().add(snippetCompPart);

            }

        } else {
            MPartStack snippetPartStack = (MPartStack) modelService.cloneSnippet(application, SNIPPET_STACK_ID, null);

            snippetCompPart = (MCompositePart) snippetPartStack.getChildren().get(0);
            snippetCompPart.setLabel(compositeLabel);
            snippetCompPart.setCloseable(true);
            stack.getChildren().add(snippetCompPart);

        }
        List<String> tags = snippetCompPart.getTags();
        if (null != tags) {
            tags.add(IPresentationEngine.NO_DETACH);
        }
        partService.bringToTop(snippetCompPart);
        return snippetCompPart;
    }
}
