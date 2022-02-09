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

package org.opengauss.mppdbide.view.prefernces;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.HelpEvent;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.autosave.AutoSaveManager;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class PreferencesHandler.
 *
 * @since 3.0.0
 */
public class PreferencesHandler {
    @Optional
    @Inject
    private IWorkbench workbench;
    private StatusMessage statusMessage;
    private PreferenceManager manager;
    private PreferenceStore store;
    private Button okButton;
    private Button cancelButton;
    private volatile IPreferenceNode lastSuccessfulNode = null;

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        manager = createPreferencePageHierarchy();
        store = PreferenceWrapper.getInstance().getPreferenceStore();
        DSPreferenceDialog dlg = new DSPreferenceDialog(null, manager);

        IPreferenceNode[] node = getGeneralNode().getSubNodes();

        if (PreferenceWrapper.getInstance().isRestartSkipped()) {
            dlg.setHelpAvailable(true);
        }
        dlg.setPreferenceStore(store);
        if (node.length > 0) {
            dlg.setSelectedNode(node[0].getId());
        }
        dlg.open();
    }

    /**
     * Creates the preference page hierarchy.
     *
     * @return the preference manager
     */
    public PreferenceManager createPreferencePageHierarchy() {

        PreferenceManager preferenceManager = new PreferenceManager();

        preferenceManager.addToRoot(getGeneralNode());
        preferenceManager.addToRoot(getSQLTerminalNode());
        preferenceManager.addToRoot(getSecurityNode());
        preferenceManager.addToRoot(getEnvironmentNode());
        preferenceManager.addToRoot(getResultManagementNode());
        preferenceManager.addToRoot(getExportManagementNode());
        preferenceManager.addToRoot(getDateTimeManagementNode());
        preferenceManager.addToRoot(getDebugPreferenceNode());

        return preferenceManager;

    }

    private IPreferenceNode getDebugPreferenceNode() {
        String id = MessageConfigLoader.getProperty(IMessagesConstants.DEBUG_PREFREENCE_ID);
        PreferenceNode debugNode = new PreferenceNode(id,
                new DebugPreferencePage(id));
        return debugNode;
    }

    private IPreferenceNode getDateTimeManagementNode() {
        PreferenceNode dateTimeNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.DATE_TIME_PREFERENCES),
                new DateTimePreferencePage());
        return dateTimeNode;
    }

    /**
     * Gets the export management node.
     *
     * @return the export management node
     */
    private IPreferenceNode getExportManagementNode() {
        PreferenceNode exportImportNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_IMPORT_PREFERENCES),
                new ExportImportPreferencePage());

        PreferenceNode exportDDLNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_PREFERENCES), new ExportDDLPreferencePage());

        exportImportNode.add(exportDDLNode);
        return exportImportNode;

    }

    /**
     * Gets the result management node.
     *
     * @return the result management node
     */
    private IPreferenceNode getResultManagementNode() {
        PreferenceNode resultManagement = new PreferenceNode(MessageConfigLoader.getProperty("Result Management"),
                new ResultManagementPreferencePage());
        PreferenceNode resultManagementViewData = new PreferenceNode("Result Management/View Data",
                new ResultManagementViewDataPreferencePage());
        PreferenceNode resultManagementEditTable = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.EDITTABLE_PREFERENCE_OPTION),
                new ResultManagementEditTablePreferencePage());
        resultManagement.add(resultManagementViewData);
        resultManagement.add(resultManagementEditTable);
        return resultManagement;
    }

    /**
     * Gets the general node.
     *
     * @return the general node
     */
    private PreferenceNode getGeneralNode() {
        // General
        PreferenceNode generalNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.GENERAL_NODE), new GeneralPeferencePage());
        PreferenceNode shortCutNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.SHORTCUT_MAPPER),
                new ShortCutMapperPreferencePage());
        PreferenceNode objectBrowserNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER),
                new ObjectBrowserPreferncePage());

        generalNode.add(shortCutNode);
        generalNode.add(objectBrowserNode);
        return generalNode;
    }

    /**
     * Gets the SQL terminal node.
     *
     * @return the SQL terminal node
     */
    public PreferenceNode getSQLTerminalNode() {
        // Editor
        PreferenceNode editorNode = new PreferenceNode(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_NODE),
                new SQLTerminalPreferencePage());
        PreferenceNode colorNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_TITLE),
                new SyntaxColorPreferncePage());
        PreferenceNode sqlHistory = new PreferenceNode(MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY),
                new SQLHistoryAndQueryPreference());

        PreferenceNode templatesNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_TITLE),
                new DSTemplatePreferencePage());

        PreferenceNode foldingNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.CODE_TEMPLATE_PREFPAGE_TITLE),
                new DSFoldingPreferencePage());

        PreferenceNode formatterNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.PREF_FORMATTER_SETTING),
                new DSFormatterPreferencePage());
        PreferenceNode transactionNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.PREF_FOLDING_SETTING),
                new DSTransactionPreferencePage());

        PreferenceNode fontNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.PREF_TRANSACTION_SETTING),
                new DSFontPreferencePage());
        PreferenceNode autoCompleteNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.AUTO_COMPLETE_SETTING),
                new AutoCompletePreferencePage());

        editorNode.add(colorNode);
        editorNode.add(sqlHistory);
        editorNode.add(templatesNode);
        editorNode.add(formatterNode);
        editorNode.add(transactionNode);
        editorNode.add(foldingNode);
        editorNode.add(fontNode);

        editorNode.add(autoCompleteNode);
        return editorNode;
    }

    /**
     * Gets the security node.
     *
     * @return the security node
     */
    private PreferenceNode getSecurityNode() {
        // Security
        PreferenceNode securityNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_NODE), new SecurityPreferencePage());

        PreferenceNode passwordNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_PREFERENCE_OPTION),
                new PasswordPreferencePage());
        PreferenceNode securtiyDisclaimerNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_WARNING_OPTION),
                new SecurityDisclaimerPreferencePage());
        securityNode.add(passwordNode);
        securityNode.add(securtiyDisclaimerNode);
        return securityNode;
    }

    /**
     * Gets the environment node.
     *
     * @return the environment node
     */
    private PreferenceNode getEnvironmentNode() {
        // Environment
        PreferenceNode environmentNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.ENVIRONMENT_NODE), new EnvironmentPreferencePage());
        PreferenceNode sessionSettingNode = new PreferenceNode(
                MessageConfigLoader.getProperty(IMessagesConstants.SESSION_SETTNG_NODE),
                new SessionSettingPreferencePage());
        environmentNode.add(sessionSettingNode);
        return environmentNode;
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
     * @param statusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SavePreferenceUIWorker.
     */
    private static final class SavePreferenceUIWorker extends UIWorkerJob {

        private IWorkbench workbench;
        private PreferenceStore store;
        private StatusMessage statusMsg;

        /**
         * Instantiates a new save preference UI worker.
         *
         * @param store the store
         * @param statusMsg the status msg
         * @param workbench the workbench
         */
        private SavePreferenceUIWorker(PreferenceStore store, StatusMessage statusMsg, IWorkbench workbench) {
            super("Preferences", null);
            this.store = store;
            this.statusMsg = statusMsg;
            this.workbench = workbench;

        }

        @Override
        public Object doJob() throws MPPDBIDEException, Exception {
            store.save();
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            askForRestart();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            handleException();

        }

        /**
         * Handle exception.
         */
        private void handleException() {
            PreferenceWrapper.getInstance().setNeedRestart(false);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_SAVE_FAIL_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_SYNTAX_COLORING_SAVE_FAIL_MSG));
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            handleException();

        }

        @Override
        public void onExceptionUIAction(Exception e) {
            handleException();

        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (null != bttmStatusBar) {
                bttmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        /**
         * Ask for restart.
         */
        public void askForRestart() {
            if (!PreferenceWrapper.getInstance().isPreferenceApply()
                    && (PreferenceWrapper.getInstance().isChangeDone()
                            || PreferenceWrapper.getInstance().isDefaultStore())
                    && PreferenceWrapper.getInstance().isNeedRestart()) {
                int result = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_APP_RESTART_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_APP_RESTART_MSG),
                        MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                        MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));
                PreferenceWrapper.getInstance().setPreferenceApply(false);
                PreferenceWrapper.getInstance().setChangeDone(false);
                PreferenceWrapper.getInstance().setDefaultStore(false);
                if (result == 0) {
                    final IJobManager jm = Job.getJobManager();
                    Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
                    if (allJobs.length != 0) {

                        int result1 = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                                MessageConfigLoader.getProperty(IMessagesConstants.DS_RESTART_CONFIRMATION_TITLE),
                                MessageConfigLoader.getProperty(IMessagesConstants.DS_RESTART_MSG_FOR_JOBS),
                                MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK),
                                MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL));
                        if (result1 == 0) {
                            UIDisplayFactoryProvider.getUIDisplayStateIf().cleanUponWindowClose();
                        } else {
                            PreferenceWrapper.getInstance().setRestartSkipped(true);
                            return;
                        }
                    }
                    try {
                        store.setValue("IsRestarted", Boolean.TRUE);
                        store.save();
                    } catch (IOException exception) {
                        MPPDBIDELoggerUtility.error("Prefence.save returned exception while saving to disk :",
                                exception);
                    }
                    AutoSaveManager.getInstance().gracefulExit();
                    workbench.restart();

                } else {
                    PreferenceWrapper.getInstance().setRestartSkipped(true);
                }
            }

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DSPreferenceDialog.
     */
    private final class DSPreferenceDialog extends PreferenceDialog {

        /**
         * Instantiates a new DS preference dialog.
         *
         * @param parentShell the parent shell
         * @param manager the manager
         */
        public DSPreferenceDialog(Shell parentShell, PreferenceManager manager) {
            super(parentShell, manager);
        }

        // used only for overridden
        void showPageFlippingAbortDialog1() {

        }

        void selectCurrentPageAgain1() {
            if (lastSuccessfulNode == null) {
                return;
            }

            getTreeViewer().setSelection(new StructuredSelection(lastSuccessfulNode));
            getCurrentPage().setVisible(true);
        }

        void clearSelectedNode1() {
            setSelectedNodePreference(null);
        }

        /**
         * Adds the listeners.
         *
         * @param viewer the viewer
         */
        protected void addListeners(final TreeViewer viewer) {
            viewer.addPostSelectionChangedListener(new ViewerPostSelectionChangeListener(viewer));
            ((Tree) viewer.getControl()).addSelectionListener(new TreeSelectionListener(viewer));
            // Register help listener on the tree to use context sensitive
            // help
            viewer.getControl().addHelpListener(new ViewerControlHelpListener());
        }

        /**
         * The listener interface for receiving viewerControlHelp events. The
         * class that is interested in processing a viewerControlHelp event
         * implements this interface, and the object created with that class is
         * registered with a component using the component's
         * <code>addViewerControlHelpListener<code> method. When the
         * viewerControlHelp event occurs, that object's appropriate method is
         * invoked.
         *
         * ViewerControlHelpEvent
         */
        private class ViewerControlHelpListener implements HelpListener {
            @Override
            public void helpRequested(HelpEvent event) {
                if (getCurrentPage() == null) { // no current page? open
                                                // dialog's help
                    openDialogHelp();
                    return;
                }
                // A) A typical path: the current page has registered
                // its own help link
                // via WorkbenchHelpSystem#setHelp. When just call it and let
                // it handle the help request.
                Control pageControl = getCurrentPage().getControl();
                if (pageControl != null && pageControl.isListening(SWT.Help)) {
                    getCurrentPage().performHelp();
                    return;
                }

                /*
                 * B) Less typical path: no standard listener has been created
                 * for the page. In this case we may or may not have an override
                 * of page's performHelp method. 1) Try to get default help
                 * opened for the dialog;
                 */
                openDialogHelp();
                // 2) Next call currentPage's performHelp method. If it was
                // overridden, it might switch help
                // to something else.
                getCurrentPage().performHelp();
            }

            /**
             * Open dialog help.
             */
            private void openDialogHelp() {
                if (getPageContainer() == null) {
                    return;
                }
                for (Control currentControl = getPageContainer(); currentControl != null; currentControl = currentControl
                        .getParent()) {
                    if (currentControl.isListening(SWT.Help)) {
                        currentControl.notifyListeners(SWT.Help, new Event());
                        break;
                    }
                }
            }
        }

        /**
         * The listener interface for receiving treeSelection events. The class
         * that is interested in processing a treeSelection event implements
         * this interface, and the object created with that class is registered
         * with a component using the component's
         * <code>addTreeSelectionListener<code> method. When the treeSelection
         * event occurs, that object's appropriate method is invoked.
         *
         * TreeSelectionEvent
         */
        private class TreeSelectionListener implements SelectionListener {
            private TreeViewer viewer;

            /**
             * Instantiates a new tree selection listener.
             *
             * @param viewer the viewer
             */
            public TreeSelectionListener(TreeViewer viewer) {
                this.viewer = viewer;
            }

            @Override
            public void widgetDefaultSelected(final SelectionEvent event) {
                ISelection selection = viewer.getSelection();
                if (selection.isEmpty()) {
                    return;
                }
                IPreferenceNode singleSelection = getSingleSelection(selection);
                boolean expanded = viewer.getExpandedState(singleSelection);
                viewer.setExpandedState(singleSelection, !expanded);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {

            }
        }

        /**
         * The listener interface for receiving viewerPostSelectionChange
         * events. The class that is interested in processing a
         * viewerPostSelectionChange event implements this interface, and the
         * object created with that class is registered with a component using
         * the component's <code>addViewerPostSelectionChangeListener<code>
         * method. When the viewerPostSelectionChange event occurs, that
         * object's appropriate method is invoked.
         *
         * ViewerPostSelectionChangeEvent
         */
        private class ViewerPostSelectionChangeListener implements ISelectionChangedListener {
            private TreeViewer viewer;

            /**
             * Instantiates a new viewer post selection change listener.
             *
             * @param viewer the viewer
             */
            public ViewerPostSelectionChangeListener(TreeViewer viewer) {
                this.viewer = viewer;
            }

            /**
             * Handle error.
             */
            private void handleError() {
                try {
                    // remove the listener temporarily so that the events
                    // by the error handling dont further cause error
                    // to occur.
                    viewer.removePostSelectionChangedListener(this);
                    showPageFlippingAbortDialog1();
                    selectCurrentPageAgain1();
                    clearSelectedNode1();
                } finally {
                    viewer.addPostSelectionChangedListener(this);
                }
            }

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                final IPreferenceNode selection = getSingleSelection(event.getSelection());
                BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
                    @Override
                    public void run() {
                        if (!isCurrentPageValid()) {
                            handleError();

                        } else if (null == selection) {
                            /* when clicked on expand/collaspse */
                            return;
                        } else if (!showPage((IPreferenceNode) selection)) {
                            // Page flipping wasn't successful
                            handleError();
                        } else {
                            // Everything went well
                            lastSuccessfulNode = (IPreferenceNode) selection;
                        }
                    }
                });
            }
        }

        /**
         * Used to show warning message to user for indicating that restart is
         * required to update the changes in preferences.
         */
        @Override
        protected Control createHelpControl(Composite parent) {
            DialogMessageArea messageArea = new DialogMessageArea();
            ((GridLayout) parent.getLayout()).numColumns++;
            ((GridLayout) parent.getLayout()).numColumns++;
            messageArea.createContents(parent);
            messageArea.updateText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_RESTART_REQUIRED_MSG),
                    IMessageProvider.WARNING);
            return parent;
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            super.createButtonsForButtonBar(parent);
            okButton = getButton(IDialogConstants.OK_ID);
            okButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCEDIALOG_OK));
            getShell().setDefaultButton(okButton);
            cancelButton = getButton(IDialogConstants.CANCEL_ID);
            cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCEDIALOG_CANCEL));
        }

        @Override
        protected void handleSave() {
            statusMessage = new StatusMessage(MessageConfigLoader.getProperty(IMessagesConstants.SAVING_PREFERENCES));

            SavePreferenceUIWorker postWorker = new SavePreferenceUIWorker(store, statusMessage, workbench);
            setStatusMessage(statusMessage);
            StatusMessageList.getInstance().push(statusMessage);
            final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (null != btmStatusBar) {
                btmStatusBar.activateStatusbar();
            }
            postWorker.schedule();

        }

        @Override
        protected void configureShell(Shell newShell) {
            super.configureShell(newShell);
            newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCES_SHOW)); // $NON-NLS-1$
            newShell.setImage(IconUtility.getIconImage(IconUtility.ICO_EDIT_EDIT, this.getClass()));
        }
    }

}
