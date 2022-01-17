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

package com.huawei.mppdbide.view.autosave;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.autosave.AutoSaveDbgObjInfo;
import com.huawei.mppdbide.bl.autosave.AutoSaveInfo;
import com.huawei.mppdbide.bl.autosave.AutoSaveMetadata;
import com.huawei.mppdbide.bl.autosave.AutosaveFileUtility;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.autosave.AutoSaveTerminalStatus;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveDbgObject;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.utils.Preferencekeys;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.NonCloseableDialog;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutoSaveManager.
 *
 * @since 3.0.0
 */
public class AutoSaveManager implements Preferencekeys {

    /**
     * The worker.
     */
    AutoSaveWorker worker = null;

    /**
     * The Constant DEFAULT_INTERVAL.
     */

    public static final int DEFAULT_INTERVAL = 5;

    private AutoSaveInfo autosaveInfo;

    private static boolean isAutoSave = true;

    private static boolean isAutoSaveEncrypted = true;

    private static int autoSaveInterval = DEFAULT_INTERVAL * 1000 * 60;

    private static volatile AutoSaveManager autosaveManager = null;

    private static final Object LOCK = new Object();
    private final Object instanceLock = new Object();

    private static final String AUTOSAVE_INFO_FILENAME = "autosave.info";

    private static final String AUTOSAVE_INFO_TMP_FILENAME = "autosave_tmp.info";

    private static final String AUTOSAVE_DATA_FILE_PATTERN = ".autosave";

    private boolean isInitialized = false;

    private AutosaveFileUtility autosaveFileUtility;

    private long startTime;

    private static final long STARTUP_CUTOFF_DURATION = 10 * 1000L;

    private volatile IAutoSaveObject partObject = null;
    private boolean isOneTabLoaded;

    private AutoSaveManager() {
        this.autosaveFileUtility = new AutosaveFileUtility();
    }

    /**
     * Gets the single instance of AutoSaveManager.
     *
     * @return single instance of AutoSaveManager
     */
    public static AutoSaveManager getInstance() {
        if (autosaveManager == null) {
            synchronized (LOCK) {
                if (autosaveManager == null) {
                    autosaveManager = new AutoSaveManager();
                }
            }
        }

        return autosaveManager;
    }

    /**
     * Initialize.
     *
     * @throws FileOperationException the file operation exception
     * @throws DatabaseOperationException the database operation exception
     * @throws UnsupportedPlatformException the unsupported platform exception
     */
    public void initialize() throws FileOperationException, DatabaseOperationException {
        synchronized (instanceLock) {
            if (!isInitialized) {
                autosaveFileUtility.createFolderStructure();
                loadAutosaveInfo();
                isInitialized = true;
            }
        }
    }

    /**
     * Destroy.
     *
     * @throws FileOperationException the file operation exception
     */
    public void destroy() throws FileOperationException {
        synchronized (instanceLock) {
            if (isInitialized) {
                autosaveFileUtility.deleteFolderStructure();
                autosaveInfo.invalidate();
                autosaveInfo = null;
                isInitialized = false;
            }
        }
    }

    /**
     * Gets the autosave info.
     *
     * @return the autosave info
     */
    public AutoSaveInfo getAutosaveInfo() {
        return autosaveInfo;
    }

    /**
     * Save auto save info.
     *
     * @throws FileOperationException the file operation exception
     */
    public void saveAutoSaveInfo() throws FileOperationException {
        autosaveFileUtility.saveAutosaveInfo(autosaveInfo, AUTOSAVE_INFO_FILENAME, AUTOSAVE_INFO_TMP_FILENAME);
    }

    private void loadAutosaveInfo() throws FileOperationException {
        if (autosaveInfo == null) {
            try {
                autosaveInfo = readAutoSaveInfo();
            } catch (MPPDBIDEException exception) {
                MPPDBIDELoggerUtility.error("AutoSaveManager: load auto save info failed.", exception);
            }
            // No info available, so create new and add a new info on the disk
            if (autosaveInfo == null) {
                autosaveInfo = new AutoSaveInfo();
                autosaveFileUtility.saveAutosaveInfo(autosaveInfo, AUTOSAVE_INFO_FILENAME, AUTOSAVE_INFO_TMP_FILENAME);
            }
        }
    }

    /**
     * Delete autosave metadata.
     *
     * @param metaData the meta data
     */
    public void deleteAutosaveMetadata(AutoSaveMetadata metaData) {
        if (metaData != null) {
            boolean isRemoved = autosaveInfo.removeAutoSaveMetadata(metaData);
            if (isRemoved) {
                autosaveFileUtility.deleteFile(metaData.getAutoSaveFileName());
            }
        }
    }

    /**
     * Gets the data writer.
     *
     * @param metaData the meta data
     * @return the data writer
     * @throws FileOperationException the file operation exception
     */
    public FileOutputStream getDataWriter(AutoSaveMetadata metaData) throws FileOperationException {
        return autosaveFileUtility.getFileOutputStream(metaData);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CheckWorkerSleeping.
     */
    /*
     * A class can implement Callable interface to provide call method that
     * returns boolean
     */
    private class CheckWorkerSleeping implements Callable<Boolean> {
        @Override
        public Boolean call() {
            if (worker == null) {
                return false;
            }
            return worker.getState() == Job.SLEEPING;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CheckWorkerFinished.
     */
    private static class CheckWorkerFinished implements Callable<Boolean> {
        private AutoSaveLoader loadWorker = null;

        @Override
        public Boolean call() {
            if (getLoadWorker() == null) {
                return false;
            }
            return getLoadWorker().isFinished();
        }

        /**
         * Gets the load worker.
         *
         * @return the load worker
         */
        public AutoSaveLoader getLoadWorker() {
            return loadWorker;
        }

        /**
         * Sets the load worker.
         *
         * @param loadWorker the new load worker
         */
        public void setLoadWorker(AutoSaveLoader loadWorker) {
            this.loadWorker = loadWorker;
        }

    }

    /*
     * inputs Callable interface implementation as event loop breaker if it is
     * null, loops until Shell is disposed
     */
    private void runEventLoop(Shell loopShell, Callable<Boolean> breakConditionChecker) {

        // Use the display provided by the shell if possible
        Display display;
        if (loopShell == null) {
            display = Display.getDefault();
        } else {
            display = loopShell.getDisplay();
        }

        while (loopShell != null && !loopShell.isDisposed()) {
            try {
                /*
                 * if condition breaker parameter is present, check if breaking
                 * condition is true break event loop and return in that case
                 */
                if (breakConditionChecker != null) {
                    Boolean res = breakConditionChecker.call();
                    if (res != null && res.booleanValue()) {
                        return;
                    }
                }
            } catch (Exception exception) {
                MPPDBIDELoggerUtility.error("AutoSaveManager: Exception thrown by callable interface", exception);
            }

            try {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            } catch (Throwable exception) {
                MPPDBIDELoggerUtility.error("AutoSaveManager: display interrupted", exception);
            }
        }
        if (!display.isDisposed()) {
            display.update();
        }
    }

    /**
     * Load.
     */
    public void load() {
        NonCloseableDialog dialog = new NonCloseableDialog(
                MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_STARTUP_DIALOG_MESSAGE),
                Display.getDefault().getActiveShell());
        Job loaderJob = new AutoSaveLoader(this, dialog);

        dialog.setBlockOnOpen(false);
        dialog.open();

        loaderJob.schedule();
        CheckWorkerFinished checkWorkerFinished = new CheckWorkerFinished();
        checkWorkerFinished.setLoadWorker((AutoSaveLoader) loaderJob);
        runEventLoop(dialog.getShell(), checkWorkerFinished);
        dialog.close();
    }

    /**
     * Graceful exit.
     */
    public void gracefulExit() {
        NonCloseableDialog dialog = new NonCloseableDialog(
                MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_GRACEFUL_EXIT_MSG),
                Display.getDefault().getActiveShell());
        dialog.setBlockOnOpen(false);
        dialog.open();
        if (worker != null && worker.getState() != Job.NONE) {
            if (worker.getState() == Job.SLEEPING) {
                worker.wakeUp();
                worker.schedule();
            }
            runEventLoop(dialog.getShell(), new CheckWorkerSleeping());
        }
        dialog.close();
    }

    /**
     * Creates the periodic worker job.
     */
    public void createPeriodicWorkerJob() {
        worker = new AutoSaveWorker(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_PERIODIC_JOB_NAME),
                null);
        worker.schedule(getAutoSaveInterval());
    }

    /**
     * Gets the auto save interval.
     *
     * @return the auto save interval
     */
    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }

    /**
     * Checks if is auto save enabled.
     *
     * @return true, if is auto save enabled
     */
    public boolean isAutoSaveEnabled() {
        return isAutoSave;
    }

    /**
     * Checks if is auto save encrypted.
     *
     * @return true, if is auto save encrypted
     */
    public boolean isAutoSaveEncrypted() {
        return isAutoSaveEncrypted;
    }

    private IAutoSaveObject createPart(final UIElement uielement, final AutoSaveMetadata autosaveMD) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                partObject = null;
                if (MPPDBIDEConstants.SQL_TERMINAL.equals(autosaveMD.getType())) {
                    partObject = uielement.createNewTerminal(autosaveMD.getDatabaseName(),
                            autosaveMD.getConnectionName(), autosaveMD.getTabID(), autosaveMD.getTabLabel(),
                            autosaveMD.getTabToolTip());
                } else if (MPPDBIDEConstants.PLSQL_EDITOR.equals(autosaveMD.getType())) {
                    AutoSaveDbgObjInfo dbgObj = autosaveMD.getDbgObjInfo();
                    if (dbgObj != null) {
                        IDebugObject obj = null;
                        obj = new DebugObjects(dbgObj.getOid(), dbgObj.getName(), dbgObj.getObjType(), null);
                        partObject = uielement.createEditor(obj, autosaveMD.getTabID(), autosaveMD.getTabLabel(),
                                autosaveMD.getTabToolTip(), dbgObj.isDirty());
                        if (partObject instanceof IAutoSaveDbgObject) {
                            IAutoSaveDbgObject autosavedbgObj = (IAutoSaveDbgObject) partObject;
                            autosavedbgObj.setDirty(dbgObj.isDirty());
                            autosavedbgObj.setNamespaceName(dbgObj.getSchemaName());
                        }
                    }
                }

            }
        });

        return partObject;
    }

    private boolean isValidMetadata(AutoSaveMetadata autosaveMD) {
        if (autosaveMD.getConnectionName() == null || "".equals(autosaveMD.getConnectionName())) {

            return false;
        }

        if (autosaveMD.getDatabaseName() == null || "".equals(autosaveMD.getDatabaseName())) {
            return false;
        }

        if (autosaveMD.getTabID() == null || "".equals(autosaveMD.getTabID())) {
            return false;
        }

        if (autosaveMD.getTabLabel() == null || "".equals(autosaveMD.getTabLabel())) {
            return false;
        }

        if (autosaveMD.getTabToolTip() == null || "".equals(autosaveMD.getTabToolTip())) {
            return false;
        }

        if (autosaveMD.getType() == null || "".equals(autosaveMD.getType())) {
            return false;
        }

        if (autosaveMD.getAutoSaveFileName() == null || "".equals(autosaveMD.getAutoSaveFileName())) {
            return false;
        }

        if (!autosaveMD.calcAndCompare()) {
            return false;
        }

        return isValidEncoding(autosaveMD.getEncoding());
    }

    private boolean isValidEncoding(String encoding) {
        if (encoding == null || "".equals(encoding)) {
            return false;
        }

        return true;
    }

    private boolean isValidDbgObjInfo(AutoSaveDbgObjInfo dbgObjInfo) {
        if (dbgObjInfo.getName() == null || "".equals(dbgObjInfo.getName())) {
            return false;
        }

        if (dbgObjInfo.getSchemaName() == null || "".equals(dbgObjInfo.getSchemaName())) {
            return false;
        }

        if (dbgObjInfo.getObjType() == null) {
            return false;
        }

        if (OBJECTTYPE.PLSQLFUNCTION != dbgObjInfo.getObjType() && OBJECTTYPE.SQLFUNCTION != dbgObjInfo.getObjType()
                && OBJECTTYPE.PROCEDURE != dbgObjInfo.getObjType() && OBJECTTYPE.CFUNCTION != dbgObjInfo.getObjType()) {
            return false;
        }

        return true;
    }

    private boolean verifyAutoSaveMD(AutoSaveMetadata autosaveMD) {
        autosaveMD.setVersionNumber(-1);
        if (!isValidMetadata(autosaveMD)) {
            return false;
        }

        if (!MPPDBIDEConstants.SQL_TERMINAL.equals(autosaveMD.getType())
                && !MPPDBIDEConstants.PLSQL_EDITOR.equals(autosaveMD.getType())) {
            return false;
        }

        if (MPPDBIDEConstants.SQL_TERMINAL.equals(autosaveMD.getType()) && autosaveMD.getDbgObjInfo() != null) {
            return false;
        }

        if (MPPDBIDEConstants.PLSQL_EDITOR.equals(autosaveMD.getType())) {
            if (autosaveMD.getDbgObjInfo() == null) {
                return false;
            }

            if (!isValidDbgObjInfo(autosaveMD.getDbgObjInfo())) {
                return false;
            }
        }
        double fileLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_SQL);
        if (autosaveFileUtility.isValidFile(autosaveMD.getAutoSaveFileName(), fileLimit)) {
            autosaveMD.setVersionNumber(0);
            return true;
        }

        return false;
    }

    private AutoSaveInfo readAutoSaveInfo() throws MPPDBIDEException {
        // Read from Main File, Not found then try to read from tmp file
        AutoSaveInfo info = null;
        try {
            info = autosaveFileUtility.getAutosaveInfo(getAutosaveInfoFilename(), UIElement.getMaxTabsAllowed());
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Failed to read saved autosave info", exception);
        }

        if (null == info) {
            info = autosaveFileUtility.getAutosaveInfo(getAutosaveInfoTmpFilename(), UIElement.getMaxTabsAllowed());
            if (info != null) {
                autosaveFileUtility.rename(getAutosaveInfoTmpFilename(), getAutosaveInfoFilename());
            }
        }

        return info;
    }

    /**
     * Sets the default preferences.
     *
     * @param ps the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore ps) {
        ps.setDefault(AUTOSAVE_ENABLE_PREFERENCE_KEY, true);
        ps.setDefault(AUTOSAVE_INTERVAL_PREFERENCE_KEY, DEFAULT_INTERVAL);
        ps.setDefault(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG, true);
    }

    /**
     * Update autosave preferences.
     *
     * @param preferenceStore the preference store
     */
    public static void updateAutosavePreferences(IPreferenceStore preferenceStore) {
        synchronized (LOCK) {
            isAutoSave = preferenceStore.getBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY);
            autoSaveInterval = preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY) * 1000 * 60;
            isAutoSaveEncrypted = preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG);
        }
    }

    private void readTerminal(IAutoSaveObject obj, AutoSaveMetadata autosaveMD) {
        String terminalRawData = null;

        try {
            obj.updateStatus(AutoSaveTerminalStatus.LOADING);
            terminalRawData = readData(autosaveMD.getAutoSaveFileName(), autosaveMD.isEncrypted(),
                    autosaveMD.getEncoding());
            obj.setText(terminalRawData);

            // So that first periodic job do not trigger file write if no
            // modification done.
            obj.setModified(false);
            if (obj instanceof IAutoSaveDbgObject) {
                IAutoSaveDbgObject objDbg = (IAutoSaveDbgObject) obj;
                objDbg.getDebugObject().getSourceCode().setCode(terminalRawData);
            }
        } catch (Error | MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Error while reading SQL terminal data for Autosave", exception);
        } finally {
            obj.setEditable(true);
            obj.updateStatus(AutoSaveTerminalStatus.LOAD_FINISHED);
        }
    }

    /**
     * Read auto save files.
     *
     * @param autosaveMDList the autosave MD list
     * @param partObjectList2 the part object list 2
     * @return true, if successful
     */
    public boolean readAutoSaveFiles(List<AutoSaveMetadata> autosaveMDList, List<IAutoSaveObject> partObjectList2) {
        int terminalCnt = autosaveMDList.size();
        IAutoSaveObject obj = null;

        int count;
        for (count = 0; count < terminalCnt && !isCutOffTimeReached(); count++) {
            if (partObjectList2.size() > count) {
                obj = partObjectList2.get(count);
            }

            if (obj != null && obj.getStatus() == AutoSaveTerminalStatus.INIT) {
                readTerminal(obj, autosaveMDList.get(count));
            }
        }

        return count == terminalCnt;
    }

    /**
     * Read auto save files for lazy load.
     *
     * @param autosaveMDList the autosave MD list
     * @param partObjectList2 the part object list 2
     */
    public void readAutoSaveFilesForLazyLoad(List<AutoSaveMetadata> autosaveMDList,
            List<IAutoSaveObject> partObjectList2) {
        int terminalCnt = autosaveMDList.size();
        IAutoSaveObject obj = null;

        for (int count = 0; count < terminalCnt; count++) {
            if (partObjectList2.size() > count) {
                obj = partObjectList2.get(count);
            }

            if (obj != null && obj.getStatus() == AutoSaveTerminalStatus.INIT) {
                readTerminal(obj, autosaveMDList.get(count));
            }
        }
    }

    /**
     * Start cut off time.
     */
    public void startCutOffTime() {
        startTime = System.currentTimeMillis();
    }

    private boolean isCutOffTimeReached() {
        long currTime = System.currentTimeMillis();
        if (currTime - startTime >= STARTUP_CUTOFF_DURATION) {
            return true;
        }

        return false;
    }

    private String readData(String fileName, boolean isEncrypted, String charSet) throws MPPDBIDEException {
        
        double fileLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_SQL);
        byte[] data = autosaveFileUtility.read(fileName, fileLimit);
        String text = null;
        try {
            SecureUtil secureUtil = new SecureUtil();
            secureUtil.setPackagePath(ConnectionProfileManagerImpl.getInstance().getDiskUtility().getOsCurrentUserFolderPath());
            if (isEncrypted) {
                data = secureUtil.decryptByteArray(data);
            }

            text = new String(data, charSet);
        } catch (UnsupportedEncodingException exp) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_FILEENCODINGEXCEPTION), exp);
            throw new MPPDBIDEException(IMessagesConstants.PRESERVESQL_FILEENCODINGEXCEPTION);
        } catch (DataStudioSecurityException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DS_SECURITY_ERROR), exp);
            throw new MPPDBIDEException(IMessagesConstants.ERR_DS_SECURITY_ERROR);
        }

        return text;
    }

    private boolean loadAutoSaveInformation(AutoSaveInfo info) {
        List<IAutoSaveObject> partObjectList = new ArrayList<>(1);
        List<AutoSaveMetadata> autosaveMDList = info.getAutosaveMD();

        AutoSaveMetadata autosaveMD = null;
        final UIElement uielement = UIElement.getInstance();
        int terminalCnt = autosaveMDList.size();

        String activeTerminal = info.getActiveTerminalName();
        int activeTabId = -1;

        startCutOffTime();
        for (int element = 0; element < terminalCnt; element++) {
            autosaveMD = autosaveMDList.get(element);

            if (activeTerminal != null && activeTerminal.equals(autosaveMD.getTabID())) {
                activeTabId = element;
            }

            if (verifyAutoSaveMD(autosaveMD)) {
                setAutoSaveObjectData(partObjectList, autosaveMD, uielement, element);
            } else {
                partObjectList.add(element, null);
            }
        }

        partObject = null;
        activeTabId = getActiveTerminal(partObjectList, activeTabId);

        readActiveTerminals(partObjectList, autosaveMDList, uielement, terminalCnt, activeTabId);

        boolean isAllLoaded = readAutoSaveFiles(autosaveMDList, partObjectList);
        if (!isAllLoaded) {
            createLazyLoadJob(autosaveMDList, partObjectList);
        }

        return isAllLoaded;
    }

    private void readActiveTerminals(List<IAutoSaveObject> partObjectList, List<AutoSaveMetadata> autosaveMDList,
            final UIElement uielement, int terminalCnt, int activeTabId) {
        if (activeTabId < terminalCnt && partObjectList.get(activeTabId) != null) {
            final String tabId = autosaveMDList.get(activeTabId).getTabID();

            // Read the active terminal first
            readTerminal(partObjectList.get(activeTabId), autosaveMDList.get(activeTabId));
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    uielement.bringPartOnTop(tabId);
                }
            });
        }
    }

    private boolean setAutoSaveObjectData(List<IAutoSaveObject> partObjectList, AutoSaveMetadata autosaveMD,
            final UIElement uielement, int count) {
        boolean isMaxPopupOpened = false;
        if (!uielement.isWindowLimitReached()) {
            try {
                IAutoSaveObject obj = createPart(uielement, autosaveMD);
                partObjectList.add(count, obj);
                if (obj != null) {
                    isOneTabLoaded = true;
                    obj.setEditable(false);
                    obj.updateStatus(AutoSaveTerminalStatus.INIT);
                    obj.setConnectionName(autosaveMD.getConnectionName());
                    obj.setDatabaseName(autosaveMD.getDatabaseName());
                    obj.setElementID(autosaveMD.getTabID());
                    obj.setTabLabel(autosaveMD.getTabLabel());
                    obj.setTabToolTip(autosaveMD.getTabToolTip());
                    obj.setModifiedAfterCreate(true);
                    // connection dependent buttons
                    obj.resetConnButtons(false);

                    obj.resetButtons(); // db dependent buttons
                }
            } catch (Exception | OutOfMemoryError exception) {
                MPPDBIDELoggerUtility.error("AutoSaveManager: load auto save info failed.", exception);
            }
        } else {
            partObjectList.add(count, null);

            // Open Max Window reached Dialog only once
            if (!isMaxPopupOpened) {
                uielement.openMaxSourceViewerDialogStartup();
                isMaxPopupOpened = true;
            }
        }
        return isMaxPopupOpened;
    }

    private void createLazyLoadJob(List<AutoSaveMetadata> autosaveMDList, List<IAutoSaveObject> partObjectList2) {
        Job loader = new AutoSaveLazyLoader(autosaveMDList, partObjectList2);
        loader.schedule();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class AutoSaveLoader.
     */
    public class AutoSaveLoader extends UIWorkerJob {
        private boolean isAllLoaded;
        private IExecTimer timer;
        private boolean isFinished;

        /**
         * Instantiates a new auto save loader.
         *
         * @param autoSaveManager1 the auto save manager 1
         * @param dialog1 the dialog 1
         */
        public AutoSaveLoader(AutoSaveManager autoSaveManager1, NonCloseableDialog dialog1) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_STARTUP_JOB_NAME), null);
            isAllLoaded = false;
            isOneTabLoaded = false;
            this.isFinished = false;
        }

        /**
         * Do job.
         *
         * @return the object
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws MPPDBIDEException the MPPDBIDE exception
         * @throws Exception the exception
         */
        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            timer = new ExecTimer("AutoSaveLoader Job");
            timer.start();
            AutoSaveInfo info = readAutoSaveInfo();
            if (null != info) {
                if (isAutoSaveEnabled()) {
                    isAllLoaded = loadAutoSaveInformation(info);
                } else {
                    cleanupAutoSaveInfo(info);
                }

                autosaveInfo = info;
                deleteMetaDataEntry();
                deleteStaleFiles();
                return null;
            }
            deleteStaleFiles();
            return null;
        }

        private void cleanupAutoSaveInfo(AutoSaveInfo info) {
            List<AutoSaveMetadata> autosaveMDList = info.getAutosaveMD();
            int terminalCnt = autosaveMDList.size();

            for (int cnt = 0; cnt < terminalCnt; cnt++) {
                if (autosaveMDList.get(cnt).getAutoSaveFileName() != null
                        && !"".equals(autosaveMDList.get(cnt).getAutoSaveFileName())) {
                    deleteFile(autosaveMDList.get(cnt).getAutoSaveFileName());
                }
            }

            deleteFile(getAutosaveInfoFilename());
            deleteFile(getAutosaveInfoTmpFilename());

            deleteFolderIfEmpty();
        }

        private void deleteFolderIfEmpty() {
            autosaveFileUtility.deleteFolderStructureIfEmpty();
        }

        private void deleteFile(String fileName) {
            autosaveFileUtility.deleteFile(fileName);
        }

        private void deleteMetaDataEntry() {
            List<AutoSaveMetadata> autosaveMD = new ArrayList<>(1);
            List<AutoSaveMetadata> autosaveMDOldList = autosaveInfo.getAutosaveMD();
            for (AutoSaveMetadata item : autosaveMDOldList) {
                if (item.getVersionNumber() != -1) {
                    autosaveMD.add(item);
                }
            }

            autosaveInfo.setAutosaveMD(autosaveMD);
        }

        private void deleteStaleFiles() {
            ArrayList<String> validFileList = new ArrayList<>(1);
            if (autosaveInfo != null) {
                List<AutoSaveMetadata> autosaveMD = autosaveInfo.getAutosaveMD();
                for (AutoSaveMetadata item : autosaveMD) {
                    if (item.getAutoSaveFileName() != null && !"".equals(item.getAutoSaveFileName())) {
                        validFileList.add(item.getAutoSaveFileName());
                    }
                }
            } else {
                // Startup Metadata file is corrupted, so clean it.
                deleteFile(getAutosaveInfoFilename());
                deleteFile(getAutosaveInfoTmpFilename());
            }

            autosaveFileUtility.deleteStaleFiles(validFileList, getAutoSaveDataFilePattern());
        }

        /**
         * On success UI action.
         *
         * @param obj the obj
         */
        @Override
        public void onSuccessUIAction(Object obj) {
            if (isAllLoaded && isOneTabLoaded) {
                UIElement.getInstance().setStatusBarMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_STARTUP_LOADING_FINISHED));
            }
        }

        /**
         * On critical exception UI action.
         *
         * @param exception the exception
         */
        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            showFailedStatus();
        }

        /**
         * On operational exception UI action.
         *
         * @param e the e
         */
        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            showFailedStatus();
        }

        /**
         * On MPPDBIDE exception UI action.
         *
         * @param exception the exception
         */
        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
            super.onMPPDBIDEExceptionUIAction(exception);
            showFailedStatus();
        }

        /**
         * Final cleanup.
         *
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        @Override
        public void finalCleanup() throws MPPDBIDEException {
            try {
                timer.stopAndLog();
                setFinished(true);
            } catch (Exception exception) {
                // Execute below statement to end the event loop
                setFinished(true);
            }
        }

        /**
         * Final cleanup UI.
         */
        @Override
        public void finalCleanupUI() {
        }

        private void showFailedStatus() {
            UIElement.getInstance().setStatusBarMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.PRESERVESQL_STARTUP_LOADING_FAILED));
        }

        /**
         * Checks if is finished.
         *
         * @return true, if is finished
         */
        public boolean isFinished() {
            return isFinished;
        }

        /**
         * Sets the finished.
         *
         * @param isFinished1 the new finished
         */
        public void setFinished(boolean isFinished1) {
            this.isFinished = isFinished1;
        }
    }

    /**
     * Gets the disk utility.
     *
     * @return the disk utility
     */
    public AutosaveFileUtility getDiskUtility() {
        return autosaveFileUtility;
    }

    /**
     * Gets the active terminal.
     *
     * @param partObjectList the part object list
     * @param activeTabIdParam the active tab id param
     * @return the active terminal
     */
    public int getActiveTerminal(List<IAutoSaveObject> partObjectList, int activeTabIdParam) {
        int activeTabId = activeTabIdParam;
        int terminalCnt = partObjectList.size();
        int id = activeTabId == -1 ? 0 : activeTabId;
        int itrCnt = 0;

        activeTabId = -1;
        while (itrCnt < terminalCnt) {
            if (id == terminalCnt) {
                id = 0;
            }

            if (partObjectList.get(id) != null) {
                activeTabId = id;
                break;
            }

            id++;
            itrCnt++;
        }

        if (activeTabId == -1) {
            activeTabId = 0;
        }

        return activeTabId;
    }

    /**
     * Gets the auto save data file pattern.
     *
     * @return the auto save data file pattern
     */
    public static String getAutoSaveDataFilePattern() {
        return AUTOSAVE_DATA_FILE_PATTERN;
    }

    /**
     * Gets the autosave info filename.
     *
     * @return the autosave info filename
     */
    public static String getAutosaveInfoFilename() {
        return AUTOSAVE_INFO_FILENAME;
    }

    /**
     * Gets the autosave info tmp filename.
     *
     * @return the autosave info tmp filename
     */
    public static String getAutosaveInfoTmpFilename() {
        return AUTOSAVE_INFO_TMP_FILENAME;
    }

    /**
     * Clean file for autosave disable.
     *
     * @param fileName the file name
     */
    public void cleanFileForAutosaveDisable(String fileName) {
        autosaveFileUtility.deleteFile(fileName);
    }

    /**
     * Gets the default intervall.
     *
     * @return the default intervall
     */
    public static int getDefaultIntervall() {
        return DEFAULT_INTERVAL * 1000 * 60;
    }
}
