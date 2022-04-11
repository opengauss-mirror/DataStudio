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

package org.opengauss.mppdbide.view.core;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.HandlerUtilities;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.IUserPreference;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: ExecDialog
 * 
 * @since 3.0.0
 */
public class ExecDialog extends Dialog {

    // Parent container
    private Composite container;

    // Place holder for execution template
    private SourceViewer sourceViewer;
    private CompositeRuler fCompositeRuler;
    private static final int SPACE_BETWEEN_RULER = 1;
    private SQLSourceViewerDecorationSupport fSourceViewerDecorationSupport;
    private IDebugObject debugObject;

    // Dialog height and width
    private static final int TRIG_DIALOG_WIDTH = 450;
    private static final int TRIG_DIALOG_HEIGHT = 250;

    // Execution template usage hint label
    private Label lblUsageHint;


    private Button okBtn;
    private Button cancelBtn;

    private StatusMessage statusMessage;
    private SQLSyntax syntax;

    /**
     * Instantiates a new exec dialog.
     *
     * @param parentShell the parent shell
     * @param isDebug the is debug
     */
    public ExecDialog(Shell parentShell) {
        super(parentShell);
        setDefaultImage(IconUtility.getIconImage(IiconPath.ICO_FUNCTION_FOLDER, this.getClass()));
        this.setShellStyle(SWT.TITLE | SWT.APPLICATION_MODAL);
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {

        Rectangle bounds = Display.getDefault().getActiveShell().getBounds();

        parent.getShell().setBounds(bounds.width / 2 - TRIG_DIALOG_WIDTH / 2,
                bounds.height / 2 - TRIG_DIALOG_HEIGHT / 2, TRIG_DIALOG_WIDTH, TRIG_DIALOG_HEIGHT);

        // create the top level composite for the dialog
        container = new Composite(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 0;
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        applyDialogFont(container);
        // initialize the dialog units
        initializeDialogUnits(container);

        lblUsageHint = new Label(container, SWT.NULL);
        lblUsageHint.setText("");

        sourceViewer = new SourceViewer(container, getCompositeRuler(), null, false,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
        sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        sourceViewer.setEditable(true);

        setDocument(new Document(""));

        setDecoration();
        SQLDocumentPartitioner.connectDocument(sourceViewer.getDocument(), 0);

        sourceViewer.configure(new SQLSourceViewerConfig(getSyntax()));
        sourceViewer.addTextListener(new ITextListener() {

            @Override
            public void textChanged(TextEvent event) {
                if (sourceViewer != null && sourceViewer.getDocument() != null
                        && sourceViewer.getDocument().get().trim().length() < 1) {
                    okBtn.setEnabled(false);
                } else {
                    okBtn.setEnabled(true);
                }
            }
        });
        sourceViewer.getTextWidget().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 'a' && (e.stateMask & SWT.MODIFIER_MASK) == SWT.CTRL) {
                    sourceViewer.doOperation(ITextOperationTarget.SELECT_ALL);
                }
            }
        });

        // create the button bar
        buttonBar = createButtonBar(container);

        displayTemplateFromDebugObject(debugObject);

        if (null != debugObject.getTemplateParameters()) {
            debugObject.clearTemplateParameterValues();
        }

        debugObject.setTemplateParameters(debugObject.getTemplateParameters());

        return container;
    }

    /**
     * Sets the debug object.
     *
     * @param debugObj the debug obj
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public boolean setDebugObject(IDebugObject debugObj) throws DatabaseOperationException, DatabaseCriticalException {
        HandlerUtilities.getSourceForDbgObj(debugObj);

        this.debugObject = debugObj;
        debugObj.generateExecutionTemplate();

        return true;
    }

    /**
     * Checks if is resizable.
     *
     * @return true, if is resizable
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Gets the composite ruler.
     *
     * @return the composite ruler
     */
    public CompositeRuler getCompositeRuler() {
        fCompositeRuler = new CompositeRuler(SPACE_BETWEEN_RULER);
        fCompositeRuler.addDecorator(1, new LineNumberRulerColumn());

        return fCompositeRuler;
    }

    /**
     * Sets the document.
     *
     * @param document the new document
     */
    public void setDocument(IDocument document) {
        sourceViewer.setDocument(document);
    }

    /**
     * Set decoration for source viewer.
     */
    private void setDecoration() {
        ISharedTextColors sharedTextColors = EditorsPlugin.getDefault().getSharedTextColors();

        fSourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(sourceViewer, null, null,
                sharedTextColors);

        fSourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);

        fSourceViewerDecorationSupport.installDecorations();
    }

    /**
     * Display template from debug object.
     *
     * @param object the object
     */
    public void displayTemplateFromDebugObject(IDebugObject object) {
        sourceViewer.getDocument().set(debugObject.getExecuteTemplate());
        setUsageHint(debugObject.getUsagehint());
        MPPDBIDELoggerUtility.debug("GUI: DebugSQLObjectCore: Display execution template to debug.");
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        // Added for remembering PSWD/PORT to change button label start
        final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        okBtn = createButton(parent, UIConstants.OK_ID, okLabel, true);
        cancelBtn = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, false);
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (UIConstants.OK_ID == buttonId) {
            okBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            executePressed();
        } else if (UIConstants.CANCEL_ID == buttonId) {
            cancelPressed();
        }

    }

    /**
     * Execute pressed.
     */
    public void executePressed() {
        Database db = debugObject.getDatabase();

        // Create new terminal to execute the PL
        SQLTerminal terminal = UIElement.getInstance().createNewTerminal(db);
        if (terminal == null) {
            close();
            return;
        }
        try {
            // If no IN parameter, then get the query from debug object directly
            if (null == sourceViewer) {
                terminal.getTerminalCore().getDocument().set(debugObject.prepareExecutionQueryString());
            } else {
                terminal.getTerminalCore().getDocument().set(sourceViewer.getDocument().get());
            }

            terminal.handleExecution();
        } catch (DatabaseOperationException e) {
            terminal.getConsoleMessageWindow(true).logError(e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * Close dialog from UI.
     */
    public void closeDialogFromUI() {
        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                close();
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
     * Set execution template usage hint.
     *
     * @param hint execution template usage hint text
     */
    private void setUsageHint(String hint) {
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (debugObject.getObjectType() == OBJECTTYPE.PLSQLFUNCTION) {

            builder.append(MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_PARAMETER_VALUE)
                    + MPPDBIDEConstants.LINE_SEPARATOR);
            builder.append(null != hint && hint.length() > 0
                    ? MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_HINT) + hint
                    : "");
            builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        }

        lblUsageHint.setText(builder.toString());
    }

    /**
     * Cancel pressed.
     */
    @Override
    protected void cancelPressed() {
        // Sets this window's return code. The return code is automatically
        // returned by open if block on open is enabled.
        setReturnCode(UIConstants.CANCEL_ID);
        close();
    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXECDIALOG_EXEC_PL_SQL));
    }

   
    private static class ValueEditingSupport extends EditingSupport {
        private final TableViewer viewer;

        ValueEditingSupport(TableViewer viewer) {
            super(viewer);
            this.viewer = viewer;
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            TextCellEditor cellEditor = new TextCellEditor(viewer.getTable());
            Control textControl = cellEditor.getControl();

            // Suppress the context menu
            textControl.addListener(SWT.MenuDetect, new ExecDialogListener());

            return cellEditor;
        }

        /**
         * Added for findbugs Static inner class creation check
         */
        private static class ExecDialogListener implements Listener {
            @Override
            public void handleEvent(Event event) {
                event.doit = false;
            }
        }

        @Override
        protected void setValue(Object element, Object value) {
            ((ObjectParameter) element).setValue(value.toString());
            viewer.refresh();
        }

        @Override
        protected boolean canEdit(Object element) {
            return true;
        }

        @Override
        protected Object getValue(Object element) {
            if (element instanceof ObjectParameter) {
                Object value = ((ObjectParameter) element).getValue();
                if (null != value) {
                    return value;
                }
            }

            return "";
        }
    }


    /**
     * Close.
     *
     * @return true, if successful
     */
    @Override
    public boolean close() {
        if (fSourceViewerDecorationSupport != null) {
            fSourceViewerDecorationSupport.uninstall();
        }
        if (sourceViewer != null) {
            sourceViewer.unconfigure();
        }
        syntax = null;
        return super.close();
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

}
