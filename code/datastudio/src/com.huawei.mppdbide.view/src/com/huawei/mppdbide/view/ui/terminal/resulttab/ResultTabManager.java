/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.MemoryCleaner;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.ui.ResultSetWindow;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultTabManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ResultTabManager {

    /**
     * The main Folder Container.
     */
    protected CTabFolder mainFolderContainer = null;

    /**
     * the tabFolder
     */
    protected CTabFolder tabFolder = null;

    /**
     * the static Check tabFolder
     */
    protected CTabFolder staticChecktabFolder = null;

    /**
     * the resultSet Container
     */
    protected CTabItem resultSetContainer = null;

    /**
     * the static Check Container
     */
    protected CTabItem staticCheckContainer = null;

    /**
     * The parent.
     */
    protected Composite parent;

    /**
     * The result window counter.
     */
    protected int resultWindowCounter = 0;

    /**
     * The exec plan tab counter.
     */
    protected int execPlanTabCounter = 0;

    /**
     * The console message window.
     */
    protected ConsoleMessageWindow consoleMessageWindow;

    /**
     * The ui ID.
     */
    protected String uiID;

    /**
     * The event broker.
     */
    protected IEventBroker eventBroker;

    /**
     * The term connection.
     */
    protected TerminalExecutionConnectionInfra termConnection;

    /**
     * The part label.
     */
    protected String partLabel;

    private int dirtyTabCounter = 0;
    private MDirtyable dirtyHandler;
    private boolean isSQLTerminalcontext;
    private boolean isDoNotShowSaveChangesPopUp;
    private Map<String, Integer> resultTabCountMap;
    private final Object instanceLock = new Object();

    /**
     * Gets the main folder container.
     *
     * @return the main folder container
     */
    public CTabFolder getmainFolderContainer() {
        return mainFolderContainer;
    }

    /**
     * Gets the tab folder definite.
     *
     * @return the tab folder definite
     */
    public CTabFolder getTabFolderDefinite() {
        if (tabFolder == null) {
            createTabFolder();
            createMenu();
        }
        return tabFolder;
    }

    /**
     * Gets the static check folder definite.
     *
     * @return the static check folder definite
     */
    public CTabFolder getStaticCheckFolderDefinite() {
        return staticChecktabFolder;
    }

    /**
     * Gets the result set container.
     *
     * @return the result set container
     */
    public CTabItem getResultSetContainer() {
        return resultSetContainer;
    }

    /**
     * Gets the static check container.
     *
     * @return the static check container
     */
    public CTabItem getStaticCheckContainer() {
        return staticCheckContainer;
    }

    /**
     * Instantiates a new result tab manager.
     *
     * @param parentID the parent ID
     * @param termConnectionCopy the term connection copy
     * @param isSQLTermContext the is SQL term context
     */
    protected ResultTabManager(String parentID, TerminalExecutionConnectionInfra termConnectionCopy,
            boolean isSQLTermContext) {
        this.uiID = parentID;
        this.partLabel = parentID;
        this.termConnection = termConnectionCopy;
        this.isSQLTerminalcontext = isSQLTermContext;
        this.isDoNotShowSaveChangesPopUp = false;
        resultTabCountMap = new HashMap<String, Integer>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    /**
     * Instantiates a new result tab manager.
     *
     * @param parent the parent
     * @param parentID the parent ID
     * @param partLabel the part label
     * @param ebroker the ebroker
     * @param termConnectionCopy the term connection copy
     * @param isSQLTermContext the is SQL term context
     */
    public ResultTabManager(Composite parent, String parentID, String partLabel, IEventBroker ebroker,
            TerminalExecutionConnectionInfra termConnectionCopy, boolean isSQLTermContext) {
        this.parent = parent;
        this.uiID = parentID;
        this.partLabel = partLabel;
        this.eventBroker = ebroker;
        this.termConnection = termConnectionCopy;
        this.isSQLTerminalcontext = isSQLTermContext;
        this.isDoNotShowSaveChangesPopUp = false;
        resultTabCountMap = new HashMap<String, Integer>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        createTabFolder();
        createMenu();
        if (null != this.tabFolder) {
            this.tabFolder.addListener(SWT.Dispose, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    destroyResultTabs();
                }
            });
        }
    }

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum TABTYPE.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public enum TABTYPE {

        /**
         * The result set.
         */
        RESULT_SET,

        /**
         * The execution plan.
         */
        EXECUTION_PLAN
    }

    private void createTabFolder() {
        if (this.parent.isDisposed()) {
            return;
        }
        mainFolderContainer = new CTabFolder(this.parent, SWT.NONE);
        mainFolderContainer.setLayout(new GridLayout());
        mainFolderContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainFolderContainer.setTabPosition(SWT.BOTTOM);

        createResultSetContainer();

        mainFolderContainer.setSelection(resultSetContainer);
    }

    private void createResultSetContainer() {
        resultSetContainer = new CTabItem(mainFolderContainer, SWT.NONE);
        resultSetContainer.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_MANAGER));
        resultSetContainer.setData("resultmanager");
        resultSetContainer.setImage(IconUtility.getIconImage(IiconPath.ICO_CONSOLE_WINDOW, getClass()));
        resultSetContainer.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_MANAGER));

        Composite resultContainerCompoiste = new Composite(mainFolderContainer, SWT.NONE);
        resultContainerCompoiste.setLayout(new GridLayout(1, false));
        resultContainerCompoiste.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabFolder = new CTabFolder(resultContainerCompoiste, SWT.BORDER | SWT.NONE);
        tabFolder.setLayout(new GridLayout());
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        getConsoleMessageWindow(true);
        addtabFolderListener();
        resultSetContainer.setControl(resultContainerCompoiste);
    }

    /**
     * Creates the console.
     */
    public void createConsole() {
        CTabFolder tabFolderDefinite = getTabFolderDefinite();
        if (consoleMessageWindow == null && tabFolderDefinite != null) {
            consoleMessageWindow = new ConsoleMessageWindow();
            CTabItem consoleItem = new CTabItem(tabFolderDefinite, SWT.NONE, 0);
            consoleItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_TAB));
            consoleItem.setData("console");
            consoleItem.setImage(IconUtility.getIconImage(IiconPath.ICO_CONSOLE_WINDOW, getClass()));
            consoleItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.CONSOLE_TAB));
            Composite composite = new Composite(tabFolder, SWT.NONE);
            consoleMessageWindow.createConsoleWindow(composite);
            consoleItem.setControl(composite);
            tabFolderDefinite.setSelection(consoleItem);
        }
    }

    private void createMenu() {
        DSCTabFolder.createRightClkMenu(tabFolder);
    }

    /**
     * Sets the do not show save changes pop up flag.
     *
     * @param flag the new do not show save changes pop up flag
     */
    public void setDoNotShowSaveChangesPopUpFlag(boolean flag) {
        this.isDoNotShowSaveChangesPopUp = flag;
    }

    /**
     * Gets the do not show save changes pop up flag.
     *
     * @return the do not show save changes pop up flag
     */
    public boolean getDoNotShowSaveChangesPopUpFlag() {
        return this.isDoNotShowSaveChangesPopUp;
    }

    /**
     * Sets the dirty handler.
     *
     * @param terminalDirty the new dirty handler
     */
    public void setDirtyHandler(MDirtyable terminalDirty) {
        synchronized (instanceLock) {
            this.dirtyHandler = terminalDirty;
        }
    }

    /**
     * Modify dirty tab count.
     *
     * @param isIncrement the is increment
     */
    public void modifyDirtyTabCount(boolean isIncrement) {
        synchronized (instanceLock) {
            if (isIncrement) {
                this.dirtyTabCounter += 1;
                this.dirtyHandler.setDirty(this.dirtyTabCounter > 0);
            } else {
                this.dirtyTabCounter -= 1;
                this.dirtyHandler.setDirty(this.dirtyTabCounter == 0 ? false : true);
            }
        }
    }

    /**
     * Gets the console selected.
     *
     * @return the console selected
     */
    protected void getConsoleSelected() {
        if (tabFolder != null && !tabFolder.isDisposed()) {
            tabFolder.setSelection(0);
        }
    }

    /**
     * Addtab folder listener.
     */
    protected void addtabFolderListener() {
        tabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (event.item instanceof ResultTab) {
                    ResultTab resultTab = (ResultTab) event.item;
                    resultTab.handleFocus();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                // Ignore
            }
        });

        tabFolder.addCTabFolder2Listener(new CTabFolder2Listener() {

            @Override
            public void showList(CTabFolderEvent event) {
            }

            @Override
            public void restore(CTabFolderEvent event) {
            }

            @Override
            public void minimize(CTabFolderEvent event) {
            }

            @Override
            public void maximize(CTabFolderEvent event) {
            }

            @Override
            public void close(CTabFolderEvent event) {
                closeResultTab(event);
            }
        });

        parent.layout(true);
    }

    /**
     * Close result tab.
     *
     * @param event the event
     */
    protected void closeResultTab(CTabFolderEvent event) {
        CTabItem selection = null;
        disposeTabFolder();
        if (event.item.isDisposed()) {
            consoleMessageWindow = null;
        } else if (tabFolder != null) {
            String itemData = (String) event.item.getData();
            selection = getResultTabSelection(selection, itemData);

            // to check whether console is from result set or from
            // console
            // to identify whether data is from ctabFolder

            disposeConsoleMessageWindowOnCond(itemData);
            if (isResultOrExecutionTab(itemData)) {

                updateResultTabCountMapOnClose(itemData);
                if (null != tabFolder.getSelection()) {
                    if (!isResultTabClosed(event)) {
                        return;
                    }
                }
                setTabFolderSelection(selection);
            }
        }
        parent.layout(true);
        MemoryCleaner.cleanUpMemory();
    }

    private boolean isResultTabClosed(CTabFolderEvent event) {
        if (tabFolder.getSelection() == null) {
            return false;
        }
            String selectedTabData = (String) tabFolder.getSelection().getData();
            if (!"console".equalsIgnoreCase(selectedTabData)) {

                ResultTab selectedResultTab = (ResultTab) tabFolder.getSelection();
                // if tab is already dispose then no need to be
                // dispose
                if (null != selectedResultTab) {
                    event.doit = selectedResultTab.preDestroy();
                    if (event.doit) {
                        selectedResultTab.dispose();
                        selectedResultTab = null;
                        UIElement.getInstance().updateResultWindowCounterOnClose();
                    } else {
                        ResultSetWindow.setCancelForAllModified(false);
                        ResultSetWindow.setDiscardAllModified(false);
                        return false;
                    }

                }
            }
            ResultSetWindow.setCancelForAllModified(false);
            ResultSetWindow.setDiscardAllModified(false);
        return true;
    }

    private CTabItem getResultTabSelection(CTabItem selection, String itemData) {
        for (int i = 0; i < tabFolder.getItemCount(); i++) {
            CTabItem item = tabFolder.getItem(i);
            if (item != null && null != item.getData() && item.getData().equals(itemData)) {
                selection = tabFolder.getSelection();
                if (selection == item) {
                    selection = null;
                }
                tabFolder.setSelection(item);
                break;
            }
        }
        return selection;
    }

    private void updateResultTabCountMapOnClose(String itemData) {
        if (itemData.startsWith(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB),
                    resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB)) - 1);
        } else if (itemData.startsWith(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB),
                    resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB)) - 1);
        }
    }

    private boolean isResultOrExecutionTab(String itemData) {
        return itemData.contains(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))
                || itemData.contains(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB));
    }

    private void setTabFolderSelection(CTabItem selection) {
        if (selection != null) {
            tabFolder.setSelection(selection);
        }
    }

    private void disposeTabFolder() {
        if (tabFolder.getItems().length == 1) {
            tabFolder.dispose();
            tabFolder = null;
        }
    }

    private void disposeConsoleMessageWindowOnCond(String itemData) {
        if ("console".equals(itemData)) {
            consoleMessageWindow.getTextViewer().getControl().dispose();
            consoleMessageWindow = null;
        }
    }

    /**
     * Gets the console message window.
     *
     * @param isSelected the is selected
     * @return the console message window
     */
    public ConsoleMessageWindow getConsoleMessageWindow(boolean isSelected) {
        if (consoleMessageWindow == null) {
            createConsole();
        }
        if (isSelected) {
            getConsoleSelected();
        }
        return consoleMessageWindow;
    }

    /**
     * Gets the console window.
     *
     * @return the console window
     */
    public ConsoleMessageWindow getConsoleWindow() {
        return consoleMessageWindow;
    }

    /**
     * Creates the result.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoleDisplayData the console display data
     * @param queryExecSummary the query exec summary
     * @throws DatabaseCriticalException the database critical exception
     */
    public void createResult(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoleDisplayData,
            IQueryExecutionSummary queryExecSummary) throws DatabaseCriticalException {
        getTabFolderDefinite();
        String tooltip = null;

        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Composite composite = new Composite(tabFolder, SWT.NONE);
        updateResultTabCount();
        ResultTab resultItem1 = new ResultTab(tabFolder, SWT.CLOSE, composite, resultsetDisplaydata, consoleDisplayData,
                queryExecSummary, this, this.getTermConnection(), this.isSQLTerminalcontext);

        initializeResultTab(resultItem1);

        // If Tabfolder length is one, reset the result window counter to one.
        // Tabfolder length never be zero, since messages tab is non closable.
        setTabName(resultItem1, TABTYPE.RESULT_SET);
        if (resultTabCountMap.containsKey(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB),
                    resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB)) + 1);
        }
        resultWindowCounter++;

        resultItem1.setImage(IconUtility.getIconImage(IiconPath.ICO_RESULTSET_WINDOW, getClass()));

        tooltip = updateResultTabToolTip(queryExecSummary);

        resultItem1.setToolTipText(tooltip);

        getmainFolderContainer().setSelection(resultSetContainer);
        if (UserPreference.getInstance().isFocusOnFirstResult()) {
            if (tabFolder.getItemCount() == 0) {
                tabFolder.setSelection(resultItem1);
            }
        } else {
            tabFolder.setSelection(tabFolder.getItems().length - 1);
        }

        UIElement.getInstance().updateResultWindowCounter();
    }

    private String updateResultTabToolTip(IQueryExecutionSummary queryExecSummary) {
        String tooltip;
        String query = queryExecSummary.getQuery();
        int queryLength = query.length();
        if (queryLength > 50) {
            CharSequence tooltipStr = query.subSequence(0, 48) + "..";
            tooltip = tooltipStr.toString();
        } else {
            tooltip = query;
        }
        return tooltip;
    }

    private void initializeResultTab(ResultTab resultItem1) {
        try {
            resultItem1.init();
        } catch (OutOfMemoryError e) {
            if (!tabFolder.isDisposed()) {
                for (CTabItem tab : tabFolder.getItems()) {
                    if (null == tab.getData()) {
                        tab.dispose();
                    }
                }
            }
            StringBuilder errMsg = new StringBuilder(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY), errMsg.toString());
        }
    }

    private void updateResultTabCount() {
        if ((resultTabCountMap.containsKey(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))
                && resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB)) < 0)
                || tabFolder.getItems().length == 1) {
            resetResultWindowCounter();
        }
        if (!resultTabCountMap.containsKey(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB), 0);
        }
    }

    /**
     * Sets the tab name.
     *
     * @param resultItem1 the result item 1
     * @param tabType the tab type
     */
    protected void setTabName(ResultTab resultItem1, TABTYPE tabType) {
        String itemTextValue = "";
        int tabCounter = 0;
        switch (tabType) {
            case EXECUTION_PLAN: {
                itemTextValue = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB);
                tabCounter = execPlanTabCounter;
                break;
            }
            default: {
                itemTextValue = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB);
                tabCounter = resultWindowCounter;
                break;
            }
        }

        if (tabCounter > 0) {
            itemTextValue = itemTextValue + " (" + tabCounter + ')';
        }

        resultItem1.setText(itemTextValue);

        /*
         * the data is set to a specific string as it will be used during
         * closure. Refer to the CtabItem close handler code written here.
         */
        resultItem1.setData(itemTextValue);
    }

    /**
     * Destroy.
     */
    public void destroy() {

    }

    /**
     * Pre result tab generation.
     */
    public void preResultTabGeneration() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (tabFolder == null || !UserPreference.getInstance().isGenerateNewResultWindow()
                        || checkResultSetWindowBehaviour()) {
                    ResultSetWindow.setOpenNewTAb(false);
                    return;
                }

                DSCTabFolder.closeAllCleanUp(tabFolder);
            }
        });
    }

    private boolean checkResultSetWindowBehaviour() {
        return ResultSetWindow.isOpenNewTAb();
    }

    /**
     * Destroy result tabs.
     */
    protected void destroyResultTabs() {
        int consoleCnt = 0;
        String firstTabName = (String) tabFolder.getItem(0).getData();
        if ("console".equalsIgnoreCase(firstTabName)) {
            consoleCnt = 1;
        }

        int numofTabsToClose = this.tabFolder.getItemCount();
        ResultTab tab;
        /*
         * tabFolder itemCount keeps decreasing by 1 as we keep on destroying
         * tabs. So, use while condition. Do not use numofTabsToClose in looping
         * because that is fixed number.
         */
        while (this.tabFolder.getItemCount() > consoleCnt) {
            /*
             * as items gets disposed from tabFolder, itemCount keeps
             * decreasing. So, always dispose the result tab item at the
             * beginning
             */
            tab = (ResultTab) this.tabFolder.getItem(consoleCnt);
            // gets the item next to console, i.e. the first result tab
            tab.preDestroyWithoutDirtyCheck();
            tab.dispose();
            tab = null;
        }

        UIElement.getInstance().updateResultWindowCounterOnClose(numofTabsToClose - consoleCnt);
        this.resultTabCountMap.clear();
        this.resultTabCountMap = null;
        tabFolder.dispose();
        tabFolder = null;
        MemoryCleaner.cleanUpMemory();
    }

    /**
     * Force focus local console.
     */
    public void forceFocusLocalConsole() {
        if (null != this.tabFolder) {
            this.tabFolder.setSelection(0);
            this.tabFolder.forceFocus();
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
     * Sets the event broker.
     *
     * @param eventBroker the new event broker
     */
    public void setEventBroker(IEventBroker eventBroker) {
        this.eventBroker = eventBroker;
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    public String getPartID() {
        return uiID;
    }

    /**
     * Gets the m part label.
     *
     * @return the m part label
     */
    public String getmPartLabel() {
        return partLabel;
    }

    /**
     * Sets the m part label.
     *
     * @param partLbl the new m part label
     */
    public void setmPartLabel(String partLbl) {
        this.partLabel = partLbl;
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
     * Update exec plan result.
     *
     * @param analysedPlanOutput the analysed plan output
     * @param consoleDisplayData the console display data
     * @param queryExecSummary the query exec summary
     * @param tab the tab
     */
    public void updateExecPlanResult(UIModelAnalysedPlanNode analysedPlanOutput, IConsoleResult consoleDisplayData,
            IQueryExecutionSummary queryExecSummary, ResultTab tab) {

    }

    /**
     * Creates the exec plan result.
     *
     * @param analysedPlanOutput the analysed plan output
     * @param consoleDisplayData the console display data
     * @param queryExecSummary the query exec summary
     * @param totalRuntime the total runtime
     */
    public void createExecPlanResult(UIModelAnalysedPlanNode analysedPlanOutput, IConsoleResult consoleDisplayData,
            IQueryExecutionSummary queryExecSummary, double totalRuntime) {
        getTabFolderDefinite();

        String tooltip = null;

        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
        final Composite composite = new Composite(tabFolder, SWT.NONE);

        ExecutionPlanTab planItem1 = new ExecutionPlanTab(tabFolder, SWT.CLOSE, composite, analysedPlanOutput,
                consoleDisplayData, queryExecSummary, this, this.getTermConnection(), totalRuntime);

        planItem1.init();
        planItem1.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(final DisposeEvent e) {
                if (!composite.isDisposed()) {
                    planItem1.setDispose();
                    composite.dispose();
                }
            }
        });

        // If Tabfolder length is one, reset the result window counter to one.
        // Tabfolder length never be zero, since messages tab is non closable.
        if (resultTabCountMap.containsKey(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB))
                && resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB)) <= 0
                || tabFolder.getItems().length == 1) {
            resetExecutionPlanResultWindowCounter();
        } else if (!resultTabCountMap
                .containsKey(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB), 0);
        }
        setTabName(planItem1, TABTYPE.EXECUTION_PLAN);

        if (resultTabCountMap.containsKey(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB))) {
            resultTabCountMap.put(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB),
                    resultTabCountMap.get(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_TAB)) + 1);
        }
        execPlanTabCounter++;

        // have to use different icon
        planItem1.setImage(IconUtility.getIconImage(IiconPath.ICO_EXEC_PLAN, getClass()));

        tooltip = updateResultTabToolTip(queryExecSummary);

        planItem1.setToolTipText(tooltip);

        if (UserPreference.getInstance().isFocusOnFirstResult()) {
            if (tabFolder.getItemCount() == 0) {
                tabFolder.setSelection(planItem1);
            }
        } else {
            tabFolder.setSelection(tabFolder.getItems().length - 1);
        }

        UIElement.getInstance().updateResultWindowCounter();
    }

    /**
     * Reset result window counter.
     */
    public void resetResultWindowCounter() {
        this.resultWindowCounter = 0;
    }

    /**
     * Reset execution plan result window counter.
     */
    public void resetExecutionPlanResultWindowCounter() {
        this.execPlanTabCounter = 0;
    }

    /**
     * Gets the result tab count map.
     *
     * @return the result tab count map
     */
    public Map<String, Integer> getResultTabCountMap() {
        return resultTabCountMap;
    }
}
