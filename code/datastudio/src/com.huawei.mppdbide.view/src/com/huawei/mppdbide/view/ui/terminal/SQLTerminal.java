/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.Active;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler.Save;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotationMarkerAccess;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLEditorPlugin;
import com.huawei.mppdbide.view.core.sourceeditor.SQLPartitionScanner;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.data.DSViewDataManager;
import com.huawei.mppdbide.view.filesave.DirtyTerminalDialog;
import com.huawei.mppdbide.view.handler.ExecuteEditorItem;
import com.huawei.mppdbide.view.handler.OpenSQLHandler;
import com.huawei.mppdbide.view.handler.SaveSQLAsHandler;
import com.huawei.mppdbide.view.handler.SaveSQLHandler;
import com.huawei.mppdbide.view.prefernces.DBAssistantOption;
import com.huawei.mppdbide.view.prefernces.DSTransactionPreferencePage;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.terminal.executioncontext.SQLTerminalExecutionContext;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.IDEStartup;
import com.huawei.mppdbide.view.ui.QueryInfo;
import com.huawei.mppdbide.view.ui.SqlHistory;
import com.huawei.mppdbide.view.ui.autosave.AbstractAutoSaveObject;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.ui.saveif.ISaveablePart;
import com.huawei.mppdbide.view.ui.terminal.resulttab.ResultTabManager;
import com.huawei.mppdbide.view.ui.terminalautosave.SQLTerminalAutoSaveIf;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.MultiCheckSelectionCombo;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.consts.WHICHOPTION;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogsWithDoNotShowAgain;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminal.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class SQLTerminal extends AbstractAutoSaveObject implements ISaveablePart, SQLTerminalAutoSaveIf {
    private static final int BUTTIONS_NUMBER = 9;
    private PLSourceEditorCore sourceEditor;
    private AnnotationModel fAnnotationModel;

    /**
     * The annotation access.
     */
    private IAnnotationAccess fAnnotationAccess;
    private Composite toolbarComposite;
    private boolean isCancelQueryPressed;

    private TerminalExecutionSQLConnectionInfra termConnection;

    @Inject
    private ECommandService commandService;
    @Inject
    private EHandlerService handlerService;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private MDirtyable dirtyHandler;

    @Inject
    private ESelectionService selectionService;

    private boolean isExecuteInProgress;
    private boolean isExplainPlanInProgress = false;
    private Button executeButton;
    private Button newTabexecuteButton;
    private Button autoCommitButton;
    private Button commitButton;
    private Button rollbackButton;
    private Button reuseConnectionButton;
    private Button sqlhistoryButton;
    private Button editTerminalValueButton;
    private MultiCheckSelectionCombo analyzeOption;
    private Button executionPlanButton;
    private boolean includeAnalyze;

    private Button visualPlanButton;

    private SashForm parent;
    private String uiID;
    private SQLTerminalResultDisplayUIManager resultDisplayUIManager;
    private SQLTerminalResultConfig resultConfig;

    private MenuItem menuOpenSQL;
    private MenuItem menuSaveSQL;
    private MenuItem menuSaveAsSQL;
    private MenuItem menuFind;
    private MenuItem menuExecutionPlan;
    private MenuItem menuCancelExecution;
    private boolean isQueryEnabled = true;
    private AnnotationRulerColumn annotationRulerColumn;
    private CompositeRuler fCompositeRuler;
    private AnnotationPainter ap;
    private ResultTabManager resultManager;
    private SQLTerminalExecutionContext execContext;
    private String partLabel;
    private boolean autoCommit;
    private String toolTip;
    private String defLabelId = "";

    /**
     * Used for detecting an enter key is pressed.
     */
    private boolean enteredPressed;
    private SqlTerminalHelper helper;

    private String localText = null;
    private final Object instanceLock = new Object();

    private boolean isInited = false;
    private boolean isActivated = false;
    private boolean isProfileExistsFlag = true;
    private boolean isEditableFlag = true;
    private boolean isAddModifyListener = false;

    private String filePath;
    private MPart sqlTerminalPart;
    private boolean fileTerminalFlag = false;
    private boolean exitDsFlag = false;
    private boolean openSqlFlag = false;
    private Date lastSaveTime = new Date();
    private SQLTerminalUtility terminalUtility;
    private ArrayList<DefaultParameter> inputDailogValueList;
    private MPart tab;
    private IDebugObject debugObject;

    @Override
    public PLSourceEditorCore getSourceEditorCore() {
        return sourceEditor;
    }

    public IDebugObject getDebugObject() {
        return debugObject;
    }

    public void setDebugObject(IDebugObject debugObject) {
        this.debugObject = debugObject;
    }

    /**
     * Gets the server version.
     *
     * @return the server version
     */
    public String getServerVersion() {
        return terminalUtility.getServerVersion(getDatabase());
    }

    /**
     * Checks if is include analyze.
     *
     * @return true, if is include analyze
     */
    public boolean isIncludeAnalyze() {
        return this.includeAnalyze;
    }

    /**
     * Sets the include analyze.
     *
     * @param includeAnalyze the new include analyze
     */
    public void setIncludeAnalyze(boolean includeAnalyze) {
        this.includeAnalyze = includeAnalyze;
    }

    /**
     * Checks if is query enabled.
     *
     * @return true, if is query enabled
     */
    public boolean isQueryEnabled() {
        return isQueryEnabled;
    }

    /**
     * Sets the query enabled.
     *
     * @param isQryEnabled the new query enabled
     */
    public void setQueryEnabled(boolean isQryEnabled) {
        this.isQueryEnabled = isQryEnabled;
    }

    /**
     * sets the dailog input value into terminal object
     * 
     * @param valueList the valueList
     */
    public void setInputDailogValueTerminal(ArrayList<DefaultParameter> valueList) {
        this.inputDailogValueList = valueList;
    }

    /**
     * gets the dailog input value from terminal object
     * 
     * @return the list of input values
     */
    public ArrayList<DefaultParameter> getInputDailogValueTerminal() {
        if (inputDailogValueList != null) {
            return this.inputDailogValueList;
        }
        return null;
    }

    /**
     * Gets the execute button.
     *
     * @return the execute button
     */
    public Button getExecuteButton() {
        return executeButton;
    }

    /**
     * Gets the new tab execute button.
     *
     * @return the new tab execute button
     */
    public Button getNewTabExecuteButton() {
        return newTabexecuteButton;
    }

    private void toggleNewTabExecuteButton(boolean state) {
        if (newTabexecuteButton.isDisposed()) {
            return;
        }
        if (UserPreference.getInstance().isGenerateNewResultWindow()) {
            newTabexecuteButton.setEnabled(state);
        } else {
            newTabexecuteButton.setEnabled(false);
        }
    }

    /**
     * Enable/Disable edit terminal value button.
     *
     * @param isEnable the is enable
     */
    public void enableEditInputValueButton(boolean isEnable) {
        if (!editTerminalValueButton.isDisposed()) {
            editTerminalValueButton.setEnabled(isEnable);
        }
    }

    /**
     * Disable execution button.
     *
     * @param isEnable the is enable
     */
    public void disableExecutionButton(boolean isEnable) {
        if (!executeButton.isDisposed()) {
            executeButton.setEnabled(isEnable);
        }
        toggleNewTabExecuteButton(isEnable);
        if (!executionPlanButton.isDisposed()) {
            executionPlanButton.setEnabled(isEnable);
        }
    }

    /**
     * Reset SQL terminal button.
     */
    public void resetSQLTerminalButton() {
        if (!isActivated()) {
            return;
        }
        executeButton.setEnabled(terminalUtility.isButtonEnabled(isActivated(), sourceEditor));
        toggleNewTabExecuteButton(terminalUtility.isButtonEnabled(isActivated(), sourceEditor));
        enableDisableExplainPlanButtonGroup();
        visualPlanButton.setEnabled(isVisualPlanEnabled());
    }

    private void enableDisableExplainPlanButtonGroup() {
        executionPlanButton.setEnabled(isExecutionPlanEnabled());
        analyzeOption.setEnabled(isExecutionPlanEnabled());
    }

    /**
     * Reset auto commit button.
     */
    public void resetAutoCommitButton() {
        if (!isActivated()) {
            return;
        }
        if (!isShowAutoCommit()) {
            return;
        }
        boolean toEnable = terminalUtility.enableAutoCommit();
        if (!toEnable) {
            autoCommitButton.setSelection(false);
            autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_DISABLED, getClass()));
            if (null == getTermConnection().getConnection()) {
                autoCommitButton.setToolTipText(
                        MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_DISABLED_TOOL_TIP));
            }
            autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
        } else {
            if (autoCommit) {
                autoCommitButton.setSelection(true);
                autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_ON, getClass()));
                autoCommitButton
                        .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_ON_TOOL_TIP));
                autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
            } else {
                autoCommitButton.setSelection(false);
                autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_OFF, getClass()));
                autoCommitButton.setToolTipText(
                        MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_OFF_TOOL_TIP));
                autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
            }
        }

        autoCommitButton.setEnabled(toEnable);
    }

    private boolean isActivated() {
        return isActivated;
    }

    /**
     * Gets the source viewer.
     *
     * @return the source viewer
     */
    public ISourceViewer getSourceViewer() {
        return sourceEditor.getSourceViewer();
    }

    /**
     * Checks if is visual plan enabled.
     *
     * @return true, if is visual plan enabled
     */
    public boolean isVisualPlanEnabled() {
        Command cmd = EclipseInjections.getInstance().getCommandService()
                .getCommand("com.huawei.mppdbide.view.command.visualexplainplan");
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
        if (EclipseInjections.getInstance().getHandlerService().canExecute(parameterizedCmd)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is execution plan enabled.
     *
     * @return true, if is execution plan enabled
     */
    public boolean isExecutionPlanEnabled() {
        if (!isActivated()) {
            return false;
        }

        Command cmd = EclipseInjections.getInstance().getCommandService()
                .getCommand("com.huawei.mppdbide.command.id.executionplanandcost");
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
        if (EclipseInjections.getInstance().getHandlerService().canExecute(parameterizedCmd)) {
            return true;
        }
        return false;
    }

    /**
     * Instantiates a new SQL terminal.
     */
    public SQLTerminal() {
        termConnection = new TerminalExecutionSQLConnectionInfra();
        fAnnotationModel = new AnnotationModel();
        helper = new SqlTerminalHelper();
        isActivated = false;
        terminalUtility = new SQLTerminalUtility();

    }

    /**
     * Sets the execute DB.
     *
     * @param db the new execute DB
     */
    public void setExecuteDB(Database db) {
        helper.setExecuteDB(db);
    }

    /**
     * Sets the ui ID.
     *
     * @param uiID the new ui ID
     */
    public void setUiID(String uiID) {
        this.uiID = uiID;
    }

    /**
     * Gets the ui ID.
     *
     * @return the ui ID
     */
    public String getUiID() {
        return uiID;
    }

    private void resetConnButtonsInternal(boolean isProfileExists) {
        resetSQLHistoryButton(isProfileExists);
    }

    /**
     * Reset conn buttons.
     *
     * @param isProfileExists the is profile exists
     */
    /*
     * while Overriding the following method in subclass, method should be made
     * thread-safe similar to super class implementation since synchronization
     * is needed to reset connection buttons for the respective SQL Terminal
     */
    @Override
    public void resetConnButtons(final boolean isProfileExists) {
        synchronized (instanceLock) {
            isProfileExistsFlag = isProfileExists;
            if (!isActivated()) {
                return;
            }

            // currently only sql history button is connection dependent
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    resetConnButtonsInternal(isProfileExists);
                }
            });
        }
    }

    /**
     * Reset SQL history button.
     *
     * @param isEnable the is enable
     */
    public void resetSQLHistoryButton(boolean isEnable) {
        if (!this.isInited) {
            return;
        }
        if (isEnable) {
            sqlHistoryButtonEnableDisable(true, IMessagesConstants.SQL_HISTORY_TOOL_TRIP);
        } else {
            if (this.getSelectedDatabase() == null) {
                sqlHistoryButtonEnableDisable(false, IMessagesConstants.SQL_HISTORY_DISABLED_TOOL_TIP);
            } else if (!this.getSelectedDatabase().isConnected()) {
                sqlHistoryButtonEnableDisable(false, IMessagesConstants.SQL_HISTORY_DISABLED_TOOL_TIP);
            }
        }

        sqlhistoryButton.getParent().setToolTipText(sqlhistoryButton.getToolTipText());
    }

    private void sqlHistoryButtonEnableDisable(boolean isSqlHistoryEnabled, String message) {
        sqlhistoryButton.setEnabled(isSqlHistoryEnabled);
        sqlhistoryButton.setToolTipText(MessageConfigLoader.getProperty(message));
    }

    /**
     * Creates the part control.
     *
     * @param parentComp the parent comp
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     * @param part the part
     */
    @PostConstruct
    public void createPartControl(Composite parentComp, EPartService partService, EModelService modelService,
            MApplication application, @Active MPart part) {
        this.tab = part;
        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        if (PreferenceWrapper.getInstance().getPreferenceStore().getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE)) {
            DBAssistantWindow.setEnableA(true);
        }
        DBAssistantWindow.setAllTerminalsClosed(false);
        IDEStartup.getInstance().init(partService, modelService, application);

        Composite currComposite = getCurrentComposite(parentComp);
        SQLTerminal sqlt = (SQLTerminal) part.getObject();
        createConnectionSelection(currComposite, sqlt);

        sourceEditor = new PLSourceEditorCore(fAnnotationModel, getAnnotationAccess());

        this.setExecuteDB(sqlt.getSelectedDatabase());
        setSourceViewerCommandMenu(sqlt);

        SashForm sashForm = new SashForm(currComposite, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);
        this.parent = sashForm;
        executeButton.setEnabled(false);
        newTabexecuteButton.setEnabled(false);
        editTerminalValueButton.setEnabled(false);
        enableAutocommit();
        CompositeRuler compositeRuler = getCompositeRuler();
        sourceEditor.createEditor(sashForm, compositeRuler, null, getSQLSyntax(sqlt), true);
        sourceEditor.setDatabase(sqlt.getSelectedDatabase());
        ((ErrorAnnotationMarkerAccess) fAnnotationAccess).setDocument(sourceEditor.getSourceViewer().getDocument());

        sourceEditor.getSourceViewer().getTextWidget().addVerifyKeyListener(new SourceViewerVerifyKeyListener());

        sourceEditor.getSourceViewer().addTextListener(new SourceViewerTextListener());
        sourceEditor.getSourceViewer().getTextWidget().addModifyListener(new SourceModifyListener());
        sourceEditor.installDecorationSupport();

        /*
         * Need to get the part's object that was set before activating this, as
         * that contains the details of the terminal's database and UID.
         */
        this.uiID = sqlt.uiID;

        addSourceViewerPainter(compositeRuler);
        hookContextMenu();
        handlePendingActionOnActivation(sqlt);
        setSqlTerminalPart(part);
    }

    /**
     * Title: SourceModifyListener
     * 
     * Description:
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * 
     * @author sWX316469
     * @version [DataStudio 6.5.1, 16-Oct-2019]
     * @since 16-Oct-2019
     */

    public class SourceModifyListener implements ModifyListener {

        /**
         * Modify text.
         *
         * @param e the e
         */
        @Override
        public void modifyText(ModifyEvent e) {
            if (isFileTerminalFlag()) {
                if (isOpenSqlFlag()) {
                    dirtyHandler.setDirty(true);
                    menuSaveSQL.setEnabled(true);
                }
            }

        }

    }

    private void setSourceViewerCommandMenu(SQLTerminal sqlt) {
        sourceEditor.setSyntax(getSQLSyntax(sqlt));
        sourceEditor.setCommandAndHandlerService(commandService, handlerService, selectionService);

        helper.setSqlCmdMenuKey(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_EXECUTE_STATEMENT);
        helper.setSqlCmdMenuIcon(IiconPath.ICO_EXEC_SQL_TERMINAL);
    }

    private void addSourceViewerPainter(CompositeRuler compositeRuler) {
        AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(compositeRuler,
                sourceEditor.getSourceViewer(), new AnnotationHover(), new AnnotationConfiguration());
        fAnnotationHoverManager.install(annotationRulerColumn.getControl());

        // hover manager that shows text when we hover
        ap = SQLTerminalUtility.createAnnotationPainter(sourceEditor.getSourceViewer(), fAnnotationAccess);
        sourceEditor.getSourceViewer().addPainter(ap);
    }

    private Composite getCurrentComposite(Composite parentComp) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.FILL;
        gridData.grabExcessHorizontalSpace = true;

        Composite currComposite = new Composite(parentComp, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        currComposite.setLayout(layout);
        currComposite.setData(gridData);
        return currComposite;
    }

    /**
     * The listener interface for receiving sourceViewerText events. The class
     * that is interested in processing a sourceViewerText event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSourceViewerTextListener<code>
     * method. When the sourceViewerText event occurs, that object's appropriate
     * method is invoked.
     *
     * SourceViewerTextEvent
     */
    private class SourceViewerTextListener implements ITextListener {
        @Override
        public void textChanged(TextEvent event) {
            ITextSelection textSelection = (ITextSelection) sourceEditor.getSourceViewer().getSelection();
            if (textSelection.getLength() > 0) {
                int startLine = textSelection.getStartLine() + 1;
                int endLine = textSelection.getEndLine() + 1;
                for (int index = startLine; index <= endLine; index++) {
                    removeAnnotation(index);
                }
            } else {
                Widget widget = sourceEditor.getSourceViewer().getTextWidget();
                StyledText styText = (StyledText) widget;
                int caretOffset = styText.getCaretOffset();
                int lineAtOffset = styText.getLineAtOffset(caretOffset);
                /*
                 * Line number starts from 0 so increment to compare. If enter
                 * key is pressed, then do not increment. When enter key is
                 * pressed, the modified event is Triggered for the next line
                 */
                lineAtOffset++;
                removeAnnotation(lineAtOffset);
            }
            executeButton.setEnabled(terminalUtility.isButtonEnabled(isActivated(), sourceEditor));
            toggleNewTabExecuteButton(terminalUtility.isButtonEnabled(isActivated(), sourceEditor));
        }
    }

    /**
     * The listener interface for receiving sourceViewerVerifyKey events. The
     * class that is interested in processing a sourceViewerVerifyKey event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSourceViewerVerifyKeyListener<code> method. When the
     * sourceViewerVerifyKey event occurs, that object's appropriate method is
     * invoked.
     *
     * SourceViewerVerifyKeyEvent
     */
    private class SourceViewerVerifyKeyListener implements VerifyKeyListener {
        @Override
        public void verifyKey(VerifyEvent event) {
            // Enter key is pressed.
            if (event.character == SWT.CR) {
                enteredPressed = true;
            }
        }
    }

    private void removeAnnotation(int lineAtOffset) {
        Iterator iter = fAnnotationModel.getAnnotationIterator();
        if (iter != null) {
            while (iter.hasNext()) {
                Annotation annotation = (Annotation) iter.next();
                if (ErrorAnnotation.STRATEGY_ID.equals(annotation.getType())) {
                    ErrorAnnotation errorAnnotation = (ErrorAnnotation) annotation;
                    if (errorAnnotation.getLine() == lineAtOffset && !enteredPressed) {
                        fAnnotationModel.removeAnnotation(errorAnnotation);
                    }
                }
            }
        }
        enteredPressed = false;
    }

    private SQLSyntax getSQLSyntax(SQLTerminal sqlt) {
        return sqlt.getSelectedDatabase() != null ? sqlt.getSelectedDatabase().getSqlSyntax() : null;
    }

    private void hookContextMenu() {
        Menu menu = sourceEditor.getMenu();
        addExecutionPlanMenuItem(menu);
        addCancelMenuItem(menu);
        addFindMenuItem(menu);
        addOpenSQLMenuItem(menu);
        addSaveSQLMenuItem(menu);
        addSaveSQLAsMenuItem(menu);

        menu.addMenuListener(new MenuListener() {
            @Override
            public void menuShown(MenuEvent e) {
                contextMenuAboutToShow();
            }

            @Override
            public void menuHidden(MenuEvent e) {

            }
        });
    }

    private void setActivated(boolean value) {
        isActivated = value;
    }

    private void setProfileExistsFlag(boolean isProfileExistsFlag) {
        this.isProfileExistsFlag = isProfileExistsFlag;
    }

    private void setEditableFlag(boolean isEditableFlag) {
        this.isEditableFlag = isEditableFlag;
    }

    private void setAddModifyListener(boolean isAddModifyListener) {
        this.isAddModifyListener = isAddModifyListener;
    }

    private boolean isProfileExistsFlag() {
        return isProfileExistsFlag;
    }

    private boolean isEditableFlag() {
        return isEditableFlag;
    }

    private boolean isAddModifyListener() {
        return isAddModifyListener;
    }

    /**
     * Handle pending action on activation.
     *
     * @param sqltrminal the sqltrminal
     */
    public void handlePendingActionOnActivation(SQLTerminal sqltrminal) {
        synchronized (sqltrminal.instanceLock) {
            setActivated(true);
            if (sqltrminal != this) {
                sqltrminal.setActivated(true);

                /* Get values from sqlt and update here */
                setProfileExistsFlag(sqltrminal.isProfileExistsFlag());
                setEditableFlag(sqltrminal.isEditableFlag());
                setAddModifyListener(sqltrminal.isAddModifyListener());
                setModified(sqltrminal.isModified());
                setModifiedAfterCreate(sqltrminal.isModifiedAfterCreate());
                updateStatus(sqltrminal.getStatus());
                setConnectionName(sqltrminal.getConnectionName());
                setDatabaseName(sqltrminal.getDatabaseName());
                setElementID(sqltrminal.getElementID());
                setTabLabel(sqltrminal.getTabLabel());
                setTabToolTip(sqltrminal.getTabToolTip());
            }

            if (sqltrminal.getTextOnly() != null) {
                setDocumentContent(sqltrminal.getTextOnly());
                sqltrminal.setTextOnly(null);
            }
            setTextOnly(null);
            sourceEditor.setEditable(this.isEditableFlag());
            setInited(true);
            resetConnButtonsInternal(this.isProfileExistsFlag());
            resetButtons();
            if (this.isAddModifyListener()) {
                sourceEditor.getSourceViewer().getDocument().addDocumentListener(getAutoSaveModifyListener());
            }
        }
    }

    private String getTextOnly() {
        return localText;
    }

    private void setTextOnly(String localText) {
        this.localText = localText;
    }

    private void setInited(boolean isInited) {
        this.isInited = isInited;
    }

    private void enableAutocommit() {
        if (isShowAutoCommit() && isActivated()) {
            autoCommitButton.setEnabled(true);
        }
    }

    /**
     * Show error.
     *
     * @param errorDetails the error details
     */
    public void showError(final QueryInfo errorDetails) {

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                ErrorAnnotation errorAnnotation = new ErrorAnnotation(errorDetails.getErrLineNo(),
                        errorDetails.getServerMessageString());
                int position = errorDetails.getErrorPosition();
                fAnnotationModel.addAnnotation(errorAnnotation,
                        new Position(position - 1, errorDetails.getErrorMsgString().length()));
            }
        });
    }

    /**
     * Removes the all errors.
     */
    public void removeAllErrors() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                fAnnotationModel.removeAllAnnotations();
            }
        });
    }

    /**
     * Removes the errors in selected range.
     *
     * @param off the off
     * @param length the length
     * @param startsBefore the starts before
     * @param endsAfter the ends after
     */
    public void removeErrorsInSelectedRange(int off, int length, boolean startsBefore, boolean endsAfter) {
        @SuppressWarnings("unchecked")
        Iterator<Annotation> annotationIterator = fAnnotationModel.getAnnotationIterator(off, length, startsBefore,
                endsAfter);
        Annotation anno = null;

        while (annotationIterator.hasNext()) {
            anno = annotationIterator.next();
            fAnnotationModel.removeAnnotation(anno);
        }
    }

    /**
     * Create annotation access.
     *
     * @return the annotation access
     */
    private IAnnotationAccess getAnnotationAccess() {
        if (null == fAnnotationAccess) {
            fAnnotationAccess = new ErrorAnnotationMarkerAccess(fAnnotationModel);
        }
        return fAnnotationAccess;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AnnotationConfiguration.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class AnnotationConfiguration implements IInformationControlCreator {

        /**
         * Creates the information control.
         *
         * @param shell the shell
         * @return the i information control
         */
        public IInformationControl createInformationControl(Shell shell) {
            return new DefaultInformationControl(shell);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AnnotationHover.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    // annotation hover manager
    private class AnnotationHover implements IAnnotationHover, ITextHover {

        /**
         * Gets the hover info.
         *
         * @param sourceViewer the source viewer
         * @param lineNumber the line number
         * @return the hover info
         */
        public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
            List<String> hoverStr = new ArrayList<String>();
            Iterator<?> ite = fAnnotationModel.getAnnotationIterator();
            Annotation annotation = null;
            int cnt = 0;

            while (ite.hasNext()) {
                annotation = (Annotation) ite.next();
                switch (annotation.getType()) {
                    case ErrorAnnotation.STRATEGY_ID: {
                        cnt = ((ErrorAnnotation) annotation).getLine();
                        if (lineNumber + 1 == cnt) {
                            hoverStr.add(((ErrorAnnotation) annotation).getText());
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
            StringBuilder hoverInfo = new StringBuilder();
            if (hoverStr.size() > 1) {
                hoverInfo.append(MessageConfigLoader.getProperty(IMessagesConstants.MUTIPLE_MARKERS))
                        .append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            for (int i = 0; i < hoverStr.size(); i++) {
                hoverInfo.append("-").append(hoverStr.get(i)).append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            return hoverInfo.toString();
        }

        @Override
        public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {

            return null;
        }

        @Override
        public IRegion getHoverRegion(ITextViewer textViewer, int offset) {

            return null;
        }
    }

    /**
     * Gets the server message to hover.
     *
     * @param exception the exception
     * @return the server message to hover
     */
    public String getServerMessageToHover(MPPDBIDEException exception) {

        return exception.getServerMessage();
    }

    private void addOpenSQLMenuItem(Menu menu) {
        final OpenSQLHandler openSQLHandler = new OpenSQLHandler();
        menuOpenSQL = new MenuItem(menu, SWT.PUSH);
        menuOpenSQL.setText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_OPEN));
        menuOpenSQL.addSelectionListener(new OpenSQLSelectListener(openSQLHandler));
        menuOpenSQL.setImage(IconUtility.getIconImage(IiconPath.ICO_OPEN_SQL, this.getClass()));
    }

    private void addSaveSQLMenuItem(Menu menu) {
        final SaveSQLHandler saveSQLHandler = new SaveSQLHandler();
        menuSaveSQL = new MenuItem(menu, SWT.PUSH);
        menuSaveSQL.setText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_SAVE));
        menuSaveSQL.addSelectionListener(new SaveSQLSelectListener(saveSQLHandler));
        menuSaveSQL.setImage(IconUtility.getIconImage(IiconPath.ICO_SAVE_SQL, this.getClass()));
    }

    private void addSaveSQLAsMenuItem(Menu menu) {
        final SaveSQLAsHandler saveSQLAsHandler = new SaveSQLAsHandler();
        menuSaveAsSQL = new MenuItem(menu, SWT.PUSH);
        menuSaveAsSQL.setText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_SAVE_AS));
        menuSaveAsSQL.addSelectionListener(new SaveSQLAsSelectListener(saveSQLAsHandler));
        menuSaveAsSQL.setImage(IconUtility.getIconImage(IiconPath.ICO_SAVE_SQL_AS, this.getClass()));
    }

    private void addFindMenuItem(Menu menu) {
        menuFind = new MenuItem(menu, SWT.PUSH);
        menuFind.setText(MessageConfigLoader.getProperty(IMessagesConstants.MENU_FIND));
        menuFind.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Command cmd = commandService.getCommand("com.huawei.mppdbide.command.id.findandreplace");
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                handlerService.executeHandler(parameterizedCmd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        menuFind.setImage(IconUtility.getIconImage(IiconPath.ICO_FIND_REPLACE, this.getClass()));
    }

    private void addExecutionPlanMenuItem(Menu menu) {
        menuExecutionPlan = new MenuItem(menu, SWT.PUSH);
        menuExecutionPlan.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_TITLE));
        menuExecutionPlan.setEnabled(false);
        menuExecutionPlan.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Command cmd = commandService.getCommand("com.huawei.mppdbide.command.id.executionplanandcost");
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                handlerService.executeHandler(parameterizedCmd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        menuExecutionPlan.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_PLAN, this.getClass()));
    }

    private void addCancelMenuItem(Menu menu) {
        menuCancelExecution = new MenuItem(menu, SWT.PUSH);
        menuCancelExecution.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));
        menuCancelExecution.addSelectionListener(new SelectionListenerHelper(commandService, handlerService));
        menuCancelExecution.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_CANCEL_TERMINAL, this.getClass()));
    }

    /**
     * The listener interface for receiving openSQLSelect events. The class that
     * is interested in processing a openSQLSelect event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addOpenSQLSelectListener<code>
     * method. When the openSQLSelect event occurs, that object's appropriate
     * method is invoked.
     *
     * OpenSQLSelectEvent
     */
    private static final class OpenSQLSelectListener implements SelectionListener {
        private OpenSQLHandler openSQLHandler;

        protected OpenSQLSelectListener(OpenSQLHandler openSQLHandler) {
            this.openSQLHandler = openSQLHandler;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            openSQLHandler.execute();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    /**
     * The listener interface for receiving saveSQLSelect events. The class that
     * is interested in processing a saveSQLSelect event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSaveSQLSelectListener<code>
     * method. When the saveSQLSelect event occurs, that object's appropriate
     * method is invoked.
     *
     * SaveSQLSelectEvent
     */
    private static final class SaveSQLSelectListener implements SelectionListener {
        private SaveSQLHandler saveSQLHandler;

        protected SaveSQLSelectListener(SaveSQLHandler saveSQLHandler) {
            this.saveSQLHandler = saveSQLHandler;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            saveSQLHandler.execute();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    /**
     * The listener interface for receiving saveSQLAsSelect events. The class
     * that is interested in processing a saveSQLAsSelect event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSaveSQLAsSelectListener<code>
     * method. When the saveSQLAsSelect event occurs, that object's appropriate
     * method is invoked.
     *
     * SaveSQLAsSelectEvent
     */
    private static final class SaveSQLAsSelectListener implements SelectionListener {
        private SaveSQLAsHandler saveSQLAsHandler;

        protected SaveSQLAsSelectListener(SaveSQLAsHandler saveSQLAsHandler) {
            this.saveSQLAsHandler = saveSQLAsHandler;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            saveSQLAsHandler.execute();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    private void contextMenuAboutToShow() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        UIElement uiEle = UIElement.getInstance();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;

            boolean toggelCut = terminal.getTerminalCore().getSelectionCount() > 0;
            boolean toggelCopy = terminal.getTerminalCore().getSelectionCount() > 0;
            boolean toggelSelectAll = terminal.getTerminalCore().getDocument().get().length() > 0;
            boolean toggelExecuteStmt = true;
            Clipboard clipboard = new Clipboard(Display.getDefault());
            TextTransfer textTransfer = TextTransfer.getInstance();
            String textData = (String) clipboard.getContents(textTransfer);
            boolean togglePaste = null != textData && !"".equals(textData);

            toggelExecuteStmt = getToggleExecuteStmt(uiEle, terminal);
            menuExecutionPlan.setEnabled(getExecutionPlanEnableState(terminal));

            Button cancelButton = terminal.getTerminalCore().getExecStatusBar().getCancelButton();
            menuCancelExecution.setEnabled(cancelButton != null && cancelButton.isEnabled());

            sourceEditor.toggleCutCopySelectAll(toggelCut, toggelCopy, toggelSelectAll, toggelExecuteStmt, togglePaste);
            menuFind.setEnabled(sourceEditor.getSourceViewer().getFindReplaceTarget().canPerformFind());
        }
    }

    private boolean getExecutionPlanEnableState(SQLTerminal terminal) {
        return this.getSelectedDatabase() != null && this.getSelectedDatabase().isConnected()
                && this.getSelectedDatabase().isSupportedExplainPlan()
                && terminal.getTerminalCore().getSelectionCount() > 0;
    }

    private boolean getToggleExecuteStmt(UIElement uiEle, SQLTerminal terminal) {
        return uiEle.isSqlTerminalOnTop() && (!"".equals(terminal.getDocumentContent().trim()))
                && (executeButton != null && executeButton.isEnabled()) && (newTabexecuteButton != null
                        && UserPreference.getInstance().isGenerateNewResultWindow() && newTabexecuteButton.isEnabled());
    }

    private AnnotationRulerColumn getAnnotationRulerColumn() {
        int annotationRulerWidth = 16;

        if (annotationRulerColumn == null) {
            annotationRulerColumn = new AnnotationRulerColumn(fAnnotationModel, annotationRulerWidth,
                    fAnnotationAccess);

            annotationRulerColumn.addAnnotationType(ErrorAnnotation.getStrategyid());
        }
        return annotationRulerColumn;
    }

    /**
     * Create a compositeruler with annotation ruler and linenumber column.
     *
     * @return the composite ruler
     */
    private CompositeRuler getCompositeRuler() {
        int annotationRulerColumnIndex = 0;
        int lineNumberColumnIndex = 1;

        fCompositeRuler = new CompositeRuler(PLSourceEditorCore.SPACE_BETWEEN_RULER);
        AnnotationRulerColumn annotationRulerCol = getAnnotationRulerColumn();
        fCompositeRuler.addDecorator(annotationRulerColumnIndex, annotationRulerCol);

        LineNumberRulerColumn lineNumRulerColumn = new LineNumberRulerColumn();
        lineNumRulerColumn.setForeground(new Color(Display.getDefault(), 104, 99, 94));

        fCompositeRuler.addDecorator(lineNumberColumnIndex, lineNumRulerColumn);

        fCompositeRuler.setModel(fAnnotationModel);

        return fCompositeRuler;
    }

    private void createConnectionSelection(Composite currComposite, SQLTerminal sqlt) {
        this.toolbarComposite = new Composite(currComposite, SWT.NONE);
        GridData gdToolbarComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        toolbarComposite.setLayoutData(gdToolbarComposite);
        GridLayout layout = new GridLayout(BUTTIONS_NUMBER, false);
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        this.toolbarComposite.setLayout(layout);
        this.toolbarComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));

        addSqlTerminalExecuteBtn();
        addSqlTerminalNewTabExecuteBtn();
        addSqlTerminalHistoryBtn(sqlt);

        setAutoCommitDefaultValue();
        if (isShowAutoCommit()) {
            createAutoCommitButton();
        }
        createCommitButton();
        createRollbackButton();

        Composite executionPlanComposite = addExeutePlanComposite1();

        /*
         * drop down should not have tooltip since tooltip is overlapping
         * dropdown text. Execution plan button should have tooltip. since
         * tooltip is set to parent, so that it comes when button is disabled,
         * both buttons should have different parents
         */

        Composite executionPlanButtonComposite = addExecutePlanComposite(executionPlanComposite);

        Composite analyzeDropDownComposite = addExecutePlanComposite(executionPlanComposite);

        addExecutePlanAndAnalyzeBtn(executionPlanButtonComposite, analyzeDropDownComposite);

        addVisualPlanBtn();

        createReuseConnectionButton();
        addEditSqlTerminalValueBtn();
    }

    private void addEditSqlTerminalValueBtn() {
        Composite editTerminalValueComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdeditTerminalValueComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        editTerminalValueComposite.setLayoutData(gdeditTerminalValueComposite);
        GridLayout executelayout = new GridLayout(1, false);
        executelayout.marginHeight = 0;
        editTerminalValueComposite.setLayout(executelayout);
        editTerminalValueComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        editTerminalValueButton = new Button(editTerminalValueComposite, SWT.NONE);
        editTerminalValueButton.setData(MPPDBIDEConstants.SWTBOT_KEY, MPPDBIDEConstants.ID_EDIT_TERMINAL_VALUE_BUTTON);
        GridData gdExecuteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdExecuteButton.widthHint = 27;
        editTerminalValueButton.setLayoutData(gdExecuteButton);
        editTerminalValueButton.setImage(IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, getClass()));
        editTerminalValueButton
                .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TERMINAL_INPUT_VALUE));
        editTerminalValueButton.getParent().setToolTipText(editTerminalValueButton.getToolTipText());
        editTerminalValueButton.addSelectionListener(new EditTerminalValueListener());
    }

    private Composite addExeutePlanComposite1() {
        Composite executionPlanComposite = new Composite(toolbarComposite, SWT.NONE);
        GridData gdExecPlanComposite = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        executionPlanComposite.setLayoutData(gdExecPlanComposite);
        GridLayout execPlanLayout = new GridLayout(2, false);
        execPlanLayout.verticalSpacing = 0;
        execPlanLayout.horizontalSpacing = 0;
        executionPlanComposite.setLayout(execPlanLayout);
        executionPlanComposite.setData(new GridData(SWT.FILL, SWT.TOP, false, true, 1, 1));
        return executionPlanComposite;
    }

    private void addVisualPlanBtn() {
        Composite visualPlanComposite = new Composite(toolbarComposite, SWT.None);

        visualPlanComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridLayout visLayout = new GridLayout(1, false);
        visualPlanComposite.setLayout(visLayout);
        visualPlanComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        visualPlanButton = new Button(visualPlanComposite, SWT.NONE);
        visualPlanButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_EXECUTIONPLAN_BUTTON_001");
        GridData gdVisualPlanButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdVisualPlanButton.widthHint = 27;
        visualPlanButton.setLayoutData(gdVisualPlanButton);
        visualPlanButton.setImage(IconUtility.getIconImage(IiconPath.VIS_EXPLAIN_DETAILED_PLAN_TAB, this.getClass()));
        visualPlanButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.VIS_EXPLAIN_WINDOW_LBL));
        visualPlanButton.getParent().setToolTipText(visualPlanButton.getToolTipText());
        visualPlanButton.addSelectionListener(new VisualPlanBtnSelectionListener());
    }

    private void addExecutePlanAndAnalyzeBtn(Composite executionPlanButtonComposite,
            Composite analyzeDropDownComposite) {
        executionPlanButton = new Button(executionPlanButtonComposite, SWT.NONE);
        executionPlanButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_EXECUTIONPLAN_BUTTON_001");
        GridData gdExecutionPlanButton = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gdExecutionPlanButton.widthHint = 27;
        executionPlanButton.setLayoutData(gdExecutionPlanButton);
        executionPlanButton.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_PLAN, this.getClass()));
        executionPlanButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_COST_TOOLTIP));
        executionPlanButton.getParent().setToolTipText(executionPlanButton.getToolTipText());

        analyzeOption = new MultiCheckSelectionCombo(analyzeDropDownComposite, SWT.ARROW | SWT.DOWN);
        analyzeOption.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_EXECUTIONPLAN_BUTTON_001");
        GridData analyzeOptionGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        analyzeOption.setLayoutData(analyzeOptionGD);
        analyzeOption.addButtonListener();
        String[] inputs = {MessageConfigLoader.getProperty(IMessagesConstants.INCLUDE_ANALYZE_DROPDOWN)};
        for (String str : inputs) {
            analyzeOption.add(str);
        }

        analyzeOption.addSelectionListener(new AnalyzeBtnSelectionListener());
        executionPlanButton.addSelectionListener(new ExecutionPlanSelectionListener());
    }

    private Composite addExecutePlanComposite(Composite executionPlanComposite) {
        Composite executionPlanButtonComposite = new Composite(executionPlanComposite, SWT.NONE);
        executionPlanButtonComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        GridLayout buttonLayout = new GridLayout(1, false);
        buttonLayout.verticalSpacing = 0;
        buttonLayout.horizontalSpacing = 0;
        buttonLayout.marginHeight = 0;
        buttonLayout.marginWidth = 0;
        executionPlanButtonComposite.setLayout(buttonLayout);
        executionPlanButtonComposite.setData(new GridData(SWT.FILL, SWT.TOP, false, true, 1, 1));
        return executionPlanButtonComposite;
    }

    private void addSqlTerminalHistoryBtn(SQLTerminal sqlt) {
        Composite sqlhistoryComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdSqlhistoryComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        sqlhistoryComposite.setLayoutData(gdSqlhistoryComposite);

        sqlhistoryComposite.setLayout(new GridLayout(1, false));
        sqlhistoryComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        sqlhistoryButton = new Button(sqlhistoryComposite, SWT.NONE);
        sqlhistoryButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_003");
        GridData gdSqlhistoryButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdSqlhistoryButton.widthHint = 27;
        sqlhistoryButton.setLayoutData(gdSqlhistoryButton);

        sqlhistoryButton.setImage(IconUtility.getIconImage(IiconPath.SQL_HISTORY1, getClass()));

        sqlhistoryButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_TOOL_TRIP));
        sqlhistoryButton.setEnabled(true);
        sqlhistoryButton.getParent().setToolTipText(sqlhistoryButton.getToolTipText());
        sqlhistoryButton.addSelectionListener(new SqlHistoryBtnSelectioListener(sqlt));
    }

    private void addSqlTerminalExecuteBtn() {
        Composite executeComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdExecuteComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        executeComposite.setLayoutData(gdExecuteComposite);
        GridLayout executelayout = new GridLayout(1, false);
        executelayout.marginHeight = 0;
        executeComposite.setLayout(executelayout);
        executeComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        executeButton = new Button(executeComposite, SWT.NONE);
        executeButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_001");
        GridData gdExecuteButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdExecuteButton.widthHint = 27;
        executeButton.setLayoutData(gdExecuteButton);
        executeButton.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_SQL_TERMINAL, getClass()));

        executeButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_EXEC));
        executeButton.getParent().setToolTipText(executeButton.getToolTipText());

        executeButton.addSelectionListener(new ExecuteBtnSelectionListener());
    }

    private void addSqlTerminalNewTabExecuteBtn() {
        Composite executeInNewTabComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdexecuteInNewTabComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        executeInNewTabComposite.setLayoutData(gdexecuteInNewTabComposite);
        GridLayout executeInNewTablayout = new GridLayout(1, false);
        executeInNewTablayout.marginHeight = 0;
        executeInNewTabComposite.setLayout(executeInNewTablayout);
        newTabexecuteButton = new Button(executeInNewTabComposite, SWT.NONE);
        GridData gdExecuteInNewTabButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        newTabexecuteButton.setLayoutData(gdExecuteInNewTabButton);
        newTabexecuteButton.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_SQL_TERMINAL_NEW_TAB, getClass()));

        newTabexecuteButton
                .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_OVERWRITE_RESULTSET_TOOLTIP));
        newTabexecuteButton.getParent().setToolTipText(newTabexecuteButton.getToolTipText());

        newTabexecuteButton.addSelectionListener(new NewTabExecuteBtnSelectionListener());
    }

    /**
     * Checks if is sql terminal new tab execute btn enabled.
     *
     * @return true, if is sql terminal new tab execute btn enabled
     */
    public boolean isSqlTerminalNewTabExecuteBtnEnabled() {
        if (newTabexecuteButton.isDisposed()) {
            return false;
        }
        return newTabexecuteButton.getEnabled();
    }

    /**
     * The listener interface for receiving visualPlanBtnSelection events. The
     * class that is interested in processing a visualPlanBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addVisualPlanBtnSelectionListener<code> method. When the
     * visualPlanBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * VisualPlanBtnSelectionEvent
     */
    private class VisualPlanBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            Command command = commandService.getCommand("com.huawei.mppdbide.view.command.visualexplainplan");
            Map<String, String> parameters = new HashMap<String, String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
            handlerService.executeHandler(parameterizedCommand);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving executionPlanSelection events. The
     * class that is interested in processing a executionPlanSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addExecutionPlanSelectionListener<code> method. When the
     * executionPlanSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ExecutionPlanSelectionEvent
     */
    private class ExecutionPlanSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (explainPlanProgressState()) {
                e.doit = false;
            }
            String analyzeFlag = analyzeOption.isFirstOptionMarked() ? "yes" : "no";
            Command command = commandService.getCommand("com.huawei.mppdbide.command.id.executionplanandcost");
            Map<String, String> parameters = new HashMap<String, String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
            parameters.put("com.huawei.mppdbide.view.commandparameter.explainplan.terminal.id", uiID);
            parameters.put("com.huawei.mppdbide.view.commandparameter.explainplan.analyze", analyzeFlag);
            ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
            handlerService.executeHandler(parameterizedCommand);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving analyzeBtnSelection events. The
     * class that is interested in processing a analyzeBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addAnalyzeBtnSelectionListener<code> method. When the
     * analyzeBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * AnalyzeBtnSelectionEvent
     */
    private class AnalyzeBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            includeAnalyze = analyzeOption.isFirstOptionMarked();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving sqlHistoryBtnSelectio events. The
     * class that is interested in processing a sqlHistoryBtnSelectio event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSqlHistoryBtnSelectioListener<code> method. When the
     * sqlHistoryBtnSelectio event occurs, that object's appropriate method is
     * invoked.
     *
     * SqlHistoryBtnSelectioEvent
     */
    private class SqlHistoryBtnSelectioListener implements SelectionListener {
        private SQLTerminal sqlt;

        public SqlHistoryBtnSelectioListener(SQLTerminal sqlt) {
            this.sqlt = sqlt;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Shell shell = Display.getDefault().getActiveShell();
            Server server = getSelectedDatabase().getServer();
            SqlHistory history = new SqlHistory(shell, server.getServerConnectionInfo().getConectionName(),
                    server.getServerConnectionInfo().getProfileId(), uiID);
            SQLSyntax syntax = null;
            if (sqlt.getSelectedDatabase() != null && sqlt.getSelectedDatabase().isConnected()) {
                syntax = sqlt.getSelectedDatabase().getSqlSyntax();
            }
            history.setSyntax(syntax);
            history.open();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving executeBtnSelection events. The
     * class that is interested in processing a executeBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addExecuteBtnSelectionListener<code> method. When the
     * executeBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ExecuteBtnSelectionEvent
     */
    private class ExecuteBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            handleExecution();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    private class NewTabExecuteBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            handleExecutionNewTab();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving execObjSelect events. The class that
     * is interested in processing a execObjSelect event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addExecObjSelectListener<code>
     * method. When the execObjSelect event occurs, that object's appropriate
     * method is invoked. ExecObjSelectEvent
     */
    private class EditTerminalValueListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            handleTerminalValue();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    private void handleTerminalValue() {
        ExecuteEditorItem executeEditorItem = new ExecuteEditorItem();
        executeEditorItem.executeSQLObjWindow(debugObject, true);
    }

    /**
     * 
     * 
     * @Author: wWX633190
     * 
     * @Date: April 15,4
     * 
     * @Title: createRollbackButton
     * 
     * @Description: Create a manual rollback button
     *
     * 
     * 
     */
    private void createRollbackButton() {
        Composite rollbackComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdAutoCommitComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        rollbackComposite.setLayoutData(gdAutoCommitComposite);
        rollbackComposite.setLayout(new GridLayout(1, false));
        rollbackComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        rollbackButton = new Button(rollbackComposite, SWT.PUSH);
        rollbackButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_004");
        GridData gdAutoCommitButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdAutoCommitButton.widthHint = 27;
        rollbackButton.setLayoutData(gdAutoCommitButton);
        rollbackButton.setImage(IconUtility.getIconImage(IiconPath.ICO_TRANSACTION_ROLLBACK, getClass()));
        rollbackButton
                .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_ROLLBACK_TOOL_TIP));
        rollbackButton.setEnabled(false);
        rollbackButton.getParent().setToolTipText(rollbackButton.getToolTipText());
        rollbackButton.addSelectionListener(addRollBackBtnSelectionListener());

    }

    private SelectionListener addRollBackBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DBConnection dbConnection = getTermConnection().getConnection();

                try {
                    if (null != dbConnection && dbConnection.isTransactionOpen(getServerVersion())) {
                        if (DSViewDataManager.getInstance().isShowRollbackConfirmation()) {

                            manualRollback(dbConnection);
                        } else {
                            int rollbackType = MPPDBIDEDialogsWithDoNotShowAgain.generateYesNoMessageDialog(
                                    WHICHOPTION.ROLLBACK_CONFIRMATION, MESSAGEDIALOGTYPE.QUESTION, null, true,
                                    MessageConfigLoader
                                            .getProperty(IMessagesConstants.TRANSACTION_OPERATION_TOGGLE_TITLE),
                                    MessageConfigLoader
                                            .getProperty(IMessagesConstants.TRANSACTION_ROLLBACK_CONFIRMATION));

                            if (rollbackType != UIConstants.OK_ID) {
                                return;
                            }
                            manualRollback(dbConnection);
                        }
                    } else {
                        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                                MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_TOGGLE_TITLE),
                                MessageConfigLoader
                                        .getProperty(IMessagesConstants.NO_TRANSACTION_COMMIT_ROLLBACK_TOGGLE));
                    }
                } catch (SQLException e1) {
                    generateExceptionLogAndDialog("Transaction rollback failed",
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_ROLLBACK_EXCEPTION_BODY),
                            e1);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    /**
     * 
     * 
     * @Author: wWX633190
     * 
     * @Date: April 15,4
     * 
     * @Title: createCommitButton
     * 
     * @Description: Create a manual submit button
     *
     * 
     * 
     */
    private void createCommitButton() {
        Composite commitComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdAutoCommitComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        commitComposite.setLayoutData(gdAutoCommitComposite);
        commitComposite.setLayout(new GridLayout(1, false));
        commitComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        commitButton = new Button(commitComposite, SWT.PUSH);
        commitButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_004");
        GridData gdAutoCommitButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdAutoCommitButton.widthHint = 27;
        commitButton.setLayoutData(gdAutoCommitButton);
        commitButton.setImage(IconUtility.getIconImage(IiconPath.ICO_TRANSACTION_COMMIT, getClass()));
        commitButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_COMMIT_TOOL_TIP));
        commitButton.setEnabled(false);
        commitButton.getParent().setToolTipText(commitButton.getToolTipText());
        commitButton.addSelectionListener(addCommitBtnSelectionListener());
    }

    private SelectionListener addCommitBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DBConnection dbConnection = getTermConnection().getConnection();
                try {
                    if (null != dbConnection && dbConnection.isTransactionOpen(getServerVersion())) {
                        if (DSViewDataManager.getInstance().isShowCommitConfirmation()) {
                            manualCommit(dbConnection);
                        } else {
                            int messageDialogType = MPPDBIDEDialogsWithDoNotShowAgain.generateYesNoMessageDialog(
                                    WHICHOPTION.COMMIT_CONFIRMATION, MESSAGEDIALOGTYPE.QUESTION, null, true,
                                    MessageConfigLoader
                                            .getProperty(IMessagesConstants.TRANSACTION_OPERATION_TOGGLE_TITLE),
                                    MessageConfigLoader
                                            .getProperty(IMessagesConstants.TRANSACTION_COMMIT_CONFIRMATION));
                            if (messageDialogType != UIConstants.OK_ID) {
                                return;
                            }
                            manualCommit(dbConnection);
                        }
                    } else {
                        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                                MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_TOGGLE_TITLE),
                                MessageConfigLoader
                                        .getProperty(IMessagesConstants.NO_TRANSACTION_COMMIT_ROLLBACK_TOGGLE));
                    }
                } catch (SQLException e1) {
                    generateExceptionLogAndDialog("Transaction commit failed",
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_COMMIT_EXCEPTION_BODY), e1);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private void resetReuseConnButtonState() {
        boolean buttonState = getTermConnection().getReuseConnectionFlag();
        if (buttonState && null != getTermConnection().getDatabase()) {
            reuseConnectionButton
                    .setImage(IconUtility.getIconImage(IiconPath.ICON_REUSE_TERMINAL_CONNECTION, this.getClass()));
            reuseConnectionButton.setToolTipText(
                    MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_REUSE_CONNECTION_TOOLTIP));
            reuseConnectionButton.getParent().setToolTipText(reuseConnectionButton.getToolTipText());
        } else {
            reuseConnectionButton
                    .setImage(IconUtility.getIconImage(IiconPath.ICON_NEW_TERMINAL_CONNECTION, this.getClass()));
            reuseConnectionButton.setToolTipText(
                    MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_NEW_CONNECTION_TOOLTIP));
            getTermConnection().notifyAllWaitingJobs();
        }
    }

    private void createReuseConnectionButton() {
        Composite reuseTermConnComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdreuseTermComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        reuseTermConnComposite.setLayoutData(gdreuseTermComposite);
        reuseTermConnComposite.setLayout(new GridLayout(1, false));
        reuseTermConnComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        reuseConnectionButton = new Button(reuseTermConnComposite, SWT.NONE);
        reuseConnectionButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_REUSETERMCONN_BUTTON_001");
        GridData gdReuseConnectionButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdReuseConnectionButton.widthHint = 27;
        reuseConnectionButton.setLayoutData(gdReuseConnectionButton);
        resetReuseConnButtonState();
        reuseConnectionButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getTermConnection().setReuseConnectionFlag(!getTermConnection().getReuseConnectionFlag());
                resetReuseConnButtonState();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void setAutoCommitDefaultValue() {
        autoCommit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(DSTransactionPreferencePage.CONN_AUTOCOMMIT_PREF);
        helper.setAutoCommitFlag(autoCommit);
    }

    /**
     * Change auto comit status.
     *
     * @param status the status
     */
    public void changeAutoComitStatus(boolean status) {
        this.autoCommit = status;
        helper.setAutoCommitFlag(this.autoCommit);
        if (reuseConnectionButton != null) {
            reuseConnectionButton.setEnabled(status);
        }
        setAutoCommitStatus();
    }

    private void createAutoCommitButton() {
        Composite autoCommitComposite = new Composite(toolbarComposite, SWT.None);
        GridData gdAutoCommitComposite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        autoCommitComposite.setLayoutData(gdAutoCommitComposite);

        autoCommitComposite.setLayout(new GridLayout(1, false));
        autoCommitComposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));
        autoCommitButton = new Button(autoCommitComposite, SWT.TOGGLE);
        autoCommitButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_SQLEXECUTE_BUTTON_004");
        GridData gdAutoCommitButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdAutoCommitButton.widthHint = 27;
        autoCommitButton.setLayoutData(gdAutoCommitButton);

        autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_ON, getClass()));

        autoCommitButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_ON_TOOL_TIP));
        enableAutocommit();
        autoCommitButton.setSelection(true);
        autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
        autoCommitButton.addSelectionListener(addAutoCommitBtnSelectionListener());
    }

    private SelectionListener addAutoCommitBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    DBConnection dbConnection = getTermConnection().getConnection();
                    if (null != dbConnection) {
                        // Trying to change autocommit from OFF to ON
                        // when a transaction is open.
                        if (!autoCommit && dbConnection.isTransactionOpen(getServerVersion())) {

                            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_SQL_AUTOCOMMIT_TOGGLE_TITLE),
                                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_SQL_AUTOCOMMIT_TOGGLE));
                            return;
                        }
                    }

                    autoCommit = !autoCommit;
                    helper.setAutoCommitFlag(autoCommit);
                    if (autoCommit) {
                        if (null != dbConnection) {
                            dbConnection.getConnection().setAutoCommit(true);
                        }
                        reuseConnectionButton.setEnabled(true);
                        // Setting Values when Button is ON
                        setAutoCommitButtonOn();
                    } else {
                        if (null != dbConnection) {
                            dbConnection.getConnection().setAutoCommit(false);
                        }
                        reuseConnectionButton.setEnabled(false);
                        // Setting Values when Button is OFF
                        setAutoCommitButtonOff();
                    }
                } catch (SQLException exception) {
                    MPPDBIDELoggerUtility.error("Set AutoCommit returned exception", exception);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    /**
     * Handle execution.
     */
    public void handleExecution() {
        Command command = commandService
                .getCommand("com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar");
        Map<String, String> parameters = new HashMap<String, String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        parameters.put("terminal.id", uiID);
        parameters.put("new.tab", "false");
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
        handlerService.executeHandler(parameterizedCommand);
    }

    /**
     * Handle execution new tab.
     */
    public void handleExecutionNewTab() {
        Command command = commandService
                .getCommand("com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbarnewtab");
        Map<String, String> parameters = new HashMap<String, String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        parameters.put("terminal.id", uiID);
        parameters.put("new.tab", "true");
        ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, parameters);
        handlerService.executeHandler(parameterizedCommand);
    }

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     * @throws DatabaseCriticalException the database critical exception
     */
    public void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary) throws DatabaseCriticalException {
        helper.createResultNew(resultsetDisplaydata, consoledata, queryExecSummary);
    }

    /**
     * Creates the exec plan new.
     *
     * @param analysedPlanOutput the analysed plan output
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    public void createExecPlanNew(UIModelAnalysedPlanNode analysedPlanOutput, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary, double totalRuntime) {
        helper.createExecPlanNew(analysedPlanOutput, consoledata, queryExecSummary, totalRuntime);
    }

    public ResultTabManager getResultManager() {
        return helper.getResultManager();
    }

    /**
     * Gets the terminal result manager.
     *
     * @return the terminal result manager
     */
    public ResultTabManager getTerminalResultManager() {
        return this.resultManager;
    }

    /**
     * Gets the console message window.
     *
     * @param isSelected the is selected
     * @return the console message window
     */
    public ConsoleMessageWindow getConsoleMessageWindow(boolean isSelected) {
        return this.getResultManager().getConsoleMessageWindow(isSelected);
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {
        if (termConnection.getDatabase() != null) {
            DBAssistantWindow.toggleAssitant(sourceEditor.getSourceViewer(), this.getTermConnection().getDatabase());
        }
        sourceEditor.getSourceViewer().getControl().setFocus();
    }

    /**
     * Checks if is visible.
     *
     * @return true, if is visible
     */
    public boolean isVisible() {
        return sourceEditor != null && sourceEditor.getSourceViewer().getControl().isVisible();
    }

    /**
     * Gets the document content.
     *
     * @return the document content
     */
    public String getDocumentContent() {
        return sourceEditor.getDocument().get();
    }

    /**
     * Sets the document content.
     *
     * @param query the new document content
     */
    public void setDocumentContent(String query) {
        // First disconnect partitioner and set and connect again
        IDocumentPartitioner documentPartitioner = ((IDocumentExtension3) sourceEditor.getDocument())
                .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING);
        if (null != documentPartitioner) {
            if (documentPartitioner instanceof SQLDocumentPartitioner) {
                SQLDocumentPartitioner sqlPartitioner = (SQLDocumentPartitioner) documentPartitioner;
                sqlPartitioner.clearScanner();
            }
            documentPartitioner.disconnect();
        }
        sourceEditor.getDocument().set(query);
        SQLDocumentPartitioner.connectDocument(sourceEditor.getDocument(), 0);
    }

    /**
     * Gets the selected document content.
     *
     * @return the selected document content
     */
    public String getSelectedDocumentContent() {
        final ITextSelection textSel = (ITextSelection) sourceEditor.getSourceViewer().getSelectionProvider()
                .getSelection();
        return textSel.getText();
    }

    /**
     * Gets the selected database.
     *
     * @return the selected database
     */
    public Database getSelectedDatabase() {
        return this.getTermConnection().getDatabase();
    }

    /**
     * Sets the editable.
     *
     * @param isEditable the new editable
     */
    public void setEditable(boolean isEditable) {
        synchronized (instanceLock) {
            if (isActivated()) {
                sourceEditor.setEditable(isEditable);
            } else {
                isEditableFlag = isEditable;
            }
        }
    }

    /**
     * Gets the checks if is editable.
     *
     * @return the checks if is editable
     */
    public boolean getIsEditable() {
        synchronized (instanceLock) {
            if (isActivated()) {
                return sourceEditor.isEditable();
            } else {
                return isEditableFlag;
            }
        }
    }

    /**
     * Gets the terminal core.
     *
     * @return the terminal core
     */
    public PLSourceEditorCore getTerminalCore() {
        return sourceEditor;
    }

    /**
     * Gets the terminal docuement.
     *
     * @return the terminal docuement
     */
    public IDocument getTerminalDocuement() {
        return sourceEditor.getDocument();
    }

    /**
     * Sets the execute in progress.
     *
     * @param isExecuteInProgres the new execute in progress
     */
    public void setExecuteInProgress(boolean isExecuteInProgres) {
        this.isExecuteInProgress = isExecuteInProgres;
    }

    /**
     * Checks if is execute in progress.
     *
     * @return true, if is execute in progress
     */
    public boolean isExecuteInProgress() {
        return isExecuteInProgress;
    }

    /**
     * Checks if is cancel query pressed.
     *
     * @return true, if is cancel query pressed
     */
    public boolean isCancelQueryPressed() {
        return isCancelQueryPressed;
    }

    /**
     * Sets the cancel query pressed.
     *
     * @param isCancelQryPressed the new cancel query pressed
     */
    public void setCancelQueryPressed(boolean isCancelQryPressed) {
        this.isCancelQueryPressed = isCancelQryPressed;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        if (null != sourceEditor && DBAssistantWindow.getViewer() == sourceEditor.getSourceViewer()) {
            helper.cleanDBAssistantWindow();
        }
        this.getTermConnection().resetInformation();
        this.getTermConnection().cancelAllWaitingJobs();

        UIElement.getInstance().removePartFromStack(uiID);

        if (this.resultDisplayUIManager != null) {
            this.resultDisplayUIManager.setDisposed();
        }

        if (null != this.resultManager) {
            this.resultManager.destroy();
            this.resultManager = null;
        }
        SQLEditorPlugin.getDefault().setSQLCodeScanner(null);
        resultConfig = null;
        if (null != sourceEditor) {
            sourceEditor.preDestroy();
            sourceEditor = null;
        }
        MemoryCleaner.cleanUpMemory();

        List<IAutoSaveObject> openTerminalList = UIElement.getInstance().getAllOpenTerminals();
        if (openTerminalList.size() == 0) {
            UIElement.getInstance().clearSQLTerminalCounter();
            DBAssistantWindow.setEnableA(false);
            DBAssistantWindow.setAllTerminalsClosed(true);
        } else if (!UIElement.getInstance().isTerminalGroupOpen(this.getDefLabelId())) {
            UIElement.getInstance().removeSQLTerminalIdFromMap(this.getDefLabelId());
        }

        clearData();
    }

    private void clearData() {
        this.fAnnotationModel = null;
        this.fAnnotationAccess = null;
        this.resultDisplayUIManager = null;
        this.annotationRulerColumn = null;
        this.fCompositeRuler = null;
        this.ap = null;
        this.execContext = null;

        this.sqlTerminalPart = null;
        this.lastSaveTime = null;
        this.analyzeOption = null;
    }

    /**
     * Handle tool items.
     */
    public void handleToolItems() {
        if (this.getSelectedDatabase() != null && !isExecuteInProgress && null != executeButton) {
            executeButton.setEnabled(this.getSelectedDatabase().isConnected());
            toggleNewTabExecuteButton(this.getSelectedDatabase().isConnected());
        }
    }

    /**
     * Gets the selected qry.
     *
     * @return the selected qry
     */
    public String getSelectedQry() {
        /*
         * If any query is selected in the SQL terminal window, then get only
         * the selected query part Otherwise get the entire content
         */
        if (null != this.getSelectedDocumentContent() && 0 < this.getSelectedDocumentContent().length()) {
            return this.getSelectedDocumentContent();
        } else {
            return this.getDocumentContent();
        }

    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public IResultDisplayUIManager getResultDisplayUIManager() {
        if (this.resultDisplayUIManager == null) {
            this.resultDisplayUIManager = new SQLTerminalResultDisplayUIManager(this);
        }

        return this.resultDisplayUIManager;
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    public IResultConfig getResultConfig() {
        if (this.resultConfig == null) {
            this.resultConfig = new SQLTerminalResultConfig();
        }

        return this.resultConfig;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SQLTerminalResultConfig.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class SQLTerminalResultConfig implements IResultConfig {

        @Override
        public int getFetchCount() {
            return UserPreference.getInstance().getResultDataFetchCount();
        }

        @Override
        public ActionAfterResultFetch getActionAfterFetch() {
            boolean autoComit = false;
            try {
                if (null != getTermConnection().getConnection()) {
                    autoComit = getTermConnection().getConnection().getConnection().getAutoCommit();
                }

                if (autoComit) {
                    return ActionAfterResultFetch.ISSUE_COMMIT_CONNECTION_AFTER_FETCH;
                } else {
                    return ActionAfterResultFetch.ISSUE_NO_OP;
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error("Get AutoCommit detail returned exception", exception);
                return ActionAfterResultFetch.ISSUE_NO_OP;
            }
        }
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    public IEventBroker getEventBroker() {
        return eventBroker;
    }

    /**
     * Force focus local console.
     */
    public void forceFocusLocalConsole() {
        this.getResultManager().forceFocusLocalConsole();

    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionSQLConnectionInfra getTermConnection() {
        return this.termConnection;
    }

    @Override
    public Database getDatabase() {
        return this.termConnection.getDatabase();
    }

    /**
     * Sets the terminal execution SQL connection reconnect on terminal.
     *
     * @param flag the new terminal execution SQL connection reconnect on
     * terminal
     */
    public void setTerminalExecutionSQLConnectionReconnectOnTerminal(boolean flag) {
        this.termConnection.setReconnectOnTerminal(flag);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SelectionListenerHelper.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class SelectionListenerHelper implements SelectionListener {
        private ECommandService cmdService;
        private EHandlerService ehandlerService;

        private SelectionListenerHelper(ECommandService commandService, EHandlerService handlerService) {
            this.cmdService = commandService;
            this.ehandlerService = handlerService;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Command cmd = cmdService.getCommand("com.huawei.mppdbide.command.id.cancelSql");
            ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
            ehandlerService.executeHandler(parameterizedCmd);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * Sets the execution context.
     *
     * @param context the new execution context
     */
    public void setExecutionContext(SQLTerminalExecutionContext context) {
        this.execContext = context;
    }

    /**
     * Gets the execution context.
     *
     * @return the execution context
     */
    public SQLTerminalExecutionContext getExecutionContext() {
        return this.execContext;
    }

    /**
     * Gets the current query in execution.
     *
     * @return the current query in execution
     */
    public String getCurrentQueryInExecution() {

        return execContext.getCurrentQueryInExecution();
    }

    /**
     * Gets the document text.
     *
     * @return the document text
     */
    public String getDocumentText() {
        String text = this.getTerminalCore().getText();
        return text;
    }

    /**
     * Gets the current execution selected text.
     *
     * @return the current execution selected text
     */
    public String getCurrentExecutionSelectedText() {

        return execContext.getCurrentExecutionSelectedText();
    }

    /**
     * Gets the selection start offset.
     *
     * @return the selection start offset
     */
    public int getselectionStartOffset() {

        return execContext.getselectionStartOffset();
    }

    /**
     * Sets the selection start offset.
     *
     * @param qryEndOffset the new selection start offset
     */
    public void setselectionStartOffset(int qryEndOffset) {

        execContext.setselectionStartOffset(qryEndOffset);
    }

    /**
     * Gets the shell.
     *
     * @return the shell
     */
    public Shell getShell() {

        return execContext.getShell();
    }

    /**
     * Gets the source viewer line of offset.
     *
     * @param offset the offset
     * @return the source viewer line of offset
     * @throws BadLocationException the bad location exception
     */
    public int getsourceViewerLineOfOffset(int offset) throws BadLocationException {
        return this.getTerminalCore().getDocument().getLineOfOffset(offset);
    }

    /**
     * Gets the source viewer line offset.
     *
     * @param offset the offset
     * @return the source viewer line offset
     * @throws BadLocationException the bad location exception
     */
    public int getsourceViewerLineOffset(int offset) throws BadLocationException {
        return this.getTerminalCore().getDocument().getLineOffset(offset);
    }

    /**
     * Sets the part label.
     *
     * @param id the new part label
     */
    public void setPartLabel(String id) {

        this.partLabel = id;
        helper.setPartLabel(id);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SqlTerminalHelper.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class SqlTerminalHelper {

        /**
         * Creates the result new.
         *
         * @param resultsetDisplaydata the resultset displaydata
         * @param consoledata the consoledata
         * @param queryExecSummary the query exec summary
         * @throws DatabaseCriticalException the database critical exception
         */
        public void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
                IQueryExecutionSummary queryExecSummary) throws DatabaseCriticalException {
            getResultManager().createResult(resultsetDisplaydata, consoledata, queryExecSummary);
        }

        /**
         * Creates the exec plan new.
         *
         * @param analysedPlanOutput the analysed plan output
         * @param consoledata the consoledata
         * @param queryExecSummary the query exec summary
         * @param totalRuntime the total runtime
         */
        public void createExecPlanNew(UIModelAnalysedPlanNode analysedPlanOutput, IConsoleResult consoledata,
                IQueryExecutionSummary queryExecSummary, double totalRuntime) {
            getResultManager().createExecPlanResult(analysedPlanOutput, consoledata, queryExecSummary, totalRuntime);
        }

        /**
         * Gets the result manager.
         *
         * @return the result manager
         */
        public ResultTabManager getResultManager() {
            if (null == resultManager) {
                String label = partLabel == null ? getUiID() : partLabel;

                resultManager = new ResultTabManager(parent, getUiID(), label, eventBroker, termConnection, true);
                resultManager.setDirtyHandler(dirtyHandler);
            }

            return resultManager;
        }

        /**
         * Sets the part label.
         *
         * @param id the new part label
         */
        public void setPartLabel(String id) {
            if (null != resultManager) {
                resultManager.setmPartLabel(id);
            }
        }

        /**
         * Sets the execute DB.
         *
         * @param db the new execute DB
         */
        public void setExecuteDB(Database db) {
            getTermConnection().setDatabase(db);
        }

        /**
         * Clean DB assistant window.
         */
        public void cleanDBAssistantWindow() {
            DBAssistantWindow.setViewer(null);
            DBAssistantWindow.setDatabase(null);
            DBAssistantWindow.setVisible(false);
        }

        /**
         * Sets the auto commit flag.
         *
         * @param autoCommt the new auto commit flag
         */
        public void setAutoCommitFlag(boolean autoCommt) {
            getTermConnection().setAutoCommitFlag(autoCommt);
        }

        /**
         * Sets the sql cmd menu key.
         *
         * @param key the new sql cmd menu key
         */
        public void setSqlCmdMenuKey(String key) {
            sourceEditor.setSqlCmdMenuKey(key);
        }

        /**
         * Sets the sql cmd menu icon.
         *
         * @param ic the new sql cmd menu icon
         */
        public void setSqlCmdMenuIcon(String ic) {
            sourceEditor.setSqlCmdMenuIcon(ic);
        }
    }

    /**
     * Gets the part label.
     *
     * @return the part label
     */
    public String getPartLabel() {
        return partLabel;
    }

    /**
     * Prompt user to save.
     *
     * @return the save
     */
    @Override
    public Save promptUserToSave() {
        tab.getParent().setSelectedElement(tab);
        if (isFileTerminalFlag()) {
            int selection = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_WARNING,
                            sqlTerminalPart.getLabel()),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_BUTTON),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_NOT_SAVE_BUTTON),
                    MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));

            switch (selection) {
                case 0: {
                    return Save.YES;
                }
                case 1: {
                    return Save.NO;
                }
                case 2: {
                    return Save.CANCEL;
                }
                default: {
                    return Save.CANCEL;
                }
            }
        } else {
            String titl = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_CHANGES_TITLE);
            String msg = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_TERMINAL_DATA_BODY);
            String cncel = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC);
            String discardChnges = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_DISCARD);
            int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()), titl, msg,
                    new String[] {discardChnges, cncel}, 1);

            if (0 == userChoice) {
                return Save.NO;
            }

            return Save.CANCEL;
        }
    }

    /**
     * Save part.
     */
    @Override
    public void savePart() {
        // currently, only support file terminal
        if (isFileTerminalFlag()) {
            handlerService.executeHandler(commandService.createCommand("com.huawei.mppdbide.command.id.savesql", null));
        }
    }

    /**
     * Register modify listener.
     */
    public void registerModifyListener() {
        synchronized (instanceLock) {
            if (!isActivated()) {
                isAddModifyListener = true;
                return;
            }

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    sourceEditor.getSourceViewer().getDocument().addDocumentListener(getAutoSaveModifyListener());
                }
            });
        }
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    @Override
    public String getText() {
        synchronized (instanceLock) {
            if (!isActivated()) {
                return localText;
            }

            return getDocumentContent();
        }
    }

    /**
     * Gets the connection name.
     *
     * @return the connection name
     */
    @Override
    public String getConnectionName() {
        if (termConnection.getDatabase() == null) {
            return super.getConnectionName();
        }
        return termConnection.getDatabase().getServerName();
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    @Override
    public String getDatabaseName() {
        try {
            return termConnection.getDatabaseName();
        } catch (MPPDBIDEException exception) {
            return super.getDatabaseName();
        }
    }

    /**
     * Gets the element ID.
     *
     * @return the element ID
     */
    @Override
    public String getElementID() {
        return getUiID();
    }

    /**
     * Update editor window.
     *
     * @param dbObject the db object
     * @return true, if successful
     */
    @Override
    public boolean updateEditorWindow(Object dbObject) {
        try {
            if (!(dbObject instanceof Database)) {
                MPPDBIDELoggerUtility.debug("Database not found");
                return false;
            }
            Database db = (Database) dbObject;
            this.setExecuteDB(db);

            if (isActivated()) {
                this.sourceEditor.setDatabase(db);
                this.sourceEditor.updateSQLConfigurationsOnDBConnect(db);
            }

            if (!db.isConnected()) {
                return false;
            }

            return true;
        } finally {
            resetButtons();
        }
    }

    /**
     * Sets the text.
     *
     * @param dataTxt the new text
     */
    @Override
    public void setText(final String dataTxt) {
        onSetText(dataTxt);
    }

    private void onSetText(final String data) {
        synchronized (instanceLock) {
            if (!isActivated()) {
                localText = data;
                return;
            }

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (sourceEditor != null && sourceEditor.getSourceViewer() != null) {
                        setDocumentContent(data);
                    }
                }
            });
        }
    }

    /**
     * Gets the tab label.
     *
     * @return the tab label
     */
    @Override
    public String getTabLabel() {
        return getPartLabel();
    }

    /**
     * Gets the tab tool tip.
     *
     * @return the tab tool tip
     */
    @Override
    public String getTabToolTip() {
        return toolTip;
    }

    /**
     * Reset buttons.
     */
    @Override
    public void resetButtons() {
        if (!this.isInited || !isActivated()) {
            return;
        }

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                handleToolItems();
                resetSQLTerminalButton();
                resetAutoCommitButton();
                resetReuseConnectionButton();
                resetCommitAndRollbackButton();
            }
        });
    }

    private void resetReuseConnectionButton() {
        if (!isActivated()) {
            return;
        }

        Database db = getSelectedDatabase();
        boolean connectionPresent = (db != null) && db.isConnected();
        if (!this.reuseConnectionButton.isEnabled() && autoCommit) {
            getTermConnection().setReuseConnectionFlag(true);
        }
        this.reuseConnectionButton.setEnabled(connectionPresent && this.autoCommit);
        resetReuseConnButtonState();
    }

    /**
     * Sets the element ID.
     *
     * @param id the new element ID
     */
    @Override
    public void setElementID(String id) {
        setUiID(id);
    }

    /**
     * Sets the tab label.
     *
     * @param label the new tab label
     */
    @Override
    public void setTabLabel(String label) {
        setPartLabel(label);
    }

    /**
     * Sets the tab tool tip.
     *
     * @param toolTip1 the new tab tool tip
     */
    @Override
    public void setTabToolTip(String toolTip1) {
        toolTip = toolTip1;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return MPPDBIDEConstants.SQL_TERMINAL;
    }

    private boolean isShowAutoCommit() {
        return PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(DSTransactionPreferencePage.IS_SHOW_AUTOCOMMIT);
    }

    /**
     * Gets the updated tool tip.
     *
     * @return the updated tool tip
     */
    public String getUpdatedToolTip() {
        String sourceEditorElmTooltip = this.getPartLabel() + " - " + this.getDatabaseName() + '@'
                + this.getConnectionName();
        return sourceEditorElmTooltip;
    }

    /**
     * Sets the explain plan in progress.
     */
    public void setExplainPlanInProgress() {
        this.isExplainPlanInProgress = true;
    }

    /**
     * Reset explain plan in progress.
     */
    public void resetExplainPlanInProgress() {
        this.isExplainPlanInProgress = false;
    }

    /**
     * Explain plan progress state.
     *
     * @return true, if successful
     */
    public boolean explainPlanProgressState() {
        return this.isExplainPlanInProgress;
    }

    /**
     * Do formatting of contents.
     */

    /**
     * Removes the completed job.
     *
     * @param job the job
     */

    /**
     * Reset connection.
     */
    public void resetConnection() {
        getTermConnection().setConnection(null);
    }

    private void setAutoCommitButtonOn() {

        autoCommitButton.setSelection(true);
        autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_ON, getClass()));
        autoCommitButton.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_ON_TOOL_TIP));
        autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
    }

    private void setAutoCommitButtonOff() {
        getTermConnection().setReuseConnectionFlag(true);
        resetReuseConnButtonState();
        autoCommitButton.setSelection(false);
        autoCommitButton.setImage(IconUtility.getIconImage(IiconPath.SQL_AUTOCOMMIT_OFF, getClass()));
        autoCommitButton
                .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_OFF_TOOL_TIP));
        autoCommitButton.getParent().setToolTipText(autoCommitButton.getToolTipText());
    }

    /**
     * Sets the new document for reload.
     *
     * @param document the new new document for reload
     */
    public void setNewDocumentForReload(Document document) {
        getTerminalCore().setDocument(document, 0);
        ((ErrorAnnotationMarkerAccess) fAnnotationAccess).setDocument(sourceEditor.getSourceViewer().getDocument());
    }

    /**
     * Show dirty file terminal options.
     *
     * @return true, if successful
     */
    public static boolean showDirtyFileTerminalOptions() {
        List<IAutoSaveObject> dirtyFileTerminal = new ArrayList<>();
        UIElement.getInstance().getAllOpenTabs().stream().forEach(tab -> {
            if (tab instanceof SQLTerminal && ((SQLTerminal) tab).isFileTerminalFlag()
                    && ((SQLTerminal) tab).getDirtyHandler().isDirty()) {
                dirtyFileTerminal.add((SQLTerminal) tab);
            }
        });

        if (!dirtyFileTerminal.isEmpty()) {
            DirtyTerminalDialog dirtyTerminalDialog = new DirtyTerminalDialog(Display.getDefault().getActiveShell(),
                    dirtyFileTerminal);
            dirtyTerminalDialog.open();
            return dirtyTerminalDialog.isSaveAllSuccessFlag();
        }

        return true;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path.
     *
     * @param filePath the new file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the sql terminal part.
     *
     * @return the sql terminal part
     */
    public MPart getSqlTerminalPart() {
        return sqlTerminalPart;
    }

    /**
     * Sets the sql terminal part.
     *
     * @param sqlTerminalPart the new sql terminal part
     */
    public void setSqlTerminalPart(MPart sqlTerminalPart) {
        this.sqlTerminalPart = sqlTerminalPart;
    }

    /**
     * Checks if is file terminal flag.
     *
     * @return true, if is file terminal flag
     */
    public boolean isFileTerminalFlag() {
        return fileTerminalFlag;
    }

    /**
     * Sets the file terminal flag.
     *
     * @param fileTerminalFlag the new file terminal flag
     */
    public void setFileTerminalFlag(boolean fileTerminalFlag) {
        this.fileTerminalFlag = fileTerminalFlag;
    }

    /**
     * Gets the menu save SQL.
     *
     * @return the menu save SQL
     */
    public MenuItem getMenuSaveSQL() {
        return menuSaveSQL;
    }

    /**
     * Sets the menu save SQL.
     *
     * @param menuSaveSQL the new menu save SQL
     */
    public void setMenuSaveSQL(MenuItem menuSaveSQL) {
        this.menuSaveSQL = menuSaveSQL;
    }

    /**
     * Checks if is exit ds flag.
     *
     * @return true, if is exit ds flag
     */
    public boolean isExitDsFlag() {
        return exitDsFlag;
    }

    /**
     * Sets the exit ds flag.
     *
     * @param exitDsFlag the new exit ds flag
     */
    public void setExitDsFlag(boolean exitDsFlag) {
        this.exitDsFlag = exitDsFlag;
    }

    /**
     * Checks if is open sql flag.
     *
     * @return true, if is open sql flag
     */
    public boolean isOpenSqlFlag() {
        return openSqlFlag;
    }

    /**
     * Sets the open sql flag.
     *
     * @param openSqlFlag the new open sql flag
     */
    public void setOpenSqlFlag(boolean openSqlFlag) {
        this.openSqlFlag = openSqlFlag;
    }

    /**
     * Gets the dirty handler.
     *
     * @return the dirty handler
     */
    public MDirtyable getDirtyHandler() {
        return dirtyHandler;
    }

    /**
     * Sets the dirty handler.
     *
     * @param dirtyHandler the new dirty handler
     */
    public void setDirtyHandler(MDirtyable dirtyHandler) {
        this.dirtyHandler = dirtyHandler;
    }

    /**
     * Gets the last save time.
     *
     * @return the last save time
     */
    public Date getLastSaveTime() {
        return (Date) lastSaveTime.clone();
    }

    /**
     * Sets the last save time.
     *
     * @param lastSaveTime the new last save time
     */
    public void setLastSaveTime(Date lastSaveTime) {
        this.lastSaveTime = (Date) lastSaveTime.clone();
    }

    /**
     * Gets the menu open SQL.
     *
     * @return the menu open SQL
     */
    public MenuItem getMenuOpenSQL() {
        return menuOpenSQL;
    }

    /**
     * Sets the menu open SQL.
     *
     * @param menuOpenSQL the new menu open SQL
     */
    public void setMenuOpenSQL(MenuItem menuOpenSQL) {
        this.menuOpenSQL = menuOpenSQL;
    }

    /**
     * Gets the def label id.
     *
     * @return the def label id
     */
    public String getDefLabelId() {
        return defLabelId;
    }

    /**
     * Sets the def label id.
     *
     * @param defLabelId the new def label id
     */
    public void setDefLabelId(String defLabelId) {
        this.defLabelId = defLabelId;
    }

    /**
     * Reset commit and rollback button.
     */
    public void resetCommitAndRollbackButton() {
        DBConnection dbconnection = getTermConnection().getConnection();

        try {
            if (dbconnection != null && !dbconnection.isClosed()) {
                if (dbconnection.isTransactionOpen(getServerVersion()) && !this.autoCommit) {

                    changeTransactionButtonsStatus(true);
                } else {
                    changeTransactionButtonsStatus(false);
                }
            } else {
                changeTransactionButtonsStatus(false);
            }
        } catch (Exception e) {
            generateExceptionLogAndDialog("Reset button status failed",
                    MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_RESET_BUTTIONS_EXCEPTION), e);
        }
    }

    /**
     * Manual rollback.
     *
     * @param dbConnection the db connection
     * @Author: wWX633190
     * @Date: April 15, 2019
     * @Title: manualRollback
     * @Description: Manually rollback the transaction
     */
    private void manualRollback(DBConnection dbConnection) {
        String progressBarLabel = getProgressLabelForTransaction(
                MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_ROLLBACK_JOB));
        TransactionRollbackWorker transactionRollback = new TransactionRollbackWorker(dbConnection, progressBarLabel);
        activateStatusbar();
        transactionRollback.schedule();
    }

    /**
     * Manual commit.
     *
     * @param dbConnection the db connection
     * @Author: wWX633190
     * @Date: April 15, 2019
     * @Title: manualCommit
     * @Description: Manually commit the transaction
     */
    private void manualCommit(DBConnection dbConnection) {
        String progressBarLabel = getProgressLabelForTransaction(
                MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_COMMIT_JOB));
        TransactionCommitWorker transactionCommit = new TransactionCommitWorker(dbConnection, progressBarLabel);
        activateStatusbar();
        transactionCommit.schedule();
    }

    /**
     * Change transaction buttons status.
     *
     * @param status the status
     * @Author: wWX633190
     * @Date: April 15,2019
     * @Title: changeTransactionButtonsStatus
     * @Description: Change the status of manual commit and rollback buttons
     */
    private void changeTransactionButtonsStatus(boolean status) {
        if (!commitButton.isDisposed() && !rollbackButton.isDisposed()) {
            this.commitButton.setEnabled(status);
            this.rollbackButton.setEnabled(status);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TransactionCommitWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class TransactionCommitWorker extends TransactionWorker {
        private DBConnection dbConnection;

        public TransactionCommitWorker(DBConnection dbConnection, String progressBarLabel) {
            super(progressBarLabel, MPPDBIDEConstants.CANCELABLEJOB);
            this.dbConnection = dbConnection;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            dbConnection.getConnection().commit();
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            changeBottomBar(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_COMMIT_FEEDBACK));
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TransactionRollbackWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class TransactionRollbackWorker extends TransactionWorker {
        private DBConnection dbConnection;

        public TransactionRollbackWorker(DBConnection dbConnection, String progressBarLabel) {
            super(progressBarLabel, MPPDBIDEConstants.CANCELABLEJOB);
            this.dbConnection = dbConnection;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            dbConnection.getConnection().rollback();
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            changeBottomBar(MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_ROLLBACK_FEEDBACK));
        }
    }

    private void activateStatusbar() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMssage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        StatusMessageList.getInstance().push(statMssage);
        if (bttmStatusBar != null) {
            bttmStatusBar.activateStatusbar();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TransactionWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private abstract class TransactionWorker extends UIWorkerJob {
        public TransactionWorker(String name, Object family) {
            super(name, family);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            transOperaFail(e);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            transOperaFail(e);
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            return;
        }

        @Override
        public void finalCleanupUI() {
            return;
        }

        @Override
        protected void canceling() {
            super.canceling();
            resetCommitAndRollbackButton();
        }

        private void transOperaFail(Exception e) {
            generateExceptionLogAndDialog("Transaction operation failed",
                    MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_COMMIT_EXCEPTION_BODY), e);
        }
    }

    /**
     * Sets the auto commit status.
     */
    public void setAutoCommitStatus() {
        DBConnection dbConnection = getTermConnection().getConnection();
        if (null != dbConnection && dbConnection.getConnection() != null) {
            try {
                dbConnection.getConnection().setAutoCommit(autoCommit);
            } catch (SQLException e) {
                generateExceptionLogAndDialog("Reset auto-commit status failed",
                        MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_OPERATION_EXCEPTION_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.TRANSACTION_RESET_BUTTIONS_EXCEPTION), e);
            }
        }
    }

    /**
     * Gets the progress label for transaction.
     *
     * @param executeMessage the execute message
     * @return the progress label for transaction
     * @Author: wWX633190
     * @Date: April 15,4
     * @Title: getProgressBarLabel
     * @Description: Bottom status bar job information
     */
    private String getProgressLabelForTransaction(String executeMessage) {
        String sqlTerminalPartLabel = this.getPartLabel();
        Database database = this.getSelectedDatabase();
        String dbName = "";
        String serName = "";
        if (database != null) {
            dbName = database.getName();
            serName = database.getServerName();
        }
        return ProgressBarLabelFormatter.getProgressLabelForTextModeLoading(dbName, serName, sqlTerminalPartLabel,
                executeMessage);
    }

    /**
     * Generate exception log and dialog.
     *
     * @param log the log
     * @param diaTitle the dia title
     * @param diaBody the dia body
     * @param e the e
     * @Author: wWX633190
     * @Date: April 25, 2019
     * @Title: generateExceptionLogAndDialog
     * @Description: Generate exception logs and dialogs
     */
    private void generateExceptionLogAndDialog(String log, String diaTitle, String diaBody, Exception exception) {
        MPPDBIDELoggerUtility.error(log, exception);
        MPPDBIDEDialogs.generateDSErrorDialog(diaTitle, diaBody, exception.getMessage(), exception);
    }

    /**
     * Change bottom bar.
     *
     * @param message the message
     * @Author: wWX633190
     * @Date: April 26, 2019
     * @Title: changeBottomBar
     * @Description: Change objBrowserStatusbar message
     */
    private void changeBottomBar(String message) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        changeTransactionButtonsStatus(false);
    }

    /**
     * Show progress bar.
     */
    public void showProgressBar() {
        if (null != this.sourceEditor) {
            this.sourceEditor.showExecProgressBar();
        }
    }

    /**
     * Hide progress bar.
     */
    public void hideProgressBar() {
        if (null != this.sourceEditor) {
            this.sourceEditor.hideExecProgressBar();
        }

    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return sourceEditor.getViewerDoc();
    }

    /**
     * Sets the dirty handler.
     *
     * @param isDirty the new dirty handler
     */
    public void setDirtyHandler(boolean isDirty) {
        getDirtyHandler().setDirty(isDirty);
    }

    /**
     * Enable disable menu save SQL.
     *
     * @param isEnable the is enable
     */
    public void enableDisableMenuSaveSQL(boolean isEnable) {
        menuSaveSQL.setEnabled(isEnable);
    }

    /**
     * Sets the sql terminal part label.
     *
     * @param label the new sql terminal part label
     */
    public void setSqlTerminalPartLabel(String label) {
        sqlTerminalPart.setLabel(label);
    }

    /**
     * Sets the sql terminal part tool tip.
     *
     * @param label the new sql terminal part tool tip
     */
    public void setSqlTerminalPartToolTip(String label) {
        sqlTerminalPart.setTooltip(label);
    }

    /**
     * Enable disable menu open SQL.
     *
     * @param isEnable the is enable
     */
    public void enableDisableMenuOpenSQL(boolean isEnable) {
        menuOpenSQL.setEnabled(isEnable);
    }

    /**
     * Checks if is button enabled.
     *
     * @return true, if is button enabled
     */
    public boolean isButtonEnabled() {
        return terminalUtility.isButtonEnabled(isActivated(), sourceEditor);
    }
    
    /**
     * Transfer file terminal to SQL terminal.
     *
     * @param fileTerminal the file terminal
     */
    public void transferFileTerminalToSQLTerminal() {
        setFilePath(null);
        setFileTerminalFlag(false);
        setTabLabel(getUiID());
        getDirtyHandler().setDirty(true);
        getSqlTerminalPart().setLabel(getUiID());
        getSqlTerminalPart().setTooltip(getTabToolTip());
        getMenuOpenSQL().setEnabled(true);
        UIElement.getInstance().resetTabIcon(getSqlTerminalPart(), getSelectedDatabase().isConnected());
    }
}
