/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.AnnotationPreference;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.errorlocator.IErrorLocator;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.core.ConsoleCoreWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHover;
import com.huawei.mppdbide.view.core.sourceeditor.BreakpointAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.DebugPositionAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.ErrorPositionAnnotation;
import com.huawei.mppdbide.view.core.sourceeditor.PLAnnotationMarkerAccess;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLEditorPlugin;
import com.huawei.mppdbide.view.core.sourceeditor.SQLPartitionScanner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import com.huawei.mppdbide.view.handler.ExecuteEditorItem;
import com.huawei.mppdbide.view.handler.HandlerUtilities;
import com.huawei.mppdbide.view.handler.debug.DebugHandlerUtils;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.terminal.executioncontext.FuncProcEditorTerminalExecutionContext;
import com.huawei.mppdbide.view.ui.autosave.AbstractAutoSaveObject;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveDbgObject;
import com.huawei.mppdbide.view.ui.saveif.ISaveablePart;
import com.huawei.mppdbide.view.ui.terminal.FuncProcTerminalResultDisplayUIManager;
import com.huawei.mppdbide.view.ui.terminal.resulttab.ResultTabManager;
import com.huawei.mppdbide.view.ui.uiif.PLSourceEditorIf;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayState;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.common.SourceViewerUtil;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class PLSourceEditor. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class PLSourceEditor extends AbstractAutoSaveObject
        implements ISaveablePart, IAutoSaveDbgObject, Observer, PLSourceEditorIf {
    /**
     * Core window object
     */
    private PLSourceEditorCore sourceEditor;

    private TerminalExecutionConnectionInfra termConnection;

    private AnnotationPainter fAnnotationPainter;

    private IAnnotationAccess fAnnotationAccess;

    private AnnotationModel fAnnotationModel;

    private CompositeRuler fCompositeRuler;

    private AnnotationRulerColumn fAnnotationRuler;

    @Inject
    private ECommandService commandService;

    @Inject
    private EHandlerService handlerService;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private IEventBroker eventBroker;

    private boolean codeChanged;

    private MenuItem menuExecDBObj;

    private IDebugObject debugObject;

    private List<Annotation> errorPositionAnnotationList;

    private SashForm sashForm;

    private ResultTabManager resultManager;

    private boolean isQueryEnabled = true;

    @Inject
    private MDirtyable terminalDirty;

    private PLSourceEditorResultConfig resultConfig;

    private FuncProcTerminalResultDisplayUIManager resultDisplayUIManager;

    private FuncProcEditorTerminalExecutionContext execContext;

    private String uiID;

    private String schemaName;

    private String elementID;

    private String labelID;

    private String toolTip;

    private boolean isCriticalErr = false;

    private static final String LINE_BEGIN = "LINE_BEGIN";

    private static final String LINE_WHEN = "LINE_WHEN";

    private String localText = null;

    private final Object instanceLock = new Object();

    private boolean isActivated = false;

    private boolean isEditableFlag = true;

    private boolean isAddModifyListener = false;

    private boolean isCompileInProgress;

    private boolean isExecuteInProgress;

    private SourceViewer viewer;

    private ArrayList<DefaultParameter> valueList = new ArrayList<>();

    private MPart tab;

    /**
     * Checks if is execute in progress.
     *
     * @return true, if is execute in progress
     */
    public boolean isExecuteInProgress() {
        return isExecuteInProgress;
    }

    /**
     * Sets the execute in progress.
     *
     * @param isExecInProgress the new execute in progress
     */
    public void setExecuteInProgress(boolean isExecInProgress) {
        this.isExecuteInProgress = isExecInProgress;
    }

    /**
     * sets the debug input value list
     * 
     * @param valueList the valueList
     */
    public void setDebugInputList(ArrayList<DefaultParameter> valueList) {
        this.valueList = valueList;
    }

    /**
     * sets the debug input value list
     *
     * @return the debug input value list
     */
    public ArrayList<DefaultParameter> getDebugInputList() {
        return this.valueList;
    }

    private SQLSyntax syntax;

    /**
     * Instantiates a new PL source editor.
     */
    public PLSourceEditor() {
        this.termConnection = new TerminalExecutionConnectionInfra();
        fAnnotationModel = new AnnotationModel();
        this.elementID = null;
        isActivated = false;
        errorPositionAnnotationList = new ArrayList<>(1);
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.addObserver(this);
        }
    }

    /**
     * Sets the function execute DB.
     *
     * @param db the new function execute DB
     */
    public void setFunctionExecuteDB(Database db) {
        this.getTermConnection().setDatabase(db);
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

    /**
     * Checks if is ignore critical exception.
     *
     * @return true, if is ignore critical exception
     */
    public boolean isIgnoreCriticalException() {
        return isCriticalErr;
    }

    /**
     * Sets the critical err.
     *
     * @param criticalErr the new critical err
     */
    public void setCriticalErr(boolean criticalErr) {
        this.isCriticalErr = criticalErr;
    }

    /**
     * Sets the compile in progress.
     *
     * @param isCompileInProgres the new compile in progress
     */
    public void setCompileInProgress(boolean isCompileInProgres) {
        this.isCompileInProgress = isCompileInProgres;
    }

    /**
     * Checks if is compile in progress.
     *
     * @return true, if is compile in progress
     */
    public boolean isCompileInProgress() {
        return isCompileInProgress;
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     * @param part the part
     */
    @PostConstruct
    public void createPartControl(final Composite parent, EPartService partService, EModelService modelService,
            MApplication application, @Active MPart part) {
        this.tab = part;
        // Which ever control is being created first, it has to set the
        // partService and modelService to UIElement. those will be used on
        // further calls.
        IDEStartup.getInstance().init(partService, modelService, application);

        sourceEditor = new PLSourceEditorCore(fAnnotationModel, getAnnotationAccess());

        sourceEditor.setCommandAndHandlerService(commandService, handlerService, selectionService);
        sourceEditor.setSqlCmdMenuKey(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_COMPILE_STATEMENT);
        sourceEditor.setSqlCmdMenuIcon(IiconPath.ICO_COMPILE_FUNC);
        if (syntax == null) {
            setSyntax(((PLSourceEditor) part.getObject()).getSyntax());
        }
        Composite modifiedParent = getCurrentComposite(parent);
        sashForm = new SashForm(modifiedParent, SWT.NONE);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sashForm.setOrientation(SWT.VERTICAL);

        sourceEditor.createEditor(sashForm, getCompositeRuler(), null, syntax, false);
        installDecorationSupport();

        viewer = sourceEditor.getSourceViewer();

        viewer.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_SRCEDITOR_TEXT_001");

        viewer.getTextWidget().addModifyListener(new SrcViewerModifyListener());

        Menu menu = sourceEditor.getMenu();

        addExecDbObjectMenuItem(menu);

        menu.addMenuListener(new MenuListener() {

            @Override
            public void menuShown(MenuEvent menuEvent) {
                contextMenuAboutToShow();
            }

            @Override
            public void menuHidden(MenuEvent menuEvent) {

            }
        });
        getAnnotationPainter();
        viewer.addPainter(fAnnotationPainter);
        viewer.addTextPresentationListener(fAnnotationPainter);
        createAnnotationHover();
        fAnnotationRuler.getControl().addMouseListener(new SourceEditorMouseListener());

        handlePendingActionOnActivation((PLSourceEditor) part.getObject());
    }

    private Composite getCurrentComposite(Composite parentComp) {
        GridData grdData = new GridData();
        grdData.horizontalAlignment = SWT.FILL;
        grdData.grabExcessHorizontalSpace = true;

        Composite currComposite = new Composite(parentComp, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.horizontalSpacing = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginHeight = 0;
        currComposite.setLayout(gridLayout);
        currComposite.setData(grdData);
        return currComposite;
    }

    private boolean isEditableFlag() {
        return isEditableFlag;
    }

    private void setEditableFlag(boolean isEditableFlag) {
        this.isEditableFlag = isEditableFlag;
    }

    private boolean isAddModifyListener() {
        return isAddModifyListener;
    }

    private void setAddModifyListener(boolean isAddModifyListener) {
        this.isAddModifyListener = isAddModifyListener;
    }

    /**
     * Handle pending action on activation.
     *
     * @param sqlt the sqlt
     */
    public void handlePendingActionOnActivation(PLSourceEditor sqlt) {
        synchronized (sqlt.instanceLock) {
            setActivated(true);
            if (sqlt != this) {
                sqlt.setActivated(true);

                /* Get values from sqlt and update here */
                setEditableFlag(sqlt.isEditableFlag());
                setAddModifyListener(sqlt.isAddModifyListener());
                setModified(sqlt.isModified());
                setModifiedAfterCreate(sqlt.isModifiedAfterCreate());
                updateStatus(sqlt.getStatus());
                setConnectionName(sqlt.getConnectionName());
                setDatabaseName(sqlt.getDatabaseName());
                setElementID(sqlt.getElementID());
                setTabLabel(sqlt.getTabLabel());
                setTabToolTip(sqlt.getTabToolTip());
                setSourceChangedInEditor(sqlt.isCodeChanged());
                setNamespaceName(sqlt.getNameSpaceName());
                setDebugObject(sqlt.getDebugObject());

                setFunctionExecuteDB(sqlt.getTermConnection().getDatabase());
            }
            if (sqlt.getTextOnly() != null) {
                setFunctionDocumentContent(sqlt.getTextOnly());
                sqlt.setTextOnly(null);
            }
            this.setTextOnly(null);
            sourceEditor.setEditable(this.isEditableFlag());
            this.resetButtons();
            if (this.isAddModifyListener()) {
                sourceEditor.getSourceViewer().getDocument().addDocumentListener(getAutoSaveModifyListener());
                sourceEditor.getSourceViewer().getDocument().addDocumentListener(getDocumentChangeListener());
            }

            sourceEditor.setDatabase(getDatabase());
        }
    }

    private String getTextOnly() {
        return localText;
    }

    private void setTextOnly(String localText) {
        this.localText = localText;
    }

    /**
     * Create annotation hover
     */
    private void createAnnotationHover() {
        AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(fCompositeRuler,
                sourceEditor.getSourceViewer(), new AnnotationHover(fAnnotationModel), new AnnotationConfiguration());
        fAnnotationHoverManager.install(fAnnotationRuler.getControl());
    }

    /**
     * Sets the source viewer configuration.
     */
    public void setSourceViewerConfiguration() {
        sourceEditor.getSourceViewer().unconfigure();
        SQLSourceViewerConfig configuration;
        configuration = new SQLSourceViewerConfig(syntax);
        sourceEditor.getSourceViewer().configure(configuration);
        configuration.setDatabase(getDatabase());

    }

    private CompositeRuler getCompositeRuler() {

        fCompositeRuler = new CompositeRuler(PLSourceEditorCore.SPACE_BETWEEN_RULER);
        fCompositeRuler.addDecorator(0, annotationRuler());

        LineNumberRulerColumn lineNumColumn = new LineNumberRulerColumn();
        lineNumColumn.setForeground(new Color(Display.getDefault(), 104, 99, 94));

        fCompositeRuler.addDecorator(1, lineNumColumn);
        fCompositeRuler.setModel(fAnnotationModel);

        return fCompositeRuler;
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
     * Title: class Description: The Class AnnotationConfiguration. Copyright
     * (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class AnnotationConfiguration implements IInformationControlCreator {

        @Override
        public IInformationControl createInformationControl(Shell parent) {
            return new DefaultInformationControl(parent);
        }

    }

    /**
     * Refresh debug object after edit.
     *
     * @param debugObj the debug obj
     */
    public void refreshDebugObjectAfterEdit(IDebugObject debugObj) {
        try {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT, true);

            debugObj = debugObj.refreshSelf();

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_REFRESH_OBJECT, false);

            HandlerUtilities.displaySourceCodeWhileRefresh(debugObj, false);
        } catch (DatabaseOperationException e1) {
            // Refreshing source code failed, refresh manually
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true, "Refresh Failed",
                    "Error occured while refreshing PLSQL Function/procedure code.");
        } catch (DatabaseCriticalException e1) {
            if (isIgnoreCriticalException()) {
                MPPDBIDELoggerUtility.error("PLSourceEditor: refresh debug object after edit failed.", e1);
            } else {
                UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(e1, debugObj.getDatabase());
            }
        } catch (final MPPDBIDEException e1) {
            MPPDBIDELoggerUtility.error("Error while refershing debug object ", e1);
            Display.getDefault().asyncExec(new RefreshFaildDisplay(e1));
            ObjectBrowser ob = UIElement.getInstance().getObjectBrowserModel();
            if (null != ob) {
                ob.refreshObject(debugObj);
            }

        }
    }

    /**
     * Title: class Description: The Class RefreshFaildDisplay. Copyright (c)
     * Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class RefreshFaildDisplay implements Runnable {
        private final Exception exception;

        private RefreshFaildDisplay(Exception refreshFailedException) {
            this.exception = refreshFailedException;
        }

        @Override
        public void run() {
            ConsoleCoreWindow.getInstance()
                    .logError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_REFRESHING));
            MPPDBIDEDialogs.generateErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG_HEADING),
                    MessageConfigLoader.getProperty(IMessagesConstants.BREAKPOINT_WINDOW_ERROR_MSG), exception);

        }
    }

    /**
     * Sets the debug object.
     *
     * @param debugObject the new debug object
     */
    public void setDebugObject(IDebugObject debugObject) {
        this.debugObject = debugObject;
    }

    private void loadDebugObject() {
        if (null == sourceEditor) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_UNABLE));
            return;
        }

        this.termConnection.setDatabase(debugObject.getDatabase());

        try {
            if (null == sourceEditor.getDocument() || sourceEditor.getDocument().getLength() <= 0
                    || debugObject.isChanged(debugObject.getLatestSouceCode().getCode())
                    || ((debugObject.getLang() != null)
                            && (debugObject.getLang().equals("c") || debugObject.getLang().equals("plpgsql")))) {
                debugObject.setCodeReloaded(true);
            }
            if (debugObject.isCodeReloaded()) {
                sourceEditor.setDocument(new Document(debugObject.getLatestSouceCode().getCode()), 0);
                debugObject.setCodeReloaded(false);
            }
            registerModifyListener();
            setSourceChangedInEditor(false);
            setSourceViewerConfiguration();
        } catch (DatabaseOperationException exception) {
            handleDatabaseOperationException(exception);
        } catch (DatabaseCriticalException databaseCriticalException) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(databaseCriticalException,
                    this.debugObject.getDatabase());
        } finally {
            sourceEditor.setDatabase(debugObject.getDatabase());
        }

    }

    private void handleDatabaseOperationException(DatabaseOperationException databaseOperationException) {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE),
                databaseOperationException);
        ConsoleCoreWindow.getInstance()
                .logWarning(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE));
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_SOURCE_NOT_AVAILABLE)
                + System.lineSeparator() + databaseOperationException.getServerMessage();
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_VIEWER_ERROR), msg);
    }

    private void loadDebugObjectWhenNotActivated() {
        this.termConnection.setDatabase(debugObject.getDatabase());

        try {
            localText = debugObject.getLatestSouceCode().getCode();
            setSourceChangedInEditor(false);
        } catch (DatabaseOperationException databaseOperationException) {
            handleDatabaseOperationException(databaseOperationException);
        } catch (DatabaseCriticalException databaseCriticalException) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(databaseCriticalException,
                    this.debugObject.getDatabase());
        }
    }

    /**
     * Annotation high lighter. Currently not working, to be fixed.
     *
     * @return the annotation painter
     */
    private AnnotationPainter getAnnotationPainter() {
        Display display = Display.getDefault();
        fAnnotationPainter = new AnnotationPainter(sourceEditor.getSourceViewer(), fAnnotationAccess);
        for (AnnotationType annotationType: AnnotationType.values()) {
            String strategy = annotationType.getStrategy();
            fAnnotationPainter.addHighlightAnnotationType(strategy);
            fAnnotationPainter.setAnnotationTypeColor(strategy, new Color(display, annotationType.getRGB()));
        }
        return fAnnotationPainter;
    }

    /**
     * Display source for debug object.
     *
     * @param object the object
     */
    public void displaySourceForDebugObject(IDebugObject object) {
        synchronized (instanceLock) {
            if (isActivated()) {
                sourceEditor.lockSourceViewerRedraw();

                if (null == object) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_CODE_ERROR),
                            MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_DISPLAY_ERROR));
                    sourceEditor.unlockSourceViewerRedraw();
                    return;
                }
                if (object.isCodeReloaded()) {
                    clear();
                }

                setDebugObject(object);
                loadDebugObject();

                refreshAnnotations();
                sourceEditor.unlockSourceViewerRedraw();
                onFocus();
            } else {
                clear();
                if (null == object) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_CODE_ERROR),
                            MessageConfigLoader.getProperty(IMessagesConstants.PL_SOURCE_DISPLAY_ERROR));
                    return;
                }

                setDebugObject(object);
                loadDebugObjectWhenNotActivated();
            }
        }
    }

    /**
     * Gets the debug object.
     *
     * @return the debug object
     */
    @Override
    public IDebugObject getDebugObject() {
        return debugObject;
    }

    /**
     * Gets the debug object name space.
     *
     * @return the debug object name space
     */
    public INamespace getDebugObjectNameSpace() {
        return debugObject.getNamespace();
    }

    /**
     * Refresh debug object group.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshDebugObjectGroup() throws DatabaseOperationException, DatabaseCriticalException {
        ((INamespace) debugObject.getNamespace()).refreshDebugObjectGroup();
    }

    /**
     * Gets the debug object type.
     *
     * @return the debug object type
     */
    public OBJECTTYPE getDebugObjectType() {
        return debugObject.getType();
    }

    /**
     * Refresh annotations.
     */
    public void refreshAnnotations() {
        refresh();
    }

    /**
     * Refresh.
     */
    public void refresh() {

        // When no object is shown on editor gracefully ignore.
        if (this.debugObject == null && !isActivated()) {
            return;
        }

        fAnnotationModel.removeAllAnnotations();
    }

    /**
     * Creates the breakpoint.
     *
     * @param lineNumber the line number
     */

    private void reconnect(boolean needRestart) {
        if (needRestart) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().reconnectOnCriticalExceptionForDebug(debugObject);
        }
    }

    private void setEditableForDebug(final boolean isEditable) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (sourceEditor != null) {

                    sourceEditor.setEditable(isEditable);
                }
            }
        });
    }

    /**
     * Remove the error position annotation.
     */
    public void removeErrorPositionAnnotation() {
        if (null != this.errorPositionAnnotationList) {
            for (Annotation annotation : errorPositionAnnotationList) {
                fAnnotationModel.removeAnnotation(annotation);
            }
            this.errorPositionAnnotationList.clear();
            MPPDBIDELoggerUtility
                    .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_PLSOURCEEDITORCORE_REMOVE_ANNOTATION));
        }
    }

    /**
     * On focus.
     */
    @Focus
    public void onFocus() {
        if (DBAssistantWindow.isEnable()) {
            DBAssistantWindow.toggleAssitantEnableDisable(sourceEditor.getSourceViewer(),
                    getFunctionSelectedDatabase());
        }
        Control control = sourceEditor.getSourceViewer().getControl();
        if (!control.isDisposed()) {
            control.setFocus();
        }
    }

    /**
     * Destroy.
     */
    public void destroy() {
        if (!isActivated()) {
            localText = null;
            return;
        }
        localText = null;
        if (null != sourceEditor) {
            sourceEditor.preDestroy();
        }
        SQLEditorPlugin.getDefault().setSQLCodeScanner(null);
        if (this.debugObject != null) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().clearSqlObject();
        }
        if (this.debugObject != null) {
            this.debugObject = null;
        }
        syntax = null;
    }

    /**
     * Clear.
     */
    public void clear() {
        if (!isActivated()) {
            localText = null;
            return;
        }

        localText = null;
        if (null != sourceEditor.getDocument()) {
            fAnnotationModel.removeAllAnnotations();
            sourceEditor.getDocument().set("");
            sourceEditor.uninstallDecoration();
            sourceEditor.destroyDocument();
            MPPDBIDELoggerUtility.info(
                    MessageConfigLoader.getProperty(IMessagesConstants.GUI_PLSOURCEEDITORCORE_SOURCE_EDITOR_CLEARED));
        }

        this.debugObject = null;
    }

    /**
     * Gets the source editor core.
     *
     * @return the source editor core
     */
    public PLSourceEditorCore getSourceEditorCore() {
        return this.sourceEditor;
    }

    /**
     * Destroy source viewer.
     */
    @PreDestroy
    public void destroySourceViewer() {
        if (!isActivated()) {
            localText = null;
            return;
        }
        if (DBAssistantWindow.getViewer() == sourceEditor.getSourceViewer()) {
            DBAssistantWindow.setViewer(null);
            DBAssistantWindow.setDatabase(null);
        }
        if (null != this.resultDisplayUIManager) {
            this.resultDisplayUIManager.setDisposed();
        }

        if (null != getDebugObject()) {
            if (null != this.resultManager) {
                this.resultManager.destroy();
                this.resultManager = null;
            }
            MemoryCleaner.cleanUpMemory();

            SourceViewerUtil.removeSourceViewerId(
                    (this.elementID == null) ? getDebugObject().getPLSourceEditorElmId() : this.elementID,
                    getDebugObject().getType());
            if (getDebugObject() != null) {
                this.debugObject = null;
            }
        }

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
     * The listener interface for receiving srcViewerModify events. The class
     * that is interested in processing a srcViewerModify event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addSrcViewerModifyListener<code>
     * method. When the srcViewerModify event occurs, that object's appropriate
     * method is invoked. SrcViewerModifyEvent
     */
    private class SrcViewerModifyListener implements ModifyListener {
        @Override
        public void modifyText(ModifyEvent event) {
            setSourceChangedInEditor(true);
        }
    }

    /**
     * Sets the source changed in editor.
     *
     * @param isSourceChangedInEditor the new source changed in editor
     */
    public void setSourceChangedInEditor(boolean isSourceChangedInEditor) {
        codeChanged = isSourceChangedInEditor;
        if (terminalDirty != null) {
            terminalDirty.setDirty(codeChanged);
        }
        removeErrorPositionAnnotation();
    }

    /**
     * Checks if is code changed.
     *
     * @return true, if is code changed
     */
    public boolean isCodeChanged() {
        return codeChanged;
    }

    private void addExecDbObjectMenuItem(Menu menu) {
        final ExecuteEditorItem executeEditorItem = new ExecuteEditorItem();
        menuExecDBObj = new MenuItem(menu, SWT.PUSH);
        // DTS2016011900019 Starts
        menuExecDBObj.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_EXECUTE_DB_OBJECT));
        // DTS2016011900019 Ends
        menuExecDBObj.addSelectionListener(new ExecObjSelectListener(executeEditorItem));
        menuExecDBObj.setImage(IconUtility.getIconImage(IiconPath.ICO_RUN, this.getClass()));
    }

    /**
     * The listener interface for receiving execObjSelect events. The class that
     * is interested in processing a execObjSelect event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addExecObjSelectListener<code>
     * method. When the execObjSelect event occurs, that object's appropriate
     * method is invoked. ExecObjSelectEvent
     */
    private static final class ExecObjSelectListener implements SelectionListener {
        private final ExecuteEditorItem executeEditorItem;

        private ExecObjSelectListener(ExecuteEditorItem executeEditorItem) {
            this.executeEditorItem = executeEditorItem;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            executeEditorItem.execute();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    private void contextMenuAboutToShow() {
        Object partObject = UIElement.getInstance().getActivePartObject();
        UIElement uiEle = UIElement.getInstance();
        if (partObject instanceof PLSourceEditor) {
            PLSourceEditor editor = (PLSourceEditor) partObject;
            boolean toggelCut = editor.getSourceEditorCore().getSelectionCount() > 0;
            boolean toggelCopy = editor.getSourceEditorCore().getSelectionCount() > 0;
            boolean toggelSelectAll = editor.getSourceEditorCore().getDocument().get().length() > 0;
            boolean toggleCompile = false;

            if ((uiEle.isNewEditorOnTop() || uiEle.isEditorOnTopById()) && editor.getTermConnection() != null
                    && editor.getTermConnection().getDatabase() != null
                    && editor.getTermConnection().getDatabase().isConnected()) {
                toggleCompile = true;
            }
            Clipboard clipboard = new Clipboard(Display.getDefault());
            TextTransfer textTransfer = TextTransfer.getInstance();
            String textData = (String) clipboard.getContents(textTransfer);
            boolean togglePaste = null != textData && !"".equals(textData);
            sourceEditor.toggleCutCopySelectAll(toggelCut, toggelCopy, toggelSelectAll, toggleCompile, togglePaste);
            sourceEditor.toggleCommentsEnableDisable(partObject);
            menuExecDBObj.setEnabled(isExecSupport() && toggleCompile);
            editor.getSourceEditorCore().getMenuSQLCmd()
                    .setEnabled(!(isCompileInProgress() || isExecuteInProgress()) && toggleCompile);
        }
    }

    /**
     * Gets the select the number of rows.
     *
     * @return the select the number of rows
     */

    public int getSelectTheNumberOfRows() {
        ITextSelection selection = (ITextSelection) viewer.getSelection();
        int rowSelectCount = selection.getEndLine() - selection.getStartLine();
        return rowSelectCount;
    }

    private boolean isExecSupport() {
        return UIElement.getInstance().isEditorOnTopById();
    }

    private void installDecorationSupport() {
        sourceEditor.installDecorationSupport();
    }

    /**
     * The listener interface for receiving sourceEditorMouse events. The class
     * that is interested in processing a sourceEditorMouse event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addSourceEditorMouseListener<code> method. When the
     * sourceEditorMouse event occurs, that object's appropriate method is
     * invoked. SourceEditorMouseEvent
     */
    private class SourceEditorMouseListener implements MouseListener {
        private static final int INVALID_LINE = -1;
        private boolean isDoubleClickedBefore = false;

        @Override
        public void mouseDoubleClick(MouseEvent e) {
            int line = fAnnotationRuler.getLineOfLastMouseButtonActivity();
            /**
             * No action performed when, 1. Line number is beyond end of
             * procedure 2. Already an breakpoint operation is in progress for
             * this Line number 3. If line number falls in PL header or footer.
             */
            if (!validCheck(line)) {
                return;
            }
            MPPDBIDELoggerUtility.info("double clicked!");
            isDoubleClickedBefore = true;
        }

        @Override
        public void mouseDown(MouseEvent e) {
            // Skip the handling
        }

        private boolean validCheck(int line) {
            if (line == INVALID_LINE || debugObject == null) {
                return false;
            }
            return true;
        }

        @Override
        public void mouseUp(MouseEvent e) {
            int line = fAnnotationRuler.getLineOfLastMouseButtonActivity();
            if (!validCheck(line)) {
                return;
            }
            MPPDBIDELoggerUtility.info("mouseUp clicked!");

            Optional<BreakpointAnnotation> annotation = findAnnotation(line);
            if (isDoubleClickedBefore) {
                isDoubleClickedBefore = false;
                doubleClickRun(annotation, line);
            } else {
                singleClickRun(annotation, line);
            }
        }
    }

    private void doubleClickRun(Optional<BreakpointAnnotation> annotation, int line) {
        if (!annotation.isPresent()) {
            try {
                BreakpointAnnotation annotationNew = createBreakpoint(line, true);
                DebugServiceHelper.getInstance().notifyBreakPointStatus(
                        annotationNew,
                        true);
            } catch (BadLocationException badLocationExp) {
                MPPDBIDELoggerUtility.error("invalid set position");
            }
        } else {
            deleteBreakpoint(line, annotation.get());
            DebugServiceHelper.getInstance().notifyBreakPointStatus(
                    annotation.get(),
                    false);
        }
    }

    private void singleClickRun(Optional<BreakpointAnnotation> annotation, int line) {
        if (annotation.isPresent()) {
            try {
                BreakpointAnnotation bpAnnotation = changeBreakPoint(
                        line,
                        annotation.get()
                        );
                DebugServiceHelper.getInstance().notifyBreakPointChange(bpAnnotation);
            } catch (BadLocationException e1) {
                MPPDBIDELoggerUtility.error("invalid set position");
            }
        }
    }

    private BreakpointAnnotation createBreakpoint(int line, boolean isEnable) throws BadLocationException {
        BreakpointAnnotation annotation = new BreakpointAnnotation("[" + (line + 1), line);
        annotation.setEnable(isEnable);
        fAnnotationModel.addAnnotation(annotation,
                new Position(sourceEditor.getDocument().getLineOffset(line))
                );
        return annotation;
    }

    private void deleteBreakpoint(int line, BreakpointAnnotation annotation) {
        fAnnotationModel.removeAnnotation(annotation);
    }

    private BreakpointAnnotation changeBreakPoint(
            int line,
            BreakpointAnnotation breakpointAnnotation)
                    throws BadLocationException {
        fAnnotationModel.removeAnnotation(breakpointAnnotation);
        return createBreakpoint(line, !breakpointAnnotation.getEnable());
    }

    /**
     * description: remove debug position
     */
    public void removeDebugPosition() {
        Iterator<Annotation> annoIterator = fAnnotationModel.getAnnotationIterator();
        List<Annotation> needRemoveAnnotations = new ArrayList<Annotation>(1);
        while (annoIterator.hasNext()) {
            Annotation annotation = annoIterator.next();
            if (annotation instanceof DebugPositionAnnotation) {
                needRemoveAnnotations.add(annotation);
            }
        }
        for (Annotation anno: needRemoveAnnotations) {
            fAnnotationModel.removeAnnotation(anno);
        }
    }

    /**
     * description: create debug position annotation
     * 
     * @param line the debug pos line
     * @throws BadLocationException
     */
    public void createDebugPosition(int line) throws BadLocationException {
        DebugPositionAnnotation annotation = new DebugPositionAnnotation(line);
        fAnnotationModel.addAnnotation(annotation,
                new Position(sourceEditor.getDocument().getLineOffset(line))
                );
    }

    /**
     * Search for any annotation for given line.
     *
     * @param line the line
     * @return the annotation
     */
    private Optional<BreakpointAnnotation> findAnnotation(int line) {
        Iterator<Annotation> annotations = fAnnotationModel.getAnnotationIterator();
        boolean annotationsHasNext = annotations.hasNext();
        Annotation annotation = null;
        while (annotationsHasNext) {
            annotation = annotations.next();
            if (annotation instanceof BreakpointAnnotation && line == ((BreakpointAnnotation) annotation).getLine()) {
                return Optional.of((BreakpointAnnotation)annotation);
            }
            annotationsHasNext = annotations.hasNext();
        }
        return Optional.empty();
    }

    /**
     * Create an annotation Ruler to show breakpoint information.
     *
     * @return the annotation ruler column
     */
    private AnnotationRulerColumn annotationRuler() {
        fAnnotationRuler = new AnnotationRulerColumn(fAnnotationModel, PLSourceEditorCore.ANNOTATION_RULER_WIDTH,
                getAnnotationAccess());
        for (AnnotationType annotationType: AnnotationType.values()) {
            fAnnotationRuler.addAnnotationType(annotationType.getStrategy());
        }
        return fAnnotationRuler;
    }

    /**
     * Create annotation access.
     *
     * @return the annotation access
     */
    private IAnnotationAccess getAnnotationAccess() {
        if (null == fAnnotationAccess) {
            fAnnotationAccess = new PLAnnotationMarkerAccess();
        }

        return fAnnotationAccess;
    }

    /**
     * Prompt user to save.
     *
     * @return the save
     */
    @Override
    public Save promptUserToSave() {
        if (null != tab) {
            tab.getParent().setSelectedElement(tab);
        }
        String title = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_CHANGES_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_TERMINAL_DATA_BODY);
        String cancel = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC);
        String discardChanges = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_DISCARD);
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()), title, message,
                new String[] {discardChanges, cancel}, 1);

        if (0 == userChoice) {
            return Save.NO;
        }

        return Save.CANCEL;
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    public IResultConfig getResultConfig() {
        if (this.resultConfig == null) {
            this.resultConfig = new PLSourceEditorResultConfig();
        }

        return this.resultConfig;
    }

    /**
     * Title: class Description: The Class PLSourceEditorResultConfig. Copyright
     * (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class PLSourceEditorResultConfig implements IResultConfig {

        @Override
        public int getFetchCount() {
            return UserPreference.getInstance().getResultDataFetchCount();
        }

        @Override
        public ActionAfterResultFetch getActionAfterFetch() {
            return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
        }

    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    public IResultDisplayUIManager getResultDisplayUIManager() {
        if (this.resultDisplayUIManager == null) {
            this.resultDisplayUIManager = new FuncProcTerminalResultDisplayUIManager(this);
        }

        return this.resultDisplayUIManager;
    }

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     */
    public void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary) {
        boolean needRestart = false;
        try {
            getResultManager().preResultTabGeneration();
            getResultManager().createResult(resultsetDisplaydata, consoledata, queryExecSummary);
        } catch (DatabaseCriticalException e) {
            needRestart = UIDisplayFactoryProvider.getUIDisplayStateIf()
                    .handleCriticalExceptionForReconnect(debugObject);
        } finally {
            reconnect(needRestart);
        }
    }

    @Override
    public ResultTabManager getResultManager() {
        if (null == this.resultManager) {
            this.resultManager = new ResultTabManager(this.sashForm, this.getUiID(), null, this.eventBroker,
                    this.termConnection, false);
        }

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
    public TerminalExecutionConnectionInfra getTermConnection() {
        return this.termConnection;
    }

    /**
     * Sets the term connection reconnect on terminal.
     *
     * @param isReconnect the new term connection reconnect on terminal
     */
    public void setTermConnectionReconnectOnTerminal(boolean isReconnect) {
        this.termConnection.setReconnectOnTerminal(isReconnect);
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
     * Gets the function selected database.
     *
     * @return the function selected database
     */
    public Database getFunctionSelectedDatabase() {
        return this.getTermConnection().getDatabase();
    }

    /**
     * Gets the function document content.
     *
     * @return the function document content
     */
    public String getFunctionDocumentContent() {
        return sourceEditor.getDocument().get();
    }

    private void setFunctionDocumentContent(String query) {
        // First disconnect partitioner and set and connect again
        if (((IDocumentExtension3) sourceEditor.getDocument())
                .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING) != null) {
            ((IDocumentExtension3) sourceEditor.getDocument())
                    .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING).disconnect();
        }
        sourceEditor.getDocument().set(query);
        SQLDocumentPartitioner.connectDocument(sourceEditor.getDocument(), 0);
    }

    /**
     * Show function error.
     *
     * @param errorDetails the error details
     */
    public void showFunctionError(final QueryInfo errorDetails) {

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {

                ErrorAnnotation errorAnnotation = new ErrorAnnotation(errorDetails.getErrLineNo(),
                        errorDetails.getServerMessageString());

                int position = errorDetails.getStartOffset() + errorDetails.getErrorPosition();
                fAnnotationModel.addAnnotation(errorAnnotation,
                        new Position(position - 1, errorDetails.getErrorMsgString().length()));
            }
        });
    }

    /**
     * Checks if is function visible.
     *
     * @return true, if is function visible
     */
    public boolean isFunctionVisible() {
        Control control = sourceEditor.getSourceViewer().getControl();
        if (!control.isDisposed()) {
            return control.isVisible();
        } else {
            return false;
        }
    }

    /**
     * Sets the execution context.
     *
     * @param context the new execution context
     */
    public void setExecutionContext(FuncProcEditorTerminalExecutionContext context) {
        this.execContext = context;
    }

    /**
     * Gets the execution context.
     *
     * @return the execution context
     */
    public FuncProcEditorTerminalExecutionContext getExecutionContext() {
        return this.execContext;
    }

    /**
     * Gets the function document text.
     *
     * @return the function document text
     */
    public String getFunctionDocumentText() {
        String text = this.getTerminalCore().getDocument().get();
        return text;
    }

    /**
     * Gets the functsource viewer line of offset.
     *
     * @param offset the offset
     * @return the functsource viewer line of offset
     * @throws BadLocationException the bad location exception
     */
    public int getFunctsourceViewerLineOfOffset(int offset) throws BadLocationException {
        return this.getTerminalCore().getDocument().getLineOfOffset(offset);
    }

    /**
     * Removes the funct all errors.
     */
    public void removeFunctAllErrors() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                fAnnotationModel.removeAllAnnotations();
            }
        });
    }

    /**
     * Removes the funct errors in selected range.
     *
     * @param off the off
     * @param length the length
     * @param startsBefore the starts before
     * @param endsAfter the ends after
     */
    public void removeFunctErrorsInSelectedRange(int off, int length, boolean startsBefore, boolean endsAfter) {
        Iterator<Annotation> annotationIterator = fAnnotationModel.getAnnotationIterator(off, length, startsBefore,
                endsAfter);
        Annotation anno = null;

        while (annotationIterator.hasNext()) {
            anno = annotationIterator.next();
            fAnnotationModel.removeAnnotation(anno);
        }

    }

    @Override
    public Database getDatabase() {
        if (null != debugObject) {
            return debugObject.getDatabase();
        }
        return null;
    }

    /**
     * Gets the database error locator.
     *
     * @return the database error locator
     */
    public IErrorLocator getDatabaseErrorLocator() {
        Database database = getDatabase();

        if (null != database) {
            return database.getErrorLocator();
        }
        return null;
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
                    sourceEditor.getSourceViewer().getDocument().addDocumentListener(getDocumentChangeListener());
                }

            });
        }
    }

    /**
     * Gets the document change listener.
     *
     * @return the document change listener
     */
    public IDocumentListener getDocumentChangeListener() {
        IDocumentListener listener = new IDocumentListener() {

            @Override
            public void documentChanged(DocumentEvent event) {
                setSourceChangedInEditor(true);

            }

            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
            }
        };
        return listener;
    }

    /**
     * Checks if is obj dirty.
     *
     * @return true, if is obj dirty
     */
    @Override
    public boolean isObjDirty() {
        synchronized (instanceLock) {
            if (terminalDirty != null) {
                return terminalDirty.isDirty();
            } else {
                return isCodeChanged();
            }
        }

    }

    /**
     * Gets the dbg obj type.
     *
     * @return the dbg obj type
     */
    @Override
    public OBJECTTYPE getDbgObjType() {
        return debugObject.getObjectType();
    }

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    @Override
    public long getOid() {
        return debugObject.getOid();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return debugObject.getName();
    }

    /**
     * Gets the name space name.
     *
     * @return the name space name
     */
    @Override
    public String getNameSpaceName() {
        if (debugObject == null || debugObject.getNamespace() == null) {
            return schemaName;
        }

        return debugObject.getNamespace().getName();
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    @Override
    public String getText() {
        return getFunctionDocumentContent();
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
        if (debugObject == null || debugObject.getDatabase() == null) {
            return super.getDatabaseName();
        }

        return debugObject.getDatabase().getName();
    }

    /**
     * Gets the element ID.
     *
     * @return the element ID
     */
    @Override
    public String getElementID() {
        if (debugObject == null || debugObject.getDatabase() == null) {
            return elementID;
        }

        return debugObject.getPLSourceEditorElmId();
    }

    /**
     * Update editor window.
     *
     * @param dbObject the db object
     * @return true, if successful
     */
    @Override
    public boolean updateEditorWindow(Object dbObject) {
        if (!(dbObject instanceof Database)) {
            MPPDBIDELoggerUtility.debug("Database not found");
            return false;
        }

        Database db = (Database) dbObject;
        setFunctionExecuteDB(db);
        if (isActivated()) {
            this.sourceEditor.setDatabase(db);
            this.sourceEditor.updateSQLConfigurationsOnDBConnect(getDebugObject().getDatabase());
        }
        this.debugObject.setDatabase(db);
        if (!db.isConnected()) {
            return false;
        }

        IDebugObject dbg = null;
        INamespace ns = null;
        try {
            ns = db.getNameSpaceByName(getNameSpaceName());
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.debug("Namespace not found");
        }
        if (ns != null) {
            this.debugObject.setNamespace(ns);
            dbg = ns.getDebugObjectById(getOid());
        }

        if (dbg != null) {
            if ((dbg.getName() != null && !dbg.getName().equals(this.getName()))
                    || dbg.getObjectType() != this.getDbgObjType()) {
                MPPDBIDELoggerUtility.debug("No debug object with same name or type found");
            } else {
                this.debugObject = dbg;
            }
        }

        return true;
    }

    /**
     * Sets the editable.
     *
     * @param isEditable the new editable
     */
    @Override
    public void setEditable(boolean isEditable) {
        synchronized (instanceLock) {
            if (isActivated()) {
                setEditableForDebug(isEditable);
            } else {
                isEditableFlag = isEditable;
            }
        }
    }

    /**
     * Sets the text.
     *
     * @param data the new text
     */
    @Override
    public void setText(final String data) {
        synchronized (instanceLock) {
            if (!isActivated()) {
                localText = data;
                return;
            }

            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (sourceEditor != null && sourceEditor.getSourceViewer() != null) {
                        setFunctionDocumentContent(data);
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
        if (debugObject == null || debugObject.getDatabase() == null) {
            return labelID;
        }
        return debugObject.getWindowTitleName();
    }

    /**
     * Gets the tab tool tip.
     *
     * @return the tab tool tip
     */
    @Override
    public String getTabToolTip() {
        if (debugObject == null || debugObject.getDatabase() == null) {
            return toolTip;
        }
        return debugObject.getPLSourceEditorElmTooltip();
    }

    /**
     * Sets the dirty.
     *
     * @param objDirty the new dirty
     */
    @Override
    public void setDirty(boolean objDirty) {
        synchronized (instanceLock) {
            setSourceChangedInEditor(objDirty);
        }
    }

    /**
     * Sets the namespace name.
     *
     * @param schemaName1 the new namespace name
     */
    @Override
    public void setNamespaceName(String schemaName1) {
        this.schemaName = schemaName1;
    }

    /**
     * Sets the element ID.
     *
     * @param id the new element ID
     */
    @Override
    public void setElementID(String id) {
        this.elementID = id;
    }

    /**
     * Sets the tab label.
     *
     * @param label the new tab label
     */
    @Override
    public void setTabLabel(String label) {
        this.labelID = label;
    }

    /**
     * Sets the tab tool tip.
     *
     * @param toolTip1 the new tab tool tip
     */
    @Override
    public void setTabToolTip(String toolTip1) {
        this.toolTip = toolTip1;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return MPPDBIDEConstants.PLSQL_EDITOR;
    }

    /**
     * Checks if is activated.
     *
     * @return true, if is activated
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Sets the activated.
     *
     * @param isActivated1 the new activated
     */
    public void setActivated(boolean isActivated1) {
        this.isActivated = isActivated1;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        destroy();
    }

    /**
     * Gets the syntax.
     *
     * @return the syntax
     */
    public SQLSyntax getSyntax() {
        return syntax;
    }

    /**
     * Sets the syntax.
     *
     * @param syntax the new syntax
     */
    public void setSyntax(SQLSyntax syntax) {
        this.syntax = syntax;
    }

    /**
     * Update.
     *
     * @param o the o
     * @param arg the arg
     */
    @Override
    public void update(Observable o, Object arg) {
        if (this.sourceEditor != null && getDebugObject() != null) {
            this.sourceEditor.updateSQLConfigurationsOnDBConnect(getDebugObject().getDatabase());
        }
    }

    /**
     * Gets the def label id.
     *
     * @return the def label id
     */
    @Override
    public String getDefLabelId() {

        return null;
    }

    /**
     * Enabledisable text widget.
     *
     * @param isEnable the is enable
     */
    public void enabledisableTextWidget(boolean isEnable) {
        getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(isEnable);
    }

    /**
     * Sets the document.
     *
     * @param doc the new document
     */
    public void setDocument(IDocument doc) {
        getSourceEditorCore().setDocument(doc, 0);
    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public String getDocument() {
        if (getSourceEditorCore() != null && getSourceEditorCore().getSourceViewer() != null
                && getSourceEditorCore().getSourceViewer().getDocument() != null) {
            return getSourceEditorCore().getSourceViewer().getDocument().get();
        }
        return "";
    }

    /**
     * Gets the source viewer line of offset.
     *
     * @param errorLineNumber the error line number
     * @return the source viewer line of offset
     * @throws BadLocationException the bad location exception
     */
    public int getsourceViewerLineOfOffset(int errorLineNumber) throws BadLocationException {
        return getTerminalCore().getsourceViewerLineOfOffset(errorLineNumber);
    }

    /**
     * Gets the console msg queue.
     *
     * @param isConsole the is console
     * @return the console msg queue
     */
    public MessageQueue getConsoleMsgQueue(boolean isConsole) {
        return getConsoleMessageWindow(isConsole).getMsgQueue();
    }

    /**
     * Log error in ui.
     *
     * @param canLog the can log
     * @param message the message
     */
    public void logErrorInUi(boolean canLog, String message) {
        getConsoleMessageWindow(canLog).logErrorInUI(message);
    }

    /**
     * Show progress bar.
     */
    public void showProgressBar() {
        if (this.sourceEditor != null) {
            this.sourceEditor.showExecProgressBar();
        }
    }

    /**
     * Hide progress bar.
     */
    public void hideProgressBar() {
        if (this.sourceEditor != null) {
            this.sourceEditor.hideExecProgressBar();
        }
    }
}
