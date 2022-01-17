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
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DatabaseUtils;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.contentassistprocesser.ContentAssistProcesserCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.sourceeditor.templates.TemplateCompletionProcessor;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.CharPairsUtil;
import com.huawei.mppdbide.view.utils.DSDefaultCharacterPairMatcherUtil;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.iconmapper.IcoPathMapper;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLContentAssistProcessor.
 *
 * @since 3.0.0
 */
public class SQLContentAssistProcessor implements IContentAssistProcessor {
    private IExecTimer timer;
    private SQLContentAssist assistant = null;
    private ContentAssistProcesserCore core;
    private volatile Database database;
    private LinkedHashMap<String, ServerObject> autoMap = null;
    private static LinkedHashMap<String, ServerObject> map = null;
    private static boolean lookupTemplates = false;

    /**
     * Instantiates a new SQL content assist processor.
     *
     * @param assistant the assistant
     */
    public SQLContentAssistProcessor(SQLContentAssist assistant) {
        this.assistant = assistant;
    }

    /**
     * Sets the lookup templates.
     *
     * @param lookupTemplates the new lookup templates
     */
    public static void setLookupTemplates(boolean lookupTemplates) {
        SQLContentAssistProcessor.lookupTemplates = lookupTemplates;
    }

    /**
     * Make template proposals.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return the i completion proposal[]
     */
    private ICompletionProposal[] makeTemplateProposals(ITextViewer viewer, int offset) {
        TemplateCompletionProcessor templateProcessor = new TemplateCompletionProcessor(assistant);
        return templateProcessor.computeCompletionProposals(viewer, offset);
    }

    /**
     * Compute completion proposals.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return the i completion proposal[]
     */
    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        setContentAssistDefaultValues();
        if (lookupTemplates) {
            return makeTemplateProposals(viewer, offset);
        }
        if (isPartitionContainSQLComment(viewer, offset)) {
            return new ICompletionProposal[] {
                new FakeCompletionProposal(MessageConfigLoader.getProperty(IMessagesConstants.NO_PROPOSAL), offset)};
        }
        timer = new ExecTimer(" Autosuggest Object Load Time ");
        timer.start();
        core = new ContentAssistProcesserCore(database);

        String fullContent = viewer.getDocument().get();
        String fullPretext = null;
        String prefix = null;
        SortedMap<String, ServerObject> aliasMap = null;

        if (validateFullText(fullContent)) {
            fullPretext = fullContent.substring(0, offset);
            prefix = core.findString(fullPretext, DatabaseUtils.getCharacterList(database));
            if (canCheckForAlias(viewer)) {
                assistant.enableAutoInsert(true);
                assistant.setShowEmptyList(true);
                SQLContentAssistAliasProcessor aliasProcessor = new SQLContentAssistAliasProcessor(prefix, fullContent,
                        offset, DatabaseUtils.getCharacterList(database));

                boolean isSuccess = aliasProcessor.computeAliasProposal(core);
                if (isSuccess) {
                    prefix = aliasProcessor.getProcessedPrefix();
                    aliasMap = aliasProcessor.getComputedAliasMap();
                }
            }
        }
        getValidAutoSuggestList(fullPretext, prefix);
        addAutoMapAndSetup(aliasMap);
        assistant.setCurrentPrefix(core.getCurrentPrefix());
        boolean isLoading = false;
        if (null == autoMap) {
            return new ICompletionProposal[0];
        } else {
            isLoading = loadUnloadedData();
        }
        clearAliasMap(aliasMap);
        return prepareCompletionProposal(autoMap, offset, core.getReplaceLength(), isLoading, viewer);

    }

    private boolean validateFullText(String fullContent) {
        return null != fullContent && !fullContent.isEmpty();
    }

    private void setContentAssistDefaultValues() {
        assistant.enableAutoInsert(false);
        assistant.setShowEmptyList(false);
        assistant.setCurrentPrefix(new String[0]);
        this.assistant.setEmptyMessage(MessageConfigLoader.getProperty(IMessagesConstants.NO_PROPOSAL));
    }

    private void getValidAutoSuggestList(String fullPretext, String prefix) {
        if (!MPPDBIDEConstants.INVALID_INSERT.equals(prefix)) {
            autoMap = core.getContextProposals(prefix, fullPretext);
        }
    }

    private boolean canCheckForAlias(ITextViewer viewer) {
        SourceViewer view = (SourceViewer) viewer;
        boolean isAliasCheck = false;
        if (view != null && view.getData("ISALIASCHECK") != null) {
            isAliasCheck = ((Boolean) view.getData("ISALIASCHECK")).booleanValue();
        }
        return isAliasCheck;
    }

    /**
     * Load unloaded data.
     *
     * @return true, if successful
     */
    private boolean loadUnloadedData() {
        boolean isLoading = false;
        if (validateForNonLoaded()) {
            isLoading = true;
            // start
            final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            StatusMessage statMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_AUTO_SUGGEST));
            AutoSuggestWorker job = new AutoSuggestWorker(MPPDBIDEConstants.CANCELABLEJOB, core.getCurrentPrefix(),
                    statMsg);
            job.setTaskDB(database);
            StatusMessageList.getInstance().push(statMsg);
            activateBottomStatusbar(btmStatusBar);
            // end
            job.schedule();
        }
        return isLoading;
    }

    private boolean validateForNonLoaded() {
        return database != null && core != null && !database.isLoadingNamespaceInProgress()
                && core.isAnyNonLoadedObject();
    }

    /**
     * Clear alias map.
     *
     * @param aliasMap the alias map
     */
    private void clearAliasMap(SortedMap<String, ServerObject> aliasMap) {
        if (null != aliasMap) {
            aliasMap.clear();
        }
    }

    /**
     * Checks if is partition contain SQL comment.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return true, if is partition contain SQL comment
     */
    private boolean isPartitionContainSQLComment(ITextViewer viewer, int offset) {
        try {
            ITypedRegion region = (ITypedRegion) DSDefaultCharacterPairMatcherUtil.getRegion(viewer.getDocument(),
                    offset, new CharPairsUtil(new char[0]), true, DSDefaultCharacterPairMatcherUtil.SQL_PARTITIONING,
                    false, true);

            if (null != region) {
                String partition = region.getType();
                return partition.equals(DSDefaultCharacterPairMatcherUtil.SQL_COMMENT)
                        || partition.equals(DSDefaultCharacterPairMatcherUtil.SQL_MULTILINE_COMMENT)
                        || partition.equals(DSDefaultCharacterPairMatcherUtil.SINGLE_LINE_COMMENT);
            }
        } catch (BadLocationException exception) {
            MPPDBIDELoggerUtility.error(
                    "SQLContentAssistProcessor: BadLocationException occurred while getting partition.", exception);
        }
        return false;
    }

    /**
     * Activate bottom statusbar.
     *
     * @param btmStatusBar the btm status bar
     */
    private void activateBottomStatusbar(final BottomStatusBar btmStatusBar) {
        if (btmStatusBar != null) {
            btmStatusBar.activateStatusbar();
        }
    }

    /**
     * Adds the auto map and setup.
     *
     * @param aliasMap the alias map
     */
    private void addAutoMapAndSetup(SortedMap<String, ServerObject> aliasMap) {
        if (null != aliasMap && !aliasMap.isEmpty()) {
            autoMap.putAll(aliasMap);
        }

        if (((UserPreference) UserPreference.getInstance()).isIsenableTestability()) {
            setMap(autoMap);
        }
    }

    /**
     * Prepare completion proposal.
     *
     * @param serverObjMap the server obj map
     * @param offset the offset
     * @param lastWordLen the last word len
     * @param isLoading the is loading
     * @param viewer the viewer
     * @return the i completion proposal[]
     */
    private ICompletionProposal[] prepareCompletionProposal(LinkedHashMap<String, ServerObject> serverObjMap,
            int offset, int lastWordLen, boolean isLoading, ITextViewer viewer) {
        boolean isAutoSuggest = true;
        int itemCount = 0;
        String message = null;
        ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>(itemCount);
        if (validateMap(serverObjMap, isLoading)) {
            message = MessageConfigLoader.getProperty(IMessagesConstants.AUTO_SUGGEST_LOADING);
            return new ICompletionProposal[] {new FakeCompletionProposal(message, offset)};
        }

        itemCount = serverObjMap.size();

        ICompletionProposal[] proposalArray = null;
        proposalArray = getProposalArray(isLoading, itemCount);
        String objectName = null;
        Iterator<Entry<String, ServerObject>> itr = serverObjMap.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, ServerObject> entry = (Entry<String, ServerObject>) itr.next();
            ServerObject obj = entry.getValue();

            objectName = obj.getAutoSuggestionName(isAutoSuggest);

            if (offset < lastWordLen) {
                return new ICompletionProposal[0];
            }
            addProposal(offset, lastWordLen, proposals, objectName, entry, obj);
        }
        addLabelIfLoading(offset, isLoading, proposals);
        isAutoSuggest = false;
        postCompleteOperation(serverObjMap);

        return proposals.toArray(proposalArray);
    }

    private boolean validateMap(LinkedHashMap<String, ServerObject> serverObjMap, boolean isLoading) {
        return (null == serverObjMap || 0 == serverObjMap.size()) && isLoading;
    }

    private void addProposal(int offset, int lastWordLen, ArrayList<ICompletionProposal> proposals, String objectName,
            Entry<String, ServerObject> entry, ServerObject obj) {
        CompletionProposal proposal = null;
        if (null != objectName) {
            proposal = new CompletionProposal(objectName, offset - lastWordLen, lastWordLen, objectName.length(),
                    getImageFor(obj.getType()), entry.getKey(), null, null);
            proposals.add(proposal);
        }
    }

    private void postCompleteOperation(LinkedHashMap<String, ServerObject> serverObjMap) {
        try {
            timer.stopAndLog();
            serverObjMap.clear();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Exception while getting elapsed time", exception);
        }
    }

    private void addLabelIfLoading(int offset, boolean isLoading, ArrayList<ICompletionProposal> proposals) {
        if (isLoading) {
            proposals.add(new FakeCompletionProposal(
                    MessageConfigLoader.getProperty(IMessagesConstants.AUTO_SUGGEST_LOADING), offset));
        }
    }

    private ICompletionProposal[] getProposalArray(boolean isLoading, int itemCount) {
        ICompletionProposal[] proposalArray;
        if (isLoading) {
            proposalArray = new ICompletionProposal[itemCount + 1];
        } else {
            proposalArray = new ICompletionProposal[itemCount];
        }
        return proposalArray;
    }

    /**
     * Compute context information.
     *
     * @param viewer the viewer
     * @param offset the offset
     * @return the i context information[]
     */
    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {

        return new IContextInformation[0];
    }

    /**
     * Gets the completion proposal auto activation characters.
     *
     * @return the completion proposal auto activation characters
     */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        // Bug Fix: Auto activation showing templates after template hot key
        // pressed and closed.
        // Setting flag as false so that auto activator shows DB Objects, only
        // if popup is not active
        if (!assistant.isProposalPopupActive()) {
            SQLContentAssistProcessor.setLookupTemplates(false);
        }

        return ".".toCharArray();
    }

    /**
     * Gets the context information auto activation characters.
     *
     * @return the context information auto activation characters
     */
    @Override
    public char[] getContextInformationAutoActivationCharacters() {

        return new char[0];
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    @Override
    public String getErrorMessage() {

        return null;
    }

    /**
     * Gets the context information validator.
     *
     * @return the context information validator
     */
    @Override
    public IContextInformationValidator getContextInformationValidator() {

        return null;
    }

    /**
     * Gets the image for.
     *
     * @param type the type
     * @return the image for
     */
    private Image getImageFor(OBJECTTYPE type) {
        String iconName = IcoPathMapper.getImagePathForObject(type);
        if (null != iconName) {
            return IconUtility.getIconImage(iconName, this.getClass());
        }
        return null;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * Sets the map.
     *
     * @param map the map
     */
    public static void setMap(LinkedHashMap<String, ServerObject> map) {
        SQLContentAssistProcessor.map = map;
    }

    /**
     * Gets the map.
     *
     * @return the map
     */
    public static LinkedHashMap<String, ServerObject> getMap() {
        return SQLContentAssistProcessor.map;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AutoSuggestWorker.
     */
    private final class AutoSuggestWorker extends UIWorkerJob {

        private String[] prefixes;

        private DBConnection connection = null;
        private StatusMessage statMsg;

        /**
         * Instantiates a new auto suggest worker.
         *
         * @param family the family
         * @param prefixes the prefixes
         * @param statMsg the stat msg
         */
        public AutoSuggestWorker(Object family, String[] prefixes, StatusMessage statMsg) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_AUTO_SUGGEST), family);
            this.prefixes = prefixes;
            this.statMsg = statMsg;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            connection = database.getConnectionManager().getFreeConnection();
            core.findNonLoadedDatabaseObjectsOnDemand(connection);
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (isSameSuggestionActive()) {
                assistant.hide();
                assistant.showPossibleCompletions();
            }

            /* Trigger refresh of loaded schema in OB */
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        String schemaName = "";
                        if (prefixes.length > 0) {
                            /* Schema name is the 1st element in prefix array */
                            schemaName = prefixes[0];
                        }

                        if (schemaName.isEmpty()) {
                            return;
                        }

                        INamespace schema = database.getNameSpaceByName(schemaName);
                        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                        if (null != objectBrowserModel) {
                            objectBrowserModel.refreshObject(schema);
                        }
                    } catch (DatabaseOperationException exception) {
                        MPPDBIDELoggerUtility.error("Error while fetching objects from Database", exception);
                        return;
                    }
                }

            });
        }

        /**
         * Checks if is same suggestion active.
         *
         * @return true, if is same suggestion active
         */
        private boolean isSameSuggestionActive() {
            String[] currentPrefix = assistant.getCurrentPrefix();
            boolean isSame = false;
            for (int index = 0; index < currentPrefix.length && index < prefixes.length; ++index) {
                if (currentPrefix[index] != null && prefixes[index] != null
                        && currentPrefix[index].equals(prefixes[index])) {
                    isSame = true;
                } else {
                    return false;
                }
            }
            return isSame && assistant.isProposalPopupActive();
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Error while initializing the SQL Connection", exception);
            assistant.hide();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("SQLContentAssistProcessor: Critical exception occurred.", exception);
            assistant.hide();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("SQLContentAssistProcessor: Operation exception occurred.", exception);
            assistant.hide();
        }

        @Override
        public void finalCleanup() {
            if (connection != null) {
                database.getConnectionManager().releaseConnection(connection);
            }
        }

        @Override
        public void finalCleanupUI() {
            BottomStatusBar btstat = UIElement.getInstance().getProgressBarOnTop();
            if (btstat != null) {
                btstat.hideStatusbar(this.statMsg);
            }
        }

        @Override
        protected void canceling() {
            super.canceling();
            try {
                if (connection != null) {
                    connection.cancelQuery();
                }
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("Cancel Query for autosuggest failed", exception);
            }
        }

    }
}
