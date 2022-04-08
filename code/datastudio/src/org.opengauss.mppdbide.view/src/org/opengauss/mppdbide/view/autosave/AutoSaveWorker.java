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

package org.opengauss.mppdbide.view.autosave;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.bl.autosave.AutoSaveDbgObjInfo;
import org.opengauss.mppdbide.bl.autosave.AutoSaveInfo;
import org.opengauss.mppdbide.bl.autosave.AutoSaveMetadata;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import org.opengauss.mppdbide.view.ui.autosave.AutoSaveTerminalStatus;
import org.opengauss.mppdbide.view.ui.autosave.IAutoSaveDbgObject;
import org.opengauss.mppdbide.view.ui.autosave.IAutoSaveObject;
import org.opengauss.mppdbide.view.ui.terminalautosave.SQLTerminalAutoSaveIf;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class AutoSaveWorker.
 *
 * @since 3.0.0
 */
public class AutoSaveWorker extends UIWorkerJob {
    private boolean autoSaveEncrypted;
    private boolean isEnabled;
    private String encoding;
    private IExecTimer timer;
    private int noOfFilesSaved;

    /**
     * Instantiates a new auto save worker.
     *
     * @param name the name
     * @param family the family
     */
    public AutoSaveWorker(String name, Object family) {
        super(name, family);
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws FileOperationException the file operation exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob()
            throws DatabaseOperationException, DatabaseCriticalException, FileOperationException, Exception {
        timer = new ExecTimer("Terminal Auto Save ");
        noOfFilesSaved = 0;
        timer.start();
        isEnabled = AutoSaveManager.getInstance().isAutoSaveEnabled();
        if (isEnabled) {
            autoSaveEncrypted = AutoSaveManager.getInstance().isAutoSaveEncrypted();
            if (BLPreferenceManager.getInstance().getBLPreference().getFileEncoding().isEmpty()) {
                encoding = StandardCharsets.UTF_8.name();
            } else {
                encoding = BLPreferenceManager.getInstance().getBLPreference().getFileEncoding();
            }
            AutoSaveManager.getInstance().initialize();
            setAutosaveMembers();

            List<IAutoSaveObject> openTabsList = UIElement.getInstance().getAllOpenTabs();

            // AutoSave is not applicable for file terminal
            List<IAutoSaveObject> openSQLTerminalTabList = openTabsList.stream().collect(Collectors.toList());
            openTabsList.stream().forEach(tab -> {
                if (tab instanceof SQLTerminalAutoSaveIf && ((SQLTerminalAutoSaveIf) tab).isFileTerminalFlag()) {
                    openSQLTerminalTabList.remove(tab);
                }
            });

            boolean isUpdated = deleteMetaData(openSQLTerminalTabList);
            if (openSQLTerminalTabList != null) {
                List<IAutoSaveObject> modifiedTabList = addUpdateMetaData(isUpdated, openSQLTerminalTabList);
                storeAutoSaveContent(modifiedTabList);
                if (modifiedTabList.size() != 0) {
                    AutoSaveManager.getInstance().saveAutoSaveInfo();
                }
            }
        } else {
            AutoSaveManager.getInstance().destroy();
        }
        return null;
    }

    private void setAutosaveMembers() {
        AutoSaveInfo autosaveInfo = AutoSaveManager.getInstance().getAutosaveInfo();
        autosaveInfo.setTimestamp(new Date().toString());
        if (UIElement.getInstance().getActivePartObject() instanceof IAutoSaveObject) {
            autosaveInfo.setActiveTerminalName(
                    ((IAutoSaveObject) UIElement.getInstance().getActivePartObject()).getElementID());
        } else {
            autosaveInfo.setActiveTerminalName("");
        }
    }

    private boolean deleteMetaData(List<IAutoSaveObject> openTabsList) {
        AutoSaveInfo autosaveInfo = AutoSaveManager.getInstance().getAutosaveInfo();
        List<AutoSaveMetadata> autosaveMDList = autosaveInfo.getAutosaveMD();
        Iterator<AutoSaveMetadata> metadataIterator = autosaveMDList.iterator();
        boolean isMetaDataChanged = false;
        while (metadataIterator.hasNext()) {
            AutoSaveMetadata metaData = metadataIterator.next();
            String uiID = metaData.getTabID();
            boolean isMetaDataFound = false;
            for (IAutoSaveObject openTab : openTabsList) {
                if (uiID.equals(openTab.getElementID())) {
                    if (openTab.isModifiedAfterCreate()) {
                        isMetaDataFound = true;
                    }
                    break;
                }
            }
            if (!isMetaDataFound) {
                AutoSaveManager.getInstance().deleteAutosaveMetadata(metaData);
                isMetaDataChanged = true;
            }
        }

        return isMetaDataChanged;
    }

    private List<IAutoSaveObject> addUpdateMetaData(boolean metaDataChangeDoneParam, List<IAutoSaveObject> openTabsList)
            throws MPPDBIDEException {
        boolean metaDataChangeDone = metaDataChangeDoneParam;
        AutoSaveInfo autosaveInfo = AutoSaveManager.getInstance().getAutosaveInfo();

        List<IAutoSaveObject> modifiedTabList = new ArrayList<IAutoSaveObject>();
        Iterator<IAutoSaveObject> openTabIterator = openTabsList.iterator();
        List<AutoSaveMetadata> mdList = new ArrayList<AutoSaveMetadata>();

        int tabIdFileCntr = 1;

        while (openTabIterator.hasNext()) {
            IAutoSaveObject openTab = openTabIterator.next();
            String uiID = openTab.getElementID();
            AutoSaveMetadata metaData = autosaveInfo.getMetaData(uiID);

            // only create entries in file if there is a modification
            if (openTab.isModified() && AutoSaveTerminalStatus.LOAD_FINISHED == openTab.getStatus()) {
                if (metaData == null) {
                    metaData = createAutosaveMetaData(openTab, tabIdFileCntr);
                    tabIdFileCntr++;
                    AutoSaveManager.getInstance().getAutosaveInfo().addAutoSaveMetadata(metaData);
                } else {
                    updateModifiedMetaData(metaData, openTab, autoSaveEncrypted, encoding);
                }

                metaDataChangeDone = true;
                mdList.add(metaData);
                modifiedTabList.add(openTab);
            } else {
                if (metaData != null) {
                    updateModifiedMetaData(metaData, openTab, metaData.isEncrypted(), metaData.getEncoding());
                    metaDataChangeDone = true;
                    mdList.add(metaData);
                }
            }
        }

        if (metaDataChangeDone) {
            autosaveInfo.setAutosaveMD(mdList);
            AutoSaveManager.getInstance().saveAutoSaveInfo();
        }

        return modifiedTabList;
    }

    private void updateModifiedMetaData(AutoSaveMetadata autosaveMD, IAutoSaveObject openTab, boolean isEncryptFlag,
            String encoding1) {
        autosaveMD.setConnectionName(openTab.getConnectionName());
        autosaveMD.setDatabaseName(openTab.getDatabaseName());
        autosaveMD.setTabID(openTab.getElementID());
        autosaveMD.setTabLabel(openTab.getTabLabel());
        autosaveMD.setTabToolTip(openTab.getTabToolTip());
        autosaveMD.setType(openTab.getType());
        autosaveMD.setEncrypted(isEncryptFlag);
        autosaveMD.setEncoding(encoding1);
        if (openTab instanceof IAutoSaveDbgObject) {
            IAutoSaveDbgObject dbgObjTab = (IAutoSaveDbgObject) openTab;
            AutoSaveDbgObjInfo dbgObjInfo = new AutoSaveDbgObjInfo();
            dbgObjInfo.setDirty(dbgObjTab.isObjDirty());
            dbgObjInfo.setName(dbgObjTab.getName());
            dbgObjInfo.setOid(dbgObjTab.getOid());
            dbgObjInfo.setSchemaName(dbgObjTab.getNameSpaceName());
            dbgObjInfo.setObjType(dbgObjTab.getDbgObjType());
            autosaveMD.setDbgObjInfo(dbgObjInfo);
        }

        autosaveMD.setTimestamp(new Date().toString());
        autosaveMD.updateShaval();
    }

    private AutoSaveMetadata createAutosaveMetaData(IAutoSaveObject openTabs, int tabIdFileCntr) {
        AutoSaveMetadata newOpenTab = new AutoSaveMetadata();
        newOpenTab.setConnectionName(openTabs.getConnectionName());
        newOpenTab.setDatabaseName(openTabs.getDatabaseName());
        newOpenTab.setEncrypted(autoSaveEncrypted);
        newOpenTab.setEncoding(encoding);
        newOpenTab.setAutoSaveFileName("tabinfo" + System.currentTimeMillis() + tabIdFileCntr + ".autosave");
        newOpenTab.setTabID(openTabs.getElementID());
        newOpenTab.setTabLabel(openTabs.getTabLabel());
        newOpenTab.setTabToolTip(openTabs.getTabToolTip());
        newOpenTab.setType(openTabs.getType());
        if (openTabs instanceof IAutoSaveDbgObject) {
            AutoSaveDbgObjInfo dbgObjInfo = new AutoSaveDbgObjInfo();
            IAutoSaveDbgObject dbgObjTab = (IAutoSaveDbgObject) openTabs;
            dbgObjInfo.setDirty(dbgObjTab.isObjDirty());
            dbgObjInfo.setName(dbgObjTab.getName());
            dbgObjInfo.setOid(dbgObjTab.getOid());
            dbgObjInfo.setSchemaName(dbgObjTab.getNameSpaceName());
            dbgObjInfo.setObjType(dbgObjTab.getDbgObjType());
            newOpenTab.setDbgObjInfo(dbgObjInfo);
        }
        newOpenTab.setTimestamp(new Date().toString());
        newOpenTab.updateShaval();
        return newOpenTab;
    }

    private void storeAutoSaveContent(List<IAutoSaveObject> modifiedTabList) {
        IExecTimer timertoWriteintoFile = null;
        for (Iterator<IAutoSaveObject> iterator = modifiedTabList.iterator(); iterator.hasNext();) {
            IAutoSaveObject iAutoSaveObject = (IAutoSaveObject) iterator.next();

            AutoSaveInfo autosaveInfo = AutoSaveManager.getInstance().getAutosaveInfo();
            AutoSaveMetadata autoSaveMetadata = autosaveInfo.getMetaData(iAutoSaveObject.getElementID());
            if (autoSaveMetadata != null) {
                iAutoSaveObject.setModified(false);
                BufferedOutputStream writer = null;
                try (FileOutputStream storeAutosaveData = AutoSaveManager.getInstance()
                        .getDataWriter(autoSaveMetadata)) {
                    writer = new BufferedOutputStream(storeAutosaveData);
                    String text = iAutoSaveObject.getText();
                    if (text.length() != 0) {
                        byte[] bytes = text.getBytes(autoSaveMetadata.getEncoding());
                        if (autoSaveMetadata.isEncrypted()) {
                            SecureUtil secureUtil = new SecureUtil();
                            secureUtil.setPackagePath(ConnectionProfileManagerImpl.getInstance().getDiskUtility()
                                    .getOsCurrentUserFolderPath());
                            bytes = secureUtil.encryptByteArray(bytes);
                        }
                        timertoWriteintoFile = new ExecTimer(
                                "Write Autosave file " + autoSaveMetadata.getAutoSaveFileName());
                        timertoWriteintoFile.start();
                        writer.write(bytes);
                        writer.flush();
                    }

                    iAutoSaveObject.setTabStatusMsg(MessageConfigLoader.getProperty(
                            IMessagesConstants.PRESERVESQL_PERIODIC_SAVE_TAB_STATUS, System.currentTimeMillis()));
                    noOfFilesSaved++;
                } catch (FileOperationException | IOException | DataStudioSecurityException
                        | OutOfMemoryError exception) {
                    iAutoSaveObject.setModified(true);
                    MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(
                            IMessagesConstants.PRESERVESQL_FILEWRITEEXCEPTION, new Date().toString()), exception);

                    // delete the file if possible
                    AutoSaveManager.getInstance().cleanFileForAutosaveDisable(autoSaveMetadata.getAutoSaveFileName());
                } finally {
                    storeAutoSaveFinally(timertoWriteintoFile, writer);
                }

                updateModifiedMetaData(autoSaveMetadata, iAutoSaveObject, autoSaveMetadata.isEncrypted(),
                        autoSaveMetadata.getEncoding());
            }
        }
    }

    private void storeAutoSaveFinally(IExecTimer timertoWriteintoFile, BufferedOutputStream writer) {
        if (null != writer) {
            try {
                writer.close();
                if (null != timertoWriteintoFile) {
                    timertoWriteintoFile.stopAndLogNoException();
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("AutoSaveWorker: Stream close Error.", exception);
            }
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        // This will be changed into logger once tab wise status bar is
        // implemented
        if (isEnabled) {
            UIElement.getInstance().setStatusBarMessage(MessageConfigLoader
                    .getProperty(IMessagesConstants.PRESERVESQL_PERIODIC_SAVE_FINISHED, new Date().toString()));
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        return;
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        return;
    }

    /**
     * On MPPDBIDE exception.
     *
     * @param exception the exception
     */
    public void onMPPDBIDEException(MPPDBIDEException exception) {
        super.onMPPDBIDEException(exception);
        MPPDBIDELoggerUtility.error("AutoSaveWorker: MPPDBException thrown.", exception);
    }

    /**
     * On exception.
     *
     * @param exception the exception
     */
    public void onException(Exception exception) {
        super.onException(exception);
        MPPDBIDELoggerUtility.error("AutoSaveWorker: OnException handle.", exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (MPPDBIDELoggerUtility.isInfoEnabled()) {
            MPPDBIDELoggerUtility.info("Number of files autosaved :" + noOfFilesSaved);
        }
        timer.stopAndLog();
        if (AutoSaveManager.getInstance().isAutoSaveEnabled()) {
            schedule(AutoSaveManager.getInstance().getAutoSaveInterval());
        } else {
            schedule(AutoSaveManager.getDefaultIntervall());
        }
    }
}
