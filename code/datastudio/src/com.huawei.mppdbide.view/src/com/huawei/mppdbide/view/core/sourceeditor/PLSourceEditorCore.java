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

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.annotation.PreDestroy;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension2;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IFindReplaceTarget;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.gauss.sqlparser.SQLFoldingConstants;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.SelectMenuItem;
import com.huawei.mppdbide.view.handler.debug.DebugHandlerUtils;
import com.huawei.mppdbide.view.handler.debug.DebugServiceHelper;
import com.huawei.mppdbide.view.prefernces.DSFormatterPreferencePage;
import com.huawei.mppdbide.view.prefernces.FormatterPreferenceKeys;
import com.huawei.mppdbide.view.prefernces.IAutoCompletePreference;
import com.huawei.mppdbide.view.prefernces.KeyBindingWrapper;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.ui.FindAndReplaceOptions;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminalFormatterUIWorker;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayState;
import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.TerminalStatusBar;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class Description: The Class PLSourceEditorCore.
 *
 * @since 3.0.0
 */
public final class PLSourceEditorCore extends SelectMenuItem implements IPropertyChangeListener {
    private static final String FORMAT_COMMAND_ID = "com.huawei.mppdbide.command.id.format";

    private ECommandService commandService;

    private EHandlerService handlerService;

    private ESelectionService selectionService;

    private volatile SourceViewer viewer;

    private IAnnotationAccess fAnnotationAccess;

    private IDocument doc;

    private ArrayList<UIWorkerJob> jobsAssociatedToTerminal;

    /**
     * The Constant ANNOTATION_RULER_WIDTH.
     */
    public static final int ANNOTATION_RULER_WIDTH = 16;

    private SQLSourceViewerDecorationSupport fSourceViewerDecorationSupport;

    /**
     * The Constant SPACE_BETWEEN_RULER.
     */
    public static final int SPACE_BETWEEN_RULER = 1;

    /**
     * The Constant SERVER_BREAKPOINT_LINE_OFFSET.
     */
    public static final int SERVER_BREAKPOINT_LINE_OFFSET = 1;

    private SQLSourceViewerConfig config;

    private AnnotationModel fAnnotationModel;

    /**
     * The exec progres bar.
     */
    TerminalStatusBar execProgresBar;

    private Menu menu;

    private MenuItem menuCut;

    private MenuItem menuPaste;

    private MenuItem menuSQLCmd;

    private String sqlCmdMenuKey;

    private String sqlCmdMenuIcon;

    private FindAndReplaceOptions findAndReplaceoptions;

    private int lastSearchReturnIndex;

    private boolean atleastOneMatchFound;

    private String prevText = "";

    private FindAndReplaceOptions options;

    private int counterForTxtInEditor = 0;

    private MenuItem toggleLineComments;

    private MenuItem toggleBlockComments;

    private MenuItem menuFormat;

    /**
     * The Constant SL_COMMENT.
     */
    public static final String SL_COMMENT = "--";

    /**
     * The Constant INDENT_OPERATION.
     */
    public static final String INDENT_OPERATION = "indent";

    /**
     * The Constant UNINDENT_OPERATION.
     */
    public static final String UNINDENT_OPERATION = "unindent";

    private PreferenceStore preferenceStore;

    private boolean disableCommentIndentUndo;

    private SQLSyntax syntax;

    private Font font;

    private ResourceManager resManager;

    /**
     * Title: enum Description: The Enum EDITORACTIONKEY.
     */
    private enum EDITORACTIONKEY {
        NOKEY, AUTO_SUGGEST, CODE_TEMPLATE, KEY_AUTO_SUGGEST, INSERT_AUTO_SUGGEST
    };

    private int highlightLineNum = -1;

    /**
     * Instantiates a new PL source editor core.
     *
     * @param fAnnotationModel the f annotation model
     * @param fAnnotationAccess the f annotation access
     */

    /**
     * Default constructor will create the annotation model which doesn't need
     * any component creation.
     */
    public PLSourceEditorCore(AnnotationModel fAnnotationModel, IAnnotationAccess fAnnotationAccess) {
        this.fAnnotationModel = fAnnotationModel;
        this.fAnnotationAccess = fAnnotationAccess;
        preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        preferenceStore.addPropertyChangeListener(this);
        jobsAssociatedToTerminal = new ArrayList<UIWorkerJob>(5);
    }

    /**
     * Set highlight line num
     *
     * @param lineNum the highlight line num
     */
    public void setHighlightLineNum (int lineNum) {
        this.highlightLineNum = lineNum;
    }

    /**
     * Get highlight line num
     *
     * @return int the highlight line num
     */
    public int getHighlightLineNum () {
        return this.highlightLineNum;
    }

    /**
     * Creates the editor.
     *
     * @param parent the parent
     * @param compositeRuler the composite ruler
     * @param ruler the ruler
     * @param syntx the syntx
     * @param isSQLTerminal the is SQL terminal
     */
    public void createEditor(Composite parent, CompositeRuler compositeRuler, OverviewRuler ruler, SQLSyntax syntx,
            boolean isSQLTerminal) {
        Composite viewerComposite = new Composite(parent, SWT.NONE);
        viewerComposite.setLayout(new GridLayout(1, false));

        // check is folding enabled

        boolean isFoldingEnabled = PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean("sqlterminal.folding");

        if (isFoldingEnabled) {
            ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();
            IOverviewRuler overviewRuler = new OverviewRuler(fAnnotationAccess, 12, sharedColors);
            viewer = new ProjectionViewer(viewerComposite, compositeRuler, overviewRuler, true,
                    SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        } else {
            viewer = new SourceViewer(viewerComposite, compositeRuler, ruler, ruler != null,
                    SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

        }

        viewer.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_SQLTERMINAL_TEXT_001");
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        setDocument(new Document(""), 0);

        if (isFoldingEnabled && viewer instanceof ProjectionViewer) {
            ProjectionSupport projectionSupport = new ProjectionSupport((ProjectionViewer) viewer, fAnnotationAccess,
                    getSharedColors());
            projectionSupport.install();

            // turn projection mode on
            viewer.doOperation(ProjectionViewer.TOGGLE);
        }

        resManager = new LocalResourceManager(JFaceResources.getResources(), viewerComposite);
        font = resManager.createFont(FontDescriptor.createFrom("Courier New",
                preferenceStore.getInt(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE), SWT.NORMAL));

        viewer.getTextWidget().setFont(font);

        setDecoration();

        menu = new Menu(getControl());
        viewer.getTextWidget().setMenu(menu);
        addCutMenuItem(menu);
        addCopyMenuItem(menu);
        addPasteMenuItem(menu);
        addSelectAllMenuItem(menu);
        addToggleLineCommentMenuItem(menu);
        addToggleBlockCommentMenuItem(menu);
        addFormatMenuItem(menu);
        addExecStmtMenuItem(menu);

        this.config = new SQLSourceViewerConfig(syntx);
        setTabStrategy();

        viewer.configure(this.config);

        this.execProgresBar = createStatusBar(viewerComposite, isSQLTerminal);

        addListners();
    }

    /**
     * Gets the shared colors.
     *
     * @return the shared colors
     */
    protected ISharedTextColors getSharedColors() {
        return EditorsPlugin.getDefault().getSharedTextColors();
    }

    /**
     * Creates the status bar.
     *
     * @param gridComposite the grid composite
     * @param isTerminal the is terminal
     * @return the terminal status bar
     */
    private TerminalStatusBar createStatusBar(Composite gridComposite, boolean isTerminal) {
        TerminalStatusBar statusBar = null;
        TerminalStatusBar prevStatusBar = UIDisplayState.getInstaDisplayState().getPrevStatusBar();
        boolean useOldTimerObject = UIDisplayState.getInstaDisplayState().isDebugInProgresOnNewEditor()
                && prevStatusBar != null;
        if (useOldTimerObject) {
            statusBar = new TerminalStatusBar(prevStatusBar);
        } else {
            statusBar = new TerminalStatusBar();
        }
        statusBar.setHandlerServices(commandService, handlerService);
        statusBar.createStatusGrid(gridComposite, isTerminal);
        if (useOldTimerObject) {
            statusBar.justShowBar();
            UIDisplayState.getInstaDisplayState().setDebugInProgresNewEd(false);
        }
        return statusBar;
    }

    /**
     * Gets the exec status bar.
     *
     * @return the exec status bar
     */
    public TerminalStatusBar getExecStatusBar() {
        return this.execProgresBar;
    }

    /**
     * Show exec progress bar.
     */
    public void showExecProgressBar() {
        this.execProgresBar.showProgresBar();
    }

    /**
     * Hide exec progress bar.
     */
    public void hideExecProgressBar() {
        if (null != this.execProgresBar) {
            this.execProgresBar.hideProgresBar();
        }
    }

    /**
     * Sets the tab strategy.
     */
    private void setTabStrategy() {
        String[] types = config.getConfiguredContentTypes(viewer);
        config.setUpdatedPrefixes(viewer);
        configurePrefixesForEachContentType(types);
    }

    /**
     * Update SQL configurations on DB connect.
     *
     * @param db the db
     */
    public void updateSQLConfigurationsOnDBConnect(Database db) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                SQLSyntax sytx = null;
                if (db != null && db.isConnected()) {
                    sytx = db.getSqlSyntax();
                    if (viewer != null) {
                        SQLSourceViewerConfig configuration = reconfigureSourceViewerConfig(sytx);
                        configuration.setDatabase(db);
                    }
                } else {
                    reconfigureSourceViewerConfig(sytx).setDatabase(db);
                }
            }
        });
    }

    /**
     * Reconfigure source viewer config.
     *
     * @param sytx the sytx
     * @return the SQL source viewer config
     */
    private SQLSourceViewerConfig reconfigureSourceViewerConfig(SQLSyntax sytx) {
        this.viewer.unconfigure();
        SQLSourceViewerConfig configuration = new SQLSourceViewerConfig(sytx);
        this.viewer.configure(configuration);
        return configuration;
    }

    /**
     * Sets the command and handler service.
     *
     * @param commandServce the command servce
     * @param handlerServce the handler servce
     * @param selectionServce the selection servce
     */
    public void setCommandAndHandlerService(ECommandService commandServce, EHandlerService handlerServce,
            ESelectionService selectionServce) {
        this.commandService = commandServce;
        this.handlerService = handlerServce;
        this.selectionService = selectionServce;
    }

    /**
     * Gets the menu.
     *
     * @return the menu
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Sets the sql cmd menu key.
     *
     * @param sqlCmdMenuKey the new sql cmd menu key
     */
    public void setSqlCmdMenuKey(String sqlCmdMenuKey) {
        this.sqlCmdMenuKey = sqlCmdMenuKey;
    }

    /**
     * Sets the sql cmd menu icon.
     *
     * @param sqlCmdMenuIcon the new sql cmd menu icon
     */
    public void setSqlCmdMenuIcon(String sqlCmdMenuIcon) {
        this.sqlCmdMenuIcon = sqlCmdMenuIcon;
    }

    /**
     * Gets the find and replaceoptions.
     *
     * @return the find and replaceoptions
     */
    public FindAndReplaceOptions getFindAndReplaceoptions() {
        return findAndReplaceoptions;
    }

    /**
     * Sets the find and replaceoptions.
     *
     * @param optins the new find and replaceoptions
     */
    public void setFindAndReplaceoptions(FindAndReplaceOptions optins) {
        this.findAndReplaceoptions = optins;
    }

    /**
     * Gets the control.
     *
     * @return the control
     */
    public Control getControl() {
        Control srcContrl = viewer.getControl();
        if (srcContrl instanceof Composite) {
            Composite cmpst = (Composite) srcContrl;
            Control[] childControls = cmpst.getChildren();
            Control childControl = null;
            for (int count = 0; count < childControls.length; count++) {
                childControl = childControls[count];
                if (childControl instanceof StyledText) {
                    srcContrl = childControl;
                    break;
                }
            }
        }

        return srcContrl;
    }

    /**
     * Create decoration (line highlighting, breakpoint line hightlighting etc).
     */
    private void setDecoration() {

        ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();
        fSourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(viewer, null, fAnnotationAccess,
                sharedColors);
        fSourceViewerDecorationSupport.installDecorations();

    }

    /**
     * Gets the f source viewer decoration support.
     *
     * @return the f source viewer decoration support
     */
    public SourceViewerDecorationSupport getfSourceViewerDecorationSupport() {
        return fSourceViewerDecorationSupport;
    }

    /**
     * Install decoration support.
     */
    public void installDecorationSupport() {

        fSourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);
    }

    /**
     * Sets the document.
     *
     * @param document the new document
     * @param fileSizeInMB the file size in MB
     */
    public void setDocument(final IDocument document, double fileSizeInMB) {
        if (null != this.doc) {
            fAnnotationModel.removeAllAnnotations();
        }

        if (viewer.getDocument() != null) {
            IDocumentPartitioner docPartitioner = ((IDocumentExtension3) viewer.getDocument())
                    .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING);
            if (docPartitioner != null) {
                docPartitioner.disconnect();
            }
        }
        this.doc = document;
        viewer.setDocument(document, fAnnotationModel);
        SQLDocumentPartitioner.connectDocument(document, fileSizeInMB);
        MPPDBIDELoggerUtility.debug("GUI: PLSourceEditorCore: Display source.");
    }

    /**
     * Load SQL data.
     *
     * @param newData the new data
     * @param caretOffset the caret offset
     * @param replaceoffSet the replaceoff set
     */
    public void loadSQLData(String newData, int caretOffset, int replaceoffSet, double fileSizeInMB) {
        // Shift caret before start of the loaded data
        fAnnotationModel.removeAllAnnotations();
        if (!(viewer.getDocument() instanceof IDocumentExtension3)) {
            return;
        }
        IDocumentPartitioner documentPartitioner = ((IDocumentExtension3) viewer.getDocument())
                .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING);
        if (null != documentPartitioner) {
            documentPartitioner.disconnect();
        }
        try {

            viewer.getDocument().replace(replaceoffSet, 0, newData);

        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("Error while loading SQL data", exception);
        }

        viewer.getTextWidget().setCaretOffset(caretOffset);
        viewer.revealRange(caretOffset, 1);

        SQLDocumentPartitioner.connectDocument(viewer.getDocument(), fileSizeInMB);
    }

    /**
     * Destroy document.
     */
    public void destroyDocument() {
        this.doc = null;
    }

    /**
     * Sets the editable.
     *
     * @param isEditable the new editable
     */
    public void setEditable(final boolean isEditable) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                viewer.setEditable(isEditable);
            }
        });
    }

    /**
     * Goto line.
     *
     * @param line the line
     * @param db the db
     */
    public void gotoLine(int line, Database db) {
        if (null == doc) {
            return;
        }

        int lineOffset = line - SERVER_BREAKPOINT_LINE_OFFSET;
        try {
            int start = doc.getLineOffset(lineOffset);
            int length = doc.getLineLength(lineOffset);
            viewer.getTextWidget().setSelection(start);
            viewer.revealRange(start, length);
        } catch (BadLocationException e) {
            UIDisplayStateIf displayState = UIDisplayFactoryProvider.getUIDisplayStateIf();
        }
    }

    /**
     * Go to line number.
     *
     * @param line the line
     */
    public void goToLineNumber(int line) {
        if (null == doc) {
            return;
        }

        int lineOffset = line - MPPDBIDEConstants.LINE_NUMBER_OFFSET;
        try {
            int start = doc.getLineOffset(lineOffset);
            int length = doc.getLineLength(lineOffset);
            viewer.revealRange(start, length);
            if (viewer instanceof ProjectionViewer) {
                ProjectionViewer projectionViewer = (ProjectionViewer)viewer;
                projectionViewer.getProjectionAnnotationModel().expandAll(start, length);
            }
            viewer.setSelectedRange(start, 0);

        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("Error getting Line number", exception);

        }

    }

    /**
     * Gets the source viewer.
     *
     * @return the source viewer
     */
    public SourceViewer getSourceViewer() {
        return viewer;
    }

    /**
     * Gets the editor action keys.
     *
     * @param event the event
     * @return the editor action keys
     */
    private EDITORACTIONKEY getEditorActionKeys(KeyEvent event) {
        try {
            KeyStroke actualKeyStroke = SWTKeySupport
                    .convertAcceleratorToKeyStroke(SWTKeySupport.convertEventToUnmodifiedAccelerator(event));

            if (KeyStroke.getInstance(KeyBindingWrapper.getInstance().getAutoSugestKey()).equals(actualKeyStroke)) {
                return EDITORACTIONKEY.AUTO_SUGGEST;
            } else if (KeyStroke.getInstance(KeyBindingWrapper.getInstance().getCodeTemplateKey())
                    .equals(actualKeyStroke)) {
                return EDITORACTIONKEY.CODE_TEMPLATE;
            } else if (KeyStroke.getInstance("SHIFT+9").equals(actualKeyStroke)
                    || KeyStroke.getInstance(",").equals(actualKeyStroke)) {
                return EDITORACTIONKEY.INSERT_AUTO_SUGGEST;
            } else if (!(actualKeyStroke.toString().contains("CTRL") || actualKeyStroke.toString().contains("ALT"))) {

                return EDITORACTIONKEY.KEY_AUTO_SUGGEST;
            }
        } catch (ParseException exception) {
            MPPDBIDELoggerUtility.error("Parse exception while getting key stroke", exception);
        }

        return EDITORACTIONKEY.NOKEY;
    }

    /**
     * Add listener to the current source editor control.
     */
    private void addListners() {
        /*
         * Adding a selection change listener and postSelection change listener
         * for mars eclipse environment when the UPPERCASE and LOWERCASE and
         * other commands and their handler could be executed
         */
        viewer.addPostSelectionChangedListener(new PostEditorSelectionChangedListener());
        viewer.addSelectionChangedListener(new EditorSelectionChangedListener());
        viewer.appendVerifyKeyListener(appendViewerVerifyKeyListener());
        viewer.getTextWidget().addKeyListener(addViewerKeyListener());
        viewer.getTextWidget().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                cancelHighlight();
            }
        });
        viewer.getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
            @Override
            public void verifyKey(VerifyEvent event) {

                if (event.keyCode != SWT.TAB) {

                    return;
                }

                if (event.stateMask == SWT.SHIFT) {
                    performIndentUnindentOperation(UNINDENT_OPERATION, event);
                } else {
                    performIndentUnindentOperation(INDENT_OPERATION, event);
                }

            }

        });

        viewer.addTextListener(new ITextListener() {
            @Override
            public void textChanged(TextEvent event) {
                if (!disableCommentIndentUndo) {
                    DBAssistantWindow.toggleCurrentAssitant(false);
                }
            }
        });
    }

    private void cancelHighlight () {
        int lineNum = getHighlightLineNum();
        if (lineNum != -1) {
            DebugServiceHelper.getInstance().notifyCancelHighlight(lineNum);
        }
    }

    private KeyListener addViewerKeyListener() {
        return new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent event) {
                viewer.setData("ISALIASCHECK", false);

                EDITORACTIONKEY action = getEditorActionKeys(event);

                if (event.character == '(' || event.character == ',') {
                    if (viewer.canDoOperation(SourceViewer.CONTENTASSIST_PROPOSALS)) {
                        viewer.doOperation(SourceViewer.CONTENTASSIST_PROPOSALS);
                    }
                    return;
                }

                if ((EDITORACTIONKEY.KEY_AUTO_SUGGEST == action) && (event.keyCode > 32 && event.keyCode <= 126)
                        || event.keyCode == 0) {
                    Widget widget = event.widget;
                    StyledText text = (StyledText) widget;
                    String prefix = getPrefixForAutoSuggest(text);

                    if (prefix != null && prefix.length() >= PreferenceWrapper.getInstance().getPreferenceStore()
                            .getInt(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY)) {
                        if (event.keyCode == 46) {
                            viewer.setData("ISALIASCHECK", true);
                        }
                        if (viewer.canDoOperation(SourceViewer.CONTENTASSIST_PROPOSALS)) {
                            viewer.doOperation(SourceViewer.CONTENTASSIST_PROPOSALS);

                            return;
                        }
                    }
                }

            }

        };
    }

    private VerifyKeyListener appendViewerVerifyKeyListener() {
        return new VerifyKeyListener() {
            @Override
            public void verifyKey(VerifyEvent event) {
                viewer.setData("ISALIASCHECK", false);
                EDITORACTIONKEY action = getEditorActionKeys(event);

                if (EDITORACTIONKEY.CODE_TEMPLATE == action) {
                    if (viewer.canDoOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION)) {
                        viewer.doOperation(SourceViewer.CONTENTASSIST_CONTEXT_INFORMATION);
                    }
                    event.doit = false;
                    return;
                }

                if (EDITORACTIONKEY.AUTO_SUGGEST == action) {
                    if (viewer.canDoOperation(SourceViewer.CONTENTASSIST_PROPOSALS)) {
                        viewer.setData("ISALIASCHECK", true);
                        viewer.doOperation(SourceViewer.CONTENTASSIST_PROPOSALS);
                    }
                    event.doit = false;
                    return;
                }

            }
        };
    }

    private String getPrefixForAutoSuggest(StyledText text) {
        boolean isContinue = true;
        int offset = text.getCaretOffset() == 0 ? 0 : text.getCaretOffset() - 1;
        char ch = '\0';
        while (isContinue && offset > 0) {
            try {

                ch = viewer.getDocument().getChar(offset);

                if (Character.isWhitespace(ch)) {
                    break;
                }
                offset--;
            } catch (BadLocationException exception) {
                MPPDBIDELoggerUtility.error("Error while getting prefix for auto suggest", exception);
            }
        }

        String prefix = null;
        try {
            if (text.getCaretOffset() > 0) {
                prefix = viewer.getDocument().get(offset, text.getCaretOffset() - offset);
                prefix = prefix.trim();
            }
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("Error while getting prefix for auto suggest", exception);
        }
        return prefix;
    }

    /**
     * Enable toggle current assist.
     */
    private void enableToggleCurrentAssist() {
        disableCommentIndentUndo = false;
    }

    /**
     * Disable toggle current assist.
     */
    private void disableToggleCurrentAssist() {
        disableCommentIndentUndo = true;
    }

    /**
     * Perform indent unindent operation.
     *
     * @param operation the operation
     * @param event the event
     */
    public void performIndentUnindentOperation(String operation, VerifyEvent event) {
        try {
            disableToggleCurrentAssist();
            IDocumentExtension2 extension = (IDocumentExtension2) doc;
            extension.ignorePostNotificationReplaces();
            if (event != null) {
                handleVerifyEvent(operation, event);
            } else {
                handleNullEvent(operation);
            }

            extension.acceptPostNotificationReplaces();
        } catch (OutOfMemoryError e) {
            displayErrorDialog();
        } finally {
            enableToggleCurrentAssist();
        }
    }

    private void handleNullEvent(String operation) {
        if (operation.equals(INDENT_OPERATION)) {
            ITextSelection textSelection = (ITextSelection) viewer.getSelection();
            if (textSelection.getLength() > 0) {
                performShiftOperation();
            }
        } else if (operation.equals(UNINDENT_OPERATION)) {
            performShiftLeft();
        }
    }

    private void handleVerifyEvent(String operation, VerifyEvent event) {
        if (operation.equals(INDENT_OPERATION)) {
            ITextSelection textSelection = (ITextSelection) viewer.getSelection();
            if (textSelection.getLength() > 0
                    && (textSelection.getText() != null && textSelection.getText().contains("\n"))) {
                performShiftOperation();
                event.doit = false;
            } else if (DSFormatterPreferencePage.isTabToSpaceEnabled()) {
                insertSpacesOnIndentForSingleLine();
                event.doit = false;
            }
        } else if (operation.equals(UNINDENT_OPERATION)) {
            performShiftLeft();
            event.doit = false;
        }
    }

    /**
     * Gets the selected line count.
     *
     * @return the selected line count
     */
    public boolean getSelectedLineCount() {
        ITextSelection selection = (ITextSelection) viewer.getSelection();
        String text = selection.getText();
        /*
         * Issue: Unable to perform Indent/Unindent for the single line selected
         * Query Code change:Condition now checks for single line also. if
         * nothing is selected,buttons are not enabled
         */

        if ((selection.getEndLine() - selection.getStartLine() >= 0) && (text != null && !text.isEmpty())) {
            return true;
        } else {
            if (text != null && text.endsWith("\n")) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Perform shift operation.
     */
    public void performShiftOperation() {
        reconcilerUninstall();
        viewer.doOperation(ITextOperationTarget.SHIFT_RIGHT);
        reconcilerInstall();
    }

    /**
     * Insert spaces on indent for single line.
     */
    private void insertSpacesOnIndentForSingleLine() {
        ITextSelection selection = (ITextSelection) viewer.getSelection();
        try {
            String spacesString = DSFormatterPreferencePage.getStringWithSpaces();
            doc.replace(selection.getOffset(), selection.getLength(), spacesString);
            viewer.getTextWidget().setCaretOffset(selection.getOffset() + spacesString.length());
            viewer.revealRange(viewer.getTextWidget().getCaretOffset(), 1);
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", exception);
        }
    }

    /**
     * The listener interface for receiving editorSelectionChanged events. The
     * class that is interested in processing a editorSelectionChanged event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addEditorSelectionChangedListener<code> method. When the
     * editorSelectionChanged event occurs, that object's appropriate method is
     * invoked. EditorSelectionChangedEvent
     */
    private class EditorSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            selectionService.setSelection(selection);

            String selectionStr = ((TextSelection) selection).getText();
            if ((null != selectionStr) && !"".equals(selectionStr.trim())) {
                DBAssistantWindow.execSQL(selectionStr);
            }
        }
    }

    /**
     * The listener interface for receiving postEditorSelectionChanged events.
     * The class that is interested in processing a postEditorSelectionChanged
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addPostEditorSelectionChangedListener<code> method. When the
     * postEditorSelectionChanged event occurs, that object's appropriate method
     * is invoked. PostEditorSelectionChangedEvent
     */
    private class PostEditorSelectionChangedListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            ISelection selection = event.getSelection();
            selectionService.setSelection(selection);
        }
    }

    /**
     * Gets the document.
     *
     * @return the document
     */
    public IDocument getDocument() {
        return doc;
    }

    /**
     * Gets the source viewer line of offset.
     *
     * @param offset the offset
     * @return the source viewer line of offset
     * @throws BadLocationException the bad location exception
     */
    public int getsourceViewerLineOfOffset(int offset) throws BadLocationException {
        return viewer.getDocument().getLineOfOffset(offset);
    }

    /**
     * Gets the selection count.
     *
     * @return the selection count
     */
    public int getSelectionCount() {
        return viewer.getTextWidget().getSelectionCount();
    }

    /**
     * Lock source viewer redraw.
     */
    public void lockSourceViewerRedraw() {
        this.viewer.setRedraw(false);
    }

    /**
     * Unlock source viewer redraw.
     */
    public void unlockSourceViewerRedraw() {
        this.viewer.setRedraw(true);
    }

    /**
     * Select all doc text.
     */
    public void selectAllDocText() {
        viewer.doOperation(ITextOperationTarget.SELECT_ALL);
    }

    /**
     * Cut selected doc text.
     */
    public void cutSelectedDocText() {
        viewer.doOperation(ITextOperationTarget.CUT);
    }

    /**
     * Copy doc text.
     */
    public void copyDocText() {
        try {
            LineBackgroundListener lineBackgroundListener = getLineBackgroundColorListener();
            /*
             * To avoid copying of Blue background color override it with
             * background color listener
             */
            StyledText styledText = (StyledText) viewer.getTextWidget();
            styledText.addLineBackgroundListener(lineBackgroundListener);
            viewer.doOperation(ITextOperationTarget.COPY);
            // Remove background color listener so UI screen will get blue
            // background selection.
            styledText.removeLineBackgroundListener(lineBackgroundListener);
        } catch (OutOfMemoryError e) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY), e.getMessage());
        }

    }

    /**
     * Gets the line background color listener.
     *
     * @return the line background color listener
     */
    public static LineBackgroundListener getLineBackgroundColorListener() {
        return new LineBackgroundListener() {
            @Override
            public void lineGetBackground(LineBackgroundEvent event) {
                event.lineBackground = SQLSyntaxColorProvider.BACKGROUND_COLOR;
            }
        };
    }

    /**
     * Paste selected doc text.
     */
    public void pasteSelectedDocText() {
        viewer.doOperation(ITextOperationTarget.PASTE);
    }

    /**
     * Can undo.
     *
     * @return true, if successful
     */
    public boolean canUndo() {
        return viewer.canDoOperation(ITextOperationTarget.UNDO);
    }

    /**
     * Can redo.
     *
     * @return true, if successful
     */
    public boolean canRedo() {
        return viewer.canDoOperation(ITextOperationTarget.REDO);
    }

    /**
     * Undo.
     */
    public void undo() {

        reconcilerUninstall();

        disableToggleCurrentAssist();
        viewer.doOperation(ITextOperationTarget.UNDO);
        postToggleLineComment();

    }

    /**
     * Redo.
     */
    public void redo() {

        reconcilerUninstall();

        disableToggleCurrentAssist();
        viewer.doOperation(ITextOperationTarget.REDO);
        postToggleLineComment();

    }

    private void reconcilerUninstall() {

        IReconciler reconciler = getReconciler();

        if (null != reconciler) {
            reconciler.uninstall();
        }

    }

    private void reconcilerInstall() {

        IReconciler reconciler = getReconciler();

        if (null != reconciler) {
            reconciler.install(viewer);
        }

    }

    private IReconciler getReconciler() {
        Object sqlStrategy = viewer.getTextWidget().getData(SQLFoldingConstants.MONORECONCILER);

        IReconciler reconciler = null;
        if (sqlStrategy instanceof IReconciler) {
            reconciler = (IReconciler) sqlStrategy;
        }
        return reconciler;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(Database database) {
        if (null != database) {
            this.config.setDatabase(database);
        }
    }

    /**
     * Adds the cut menu item.
     *
     * @param menuItem the menu item
     */
    private void addCutMenuItem(Menu menuItem) {
        menuCut = new MenuItem(menuItem, SWT.PUSH);
        menuCut.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_CUT));
        menuCut.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                cutSelectedDocText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        menuCut.setImage(IconUtility.getIconImage(IiconPath.ICO_CUT, this.getClass()));
    }

    /**
     * Adds the paste menu item.
     *
     * @param menuItem the menu item
     */
    private void addPasteMenuItem(Menu menuItem) {
        menuPaste = new MenuItem(menuItem, SWT.PUSH);
        menuPaste.setText(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_PLVIEWER_OPTION_PASTE));
        menuPaste.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                pasteSelectedDocText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        menuPaste.setImage(IconUtility.getIconImage(IiconPath.ICO_PASTE, this.getClass()));
    }

    /**
     * Adds the exec stmt menu item.
     *
     * @param menuItem the menu item
     */
    private void addExecStmtMenuItem(Menu menuItem) {
        menuSQLCmd = new MenuItem(menuItem, SWT.PUSH);

        menuSQLCmd.setText(MessageConfigLoader.getProperty(this.sqlCmdMenuKey));

        menuSQLCmd.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Command command = commandService
                        .getCommand("com.huawei.mppdbide.command.id.executeobjectbrowseritemfromtoolbar");
                ParameterizedCommand parameterizedCommand = ParameterizedCommand.generateCommand(command, null);
                handlerService.executeHandler(parameterizedCommand);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        menuSQLCmd.setImage(IconUtility.getIconImage(sqlCmdMenuIcon, this.getClass()));

    }

    /**
     * Adds the toggle line comment menu item.
     *
     * @param menuItem the menu item
     */
    private void addToggleLineCommentMenuItem(Menu menuItem) {
        toggleLineComments = new MenuItem(menuItem, SWT.PUSH);
        toggleLineComments.setText(MessageConfigLoader
                .getProperty(IMessagesConstants.PREFERENCE_SHORTCUT_KEY_BINDING_TOGGLE_LINE_COMMENTS));
        toggleLineComments.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Command cmd = commandService.getCommand("com.huawei.mppdbide.view.command.ToggleLineComment");
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                handlerService.executeHandler(parameterizedCmd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        toggleLineComments.setImage(IconUtility.getIconImage(IiconPath.ICON_TOGGLE_LINE_COMMENTS, this.getClass()));
    }

    /**
     * Adds the toggle block comment menu item.
     *
     * @param menuItem the menu item
     */
    private void addToggleBlockCommentMenuItem(Menu menuItem) {
        toggleBlockComments = new MenuItem(menuItem, SWT.PUSH);
        toggleBlockComments.setText(MessageConfigLoader
                .getProperty(IMessagesConstants.PREFERENCE_SHORTCUT_KEY_BINDING_TOGGLE_BLOCK_COMMENTS));
        toggleBlockComments.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                Command cmd = commandService.getCommand("com.huawei.mppdbide.view.command.ToggleBlockComment");
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                handlerService.executeHandler(parameterizedCmd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        toggleBlockComments.setImage(IconUtility.getIconImage(IiconPath.ICON_TOGGLE_BLOCK_COMMENTS, this.getClass()));
    }

    /**
     * Adds the format menu item.
     *
     * @param menu the menu
     */
    private void addFormatMenuItem(Menu menu) {
        menuFormat = new MenuItem(menu, SWT.PUSH);
        menuFormat
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_TERMINAL_RIGHT_CLICK_MENU_ITEM_FORMAT));
        menuFormat.setImage(
                IconUtility.getIconImage(IiconPath.ICON_SQL_TERMINAL_RIGHT_CLICK_MENU_FORMAT, this.getClass()));
        menuFormat.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Command cmd = commandService.getCommand(FORMAT_COMMAND_ID);
                ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                handlerService.executeHandler(parameterizedCmd);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * Toggle cut copy select all.
     *
     * @param toggelCut the toggel cut
     * @param toggelCopy the toggel copy
     * @param toggelSelectAll the toggel select all
     * @param toggleCompile the toggle compile
     * @param togglePaste the toggle paste
     */
    public void toggleCutCopySelectAll(boolean toggelCut, boolean toggelCopy, boolean toggelSelectAll,
            boolean toggleCompile, boolean togglePaste) {
        menuCut.setEnabled(toggelCut);
        menuPaste.setEnabled(togglePaste);
        getMenuCopy().setEnabled(toggelCopy);
        getMenuSelectAll().setEnabled(toggelSelectAll);
        menuSQLCmd.setEnabled(toggleCompile);
        menuFormatEnableDisable();
    }

    private void menuFormatEnableDisable() {
        ParameterizedCommand parameterizedCmd = new ParameterizedCommand(commandService.getCommand(FORMAT_COMMAND_ID),
                null);
        boolean canFormat = handlerService.canExecute(parameterizedCmd);
        menuFormat.setEnabled(canFormat);
    }

    /**
     * Gets the menu SQL cmd.
     *
     * @return the menu SQL cmd
     */
    public MenuItem getMenuSQLCmd() {
        return menuSQLCmd;
    }

    /**
     * Sets the menu SQL cmd.
     *
     * @param menuSQLCmd the new menu SQL cmd
     */
    public void setMenuSQLCmd(MenuItem menuSQLCmd) {
        this.menuSQLCmd = menuSQLCmd;
    }

    /**
     * Sets the selected query.
     *
     * @param query the new selected query
     */
    public void setSelectedQuery(String query) {
        int offset = viewer.getTextWidget().getCaretOffset();

        StringBuilder txt = new StringBuilder(viewer.getTextWidget().getText());

        txt.insert(offset, query);

        viewer.getDocument().set(txt.toString());
        viewer.getTextWidget().setCaretOffset(offset + query.length());
    }

    /**
     * Find text.
     *
     * @param isReplace the is replace
     */
    public void findText(boolean isReplace) {

        lastSearchReturnIndex = 0;
        options = getFindAndReplaceoptions();
        IFindReplaceTarget findReplaceTarget = viewer.getFindReplaceTarget();

        if ((!options.isCaseSensitive() && !prevText.equalsIgnoreCase(options.getSearchText()))
                || (options.isCaseSensitive() && !prevText.equals(options.getSearchText()))) {
            atleastOneMatchFound = false;
            prevText = options.getSearchText();
            if (counterForTxtInEditor > 0) {
                counterForTxtInEditor--;
            }

        }

        int searchStartPosition = getSearchStartPosition(options.isForwardSearch());

        lastSearchReturnIndex = findReplaceTarget.findAndSelect(searchStartPosition, options.getSearchText(),
                options.isForwardSearch(), options.isCaseSensitive(), options.isWholeWord());

        if (lastSearchReturnIndex > -1 && null != options.getReplaceText() && isReplace) {
            findReplaceTarget.replaceSelection(options.getReplaceText());
        }

        findTextPart1(searchStartPosition);

        findTextPart2(findReplaceTarget, isReplace);
    }

    /**
     * Find text part 1.
     *
     * @param searchStartPosition the search start position
     */
    private void findTextPart1(int searchStartPosition) {
        if ((lastSearchReturnIndex > -1) && !atleastOneMatchFound) {
            counterForTxtInEditor++;
            atleastOneMatchFound = true;
        }
        if (options.isWholeWord() && lastSearchReturnIndex == -1 && !options.isWrapAround()) {

            if ((prevText.equals(options.getSearchText()) && counterForTxtInEditor > 0) || searchStartPosition == 0) {
                atleastOneMatchFound = true;

            } else {
                atleastOneMatchFound = false;
            }
        }
    }

    /**
     * Find text part 2.
     *
     * @param findReplaceTarget the find replace target
     * @param isReplace the is replace
     */
    private void findTextPart2(IFindReplaceTarget findReplaceTarget, boolean isReplace) {
        if (isWholeWordMatch()) {
            handleWholeWordMatch(findReplaceTarget, isReplace);
        } else if (isAlteastOneWordMatch()) {
            handleAtleastOneWordMatch(findReplaceTarget, isReplace);
        } else {
            searchTextNotFoundDialog();
        }
    }

    private void handleAtleastOneWordMatch(IFindReplaceTarget findReplaceTarget, boolean isReplace) {
        int searchStartPosition = 0;
        if (-1 == lastSearchReturnIndex && options.isWrapAround()) {
            searchStartPosition = getSearchStartPosition(options.isForwardSearch());
            lastSearchReturnIndex = findReplaceTarget.findAndSelect(searchStartPosition, options.getSearchText(),
                    options.isForwardSearch(), options.isCaseSensitive(), options.isWholeWord());
            if (lastSearchReturnIndex > -1 && null != options.getReplaceText() && isReplace) {
                findReplaceTarget.replaceSelection(options.getReplaceText());
            }
        }
        if (-1 == lastSearchReturnIndex) {
            searchTextNotFoundDialog();
        }
    }

    private boolean isAlteastOneWordMatch() {
        return atleastOneMatchFound || (options.isForwardSearch() && options.isWrapAround())
                || (options.isBackwardSearch() && options.isWrapAround());
    }

    private boolean isWholeWordMatch() {
        return options.isWholeWord() && lastSearchReturnIndex == -1 && options.isWrapAround();
    }

    private void handleWholeWordMatch(IFindReplaceTarget findReplaceTarget, boolean isReplace) {
        int searchStartPosition = 0;
        searchStartPosition = getSearchStartPosition(options.isForwardSearch());
        lastSearchReturnIndex = findReplaceTarget.findAndSelect(searchStartPosition, options.getSearchText(),
                options.isForwardSearch(), options.isCaseSensitive(), options.isWholeWord());
        if (lastSearchReturnIndex > -1 && null != options.getReplaceText() && isReplace) {
            findReplaceTarget.replaceSelection(options.getReplaceText());
        }
        if (lastSearchReturnIndex == -1) {
            searchTextNotFoundDialog();
        }
    }

    /**
     * Search text not found dialog.
     */
    public void searchTextNotFoundDialog() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_SEARCH_NOT_FOUND),
                MessageConfigLoader.getProperty(IMessagesConstants.FIND_REPLACE_SEARCH_NOT_FOUND) + '!');
        atleastOneMatchFound = false;
        lastSearchReturnIndex = 0;
    }

    /**
     * Change case.
     *
     * @param strChangeType the str change type
     */
    public void changeCase(String strChangeType) {
        try {
            ITextSelection textSel = (ITextSelection) viewer.getSelectionProvider().getSelection();
            removeAnnotation(textSel);
            IFindReplaceTarget findReplaceTarget = viewer.getFindReplaceTarget();
            ITextSelection selection = (ITextSelection) viewer.getSelection();
            String strText = getText(textSel);
            int searchStartPosition = selection.getOffset();
            int leadingWhiteSpaceCount = strText.indexOf(strText.trim());
            int selected = findReplaceTarget.findAndSelect(searchStartPosition + leadingWhiteSpaceCount, strText.trim(),
                    true, false, false);
            disableToggleCurrentAssist();
            if (selected <= -1) {
                return;
            }
            validateStringInComment(strChangeType, findReplaceTarget, strText);
        } catch (OutOfMemoryError e) {
            displayErrorDialog();
        } finally {
            enableToggleCurrentAssist();
        }
    }

    /**
     * Validate string in comment.
     *
     * @param strChangeType the str change type
     * @param findReplaceTarget the find replace target
     * @param strText the str text
     */
    private void validateStringInComment(String strChangeType, IFindReplaceTarget findReplaceTarget, String strText) {
        StringBuilder sbConverted = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        boolean isSingleQuote = false;
        boolean isDoubleQuote = false;
        boolean isMultiLineComment = false;
        boolean isSingleLineComment = false;
        int multiLineCommentDepth = 0;
        int charPosition = 0;
        char[] chText = strText.toCharArray();
        int textLength = chText.length;
        for (char ch : chText) {
            isSingleLineComment = isSingleLineCommentCheck(isMultiLineComment, charPosition, chText, textLength, ch);
            if (isMultiLineComment(isSingleLineComment, charPosition, chText, textLength, ch)) {
                if (isMultiLineComment) {
                    multiLineCommentDepth++;
                } else {
                    isMultiLineComment = true;
                }
            }
            if (isNotMultiLineComment(isSingleLineComment, charPosition, chText, textLength, ch)) {

                if (multiLineCommentDepth > 0) {
                    multiLineCommentDepth--;
                } else {
                    isMultiLineComment = false;
                }
            }
            isSingleQuote = setSingleQuote(isDoubleQuote, isMultiLineComment, isSingleLineComment, ch);
            isDoubleQuote = setDoubleQuote(isSingleQuote, isMultiLineComment, isSingleLineComment, ch);
            if (isNotComment(isSingleQuote, isDoubleQuote, isMultiLineComment, isSingleLineComment)) {
                handleNotComment(strChangeType, sbConverted, ch);
            } else {
                sbConverted.append(Character.toString(ch));
            }
            charPosition++;
        }
        findReplaceTarget.replaceSelection(sbConverted.toString().trim());
    }

    /**
     * Sets the double quote.
     *
     * @param isSingleQuote the is single quote
     * @param isMultiLineComment the is multi line comment
     * @param isSingleLineComment the is single line comment
     * @param ch the ch
     * @return true, if successful
     */
    private boolean setDoubleQuote(boolean isSingleQuote, boolean isMultiLineComment, boolean isSingleLineComment,
            char ch) {
        boolean isDoubleQuote = false;
        if (isDoubleQuote(isSingleQuote, isMultiLineComment, isSingleLineComment, ch)) {
            isDoubleQuote = !isDoubleQuote;
        }
        return isDoubleQuote;
    }

    /**
     * Sets the single quote.
     *
     * @param isDoubleQuote the is double quote
     * @param isMultiLineComment the is multi line comment
     * @param isSingleLineComment the is single line comment
     * @param ch the ch
     * @return true, if successful
     */
    private boolean setSingleQuote(boolean isDoubleQuote, boolean isMultiLineComment, boolean isSingleLineComment,
            char ch) {
        boolean isSingleQuote = false;
        if (isSingleQuote(isDoubleQuote, isMultiLineComment, isSingleLineComment, ch)) {
            isSingleQuote = !isSingleQuote;
        }
        return isSingleQuote;
    }

    /**
     * Checks if is single line comment.
     *
     * @param isMultiLineComment the is multi line comment
     * @param charPosition the char position
     * @param chText the ch text
     * @param textLength the text length
     * @param ch the ch
     * @return true, if is single line comment
     */
    private boolean isSingleLineCommentCheck(boolean isMultiLineComment, int charPosition, char[] chText,
            int textLength, char ch) {
        boolean isSingleLineComment = false;
        if (isSingleLineComment(isMultiLineComment, charPosition, chText, textLength, ch)) {
            isSingleLineComment = true;
        }
        if ('\n' == ch) {
            isSingleLineComment = false;
        }
        return isSingleLineComment;
    }

    /**
     * Handle not comment.
     *
     * @param strChangeType the str change type
     * @param sbConverted the sb converted
     * @param ch the ch
     */
    private void handleNotComment(String strChangeType, StringBuilder sbConverted, char ch) {
        if (IMessagesConstants.UPPER_CASE.equals(strChangeType)) {
            sbConverted.append(Character.toString(ch).toUpperCase(Locale.ENGLISH));
        } else if (IMessagesConstants.LOWER_CASE.equals(strChangeType)) {
            sbConverted.append(Character.toString(ch).toLowerCase(Locale.ENGLISH));
        }
    }

    /**
     * Checks if is not comment.
     *
     * @param isSingleQuote the is single quote
     * @param isDoubleQuote the is double quote
     * @param isMultiLineComment the is multi line comment
     * @param isSingleLineComment the is single line comment
     * @return true, if is not comment
     */
    private boolean isNotComment(boolean isSingleQuote, boolean isDoubleQuote, boolean isMultiLineComment,
            boolean isSingleLineComment) {
        return !isSingleLineComment && !isMultiLineComment && !isDoubleQuote && !isSingleQuote;
    }

    /**
     * Checks if is double quote.
     *
     * @param isSingleQuote the is single quote
     * @param isMultiLineComment the is multi line comment
     * @param isSingleLineComment the is single line comment
     * @param ch the ch
     * @return true, if is double quote
     */
    private boolean isDoubleQuote(boolean isSingleQuote, boolean isMultiLineComment, boolean isSingleLineComment,
            char ch) {
        return !isSingleQuote && !isSingleLineComment && !isMultiLineComment && '\"' == ch;
    }

    /**
     * Checks if is single quote.
     *
     * @param isDoubleQuote the is double quote
     * @param isMultiLineComment the is multi line comment
     * @param isSingleLineComment the is single line comment
     * @param ch the ch
     * @return true, if is single quote
     */
    private boolean isSingleQuote(boolean isDoubleQuote, boolean isMultiLineComment, boolean isSingleLineComment,
            char ch) {
        return !isDoubleQuote && !isSingleLineComment && !isMultiLineComment && '\'' == ch;
    }

    /**
     * Checks if is not multi line comment.
     *
     * @param isSingleLineComment the is single line comment
     * @param charPosition the char position
     * @param chText the ch text
     * @param textLength the text length
     * @param ch the ch
     * @return true, if is not multi line comment
     */
    private boolean isNotMultiLineComment(boolean isSingleLineComment, int charPosition, char[] chText, int textLength,
            char ch) {
        return '*' == ch && textLength > charPosition + 1 && '/' == chText[charPosition + 1] && !isSingleLineComment;
    }

    /**
     * Checks if is multi line comment.
     *
     * @param isSingleLineComment the is single line comment
     * @param charPosition the char position
     * @param chText the ch text
     * @param textLength the text length
     * @param ch the ch
     * @return true, if is multi line comment
     */
    private boolean isMultiLineComment(boolean isSingleLineComment, int charPosition, char[] chText, int textLength,
            char ch) {
        return '/' == ch && textLength > charPosition + 1 && '*' == chText[charPosition + 1] && !isSingleLineComment;
    }

    /**
     * Checks if is single line comment.
     *
     * @param isMultiLineComment the is multi line comment
     * @param charPosition the char position
     * @param chText the ch text
     * @param textLength the text length
     * @param ch the ch
     * @return true, if is single line comment
     */
    private boolean isSingleLineComment(boolean isMultiLineComment, int charPosition, char[] chText, int textLength,
            char ch) {
        return '-' == ch && textLength > charPosition + 1 && '-' == chText[charPosition + 1] && !isMultiLineComment;
    }

    /**
     * Gets the text.
     *
     * @param textSel the text sel
     * @return the text
     */
    private String getText(ITextSelection textSel) {
        String strText = textSel.getText();
        if (null == strText) {
            strText = "";
        }
        return strText;
    }

    /**
     * Removes the annotation.
     *
     * @param textSel the text sel
     */
    private void removeAnnotation(ITextSelection textSel) {
        if (textSel.getLength() > 0) {
            int startLine = textSel.getStartLine() + 1;
            int endLine = textSel.getEndLine() + 1;
            for (int index = startLine; index <= endLine; index++) {
                Iterator<Annotation> iter = fAnnotationModel.getAnnotationIterator();
                while (iter.hasNext()) {
                    Annotation annotation = iter.next();
                    if (annotation instanceof ErrorAnnotation) {
                        ErrorAnnotation errorAnnotation = (ErrorAnnotation) annotation;
                        if (errorAnnotation.getLine() == index) {
                            fAnnotationModel.removeAnnotation(errorAnnotation);
                        }
                    }
                }
            }
        }
    }

    /**
     * Toggle block comment.
     */
    public void toggleBlockComment() {

        try {
            reconcilerUninstall();
            disableToggleCurrentAssist();
            IDocument document = viewer.getDocument();
            ITextSelection selection = (ITextSelection) viewer.getSelection();
            int selOffset = selection.getOffset();
            int selLength = selection.getLength();
            String selText = null != selection.getText() ? selection.getText() : "";
            int blockCommentOpenLen = MPPDBIDEConstants.ML_COMMENT_START.length();
            int blockCommentEndLen = MPPDBIDEConstants.ML_COMMENT_END.length();

            DocumentRewriteSession rewriteSession = null;
            rewriteSession = getRewriteSession(document);

            if (selText.startsWith(MPPDBIDEConstants.ML_COMMENT_START)
                    && selText.endsWith(MPPDBIDEConstants.ML_COMMENT_END)) {
                // Remove comments
                selLength = selLength - blockCommentEndLen - blockCommentOpenLen;
                document.replace(selOffset, blockCommentOpenLen, "");
                if ((selOffset + selLength - 2) >= 0 && "/ ".equals(document.get(selOffset + selLength - 2, 2))) {
                    document.replace(selOffset + selLength - 1, blockCommentEndLen + 1, "");
                } else {
                    document.replace(selOffset + selLength, blockCommentEndLen, "");
                }
            } else {
                // Add comment
                document.replace(selOffset, 0, MPPDBIDEConstants.ML_COMMENT_START);
                if ("/".equals(document.get(selOffset + selLength + blockCommentOpenLen - 1, 1))) {
                    document.replace(selOffset + selLength + blockCommentOpenLen, 0,
                            " " + MPPDBIDEConstants.ML_COMMENT_END);
                    selLength += blockCommentOpenLen + blockCommentEndLen + 1;
                } else {
                    document.replace(selOffset + selLength + blockCommentOpenLen, 0, MPPDBIDEConstants.ML_COMMENT_END);
                    selLength += blockCommentOpenLen + blockCommentEndLen;
                }
            }

            handleDocRewriteSession(document, selOffset, selLength, rewriteSession);
        } catch (BadLocationException ble) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", ble);
        } catch (OutOfMemoryError e) {
            displayErrorDialog();
        } finally {
            postToggleLineComment();
        }
    }

    /**
     * Perform shift left.
     */
    private void performShiftLeft() {

        reconcilerUninstall();

        if (viewer.getUndoManager() != null) {
            viewer.getUndoManager().beginCompoundChange();
        }

        IDocument document = getDocument();
        try {
            ITextSelection selection = (ITextSelection) viewer.getSelection();
            IRegion block = getTextBlockFromSelection(selection);
            ITypedRegion[] regions = TextUtilities.computePartitioning(document, SQLPartitionScanner.SQL_PARTITIONING,
                    block.getOffset(), block.getLength(), false);
            // [start line, end line, start line, end line, ...]
            int[] lines = new int[regions.length * 2];
            for (int iregion = 0, jregion = 0; iregion < regions.length; iregion++, jregion += 2) {
                // start line of region
                lines[jregion] = getFirstCompleteLineOfRegion(regions[iregion]);
                // end line of region
                int length = regions[iregion].getLength();
                int offset = regions[iregion].getOffset() + length;
                if (length > 0) {
                    offset--;
                }

                lines[jregion + 1] = lines[jregion] == -1 ? -1 : document.getLineOfOffset(offset);
            }

            viewer.setRedraw(false);

            // Perform the shift operation.
            for (int iPos = 0, jPosos = 0; iPos < regions.length; iPos++, jPosos += 2) {
                String[] prefixes = config.getIndentPrefixes(viewer, config.getConfiguredContentTypes(viewer)[0]);
                if (prefixes.length > 0 && lines[jPosos] >= 0 && lines[jPosos + 1] >= 0) {
                    shiftLeft(lines[jPosos], lines[jPosos + 1], prefixes);
                }
            }
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", exception);
        }

        viewer.setRedraw(true);
        if (viewer.getUndoManager() != null) {
            viewer.getUndoManager().endCompoundChange();
        }

        reconcilerInstall();

    }

    /**
     * Shift left.
     *
     * @param startLine the start line
     * @param endLine the end line
     * @param prefixes the prefixes
     */
    private void shiftLeft(int startLine, int endLine, String[] prefixes) {
        IDocument document = getDocument();

        try {
            String text;
            IRegion[] occurrences = new IRegion[endLine - startLine + 1];

            // find all the first occurrences of prefix in the given lines
            for (int count = 0; count < occurrences.length; count++) {
                IRegion line = document.getLineInformation(startLine + count);
                text = document.get(line.getOffset(), line.getLength());

                int index = -1;
                int length = 0;
                if (text.length() != 0 && text.charAt(0) == '\t') {
                    index = line.getOffset();
                    length = 1;
                } else {
                    index = line.getOffset();
                    int spaceCounter = 0;
                    int loopBoundary = text.length() < DSFormatterPreferencePage.getIndentSize() ? text.length()
                            : DSFormatterPreferencePage.getIndentSize();
                    while (spaceCounter < loopBoundary && text.charAt(spaceCounter) == ' ') {
                        spaceCounter++;
                    }

                    length = spaceCounter;
                }

                if (length == 0) {
                    // found a non-shifting line
                    continue;
                }

                occurrences[count] = new Region(index, length);
            }

            // OK - change the document
            int decrement = 0;
            for (int count = 0; count < occurrences.length; count++) {
                IRegion region = occurrences[count];
                if (region != null) {
                    document.replace(region.getOffset() - decrement, region.getLength(), ""); // $NON-NLS-1$
                    decrement += region.getLength();
                }
            }

        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", exception);
        }
    }

    /**
     * Gets the first complete line of region.
     *
     * @param region the region
     * @return the first complete line of region
     */
    private int getFirstCompleteLineOfRegion(IRegion region) {

        try {

            IDocument document = getDocument();

            int startLine = document.getLineOfOffset(region.getOffset());

            int offset = document.getLineOffset(startLine);
            if (offset >= region.getOffset()) {
                return startLine;

            }

            offset = document.getLineOffset(startLine + 1);
            return (offset > region.getOffset() + region.getLength()) ? -1 : startLine + 1;

        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", exception);
        }

        return -1;
    }

    /**
     * Gets the text block from selection.
     *
     * @param selection the selection
     * @return the text block from selection
     * @throws BadLocationException the bad location exception
     */
    private IRegion getTextBlockFromSelection(ITextSelection selection) throws BadLocationException {
        IDocument document = getDocument();
        int start = document.getLineOffset(selection.getStartLine());
        int end;
        int endLine = selection.getEndLine();
        if (document.getNumberOfLines() > endLine + 1) {
            end = document.getLineOffset(endLine + 1);
        } else {
            end = document.getLength();
        }
        return new Region(start, end - start);
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return getDocument().get();
    }

    /**
     * Toggle line comment.
     */
    public void toggleLineComment() {
        try {
            ITextSelection textSelection = handlePreToggleLineComment();
            IDocument document = viewer.getDocument();
            int selOffset = textSelection.getOffset();
            int selLength = textSelection.getLength();
            boolean firstLineFlag = true;
            int startLine = textSelection.getStartLine();
            int endLine = textSelection.getEndLine();
            int lineCommentLen = SL_COMMENT.length();
            boolean lineComment = false;
            int selLineNo = 0;
            DocumentRewriteSession rewriteSession = getRewriteSession(document);
            for (int lineNum = startLine; lineNum <= endLine; lineNum++) {
                int lineOffset = document.getLineOffset(lineNum);
                int lineLength = document.getLineLength(lineNum);
                int trimLength = 0;
                selLineNo++;
                String lineText = document.get(lineOffset, lineLength);
                lineComment = isLineComment(selLineNo, lineText);
                if (lineComment) {
                    trimLength = deleteTabOrSpaceChar(lineText);
                    if (lineText.trim().startsWith("--")) { // Remove comment
                        document.replace(lineOffset + trimLength, lineCommentLen, "");
                        if (isSeloffsetAndLineoffsetEqual(selOffset, firstLineFlag, lineOffset)) {
                            selLength -= lineCommentLen;
                        } else if (isSeloffsetAndLineoffsetNotEqual(selOffset, firstLineFlag, lineOffset)) {
                            selOffset -= lineCommentLen;
                        } else {
                            selLength -= lineCommentLen;
                        }
                    }
                } else {
                    document.replace(lineOffset, 0, SL_COMMENT);    // Add
                                                                    // comment
                    if (isSeloffsetAndLineoffsetNotEqual(selOffset, firstLineFlag, lineOffset)) {
                        selOffset += lineCommentLen;
                    } else {
                        selLength += lineCommentLen;
                    }
                }
                firstLineFlag = false;
            }
            handleDocRewriteSession(document, selOffset, selLength, rewriteSession);
        } catch (OutOfMemoryError e) {
            displayErrorDialog();
        } catch (BadLocationException ble) {
            MPPDBIDELoggerUtility.error("PLSourceEditorCore: BadLocationException occurred.", ble);
        } finally {
            postToggleLineComment();
        }
    }

    private void postToggleLineComment() {
        enableToggleCurrentAssist();
        reconcilerInstall();
    }

    private ITextSelection handlePreToggleLineComment() {
        reconcilerUninstall();
        disableToggleCurrentAssist();
        ITextSelection textSelection = (ITextSelection) viewer.getSelection();
        return textSelection;
    }

    private void handleDocRewriteSession(IDocument document, int selOffset, int selLength,
            DocumentRewriteSession rewriteSession) {
        stopDocumentRewriteSession(document, rewriteSession);
        // set the selection after toggle the comments
        setViewerSelection(selOffset, selLength);
    }

    private void displayErrorDialog() {
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
    }

    /**
     * Delete tab or space char.
     *
     * @param lineText the line text
     * @return the int
     */
    private int deleteTabOrSpaceChar(String lineText) {
        int trimLength = 0;
        if (!"".equals(lineText)) {

            StringBuffer sb = new StringBuffer(lineText);
            for (;;) {
                if (isLineStartWithHaveSpaceOrTabChar(sb)) {
                    sb.deleteCharAt(0);
                    trimLength++;
                } else {
                    break;
                }
            }
        }
        return trimLength;
    }

    /**
     * Checks if is seloffset and lineoffset not equal.
     *
     * @param selOffset the sel offset
     * @param firstLineFlag the first line flag
     * @param lineOffset the line offset
     * @return true, if is seloffset and lineoffset not equal
     */
    private boolean isSeloffsetAndLineoffsetNotEqual(int selOffset, boolean firstLineFlag, int lineOffset) {
        return lineOffset != selOffset && firstLineFlag;
    }

    /**
     * Checks if is seloffset and lineoffset equal.
     *
     * @param selOffset the sel offset
     * @param firstLineFlag the first line flag
     * @param lineOffset the line offset
     * @return true, if is seloffset and lineoffset equal
     */
    private boolean isSeloffsetAndLineoffsetEqual(int selOffset, boolean firstLineFlag, int lineOffset) {
        return lineOffset == selOffset && firstLineFlag;
    }

    /**
     * Checks if is line start with have space or tab char.
     *
     * @param sb the sb
     * @return true, if is line start with have space or tab char
     */
    private boolean isLineStartWithHaveSpaceOrTabChar(StringBuffer sb) {
        return sb.charAt(0) == ' ' || sb.charAt(0) == '\t';
    }

    /**
     * Checks if is line comment.
     *
     * @param selLineNo the sel line no
     * @param lineText the line text
     * @return true, if is line comment
     */
    private boolean isLineComment(int selLineNo, String lineText) {
        return (lineText.trim()).startsWith(SL_COMMENT);
    }

    /**
     * Gets the rewrite session.
     *
     * @param document the document
     * @return the rewrite session
     */
    private DocumentRewriteSession getRewriteSession(IDocument document) {
        DocumentRewriteSession rewriteSession = null;
        if (document instanceof IDocumentExtension4) {
            rewriteSession = ((IDocumentExtension4) document)
                    .startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
        }
        return rewriteSession;
    }

    /**
     * Stop document rewrite session.
     *
     * @param document the document
     * @param rewriteSession the rewrite session
     */
    private void stopDocumentRewriteSession(IDocument document, DocumentRewriteSession rewriteSession) {
        if (rewriteSession != null) {
            ((IDocumentExtension4) document).stopRewriteSession(rewriteSession);
        }
    }

    /**
     * Sets the viewer selection.
     *
     * @param selOffset the sel offset
     * @param selLength the sel length
     */
    private void setViewerSelection(int selOffset, int selLength) {
        if (selLength > 0) {
            viewer.getSelectionProvider().setSelection(new TextSelection(selOffset, selLength));
        }
    }

    /**
     * Gets the search start position.
     *
     * @param isFwdSearch the is fwd search
     * @return the search start position
     */
    public int getSearchStartPosition(boolean isFwdSearch) {
        TextViewer textViewer = (TextViewer) viewer;

        int searchStartPosition = 0;
        if (!isFwdSearch) {
            searchStartPosition = -1;
        }

        if (lastSearchReturnIndex != -1) {
            ITextSelection selection = (ITextSelection) textViewer.getSelection();
            searchStartPosition = selection.getOffset();
            if (isFwdSearch) {
                searchStartPosition += selection.getLength();
            } else {
                searchStartPosition--;
                if (!options.isWrapAround() && searchStartPosition < 0) {
                    searchStartPosition = 0;
                }
            }
        }

        return searchStartPosition;
    }

    /**
     * Clear doc content.
     */
    public void clearDocContent() {
        this.doc.set("");
    }

    /**
     * Clear status bar.
     */
    public void clearStatusBar() {
        this.execProgresBar = null;
    }

    /**
     * Uninstall decoration.
     */
    public void uninstallDecoration() {
        if (fSourceViewerDecorationSupport != null) {
            fSourceViewerDecorationSupport.uninstall();
            fSourceViewerDecorationSupport = null;
        }
    }

    /**
     * Toggle comments enable disable.
     *
     * @param partObject the part object
     */
    public void toggleCommentsEnableDisable(Object partObject) {
        boolean isEnabled = this.getSourceViewer().isEditable();
        if (partObject instanceof SQLTerminal) {
            SQLTerminal terminal = (SQLTerminal) partObject;
            isEnabled = terminal.getTerminalCore().getSourceViewer().isEditable();
        } else if (partObject instanceof PLSourceEditor) {
            PLSourceEditor sourceEditor = (PLSourceEditor) partObject;
            isEnabled = sourceEditor.getSourceEditorCore().getSourceViewer().isEditable();
        }
        toggleLineComments.setEnabled(isEnabled);
        toggleBlockComments.setEnabled(isEnabled);

    }

    /**
     * Property change.
     *
     * @param event the event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        String preferencePropertyName = event.getProperty();
        if (preferencePropertyName.equals(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF)) {
            configurePrefixes();
            // handle for insert tab and perform change of tab width
            Boolean isInsertTabOptionSelected = (Boolean) event.getNewValue();
            if (!isInsertTabOptionSelected) {
                reconfigureTabWidth();
            }
        } else if (preferencePropertyName.equals(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF)) {
            configurePrefixes();
            // change the tab width
            reconfigureTabWidth();
        }

    }

    /**
     * Configure prefixes.
     */
    private void configurePrefixes() {
        String[] types = config.getConfiguredContentTypes(viewer);
        config.setUpdatedPrefixes(viewer);
        configurePrefixesForEachContentType(types);
    }

    /**
     * Configure prefixes for each content type.
     *
     * @param types the types
     */
    private void configurePrefixesForEachContentType(String[] types) {
        for (String type : types) {
            String[] indentPrefixes = config.getIndentPrefixes(viewer, type);
            viewer.setIndentPrefixes(indentPrefixes, type);
        }
    }

    /**
     * Reconfigure tab width.
     */
    private void reconfigureTabWidth() {
        if (viewer.getTextWidget() != null) {
            viewer.getTextWidget().setTabs(config.getTabWidth(viewer));
        }
    }

    /**
     * Checks if is editable.
     *
     * @return true, if is editable
     */
    public boolean isEditable() {
        return viewer.isEditable();
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
     * Gets the viewer doc.
     *
     * @return the viewer doc
     */
    public String getViewerDoc() {
        return viewer.getDocument().get();
    }

    /**
     * Reset font.
     */
    public void resetFont() {
        FontData fontData = this.font.getFontData()[0];
        fontData.setHeight(preferenceStore.getInt(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE));
        font = resManager.createFont(FontDescriptor.createFrom(fontData));

        viewer.getTextWidget().setFont(font);

        viewer.setDocument(this.doc, fAnnotationModel);
    }

    private void clearFont() {
        if (null != resManager && null != preferenceStore) {
            resManager.destroyFont(FontDescriptor.createFrom("Courier New",
                    preferenceStore.getInt(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE), SWT.NORMAL));
            font = null;
            resManager = null;
        }
    }

    /**
     * get the selected query
     *
     * @return the selected query
     */
    public String getSelectedQry() {
        if (!(getSourceViewer().getSelectionProvider().getSelection() instanceof ITextSelection)) {
            return getDocument().get();
        }
        final ITextSelection textSel = (ITextSelection) getSourceViewer().getSelectionProvider().getSelection();
        String query = textSel.getText();
        if (query != null && query.length() > 0) {
            return query;
        } else {
            return getDocument().get();
        }

    }

    /**
     * start the Job to format the contents
     */
    public void doFormattingOfContents() {
        SQLTerminalFormatterUIWorker formatWorker = new SQLTerminalFormatterUIWorker(this, "Format SQL - ");
        jobsAssociatedToTerminal.add(formatWorker);
        formatWorker.schedule();
    }

    /**
     * to remove completed Job
     * 
     * @param job which job need to be removed
     */
    public void removeCompletedJob(UIWorkerJob job) {
        boolean isRemoveOk = this.jobsAssociatedToTerminal.remove(job);

        if (!isRemoveOk) {
            MPPDBIDELoggerUtility.error("Unable to remove job : " + job.getName() + " from the list");
        }
    }

    /**
     * Gets the f annotation model.
     *
     * @return the f annotation model
     */
    public AnnotationModel getfAnnotationModel() {
        return fAnnotationModel;
    }
    
    /**
     * pre destroy
     */
    @PreDestroy
    public void preDestroy() {
        if (null != viewer) {
            viewer.unconfigure();
        }
        fAnnotationAccess = null;
        if (null != doc) {
            IDocumentPartitioner documentPartitioner = ((IDocumentExtension3) doc)
                    .getDocumentPartitioner(SQLPartitionScanner.SQL_PARTITIONING);
            if (null != documentPartitioner) {
                if (documentPartitioner instanceof SQLDocumentPartitioner) {
                    SQLDocumentPartitioner partitioner = (SQLDocumentPartitioner) documentPartitioner;
                    partitioner.clearScanner();
                }
                documentPartitioner.disconnect();
            }
            doc.set("");
            destroyDocument();
        }
        if (null != fAnnotationModel) {
            fAnnotationModel.removeAllAnnotations();
        }
        uninstallDecoration();
        clearStatusBar();
        if (null != jobsAssociatedToTerminal) {
            jobsAssociatedToTerminal.clear();
            jobsAssociatedToTerminal = null;
        }
        if (null != config) {
            config.preDestroy();
            config = null;
        }
        findAndReplaceoptions = null;
        options = null;
        if (null != syntax) {
            this.syntax = null;
        }
        clearFont();
        if (null != preferenceStore) {
            preferenceStore.removePropertyChangeListener(this);
            preferenceStore = null;
        }
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_PLSOURCEEDITORCORE_SOURCE_EDITOR_CLEARED));
    }

}
