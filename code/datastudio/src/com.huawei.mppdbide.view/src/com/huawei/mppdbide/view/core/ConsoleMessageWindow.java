/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.progress.UIJob;

import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.IMessageQueue;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.utils.messaging.MessageType;
import com.huawei.mppdbide.view.ui.DBAssistantWindow;
import com.huawei.mppdbide.view.utils.IDEMemoryAnalyzer;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class ConsoleMessageWindow. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ConsoleMessageWindow extends SelectMenuItem {
    private TextViewer textViewer;

    private Document doc;

    private boolean hasTableSkippingNotice;

    private MenuItem menuClear;

    private MessageQueue msgQueue;

    /**
     * Title: enum Description: The Enum LOGTYPE. Copyright (c) Huawei
     * Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private enum LOGTYPE {
        ERROR, WARNING, INFO, FATAL, NOTICE, LOGTYPE_BUTT
    }

    /**
     * Instantiates a new console message window.
     */
    public ConsoleMessageWindow() {
        doc = new Document("");
        doc.set("");
        msgQueue = new MessageQueue();
    }

    /**
     * Creates the console window.
     *
     * @param parent the parent
     */

    public void createConsoleWindow(Composite parent) {

        /* setting parent layout */
        GridLayout gridlayout = new GridLayout();
        parent.setLayout(gridlayout);
        /* handler control */
        Composite handlerComposite = getHandlerComposite(parent);

        // create ToolBar
        GridData toolbargriddata = new GridData();
        toolbargriddata.horizontalAlignment = SWT.FILL;

        ToolBar toolbar = new ToolBar(handlerComposite, SWT.FLAT | SWT.FOCUSED);
        toolbar.setLayoutData(toolbargriddata);
        toolbar.pack();

        addClearToolItem(toolbar);

        textViewer = new TextViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        textViewer.getTextWidget().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_CONSOLE_TEXT_001");
        textViewer.setDocument(doc);
        textViewer.setEditable(false);

        addGridDataToTextViewer();

        // Add select all key binding
        textViewer.getTextWidget().addKeyListener(new SourceEditorKeyListener(textViewer));

        Menu menu = new Menu(getControl());
        textViewer.getTextWidget().setMenu(menu);

        textViewer.getTextWidget().setMenu(menu);
        textViewer.addSelectionChangedListener(addTextViewerSelectionListener());

        addCopyMenuItem(menu);
        addSelectAllMenuItem(menu);
        addClearMenuItem(menu);
        menu.addMenuListener(addMenuListenerOnMenu());

        // nested class to print message from queue

        LazyloadConsoleMessages loadConsoleMsg = new LazyloadConsoleMessages(Display.getDefault(),
                "Console msg loading", this);
        loadConsoleMsg.schedule();
    }

    private Composite getHandlerComposite(Composite parent) {
        Composite handlerComposite = new Composite(parent, SWT.NONE);
        GridLayout handlerGridLayout = new GridLayout(2, false);
        handlerGridLayout.marginTop = -4;
        handlerComposite.setLayout(handlerGridLayout);
        GridData handlerGridData = new GridData();
        handlerGridData.heightHint = 22;
        handlerComposite.setLayoutData(handlerGridData);
        return handlerComposite;
    }

    private void addGridDataToTextViewer() {
        GridData textViewerGridData = new GridData();
        textViewerGridData.verticalAlignment = GridData.FILL;
        textViewerGridData.grabExcessHorizontalSpace = true;
        textViewerGridData.grabExcessVerticalSpace = true;
        textViewerGridData.horizontalAlignment = GridData.FILL;
        textViewer.getControl().setLayoutData(textViewerGridData);
    }

    private void addClearToolItem(ToolBar toolbar) {
        ToolItem btnClear = new ToolItem(toolbar, SWT.NONE);
        btnClear.setData(MPPDBIDEConstants.SWTBOT_KEY, "SQL_CONSOLE");
        btnClear.setImage(IconUtility.getIconImage(IiconPath.ICO_CLEAR, getClass()));
        btnClear.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_CONSOLE));
        btnClear.addSelectionListener(new ClearSelectionListener());
    }

    private MenuListener addMenuListenerOnMenu() {
        return new MenuListener() {

            @Override
            public void menuShown(MenuEvent event) {
                contextMenuAboutToShow();
            }

            @Override
            public void menuHidden(MenuEvent event) {

            }
        };
    }

    private ISelectionChangedListener addTextViewerSelectionListener() {
        return new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();

                String selectionStr = ((TextSelection) selection).getText();

                if (selectionStr != null && !"".equals(selectionStr)) {
                    DBAssistantWindow.execErr(selectionStr);
                }
            }
        };
    }

    /**
     * Context menu about to show.
     */
    private void contextMenuAboutToShow() {

        if (textViewer.getDocument().get().isEmpty()) {
            menuClear.setEnabled(false);
            getMenuSelectAll().setEnabled(false);
        } else {
            menuClear.setEnabled(true);
            getMenuSelectAll().setEnabled(true);
        }
        if (textViewer.getTextWidget().getSelectionText().isEmpty()) {
            getMenuCopy().setEnabled(false);
        } else {
            getMenuCopy().setEnabled(true);
        }

    }

    /**
     * The listener interface for receiving clearSelection events. The class
     * that is interested in processing a clearSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addClearSelectionListener<code>
     * method. When the clearSelection event occurs, that object's appropriate
     * method is invoked. ClearSelectionEvent
     */
    private final class ClearSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            clear();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * Log msgs on console window.
     *
     * @param logMsg the log msg
     */
    private void logMsgsOnConsoleWindow(String logMsg) {
        if (textViewer == null || textViewer.getControl().isDisposed()) {
            return;
        }
        String newLog = doc.get() + logMsg;
        doc.set(newLog);

        int numberOfLines = doc.getNumberOfLines();
        int docLength = doc.getLength();

        if (numberOfLines > UserPreference.getInstance().getConsoleLineCount()) {
            int offset = 0;

            try {
                offset = doc.getLineOffset(numberOfLines - UserPreference.getInstance().getConsoleLineCount());
                doc.set(doc.get(offset, docLength - offset));
            } catch (BadLocationException e) {
                // Logically this should not be hit
                // Can this scenario be logged again? Will it be valid?
                String errLogMsg = MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_ERROR_MSG);
                if (MPPDBIDELoggerUtility.isDebugEnabled()) {
                    MPPDBIDELoggerUtility.debug(errLogMsg + ";"
                            + MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_LINE_NUMBER_MSG)
                            + numberOfLines
                            + MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_OFFSET_MSG) + offset
                            + ";" + MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_LENGTH_MSG)
                            + docLength);
                }

                newLog = generateLogEntry(newLog, errLogMsg, LOGTYPE.ERROR);
                doc.set(newLog);
            } finally {
                numberOfLines = doc.getNumberOfLines();
                docLength = doc.getLength();
            }
        }

        /*
         * Only if console window is open display it otherwise populate it.
         */

        try {
            textViewer.revealRange(doc.getLineOffset(numberOfLines - 1), docLength);

            // Fix for: Console window cursor position will not
            // be updated along with the contents.
            textViewer.setSelectedRange(docLength, 0);
        } catch (BadLocationException ex) {
            // Logically this should not be hit
            // Can this scenario be logged again? Will it be valid?
            String errLogMsg = MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_ERROR_SCROLLING_MSG);
            if (MPPDBIDELoggerUtility.isDebugEnabled()) {
                MPPDBIDELoggerUtility.debug(errLogMsg + "; "
                        + MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_LINE_NUMBER_MSG)
                        + numberOfLines + ";"
                        + MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_LENGTH_MSG) + docLength);
            }

            doc.set(generateLogEntry(newLog, errLogMsg, LOGTYPE.ERROR));
        }
    }

    /**
     * Append log entry.
     *
     * @param logMessage the log message
     * @param logtype the logtype
     * @param strBlder the str blder
     */
    private void appendLogEntry(String logMessage, LOGTYPE logtype, StringBuilder strBlder) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
        if (logtype != null) {
            strBlder.append(MPPDBIDEConstants.LINE_SEPARATOR).append('[').append(simpleDateFormat.format(new Date()))
            .append("] : ").append('[').append(logtype.name()).append("] ").append(logMessage);
        }        
    }

    /**
     * Log msg on display thread.
     *
     * @param logMsg the log msg
     */
    private void logMsgOnDisplayThread(String logMsg) {
        Display.getDefault().asyncExec(new LogMsgOnUICallback(logMsg));
    }

    /**
     * Title: class Description: The Class LogMsgOnUICallback. Copyright (c)
     * Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class LogMsgOnUICallback implements Runnable {
        private String logMessage;

        /**
         * Instantiates a new log msg on UI callback.
         *
         * @param logMsg the log msg
         */
        public LogMsgOnUICallback(String logMsg) {
            this.logMessage = logMsg;
        }

        @Override
        public void run() {
            logMsgsOnConsoleWindow(this.logMessage);
        }
    }

    /**
     * Log.
     *
     * @param logMessage the log message
     * @param logtype the logtype
     */
    public void log(String logMessage, LOGTYPE logtype) {
        StringBuilder logMsgBlr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        appendLogEntry(logMessage, logtype, logMsgBlr);
        logMsgsOnConsoleWindow(logMsgBlr.toString());
    }

    /**
     * Log info.
     *
     * @param logMessage the log message
     */
    public void logInfo(String logMessage) {
        log(logMessage, LOGTYPE.INFO);
    }

    /**
     * Log warning.
     *
     * @param logMessage the log message
     */
    public void logWarning(String logMessage) {
        log(logMessage, LOGTYPE.WARNING);
    }

    /**
     * Log fatal.
     *
     * @param logMessage the log message
     */
    public void logFatal(String logMessage) {
        log(logMessage, LOGTYPE.FATAL);
    }

    /**
     * Log error.
     *
     * @param logMessage the log message
     */
    public void logError(String logMessage) {
        log(logMessage, LOGTYPE.ERROR);
    }

    /**
     * Log notice.
     *
     * @param logMessage the log message
     */
    public void logNotice(String logMessage) {
        log(logMessage, LOGTYPE.NOTICE);
    }

    /**
     * Log info in UI.
     *
     * @param logMessage the log message
     */

    public void logInfoInUI(final String logMessage) {
        prepareAndLogMsgOnUI(logMessage, LOGTYPE.INFO);
    }

    /**
     * Log warning in UI.
     *
     * @param logMessage the log message
     */
    public void logWarningInUI(final String logMessage) {
        prepareAndLogMsgOnUI(logMessage, LOGTYPE.WARNING);
    }

    /**
     * Log fatal in UI.
     *
     * @param logMessage the log message
     */
    public void logFatalInUI(final String logMessage) {
        prepareAndLogMsgOnUI(logMessage, LOGTYPE.FATAL);
    }

    /**
     * Log error in UI.
     *
     * @param logMessage the log message
     */
    public void logErrorInUI(final String logMessage) {
        prepareAndLogMsgOnUI(logMessage, LOGTYPE.ERROR);
    }

    /**
     * Prepare and log msg on UI.
     *
     * @param logMessage the log message
     * @param type the type
     */
    private void prepareAndLogMsgOnUI(String logMessage, LOGTYPE type) {
        StringBuilder blr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        appendLogEntry(logMessage, type, blr);
        logMsgOnDisplayThread(blr.toString());
        if (IDEMemoryAnalyzer.is90PercentReached()) {
            logWarning(MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_WINDOW_MEMORY_MSG)
                    + IDEMemoryAnalyzer.getTotalUsedMemoryPercentage() + '%');
        }
    }

    /**
     * Log notice in UI.
     *
     * @param logMessage the log message
     */
    public void logNoticeInUI(final String logMessage) {
        prepareAndLogMsgOnUI(logMessage, LOGTYPE.NOTICE);
    }

    /**
     * Adds the clear menu item.
     *
     * @param menu the menu
     */
    private void addClearMenuItem(Menu menu) {
        menuClear = new MenuItem(menu, SWT.PUSH);

        menuClear.setText(MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_CONSOLE));
        menuClear.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                clear();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
        menuClear.setImage(IconUtility.getIconImage(IiconPath.ICO_CLEAR, this.getClass()));
    }

    /**
     * Clear.
     */
    public void clear() {
        doc.set("");
    }

    /**
     * Checks if is doc empty.
     *
     * @return true, if is doc empty
     */
    public boolean isDocEmpty() {
        return 0 == doc.getLength();
    }

    /**
     * On focus.
     */
    public void onFocus() {
        textViewer.getControl().setFocus();
    }

    /**
     * Select all doc text.
     */
    public void selectAllDocText() {
        textViewer.doOperation(ITextOperationTarget.SELECT_ALL);
    }

    /**
     * Copy doc text.
     */
    public void copyDocText() {
        textViewer.doOperation(ITextOperationTarget.COPY);
    }

    /**
     * Gets the control.
     *
     * @return the control
     */
    public Control getControl() {
        return textViewer.getControl();
    }

    /**
     * Prints the BL queue messages to console.
     *
     * @param msgQueue2 the msg queue 2
     */
    private void printBLQueueMessagesToConsole(MessageQueue msgQueue2) {
        IMessageQueue messageQueue = msgQueue2;
        int batch = UserPreference.getInstance().getBatchMsgSize();
        int printableSize = UserPreference.getInstance().getConsoleLineCount();
        int totalMsgInQueue = messageQueue.size();
        final int printBatchSize = 10;

        hasTableSkippingNotice = false;

        if (totalMsgInQueue > printableSize) {
            for (int i = 0; i < (totalMsgInQueue - printableSize); i++) {
                messageQueue.pop();
            }

            totalMsgInQueue = printableSize;
        }

        batch = findBatchSize(totalMsgInQueue, batch);
        StringBuilder logBldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        Message msg;

        for (int cnt = 0; cnt < batch; cnt++) {
            msg = messageQueue.pop();
            if (null != msg) {
                appendLogEntry(msg.getMessage(), getLogTypeForMessageType(msg.getType()), logBldr);

                if (cnt % printBatchSize == 0) {
                    logMsgOnDisplayThread(logBldr.toString());
                    logBldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
                }
            }

            if (logBldr.length() > 0) {
                logMsgOnDisplayThread(logBldr.toString());
                logBldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            }
        }

    }

    /**
     * Find batch size.
     *
     * @param size the size
     * @param batch the batch
     * @return the int
     */
    private int findBatchSize(int size, int batch) {
        int batchSize;

        if (0 == size) {
            batchSize = 0;
        } else if (size > batch) {
            batchSize = batch;
        } else {
            batchSize = size;
        }

        return batchSize;
    }

    /**
     * Gets the log type for message type.
     *
     * @param msgType the msg type
     * @return the log type for message type
     */
    private LOGTYPE getLogTypeForMessageType(MessageType msgType) {
        LOGTYPE logtype = null;

        switch (msgType) {
            case INFO: {
                logtype = LOGTYPE.INFO;
                break;
            }
            case WARN: {
                logtype = LOGTYPE.WARNING;
                break;
            }
            case NOTICE: {
                logtype = LOGTYPE.NOTICE;
                break;
            }
            case ERROR: {
                logtype = LOGTYPE.ERROR;
                break;
            }
        }

        return logtype;
    }

    /**
     * Checks for table skipping notice.
     *
     * @return true, if successful
     */
    public boolean hasTableSkippingNotice() {
        return hasTableSkippingNotice;
    }

    /**
     * Generate log message to the standard format.
     *
     * @param currentLog the current log
     * @param logMessage the log message
     * @param logtype the logtype
     * @return the string
     */
    private String generateLogEntry(String currentLog, String logMessage, LOGTYPE logtype) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
        StringBuilder strBlder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        strBlder.append(currentLog);
        strBlder.append(MPPDBIDEConstants.LINE_SEPARATOR).append('[').append(simpleDateFormat.format(new Date()))
                .append("] : ").append('[').append(logtype.name()).append("] ").append(logMessage);

        return strBlder.toString();
    }

    /**
     * Gets the text viewer.
     *
     * @return the text viewer
     */
    public TextViewer getTextViewer() {
        return textViewer;
    }

    /**
     * Gets the msg queue.
     *
     * @return the msg queue
     */
    public MessageQueue getMsgQueue() {
        return msgQueue;
    }

    /**
     * Title: class Description: The Class LazyloadConsoleMessages. Copyright
     * (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class LazyloadConsoleMessages extends UIJob {
        private static final int SCHEDULE_TIME = 250;

        private ConsoleMessageWindow consolewin;

        private MessageQueue msgQueue;

        /**
         * Instantiates a new lazyload console messages.
         *
         * @param jobDisplay the job display
         * @param name the name
         * @param consoleMessageWindow the console message window
         */
        private LazyloadConsoleMessages(Display jobDisplay, String name, ConsoleMessageWindow consoleMessageWindow) {
            super(jobDisplay, name);
            this.consolewin = consoleMessageWindow;
            this.msgQueue = consolewin.getMsgQueue();
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor arg0) {
            if (!msgQueue.isEmpty()) {
                consolewin.printBLQueueMessagesToConsole(msgQueue);
            }
            if (!getTextViewer().getControl().isDisposed()) {
                schedule(SCHEDULE_TIME);
            }
            return Status.OK_STATUS;
        }

    }

    /**
     * Log info.
     *
     * @param consoleMsgsToShow the console msgs to show
     */
    public void logInfo(IConsoleResult consoleMsgsToShow) {
        logByType(LOGTYPE.INFO, consoleMsgsToShow);
        logByType(LOGTYPE.NOTICE, consoleMsgsToShow.getHintMessages());
    }

    private void logByType(LOGTYPE logType, List<String> messages) {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (String str : messages) {
            appendLogEntry(str, logType, sb);
        }
        this.logMsgsOnConsoleWindow(sb.toString());
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        this.doc = null;
        this.msgQueue = null;
    }
}
