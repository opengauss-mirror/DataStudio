/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.SourceEditorKeyListener;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLEditorPlugin;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.core.sourceeditor.templates.ColumnLayout;
import com.huawei.mppdbide.view.core.sourceeditor.templates.StatusInfo;
import com.huawei.mppdbide.view.core.sourceeditor.templates.TemplateContentProvider;
import com.huawei.mppdbide.view.core.sourceeditor.templates.TemplateStoreManager;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplateFactory;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplateIf;
import com.huawei.mppdbide.view.core.sourceeditor.templates.persistence.TemplatePersistenceDataIf;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSTemplatePreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSTemplatePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    /**
     * Qualified key for exact match preference.
     */
    private static final String DEFAULT_MATCHCASE_PREFERENCE_KEY = "com.huawei.mppdbide.editor.codetemplate.preferences.matchcase_templates";

    /**
     * Table presenting the templates.
     */
    private CheckboxTableViewer tableViewer;

    /* All buttons */
    private Button addButton;
    private Button editButton;
    private Button removeButton;
    private Button restoreButton;
    private Button revertButton;

    /**
     * Viewer displays the pattern of selected template.
     */
    private SourceViewer patternViewer;
    /**
     * Match Case checkbox.
     */
    private Button matchCaseButton;

    private boolean changePresent = false;

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplateDialog.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public static class TemplateDialog extends StatusDialog {

        /**
         * 
         * Title: class
         * 
         * Description: The Class TextViewerAction.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private static class TextViewerAction extends Action implements IUpdate {

            private int operationCode = -1;
            private ITextOperationTarget operationTarget;

            /**
             * It Creates a new action.
             *
             * @param viewer the viewer
             * @param operationCode the opcode
             */
            public TextViewerAction(ITextViewer viewer, int operationCode) {
                this.operationCode = operationCode;
                this.operationTarget = viewer.getTextOperationTarget();
                update();
            }

            /**
             * Updates the enabled state of the action. Fires a property change
             * if the enabled state changes.
             *
             * Action#firePropertyChange(String, Object, Object)
             */
            public final void update() {
                if (operationCode == ITextOperationTarget.REDO || operationCode == ITextOperationTarget.UNDO) {
                    return;
                }

                boolean wasEnabled = isEnabled();
                boolean isEnabled = operationTarget != null && operationTarget.canDoOperation(operationCode);
                setEnabled(isEnabled);

                if (wasEnabled != isEnabled) {
                    firePropertyChange(ENABLED, wasEnabled ? Boolean.TRUE : Boolean.FALSE,
                            isEnabled ? Boolean.TRUE : Boolean.FALSE);
                }
            }

            /**
             * run
             */
            public void run() {
                if (operationCode != -1 && operationTarget != null) {
                    operationTarget.doOperation(operationCode);
                }
            }
        }

        private static final int DESCRIPTION_MAX_LEN = 100;
        private static final int PATTERN_MAX_LEN = 1000;

        private final TemplateIf originalTemplate;

        private Text nameText;
        private Text descriptionText;
        private SourceViewer patternEditor;

        private Map globalActions = new HashMap(10);
        private List<String> selectionActions = new ArrayList<String>(3);

        private TemplateIf newTemplate;
        private StatusInfo validationStatus;
        private boolean suppressError = true;

        private Label nameCharLengthLbl;
        private Label descriptionCharLengthLbl;
        private Label patternCharLengthLbl;

        private Button okButton;
        private boolean disableOk = true;

        /**
         * Instantiates a new template dialog.
         *
         * @param parent the parent
         * @param template the template
         * @param edit the edit
         */
        public TemplateDialog(Shell parent, TemplateIf template, boolean edit) {
            super(parent);

            String titleCode = edit ? IMessagesConstants.CODE_TEMPLATE_DIALOG_TITLE_EDIT
                    : IMessagesConstants.CODE_TEMPLATE_DIALOG_TITLE_NEW;

            disableOk = !edit;

            String title = MessageConfigLoader.getProperty(titleCode);
            setTitle(title);

            originalTemplate = template;

            validationStatus = new StatusInfo();
        }

        /**
         * Checks if is resizable.
         *
         * @return true, if is resizable
         */
        protected boolean isResizable() {
            return false;
        }

        /**
         * Creates the.
         */
        public void create() {
            super.create();

            // update initial OK button to be disabled for new templates
            boolean valid = nameText == null || nameText.getText().trim().length() != 0;
            if (!valid) {
                StatusInfo status = new StatusInfo();
                status.setError("");
                disableOk = true;
                updateButtonsEnableState(status);
            }
        }

        /**
         * Creates the dialog area.
         *
         * @param ancestor the ancestor
         * @return the control
         */
        protected Control createDialogArea(Composite ancestor) {
            Composite parent1 = (Composite) super.createDialogArea(ancestor);
            Composite parent = getParentComposite(parent1);

            createMandatoryLabel(parent, MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_NAME));

            Composite composite = getCompositeForNameText(parent);

            addNameText(composite);

            addNameCharLength(parent);

            addNameRules(parent);

            addDescription(parent);

            composite = getDescroptionTextcomposite(parent);

            addDescriptionText(parent, composite);

            createMandatoryLabel(parent,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_PATTERN),
                    GridData.VERTICAL_ALIGN_BEGINNING);

            composite = getDescroptionTextcomposite(parent);

            addPatternEditor(parent, composite);

            setLabelText();
            initializeActions();

            applyDialogFont(parent);

            setLabelData();
            return composite;
        }

        private void setLabelData() {
            nameText.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.newedit.name.text");
            descriptionText.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.newedit.description.text");
            patternEditor.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY,
                    "pref.editor.templates.newedit.pattern.textarea");
            nameCharLengthLbl.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.newedit.name.max.label");
            descriptionCharLengthLbl.setData(MPPDBIDEConstants.SWTBOT_KEY,
                    "pref.editor.templates.newedit.description.max.label");
            patternCharLengthLbl.setData(MPPDBIDEConstants.SWTBOT_KEY,
                    "pref.editor.templates.newedit.pattern.max.label");
        }

        private void setLabelText() {
            descriptionText.setText(originalTemplate.getDescription());
            nameText.setText(originalTemplate.getName());

            nameCharLengthLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                    nameText.getText().length(), UIConstants.NAME_MAX_LEN));
            descriptionCharLengthLbl
                    .setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                            descriptionText.getText().length(), DESCRIPTION_MAX_LEN));
            patternCharLengthLbl
                    .setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                            patternEditor.getDocument().get().length(), PATTERN_MAX_LEN));

            nameText.addModifyListener(new AddModifyListener());
            descriptionText.addModifyListener(new AddModifyListener());
        }

        private void addDescription(Composite parent) {
            Label fillerCol3Lbl = new Label(parent, SWT.NONE);
            fillerCol3Lbl.setLayoutData(new GridData());

            Label desc = createLabel(parent,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_DESCRIPTION));
            GridData gd = new GridData(GridData.FILL_BOTH);

            // Setting minimum width for First Column of Layout based on
            // description as it is largest text.
            gd.minimumWidth = convertWidthInCharsToPixels(desc.getText().length()) - 10;
            gd.widthHint = gd.minimumWidth;
            desc.setLayoutData(gd);
        }

        private void addNameRules(Composite parent) {
            Label fillerCol1Lbl = new Label(parent, SWT.NONE);
            fillerCol1Lbl.setLayoutData(new GridData());

            Label nameRules = createLabel(parent,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_NAME_RESTRICTION_MSG));
            FontData fontData = nameRules.getFont().getFontData()[0];
            fontData.setStyle(SWT.ITALIC);
            Font font = new Font(null, new FontData(fontData.getName(), fontData.getHeight(), SWT.ITALIC));
            nameRules.setFont(font);
        }

        private void addPatternEditor(Composite parent, Composite composite) {
            patternEditor = createEditor(composite, originalTemplate.getPattern());
            patternEditor.getTextWidget().addKeyListener(new SourceEditorKeyListener(patternEditor));
            patternEditor.getTextWidget().setTextLimit(PATTERN_MAX_LEN);

            patternCharLengthLbl = createLabel(parent, MessageConfigLoader
                    .getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR, PATTERN_MAX_LEN, PATTERN_MAX_LEN));
            GridData gdPattern = new GridData(GridData.VERTICAL_ALIGN_END | GridData.FILL_HORIZONTAL);
            gdPattern.minimumWidth = convertWidthInCharsToPixels(patternCharLengthLbl.getText().length() - 2);
            patternCharLengthLbl.setLayoutData(gdPattern);
        }

        private void addDescriptionText(Composite parent, Composite composite) {
            descriptionText = new Text(composite, SWT.BORDER);
            descriptionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            descriptionText.setTextLimit(DESCRIPTION_MAX_LEN);
            BidiUtils.applyBidiProcessing(descriptionText, BidiUtils.BTD_DEFAULT);

            descriptionCharLengthLbl = createLabel(parent, MessageConfigLoader.getProperty(
                    IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR, DESCRIPTION_MAX_LEN, DESCRIPTION_MAX_LEN));
            descriptionCharLengthLbl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        private Composite getDescroptionTextcomposite(Composite parent) {
            GridLayout gridLayout;
            Composite composite;
            composite = new Composite(parent, SWT.NONE);
            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            composite.setLayout(gridLayout);
            return composite;
        }

        private void addNameCharLength(Composite parent) {
            nameCharLengthLbl = createLabel(parent,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                            UIConstants.NAME_MAX_LEN, UIConstants.NAME_MAX_LEN));
            nameCharLengthLbl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        private void addNameText(Composite composite) {
            nameText = createText(composite);
            nameText.setTextLimit(UIConstants.NAME_MAX_LEN);

            BidiUtils.applyBidiProcessing(nameText, BidiUtils.BTD_DEFAULT);
        }

        private Composite getCompositeForNameText(Composite parent) {
            GridLayout gridLayout;
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            gridLayout = new GridLayout();
            gridLayout.numColumns = 1;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            composite.setLayout(gridLayout);
            return composite;
        }

        private Composite getParentComposite(Composite parent1) {
            Composite parent = new Composite(parent1, SWT.NONE);
            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            gridLayout.marginHeight = 0;
            gridLayout.marginWidth = 0;
            gridLayout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            gridLayout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            parent.setLayout(gridLayout);

            // Fixing the Dialog size to a specific width based on trial and
            // hit.
            GridData gdDialog = new GridData(GridData.FILL_BOTH);
            gdDialog.widthHint = 600;
            parent.setLayoutData(gdDialog);
            return parent;
        }

        /**
         * The listener interface for receiving addModify events. The class that
         * is interested in processing a addModify event implements this
         * interface, and the object created with that class is registered with
         * a component using the component's <code>addAddModifyListener<code>
         * method. When the addModify event occurs, that object's appropriate
         * method is invoked.
         *
         * AddModifyEvent
         */
        private class AddModifyListener implements ModifyListener {

            /**
             * Modify text.
             *
             * @param modifyEvent the modifyEvent
             */
            public void modifyText(ModifyEvent modifyEvent) {
                doTextWidgetChanged(modifyEvent.widget);
            }

        }

        private void doTextWidgetChanged(Widget widgetText) {
            if (widgetText == nameText) {
                suppressError = false;
                nameCharLengthLbl
                        .setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                                nameText.getText().length(), UIConstants.NAME_MAX_LEN));
                updateButtons();
            } else if (widgetText == descriptionText) {
                descriptionCharLengthLbl
                        .setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR,
                                descriptionText.getText().length(), DESCRIPTION_MAX_LEN));
            }
        }

        private void doSourceChanged(IDocument document) {
            suppressError = false;
            patternCharLengthLbl.setText(MessageConfigLoader.getProperty(
                    IMessagesConstants.CODE_TEMPLATE_DIALOG_MAX_CHAR, document.get().length(), PATTERN_MAX_LEN));
            updateAction(ITextEditorActionConstants.UNDO);
            updateButtons();
        }

        private static void createMandatoryLabel(Composite parent, String name) {
            createMandatoryLabel(parent, name, GridData.VERTICAL_ALIGN_CENTER);
        }

        private static void createMandatoryLabel(Composite parent, String name, int verticalAlign) {
            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayoutData(new GridData(verticalAlign));

            GridLayout gridLayout = new GridLayout();
            gridLayout.numColumns = 3;
            gridLayout.marginWidth = 0;
            gridLayout.marginHeight = 0;
            gridLayout.horizontalSpacing = 0;
            comp.setLayout(gridLayout);

            Label lbl = new Label(comp, SWT.NULL);
            lbl.setText(name);
            lbl.setLayoutData(new GridData());

            Label asteriskLbl = new Label(comp, SWT.NULL);
            asteriskLbl
                    .setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_MANDATORY_MARK));
            asteriskLbl.setForeground(new Color(null, 255, 0, 0));
            asteriskLbl.setLayoutData(new GridData());

            Label colonLbl = new Label(comp, SWT.NULL);
            colonLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_COLON));
            colonLbl.setLayoutData(new GridData());
        }

        private Label createLabel(Composite parent, String name) {
            Label lbl = new Label(parent, SWT.NULL);
            lbl.setText(name);
            lbl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            return lbl;
        }

        private static Text createText(Composite parent) {
            Text txt = new Text(parent, SWT.BORDER);
            txt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            return txt;
        }

        private SourceViewer createEditor(Composite parent, String pattern) {
            SourceViewer srcViewer = createViewer(parent);
            srcViewer.setEditable(true);

            IDocument doc = srcViewer.getDocument();
            if (doc != null) {
                doc.set(pattern);
            } else {
                doc = new Document(pattern);
                srcViewer.setDocument(doc);
            }

            int nLines = doc.getNumberOfLines();
            if (nLines < 5) {
                nLines = 5;
            } else if (nLines > 12) {
                nLines = 12;
            }

            SQLDocumentPartitioner.connectDocument(doc, 0);

            Control control = srcViewer.getControl();
            GridData gridData = new GridData(GridData.FILL_BOTH);
            gridData.widthHint = convertWidthInCharsToPixels(80);
            gridData.heightHint = convertHeightInCharsToPixels(nLines);
            control.setLayoutData(gridData);

            srcViewer.addTextListener(new ITextListener() {

                /**
                 * Text changed.
                 *
                 * @param event the event
                 */
                public void textChanged(TextEvent event) {
                    if (event.getDocumentEvent() != null) {
                        doSourceChanged(event.getDocumentEvent().getDocument());
                    }
                }
            });

            srcViewer.addSelectionChangedListener(new ISelectionChangedListener() {

                /**
                 * Selection changed.
                 *
                 * @param event the event
                 */
                public void selectionChanged(SelectionChangedEvent event) {
                    updateSelectionDependentActions();
                }
            });

            return srcViewer;
        }

        /**
         * Creates the viewer.
         *
         * @param parent the parent
         * @return the source viewer
         */
        protected SourceViewer createViewer(Composite parent) {
            SourceViewer viewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            viewer.configure(new SQLSourceViewerConfig(null));
            setDecoration(viewer);
            return viewer;
        }

        private void initializeActions() {
            TextViewerAction action = new TextViewerAction(patternEditor, ITextOperationTarget.UNDO);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_UNDO));
            globalActions.put(ITextEditorActionConstants.UNDO, action);

            action = new TextViewerAction(patternEditor, ITextOperationTarget.REDO);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_REDO));
            globalActions.put(ITextEditorActionConstants.REDO, action);

            action = new TextViewerAction(patternEditor, ITextOperationTarget.CUT);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_CUT));
            globalActions.put(ITextEditorActionConstants.CUT, action);

            action = new TextViewerAction(patternEditor, ITextOperationTarget.COPY);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_COPY));
            globalActions.put(ITextEditorActionConstants.COPY, action);

            action = new TextViewerAction(patternEditor, ITextOperationTarget.PASTE);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_PASTE));
            globalActions.put(ITextEditorActionConstants.PASTE, action);

            action = new TextViewerAction(patternEditor, ITextOperationTarget.SELECT_ALL);
            action.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DIALOG_SELECT_ALL));
            globalActions.put(ITextEditorActionConstants.SELECT_ALL, action);

            selectionActions.add(ITextEditorActionConstants.CUT);
            selectionActions.add(ITextEditorActionConstants.COPY);
            selectionActions.add(ITextEditorActionConstants.PASTE);

            // create context menu
            MenuManager menuManager = new MenuManager(null, null);
            menuManager.setRemoveAllWhenShown(true);
            menuManager.addMenuListener(new IMenuListener() {

                /**
                 * Menu about to show.
                 *
                 * @param mgr the mgr
                 */
                public void menuAboutToShow(IMenuManager mgr) {
                    fillContextMenu(mgr);
                }
            });

            StyledText text = patternEditor.getTextWidget();
            Menu menu = menuManager.createContextMenu(text);
            text.setMenu(menu);
        }

        private void fillContextMenu(IMenuManager menu) {
            menu.add(new GroupMarker(ITextEditorActionConstants.GROUP_UNDO));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_UNDO,
                    (IAction) globalActions.get(ITextEditorActionConstants.UNDO));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_UNDO,
                    (IAction) globalActions.get(ITextEditorActionConstants.REDO));

            menu.add(new Separator(ITextEditorActionConstants.GROUP_EDIT));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT,
                    (IAction) globalActions.get(ITextEditorActionConstants.CUT));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT,
                    (IAction) globalActions.get(ITextEditorActionConstants.COPY));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT,
                    (IAction) globalActions.get(ITextEditorActionConstants.PASTE));
            menu.appendToGroup(ITextEditorActionConstants.GROUP_EDIT,
                    (IAction) globalActions.get(ITextEditorActionConstants.SELECT_ALL));
        }

        private void updateSelectionDependentActions() {
            Iterator<String> iterator = selectionActions.iterator();
            while (iterator.hasNext()) {
                updateAction((String) iterator.next());
            }
        }

        private void updateAction(String actionId) {
            IAction action = (IAction) globalActions.get(actionId);
            if (action instanceof IUpdate) {
                ((IUpdate) action).update();
            }
        }

        private void updateButtons() {
            StatusInfo status = null;
            IStatus stat = null;

            /*
             * If name is not NULL, Name length is 0, Name contains Whitespace
             * Char If pattern length is 0, then disable Ok Button.
             */
            boolean valid = nameText == null || nameText.getText().trim().length() != 0;
            boolean containsWhitespace = false;
            if (nameText != null && valid) {
                String str = nameText.getText();
                int len = str.length();
                for (int i = 0; i < len; i++) {
                    char ch = str.charAt(i);
                    if (Character.isWhitespace(ch)) {
                        containsWhitespace = true;
                    }
                }
            }

            if (!valid || containsWhitespace) {
                status = new StatusInfo();
                if (!suppressError) {
                    status.setError("");
                    disableOk = true;
                }

                stat = status;
            } else if (!isValidPattern(patternEditor.getDocument().get())) {
                status = new StatusInfo();
                if (!suppressError) {
                    status.setError("");
                    disableOk = true;
                }

                stat = status;
            } else {
                stat = validationStatus;
                disableOk = false;
            }

            updateStatus(stat);
        }

        /**
         * Creates the buttons for button bar.
         *
         * @param parent the parent
         */
        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            okButton = createButton(parent, IDialogConstants.OK_ID,
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK), true);
            okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.newedit.ok.button");
            Button cancel = createButton(parent, IDialogConstants.CANCEL_ID,
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL), false);
            cancel.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.newedit.cancel.button");
        }

        /**
         * Update buttons enable state.
         *
         * @param status the status
         */
        protected void updateButtonsEnableState(IStatus status) {
            if (okButton != null && !okButton.isDisposed()) {
                okButton.setEnabled(!disableOk);
            }
        }

        /**
         * Checks if is valid pattern.
         *
         * @param pattern the pattern
         * @return true, if is valid pattern
         */
        protected boolean isValidPattern(String pattern) {
            if (pattern.length() == 0) {
                return false;
            }

            for (int i = 0; i < pattern.length(); i++) {
                char ch = pattern.charAt(i);
                if (!(ch == 9 || ch == 10 || ch == 13 || ch >= 32)) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Ok pressed.
         */
        protected void okPressed() {
            String name = nameText == null ? originalTemplate.getName() : nameText.getText();
            String pattern = patternEditor.getDocument().get();
            newTemplate = TemplateFactory.getTemplate(name, descriptionText.getText(), pattern);
            TemplatePersistenceDataIf[] templateData = TemplateStoreManager.getInstance().getTemplateData(false);
            Optional<TemplatePersistenceDataIf> existingTemplates = Arrays.stream(templateData)
                .filter(template -> name.equals(template.getTemplate().getName()))
                .findAny();
            if (existingTemplates.isPresent()) {
                Shell shell = Display.getCurrent().getActiveShell();
                MessageDialog.openInformation(shell,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_DUPLICATE_MSG, name));
            } else {
                super.okPressed();
            }
        }

        /**
         * Gets the template.
         *
         * @return the template
         */
        public TemplateIf getTemplate() {
            return newTemplate;
        }

        /**
         * Gets the template processor.
         *
         * @return the template processor
         */
        protected IContentAssistProcessor getTemplateProcessor() {
            return null;
        }

        /**
         * Gets the dialog bounds settings.
         *
         * @return the dialog bounds settings
         */
        protected IDialogSettings getDialogBoundsSettings() {
            return null;
        }

        /**
         * Close.
         *
         * @return true, if successful
         */
        public boolean close() {
            if (patternEditor != null && null != patternEditor.getDocument()) {
                try {
                    patternEditor.getDocument().set("");
                } catch (OutOfMemoryError exception) {
                    MPPDBIDELoggerUtility.error("DSTemplatePreferencePage: Out of memory error", exception);
                    SQLEditorPlugin.getDefault().setSQLCodeScanner(null);
                }
            }

            return super.close();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplateLabelProvider.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class TemplateLabelProvider extends LabelProvider implements ITableLabelProvider {

        /**
         * Gets the column image.
         *
         * @param element the element
         * @param columnIndex the column index
         * @return the column image
         */
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * Gets the column text.
         *
         * @param element the element
         * @param columnIndex the column index
         * @return the column text
         */
        public String getColumnText(Object element, int columnIndex) {
            TemplatePersistenceDataIf templdata = (TemplatePersistenceDataIf) element;
            TemplateIf template = templdata.getTemplate();

            switch (columnIndex) {
                case 0: {
                    return template.getName();
                }
                case 1: {
                    return template.getDescription();
                }
                default: {
                    return "";
                }
            }
        }
    }

    /**
     * Instantiates a new DS template preference page.
     */
    public DSTemplatePreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_TITLE));

        setPreferenceStore(PreferenceWrapper.getInstance().getPreferenceStore());
        setDescription(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_MESSAGE));
    }

    /**
     * Inits the.
     *
     * @param workbench the workbench
     */
    public void init(IWorkbench workbench) {
    }

    /**
     * Creates the control.
     *
     * @param parentObj the parent obj
     */
    @Override
    public void createControl(Composite parentObj) {
        super.createControl(parentObj);
        // rewrite default & apply
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
        getApplyButton().setEnabled(false);

        getDefaultsButton().setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.restoredefault.button");
        getApplyButton().setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.apply.button");
    }

    /**
     * Creates the contents.
     *
     * @param ancestor the ancestor
     * @return the control
     */
    protected Control createContents(Composite ancestor) {
        Composite parentComposite = getParentComposite(ancestor);

        Composite innerParent = getParentComposite(parentComposite);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        innerParent.setLayoutData(gd);

        Composite tableComposite = new Composite(innerParent, SWT.NONE);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = 360;
        data.heightHint = convertHeightInCharsToPixels(10);
        tableComposite.setLayoutData(data);

        ColumnLayout clmLayout = new ColumnLayout();
        tableComposite.setLayout(clmLayout);

        Table table = addDsTemplateTable(tableComposite);
        GC gc = new GC(getShell());
        gc.setFont(JFaceResources.getDialogFont());

        TemplateViewerComparator templtViewerComparator = new TemplateViewerComparator();

        TableColumn clm1 = addDsTemplateTableClm(clmLayout, table, gc, templtViewerComparator);

        addDsTemplateTableClm3(clmLayout, table, gc);

        gc.dispose();

        addCheckBoxTableViewer(table, templtViewerComparator, clm1);

        addDsTemplateButtons(innerParent);

        addDsTemplatePreviewUi(parentComposite);

        updateButtons();
        Dialog.applyDialogFont(parentComposite);
        innerParent.layout();

        addDefaultTemplateValues();
        return parentComposite;
    }

    /**
     * Gets the parent composite.
     *
     * @param ancestor the ancestor
     * @return the parent composite
     */
    private Composite getParentComposite(Composite ancestor) {
        Composite parentComposite = new Composite(ancestor, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        parentComposite.setLayout(gridLayout);
        return parentComposite;
    }

    /**
     * Adds the default template values.
     */
    private void addDefaultTemplateValues() {
        addButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.new.button");
        editButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.edit.button");
        removeButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.remove.button");
        restoreButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.restoreremoved.button");
        revertButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.revertdefault.button");
        matchCaseButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.matchcase.button");
        tableViewer.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.datagrid");
        patternViewer.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.preview.textarea");

    }

    /**
     * Adds the ds template preview ui.
     *
     * @param parentComposite the parent composite
     */
    private void addDsTemplatePreviewUi(Composite parentComposite) {
        patternViewer = doCreateViewer(parentComposite);

        matchCaseButton = new Button(parentComposite, SWT.CHECK);
        matchCaseButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_MATCH_CASE));
        GridData gd1 = new GridData();
        gd1.horizontalSpan = 2;
        matchCaseButton.setLayoutData(gd1);
        setDefaultMatchCase();
        matchCaseButton.addSelectionListener(new MatchCaseBtnSelectionListener());
    }

    /**
     * Adds the ds template buttons.
     *
     * @param innerParent the inner parent
     */
    private void addDsTemplateButtons(Composite innerParent) {
        GridLayout gridLayout;
        Composite buttons = new Composite(innerParent, SWT.NONE);
        buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        gridLayout = new GridLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        buttons.setLayout(gridLayout);

        addButton = new Button(buttons, SWT.PUSH);
        addButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_NEW));
        addButton.setLayoutData(getButtonGridData(addButton));
        addBtnSelectionListener();

        editButton = new Button(buttons, SWT.PUSH);
        editButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_EDIT));
        editButton.setLayoutData(getButtonGridData(editButton));
        editBtnSelectionListener();

        removeButton = new Button(buttons, SWT.PUSH);
        removeButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_REMOVE));
        removeButton.setLayoutData(getButtonGridData(removeButton));
        removeBtnSelectionListener();

        createSeparator(buttons);

        restoreButton = new Button(buttons, SWT.PUSH);
        restoreButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_RESTORE));
        restoreButton.setLayoutData(getButtonGridData(restoreButton));
        restoreBtnSelectionListener();

        revertButton = new Button(buttons, SWT.PUSH);
        revertButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_REVERT));
        revertButton.setLayoutData(getButtonGridData(revertButton));
        revertBtnSelectionListener();
    }

    /**
     * Adds the check box table viewer.
     *
     * @param table the table
     * @param templtViewerComparator the templt viewer comparator
     * @param clm1 the clm 1
     */
    private void addCheckBoxTableViewer(Table table, TemplateViewerComparator templtViewerComparator,
            TableColumn clm1) {
        tableViewer = new CheckboxTableViewer(table);
        tableViewer.setLabelProvider(new TemplateLabelProvider());
        tableViewer.setContentProvider(new TemplateContentProvider());
        tableViewer.setComparator(templtViewerComparator);

        // Specify default sorting here
        table.setSortColumn(clm1);
        table.setSortDirection(templtViewerComparator.getDirection());

        tableViewer.addDoubleClickListener(new TableViewerDoubleClickListener());

        tableViewer.addSelectionChangedListener(new TableViewerSelectionListener());

        tableViewer.addCheckStateListener(new TableviewerCheckStateListener());

        BidiUtils.applyTextDirection(tableViewer.getControl(), BidiUtils.BTD_DEFAULT);

        tableViewer.setInput(TemplateStoreManager.getInstance().getTemplateStore());
        tableViewer.setAllChecked(false);
        tableViewer.setCheckedElements(getEnabledTemplates());
    }

    /**
     * Adds the ds template table clm 3.
     *
     * @param clmLayout the clm layout
     * @param table the table
     * @param gc the gc
     * @return the table column
     */
    private TableColumn addDsTemplateTableClm3(ColumnLayout clmLayout, Table table, GC gc) {
        int minWidth;
        TableColumn column3 = new TableColumn(table, SWT.NONE);
        column3.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_COLUMN_DESCRIPTION));
        minWidth = computeMinimumColumnWidth(gc,
                MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_COLUMN_DESCRIPTION));
        clmLayout.addColumnData(new ColumnWeightData(3, minWidth, true));

        column3.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.datagrid.table.description");
        return column3;
    }

    /**
     * Adds the ds template table clm.
     *
     * @param clmLayout the clm layout
     * @param table the table
     * @param gc the gc
     * @param templtViewerComparator the templt viewer comparator
     * @return the table column
     */
    private TableColumn addDsTemplateTableClm(ColumnLayout clmLayout, Table table, GC gc,
            TemplateViewerComparator templtViewerComparator) {
        TableColumn clm1 = new TableColumn(table, SWT.NONE);
        clm1.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_COLUMN_NAME));
        int minWidth = computeMinimumColumnWidth(gc,
                MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_COLUMN_NAME));
        clmLayout.addColumnData(new ColumnWeightData(2, minWidth, true));
        clm1.addSelectionListener(new TemplateColumnSelectionAdapter(clm1, 0, templtViewerComparator));

        clm1.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.datagrid.table.name");

        return clm1;
    }

    /**
     * Adds the ds template table.
     *
     * @param tableComposite the table composite
     * @return the table
     */
    private Table addDsTemplateTable(Composite tableComposite) {
        Table table = new Table(tableComposite,
                SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL);

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.addKeyListener(new TableKeyListener());

        table.setData(MPPDBIDEConstants.SWTBOT_KEY, "pref.editor.templates.datagrid.table");
        return table;
    }

    /**
     * Sets the default match case.
     */
    private void setDefaultMatchCase() {
        if (getPreferenceStore() != null) {
            matchCaseButton.setSelection(getPreferenceStore().getBoolean(getMatchCasePreferenceKey()));
        }
    }

    /**
     * The listener interface for receiving matchCaseBtnSelection events. The
     * class that is interested in processing a matchCaseBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addMatchCaseBtnSelectionListener<code> method. When the
     * matchCaseBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * MatchCaseBtnSelectionEvent
     */
    private class MatchCaseBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            changePresent = true;
            getApplyButton().setEnabled(true);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // Auto-generated method stub

        }

    }

    /**
     * Revert btn selection listener.
     */
    private void revertBtnSelectionListener() {
        revertButton.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                revert();
            }
        });
    }

    /**
     * Restore btn selection listener.
     */
    private void restoreBtnSelectionListener() {
        restoreButton.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                restoreDeleted();
            }
        });
    }

    /**
     * Removes the btn selection listener.
     */
    private void removeBtnSelectionListener() {
        removeButton.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                remove();
            }
        });
    }

    /**
     * Edits the btn selection listener.
     */
    private void editBtnSelectionListener() {
        editButton.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                edit();
            }
        });
    }

    /**
     * Adds the btn selection listener.
     */
    private void addBtnSelectionListener() {
        addButton.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                add();
            }
        });
    }

    /**
     * The listener interface for receiving tableviewerCheckState events. The
     * class that is interested in processing a tableviewerCheckState event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addTableviewerCheckStateListener<code> method. When the
     * tableviewerCheckState event occurs, that object's appropriate method is
     * invoked.
     *
     * TableviewerCheckStateEvent
     */
    private class TableviewerCheckStateListener implements ICheckStateListener {

        @Override
        public void checkStateChanged(CheckStateChangedEvent event) {
            TemplatePersistenceDataIf data = (TemplatePersistenceDataIf) event.getElement();
            data.setEnabled(event.getChecked());
            changePresent = true;
            updateButtons();
        }

    }

    /**
     * The listener interface for receiving tableViewerSelection events. The
     * class that is interested in processing a tableViewerSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addTableViewerSelectionListener<code> method. When the
     * tableViewerSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * TableViewerSelectionEvent
     */
    private class TableViewerSelectionListener implements ISelectionChangedListener {

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            selectionChanged1();

        }

    }

    /**
     * The listener interface for receiving tableViewerDoubleClick events. The
     * class that is interested in processing a tableViewerDoubleClick event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addTableViewerDoubleClickListener<code> method. When the
     * tableViewerDoubleClick event occurs, that object's appropriate method is
     * invoked.
     *
     * TableViewerDoubleClickEvent
     */
    private class TableViewerDoubleClickListener implements IDoubleClickListener {

        @Override
        public void doubleClick(DoubleClickEvent event) {
            edit();

        }

    }

    /**
     * The listener interface for receiving tableKey events. The class that is
     * interested in processing a tableKey event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addTableKeyListener<code> method. When the tableKey
     * event occurs, that object's appropriate method is invoked.
     *
     * TableKeyEvent
     */
    private class TableKeyListener implements KeyListener {

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (isSelectAllKeyPress(keyEvent)) {
                Table tbl = (Table) keyEvent.getSource();
                tbl.setSelection(0, tbl.getItemCount());
                selectionChanged1();
            }

        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            // Auto-generated method stub

        }

        /**
         * Checks if is select all key press.
         *
         * @param keyEvent the e
         * @return true, if is select all key press
         */
        private boolean isSelectAllKeyPress(KeyEvent keyEvent) {
            // CTRL+a or CTRL+A
            return ((keyEvent.stateMask & SWT.CONTROL) != 0)
                    && ((keyEvent.keyCode == 'a') || (keyEvent.keyCode == 'A'));
        }

    }

    /**
     * Compute minimum column width.
     *
     * @param gc the gc
     * @param string the string
     * @return the int
     */
    private int computeMinimumColumnWidth(GC gc, String string) {
        // pad 10 to accommodate table header trimmings
        return gc.stringExtent(string).x + 10;
    }

    /**
     * Creates a separator between buttons.
     *
     * @param parent the parent composite
     * @return a separator
     */
    private Label createSeparator(Composite parent) {
        Label separatr = new Label(parent, SWT.NONE);
        separatr.setVisible(false);
        GridData grdData = new GridData();
        grdData.horizontalAlignment = GridData.FILL;
        grdData.verticalAlignment = GridData.BEGINNING;
        grdData.heightHint = 4;
        separatr.setLayoutData(grdData);
        return separatr;
    }

    /**
     * Gets the enabled templates.
     *
     * @return the enabled templates
     */
    private TemplatePersistenceDataIf[] getEnabledTemplates() {
        List<TemplatePersistenceDataIf> enabled = new ArrayList<TemplatePersistenceDataIf>();
        TemplatePersistenceDataIf[] datas = TemplateStoreManager.getInstance().getTemplateData(false);
        for (int i = 0; i < datas.length; i++) {
            if (datas[i].isEnabled()) {
                enabled.add(datas[i]);
            }
        }

        return (TemplatePersistenceDataIf[]) enabled.toArray(new TemplatePersistenceDataIf[enabled.size()]);
    }

    /**
     * Do create viewer.
     *
     * @param parent the parent
     * @return the source viewer
     */
    private SourceViewer doCreateViewer(Composite parent) {
        Label lbl = new Label(parent, SWT.NONE);
        lbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_PREVIEW));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        lbl.setLayoutData(gd);

        SourceViewer viewer2 = createViewer(parent);

        viewer2.setEditable(false);
        if (viewer2.getTextWidget().getDisplay() != null) {
            Cursor arrowCursor = viewer2.getTextWidget().getDisplay().getSystemCursor(SWT.CURSOR_ARROW);
            viewer2.getTextWidget().setCursor(arrowCursor);
        }
        viewer2.getTextWidget().addKeyListener(new SourceEditorKeyListener(viewer2, true));

        Control control = viewer2.getControl();
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        gd.heightHint = convertHeightInCharsToPixels(5);
        control.setLayoutData(gd);

        return viewer2;
    }

    /**
     * It Creates, configures and returns a source viewer to present the
     * template pattern on the preference page. User may override to provide a
     * custom source viewer featuring e.g. syntax coloring.
     *
     * @param parent the parent control
     * @return a configured source viewer
     */
    private SourceViewer createViewer(Composite parent) {
        SourceViewer viewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        viewer.configure(new SQLSourceViewerConfig(null));
        IDocument doc = new Document();
        viewer.setDocument(doc);
        setDecoration(viewer);
        SQLDocumentPartitioner.connectDocument(doc, 0);
        return viewer;
    }

    /**
     * Sets the decoration.
     *
     * @param viewer the new decoration
     */
    @SuppressWarnings("restriction")
    private static void setDecoration(SourceViewer viewer) {

        ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();

        SQLSourceViewerDecorationSupport sourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(viewer,
                null, null, sharedColors);
        sourceViewerDecorationSupport.installDecorations();

    }

    /**
     * It Return the grid data for the button.
     *
     * @param button the button
     * @return the grid data
     */
    private static GridData getButtonGridData(Button button) {
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        return data;
    }

    /**
     * Selection changed 1.
     */
    private void selectionChanged1() {
        updateViewerInput();
        updateButtons();
    }

    /**
     * Update viewer input.
     */
    protected void updateViewerInput() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

        if (selection.size() == 1) {
            TemplatePersistenceDataIf tpdata = (TemplatePersistenceDataIf) selection.getFirstElement();
            TemplateIf template = tpdata.getTemplate();
            patternViewer.getDocument().set(template.getPattern());
        } else {
            patternViewer.getDocument().set("");
        }
    }

    /**
     * Update buttons.
     */
    protected void updateButtons() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        int selectionCount = selection.size();
        int itemCount = tableViewer.getTable().getItemCount();
        int templateEntryCount = TemplateStoreManager.getInstance().getTemplateData(false).length;
        boolean canRestore = TemplateStoreManager.getInstance().getTemplateData(true).length != templateEntryCount;

        boolean canRevert = false;
        for (Iterator it = selection.iterator(); it.hasNext();) {
            TemplatePersistenceDataIf data = (TemplatePersistenceDataIf) it.next();
            if (data.isModified()) {
                canRevert = true;
                break;
            }
        }

        editButton.setEnabled(selectionCount == 1);
        removeButton.setEnabled(selectionCount > 0 && selectionCount <= itemCount);
        restoreButton.setEnabled(canRestore);
        revertButton.setEnabled(canRevert);
        if (getApplyButton() != null) {
            getApplyButton().setEnabled(changePresent);
        }
    }

    /**
     * Adds the.
     */
    private void add() {
        TemplateIf template = TemplateFactory.getTemplate("", "", "");

        TemplateIf newTemplate = editTemplate(template, false);
        if (newTemplate != null) {
            TemplatePersistenceDataIf data = TemplateFactory.getTemplatePersistenceData(newTemplate, true);
            TemplateStoreManager.getInstance().add(data);
            tableViewer.refresh();
            tableViewer.setChecked(data, true);
            tableViewer.setSelection(new StructuredSelection(data));
            tableViewer.getTable().setSelection(tableViewer.getTable().getSelectionIndex());
            changePresent = true;
            getApplyButton().setEnabled(changePresent);
        }
    }

    /**
     * Edits the template.
     *
     * @param template the template
     * @param edit the edit
     * @return the template if
     */
    protected TemplateIf editTemplate(TemplateIf template, boolean edit) {
        TemplateDialog dialog = new TemplateDialog(getShell(), template, edit);
        if (dialog.open() == Window.OK) {
            return dialog.getTemplate();
        }

        return null;
    }

    /**
     * Edits the.
     */
    private void edit() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

        Object[] objects = selection.toArray();
        if ((objects == null) || (objects.length != 1)) {
            return;
        }

        TemplatePersistenceDataIf data = (TemplatePersistenceDataIf) selection.getFirstElement();
        edit(data);
    }

    /**
     * Edits the.
     *
     * @param data the data
     */
    private void edit(TemplatePersistenceDataIf data) {
        if (null != data) {
            TemplateIf oldTemplate = data.getTemplate();
            TemplateIf newTemplate = editTemplate(TemplateFactory.getTemplate(oldTemplate), true);
            if (newTemplate != null) {
                data.setTemplate(newTemplate);
                tableViewer.refresh(data);

                selectionChanged1();
                tableViewer.setChecked(data, data.isEnabled());
                tableViewer.setSelection(new StructuredSelection(data));
                changePresent = true;
                getApplyButton().setEnabled(changePresent);
            }
        }
    }

    /**
     * Removes the.
     */
    private void remove() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

        Iterator elements = selection.iterator();
        while (elements.hasNext()) {
            TemplatePersistenceDataIf data = (TemplatePersistenceDataIf) elements.next();
            TemplateStoreManager.getInstance().delete(data);
        }

        tableViewer.getTable().setRedraw(false);
        tableViewer.refresh();
        tableViewer.getTable().setRedraw(true);
        changePresent = true;
        updateButtons();

    }

    /**
     * Restore deleted.
     */
    private void restoreDeleted() {
        TemplateStoreManager.getInstance().restoreDeleted();
        tableViewer.refresh();
        tableViewer.setCheckedElements(getEnabledTemplates());
        changePresent = true;
        updateButtons();
    }

    /**
     * Revert.
     */
    private void revert() {
        IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

        Iterator elements = selection.iterator();
        while (elements.hasNext()) {
            TemplatePersistenceDataIf data = (TemplatePersistenceDataIf) elements.next();
            data.revert();
            tableViewer.setChecked(data, data.isEnabled());
        }

        selectionChanged1();
        tableViewer.refresh();
        changePresent = true;
        updateButtons();
    }

    /**
     * Sets the visible.
     *
     * @param visible the new visible
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            setTitle(MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_TITLE));
        }
    }

    /**
     * Perform defaults.
     */
    protected void performDefaults() {
        IPreferenceStore prefs = getPreferenceStore();
        if (prefs != null) {
            matchCaseButton.setSelection(prefs.getDefaultBoolean(getMatchCasePreferenceKey()));
        }
        TemplateStoreManager.getInstance().restoreDefaults(false);

        // refresh
        tableViewer.getTable().setRedraw(false);
        tableViewer.refresh();
        tableViewer.getTable().setRedraw(true);
        tableViewer.setAllChecked(false);
        tableViewer.setCheckedElements(getEnabledTemplates());
        changePresent = true;
        updateButtons();
        MPPDBIDELoggerUtility.operationInfo("Templates in Preferences setting are set to default");
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    public boolean performOk() {
        if (matchCaseButton != null) {
            IPreferenceStore prefs = getPreferenceStore();
            if (prefs == null) {
                prefs = WorkbenchPlugin.getDefault().getPreferenceStore();
            }
            prefs.setValue(getMatchCasePreferenceKey(), matchCaseButton.getSelection());
            TemplateStoreManager.getInstance().setMatchCase(matchCaseButton.getSelection());
        }

        try {
            TemplateStoreManager.getInstance().save();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("DSTemplatePreferencePage: Ioexception occurred.", exception);
            openWriteErrorDialog(exception);
        }

        changePresent = false;
        return super.performOk();
    }

    /**
     * Perform apply.
     */
    @Override
    public void performApply() {
        super.performApply();
        getApplyButton().setEnabled(false);
        updateButtons();
        MPPDBIDELoggerUtility.operationInfo("Template is added/edited/removed in Preferences setting");
    }

    /**
     * Gets the match case preference key.
     *
     * @return the match case preference key
     */
    protected String getMatchCasePreferenceKey() {
        return DEFAULT_MATCHCASE_PREFERENCE_KEY;
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    public boolean performCancel() {
        try {
            TemplateStoreManager.getInstance().load();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("DSTemplatePreferencePage: Ioexception occurred.", exception);
            openReadErrorDialog(exception);
            return false;
        }

        changePresent = false;
        return super.performCancel();
    }

    /**
     * Open read error dialog.
     *
     * @param ex the ex
     */
    private void openReadErrorDialog(IOException ex) {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_ERROR_READ_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_ERROR_READ_MESSAGE);
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, title, message);
    }

    /**
     * Open write error dialog.
     *
     * @param ex the ex
     */
    private void openWriteErrorDialog(IOException ex) {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_ERROR_WRITE_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_ERROR_WRITE_MESSAGE);
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, title, message);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplateViewerComparator.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class TemplateViewerComparator extends ViewerComparator {
        private int sortColumn;

        private int sortOrder;

        /**
         * Instantiates a new template viewer comparator.
         */
        public TemplateViewerComparator() {
            sortColumn = 0;
            sortOrder = 1;
        }

        /**
         * Gets the direction.
         *
         * @return the direction
         */
        public int getDirection() {
            return sortOrder == 1 ? SWT.DOWN : SWT.UP;
        }

        /**
         * Sets the sort column. If the newly set sort column equals the
         * previous set sort column, the sort direction changes.
         * 
         * @param column New sort column
         */
        public void setColumn(int column) {
            if (column == sortColumn) {
                sortOrder *= -1;
            } else {
                sortColumn = column;
                sortOrder = 1;
            }
        }

        /**
         * Compare.
         *
         * @param viewer the viewer
         * @param e1 the e 1
         * @param e2 the e 2
         * @return the int
         */
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (viewer instanceof TableViewer) {
                IBaseLabelProvider baseLabel = ((TableViewer) viewer).getLabelProvider();

                String left = ((TemplateLabelProvider) baseLabel).getColumnText(e1, sortColumn);
                String right = ((TemplateLabelProvider) baseLabel).getColumnText(e2, sortColumn);
                int sortResult = getComparator().compare(left, right);
                return sortResult * sortOrder;
            }

            return super.compare(viewer, e1, e2);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TemplateColumnSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class TemplateColumnSelectionAdapter extends SelectionAdapter {
        private final TableColumn tableColumn;

        private final int columnIndex;

        private final TemplateViewerComparator viewerComparator;

        /**
         * Instantiates a new template column selection adapter.
         *
         * @param column the column
         * @param index the index
         * @param vc the vc
         */
        public TemplateColumnSelectionAdapter(TableColumn column, int index, TemplateViewerComparator vc) {
            tableColumn = column;
            columnIndex = index;
            viewerComparator = vc;
        }

        /**
         * Widget selected.
         *
         * @param e the e
         */
        public void widgetSelected(SelectionEvent e) {
            viewerComparator.setColumn(columnIndex);
            int dir = viewerComparator.getDirection();
            tableViewer.getTable().setSortDirection(dir);
            tableViewer.getTable().setSortColumn(tableColumn);
            tableViewer.refresh();
        }
    }

    /**
     * Gets the template file path.
     *
     * @return the template file path
     */
    private static URL getTemplateFilePath() {
        URL propertiesURL = null;
        ClassLoader classLoader = DSTemplatePreferencePage.class.getClassLoader();
        if (null != classLoader) {
            propertiesURL = classLoader.getResource("default_templates.json");
            return propertiesURL;
        }

        return propertiesURL;
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) throws IOException {
        URL defaultTemplatesFile = getTemplateFilePath();
        InputStream stream = null;
        String defaultTemplateJsonString = "";
        if (defaultTemplatesFile != null) {
            stream = defaultTemplatesFile.openStream();
        }

        try {
            if (stream != null) {
                byte[] defaultTemplatesBytes = IOUtils.toByteArray(stream);
                if (defaultTemplatesBytes.length != 0) {
                    defaultTemplateJsonString = new String(defaultTemplatesBytes, StandardCharsets.UTF_8);
                }
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("DSTemplatePreferencePage: Ioexception occurred.", exception);
                }
            }
        }

        preferenceStore.setDefault(UIConstants.TEMPLATESTORE_PREFERENCE_KEY, defaultTemplateJsonString);
        preferenceStore.setDefault(DEFAULT_MATCHCASE_PREFERENCE_KEY, true);
    }

    /**
     * Sets the preferences.
     *
     * @param ps the new preferences
     */
    public static void setPreferences(PreferenceStore ps) {

        TemplateStoreManager.getInstance().createTemplateStore(ps);
        TemplateStoreManager.getInstance()
                .setMatchCase(Boolean.parseBoolean(ps.getString(DEFAULT_MATCHCASE_PREFERENCE_KEY)));

    }

    /**
     * Perform clean up.
     */
    private void performCleanUp() {

        try {
            TemplateStoreManager.getInstance().load();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("TemplateStoreManager: Ioexception occurred.", exception);
        }

        IPreferenceStore prefs = getPreferenceStore();
        if (prefs != null) {
            matchCaseButton.setSelection(prefs.getBoolean(getMatchCasePreferenceKey()));
        }
        tableViewer.refresh();
        tableViewer.setAllChecked(false);
        tableViewer.setCheckedElements(getEnabledTemplates());

        changePresent = false;
        updateButtons();
    }

    /**
     * Ok to leave.
     *
     * @return true, if successful
     */
    @Override
    public boolean okToLeave() {
        if (changePresent) {
            int res = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_UNSAVEDCHANGED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_CHANGE_NOT_APPLIED_MESSAGE));
            if (UIConstants.OK_ID == res) {
                performOk();
            } else {
                performCleanUp();
            }
        }

        changePresent = false;
        return super.okToLeave();
    }

    /**
     * Dispose.
     */
    public void dispose() {
        if (patternViewer != null && null != patternViewer.getDocument()) {
            try {
                patternViewer.getDocument().set("");
            } catch (OutOfMemoryError exception) {
                MPPDBIDELoggerUtility.error("Out of memory exception happened.", exception);
                SQLEditorPlugin.getDefault().setSQLCodeScanner(null);
            }
        }

        super.dispose();
    }
}
