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

package com.huawei.mppdbide.view.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryItem;
import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;
import com.huawei.mppdbide.eclipse.dependent.EclipseInjections;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import com.huawei.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.IUserPreference;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SqlHistory.
 *
 * @since 3.0.0
 */
public class SqlHistory extends Dialog {

    private SQLSourceViewerDecorationSupport fSourceViewerDecorationSupport;
    private CompositeRuler fCompositeRuler;
    private static final int SPACE_BETWEEN_RULER = 1;
    private int displaySqlCount;
    private static final int SERIAL_NUMBER_COLUMN = 0;
    private static final int PIN_STATUS_COLUMN = 1;
    private static final int SQL_QUERY_COLUMN = 2;
    private static final int NUMBER_OF_ROWS_COLUMN = 3;
    private static final int START_TIME_COLUMN = 4;
    private static final int ELAPSED_TIME = 5;
    private static final int DATABASE_NAME_COLUMN = 6;
    private static final int EXECUTION_STATUS_COLUMN = 7;
    private SourceViewer sourceViewer = null;
    private Table table = null;
    private String profileName;
    private ISqlHistoryManager manager;
    private ToolItem openToolItem = null;
    private ToolItem saveToolItem = null;
    private ToolItem deleteToolItem = null;
    private ToolItem deleteAllToolItem = null;
    private ToolItem pinToolItem = null;
    private ToolItem unpinToolItem = null;
    private String terminalParentId = null;
    private String profileId;
    private SQLSyntax syntax;

    /**
     * Instantiates a new sql history.
     *
     * @param parent1 the parent 1
     * @param profileName the profile name
     * @param profileId the profile id
     * @param uiID the ui ID
     */
    public SqlHistory(Shell parent1, String profileName, String profileId, String uiID) {
        super(parent1);
        this.profileName = profileName;
        this.profileId = profileId;
        this.terminalParentId = uiID;
        displaySqlCount = BLPreferenceManager.getInstance().getBLPreference().getSQLHistorySize();

    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_TITLE, profileName));
        shell.setImage(IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()));
        shell.setSize(940, 750);
    }

    private void createToolbar(final Composite parent) {

        final ToolBar bar = new ToolBar(parent, SWT.FLAT | SWT.FOCUSED);

        final Image loadsqlIcon = IconUtility.getIconImage(IiconPath.LOAD_QUERY_SQL, getClass());
        final Image sqlcloseIcon = IconUtility.getIconImage(IiconPath.LOAD_QUERY_SQL_CLOSE, getClass());
        final Image deleteIcon = IconUtility.getIconImage(IiconPath.DELETE_SELECTED, getClass());

        final Image deleteallIcon = IconUtility.getIconImage(IiconPath.DELETE_ALL, getClass());

        final Image pinIcon = IconUtility.getIconImage(IiconPath.PIN_SQL, getClass());

        final Image unpinIcon = IconUtility.getIconImage(IiconPath.UNPIN_SQL, getClass());

        openToolItem = new ToolItem(bar, SWT.PUSH);
        openToolItem.setEnabled(false);
        openToolItem.setImage(loadsqlIcon);
        openToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.LOAD_SQL_TERMINAL));
        openToolItem.addSelectionListener(new LoadsqlterminalClass());

        saveToolItem = new ToolItem(bar, SWT.PUSH);
        saveToolItem.setEnabled(false);
        saveToolItem.setImage(sqlcloseIcon);
        saveToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.LOAD_TERMINAL_CLOSE_HISTORY));
        saveToolItem.addSelectionListener(new LoadsqlclosehistoryClass());

        deleteToolItem = new ToolItem(bar, SWT.PUSH);
        deleteToolItem.setEnabled(false);
        deleteToolItem.setImage(deleteIcon);
        deleteToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL));
        deleteToolItem.addSelectionListener(new DeletesqlClass());

        deleteAllToolItem = new ToolItem(bar, SWT.PUSH);
        deleteAllToolItem.setEnabled(false);
        deleteAllToolItem.setImage(deleteallIcon);
        deleteAllToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.DELETE_ALL));
        deleteAllToolItem.addSelectionListener(new DeleteAllsqlClass());

        pinToolItem = new ToolItem(bar, SWT.PUSH);
        pinToolItem.setEnabled(false);
        pinToolItem.setImage(pinIcon);
        pinToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.PIN_SQL));
        pinToolItem.addSelectionListener(new PinsqlClass());

        unpinToolItem = new ToolItem(bar, SWT.PUSH);
        unpinToolItem.setEnabled(false);
        unpinToolItem.setImage(unpinIcon);
        unpinToolItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.UNPIN_SQL));
        unpinToolItem.addSelectionListener(new UnpinsqlClass());

    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    public Control createContents(final Composite parent) {
        final ScrolledComposite mainSc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        mainSc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite currentComposite = new Composite(mainSc, SWT.BORDER);
        mainSc.setContent(currentComposite);
        GridLayout layout = new GridLayout(1, false);
        currentComposite.setLayout(layout);
        createToolbar(currentComposite);
        table = new Table(currentComposite, SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData tableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableGD.heightHint = 300;
        table.setLayoutData(tableGD);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setTopIndex(10);

        table.addKeyListener(new HistoryTblKeyListener());

        TableColumn serialno = new TableColumn(table, SWT.LEFT);
        TableColumn pinstatus = new TableColumn(table, SWT.LEFT);
        TableColumn sqlstatement = new TableColumn(table, SWT.LEFT);
        TableColumn sqlrowselection = new TableColumn(table, SWT.LEFT);
        TableColumn starttime = new TableColumn(table, SWT.LEFT);
        TableColumn elapsedtime = new TableColumn(table, SWT.LEFT);
        TableColumn databasename = new TableColumn(table, SWT.LEFT);
        TableColumn finalstatus = new TableColumn(table, SWT.LEFT);

        serialno.setText(MessageConfigLoader.getProperty(IMessagesConstants.SERIAL_NO));
        pinstatus.setText(MessageConfigLoader.getProperty(IMessagesConstants.PIN_STATUS));
        sqlstatement.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_STATEMENT));
        sqlrowselection.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_ROW_SELECTION));
        starttime.setText(MessageConfigLoader.getProperty(IMessagesConstants.START_TIME));
        elapsedtime.setText(MessageConfigLoader.getProperty(IMessagesConstants.ELAPSED_TIME));

        databasename.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_NAME));
        finalstatus.setText(MessageConfigLoader.getProperty(IMessagesConstants.FINAL_STATUS));

        serialno.pack();
        pinstatus.pack();
        sqlstatement.setWidth(200);
        sqlrowselection.pack();
        starttime.setWidth(125);
        elapsedtime.pack();
        databasename.pack();
        finalstatus.pack();

        createSourceViewer(currentComposite);
        mainSc.setExpandHorizontal(true);
        mainSc.setExpandVertical(true);
        mainSc.setMinSize(currentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        mainSc.pack();

        return parent;
    }

    /**
     * The listener interface for receiving historyTblKey events. The class that
     * is interested in processing a historyTblKey event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addHistoryTblKeyListener<code>
     * method. When the historyTblKey event occurs, that object's appropriate
     * method is invoked.
     *
     * HistoryTblKeyEvent
     */
    private static final class HistoryTblKeyListener implements KeyListener {
        @Override
        public void keyReleased(KeyEvent event) {
            try {
                if (((event.stateMask & SWT.ALT) == SWT.ALT) && (event.keyCode == 'y')) {
                    IEclipseContext eclipseContext = EclipseInjections.getInstance().getEclipseContext();
                    ECommandService commandService = eclipseContext.get(ECommandService.class);
                    EHandlerService handlerService = eclipseContext.get(EHandlerService.class);
                    Command cmd = commandService.getCommand("com.huawei.mppdbide.command.id.copybreakpointdata");
                    ParameterizedCommand parameterizedCmd = new ParameterizedCommand(cmd, null);
                    if (handlerService != null) {
                        handlerService.executeHandler(parameterizedCmd);
                    }
                }
            } catch (final NumberFormatException numberFormatException) {
                event.doit = false;
            }

        }

        @Override
        public void keyPressed(KeyEvent event) {

        }
    }

    private void createSourceViewer(Composite parent) {

        sourceViewer = new SourceViewer(parent, getCompositeRuler(), null, false,
                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
        sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sourceViewer.configure(new SQLSourceViewerConfig(getSyntax()));
        setDocument(new Document(""));
        sourceViewer.setEditable(false);
        setDecoration();
        SQLDocumentPartitioner.connectDocument(sourceViewer.getDocument(), 0);
        table.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                TableItem[] items = table.getSelection();
                String query = items[0].getText(SQL_QUERY_COLUMN);
                sourceViewer.getDocument().set(query);

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });

        table.addMouseListener(new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent event) {
                loadQuerytoSQLTerminal();
            }

            @Override
            public void mouseDown(MouseEvent event) {

            }

            @Override
            public void mouseUp(MouseEvent event) {

            }

        });

        displaySqlHistoryObject();
    }

    /**
     * Enablebuttons.
     *
     * @param value the value
     */
    protected void enablebuttons(boolean value) {
        openToolItem.setEnabled(value);
        saveToolItem.setEnabled(value);
        deleteAllToolItem.setEnabled(value);
        deleteToolItem.setEnabled(value);
        pinToolItem.setEnabled(value);
        unpinToolItem.setEnabled(value);

    }

    /**
     * Display sql history object.
     */
    public void displaySqlHistoryObject() {
        this.manager = SQLHistoryFactory.getInstance();
        List<SQLHistoryItem> items = new ArrayList<SQLHistoryItem>(10);
        try {
            items = this.manager.getHistoryContent(profileId, displaySqlCount);

        } catch (MPPDBIDEException exception) {

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.HISTORY_LOADING_IN_PROGRESS_TILTLE),
                    exception.getMessage());
        }
        setInput(items);

    }

    /**
     * Sets the input.
     *
     * @param items the new input
     */
    public void setInput(List<SQLHistoryItem> items) {
        int index = 1;
        for (SQLHistoryItem item : items) {
            TableItem row = new TableItem(table, SWT.NONE);
            setColValues(row, item, index);
            index++;
            row.setData(item);
        }
        if (table != null) {
            if (table.getItems().length > 0) {
                enablebuttons(true);
            }
        }

    }

    private void setColValues(TableItem row, SQLHistoryItem item, Integer count) {
        row.setText(SERIAL_NUMBER_COLUMN, count.toString());
        row.setText(PIN_STATUS_COLUMN,
                item.isPinned() ? MessageConfigLoader.getProperty(IMessagesConstants.SQL_HIST_PINNED) : "");
        if (null != item.getQuery()) {
            row.setText(SQL_QUERY_COLUMN, item.getQuery());
        }
        row.setText(NUMBER_OF_ROWS_COLUMN, Integer.toString(item.getResultSetSize()));
        if (null != item.getExecutionTime()) {
            row.setText(START_TIME_COLUMN, item.getExecutionTime());
        }

        row.setText(ELAPSED_TIME, item.getElapsedTime());
        if (null != item.getDatabaseName()) {
            row.setText(DATABASE_NAME_COLUMN, item.getDatabaseName());
        }
        row.setText(EXECUTION_STATUS_COLUMN,
                item.getFinalStatus() ? MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_STATUS_SUCCESS)
                        : MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_STATUS_FAILURE));

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class LoadsqlterminalClass.
     */
    private final class LoadsqlterminalClass implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            loadQuerytoSQLTerminal();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class LoadsqlclosehistoryClass.
     */
    private final class LoadsqlclosehistoryClass implements SelectionListener {

        private LoadsqlclosehistoryClass() {
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            SQLTerminal terminal = UIElement.getInstance().getTerminal(terminalParentId);
            TableItem[] items = table.getSelection();
            String query = null;
            StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            for (TableItem t : items) {
                query = t.getText(SQL_QUERY_COLUMN);
                sb.append(query);
                sb.append(MPPDBIDEConstants.LINE_SEPARATOR);
                t.dispose();
            }

            if (null != terminal) {
                terminal.getTerminalCore().setSelectedQuery(sb.toString());
                terminal.resetSQLTerminalButton();
                terminal.resetAutoCommitButton();
                terminal.setModified(true);
                terminal.setModifiedAfterCreate(true);
            }
            Shell currShell = Display.getCurrent().getActiveShell();
            currShell.dispose();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DeletesqlClass.
     */
    private final class DeletesqlClass implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            boolean isPined = false;
            boolean isUnpinned = false;
            TableItem[] tableItems = table.getSelection();
            if (tableItems.length == 0) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                        MessageConfigLoader.getProperty(IMessagesConstants.SELECT_QUERY_TO_DELETE));
                return;
            }
            for (TableItem item : tableItems) {
                if (((SQLHistoryItem) item.getData()).isPinned()) {
                    isPined = true;
                } else {
                    isUnpinned = true;
                }
            }
            if (isPined && !isUnpinned) {
                displayInfoMessage();
            } else {
                int type = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SELECTED_HISTORY_ALERT, profileName,
                                MPPDBIDEConstants.LINE_SEPARATOR));
                deleteSQLHistory(type, tableItems);
            }
            resetSerialNumber();

        }

        private void displayInfoMessage() {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.PINNED_DELETION));
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DeleteAllsqlClass.
     */
    private final class DeleteAllsqlClass implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            boolean isPinned = false;
            boolean isUnpinned = false;
            TableItem[] items = table.getItems();

            for (TableItem item : items) {
                if (((SQLHistoryItem) item.getData()).isPinned()) {
                    isPinned = true;
                } else {
                    isUnpinned = true;
                }
            }

            if (isPinned && !isUnpinned) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                        MessageConfigLoader.getProperty(IMessagesConstants.PINNED_DELETION));
            } else {
                int type = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_SQL),
                        MessageConfigLoader.getProperty(IMessagesConstants.DELETE_ALL_HISTORY_ALERT, profileName,
                                MPPDBIDEConstants.LINE_SEPARATOR));

                deleteSQLHistory(type, items);

            }
            resetSerialNumber();

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    private void deleteSQLHistory(int type, TableItem[] items) {
        this.manager = SQLHistoryFactory.getInstance();
        if (0 == type) {
            List<SQLHistoryItem> historyItems = new LinkedList<SQLHistoryItem>();
            int counter = 0;
            for (TableItem item : items) {
                if (!((SQLHistoryItem) item.getData()).isPinned()) {
                    historyItems.add((SQLHistoryItem) item.getData());
                    item.dispose();
                    counter++;
                }
            }
            if (0 != counter) {
                manager.deleteHistoryItems(historyItems);
            }
            sourceViewer.getDocument().set("");
        }
    }

    private void resetSerialNumber() {
        if (table != null) {
            TableItem[] items = table.getItems();
            int size = items.length;
            if (size <= 0) {
                enablebuttons(false);
                return;
            }

            for (int indx = 0; indx < size; indx++) {
                items[indx].setText(0, Integer.toString(indx + 1));
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PinsqlClass.
     */
    private final class PinsqlClass implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {

            TableItem[] items = table.getSelection();
            for (TableItem item : items) {
                item.setText(PIN_STATUS_COLUMN, MessageConfigLoader.getProperty(IMessagesConstants.SQL_HIST_PINNED));

                if (!((SQLHistoryItem) item.getData()).isPinned()) {
                    manager.setPinStatus((SQLHistoryItem) item.getData(), true);
                }
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class UnpinsqlClass.
     */
    private final class UnpinsqlClass implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {

            TableItem[] items = table.getSelection();
            for (TableItem item : items) {
                item.setText(PIN_STATUS_COLUMN, " ");
                if (((SQLHistoryItem) item.getData()).isPinned()) {
                    manager.setPinStatus((SQLHistoryItem) item.getData(), false);
                }

            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }

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
     * Gets the document.
     *
     * @param tble the tble
     * @return the document
     */
    public void getDocument(Table tble) {

    }

    private void loadQuerytoSQLTerminal() {

        String query = null;
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        SQLTerminal terminal = UIElement.getInstance().getTerminal(terminalParentId);
        TableItem[] items = table.getSelection();
        for (TableItem t : items) {
            query = t.getText(SQL_QUERY_COLUMN);
            sb.append(query);
            sb.append(EnvirnmentVariableValidator.validateAndGetLineSeperator());
        }

        if (null != terminal) {
            terminal.getTerminalCore().setSelectedQuery(sb.toString());
            terminal.resetSQLTerminalButton();
            terminal.resetAutoCommitButton();
            terminal.setModified(true);
            terminal.setModifiedAfterCreate(true);
        }
    }

    @SuppressWarnings("restriction")
    private void setDecoration() {
        ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();

        fSourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(sourceViewer, null, null, sharedColors);

        fSourceViewerDecorationSupport.setCursorLinePainterPreferenceKeys(IUserPreference.CURRENT_LINE_VISIBILITY,
                IUserPreference.CURRENTLINE_COLOR);
        fSourceViewerDecorationSupport.installDecorations();
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
     * Close.
     *
     * @return true, if successful
     */
    @Override
    public boolean close() {
        fSourceViewerDecorationSupport.uninstall();
        fSourceViewerDecorationSupport = null;
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
