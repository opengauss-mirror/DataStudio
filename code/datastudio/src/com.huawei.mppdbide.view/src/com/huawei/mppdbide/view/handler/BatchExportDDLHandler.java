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

package com.huawei.mppdbide.view.handler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Named;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.IServerObjectBatchOperations;
import com.huawei.mppdbide.bl.export.BatchExportDDLFilter;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.export.ExportManager;
import com.huawei.mppdbide.bl.export.ExportParameters;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TriggerMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.exportdata.ExportZipData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileCompressException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.FileValidationUtils;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.DSEventWithCount;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.utils.security.SecureRandomGenerator;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.dialog.ExportZipOptionDialog;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class BatchExportDDLHandler.
 *
 * @since 3.0.0
 */
public class BatchExportDDLHandler {

    /**
     * The valid obj list.
     */
    HashMap<String, BatchExportDDLObjects> validObjList;

    /**
     * The validation error.
     */
    String validationError;
    private String ddlOrData;
    private Database db;
    private int totalExportCount;

    /**
     * The obj tmp.
     */
    ServerObject objTmp;

    /**
     * The issame object type.
     */
    boolean issameObjectType = true;
    private static final String DATE_FORMAT = "yyyyMMddHHmmss";

    /**
     * Instantiates a new batch export DDL handler.
     */
    public BatchExportDDLHandler() {
        this.validObjList = new HashMap<String, BatchExportDDLObjects>(10);
        resetFields();
    }

    private void appendExportTypeToFileName(ServerObject object, StringBuilder name) {
        switch (object.getType()) {
            case TABLEMETADATA:
            case PARTITION_TABLE: {
                name.append("table_");
                break;
            }
            case PLSQLFUNCTION:
            case CFUNCTION:
            case SQLFUNCTION: {
                name.append("func_");
                break;
            }
            case NAMESPACE: {
                name.append("namespace_");
                break;
            }
            case VIEW_META_DATA: {
                name.append("view_");
                break;
            }
            case SEQUENCE_METADATA_GROUP: {
                name.append("sequence_");
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Gets the default file name.
     *
     * @param exportType the export type
     * @return the default file name
     */
    private String getDefaultFileName(EXPORTTYPE exportType) {
        StringBuilder name = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        getExportType(exportType, name);
        if (IHandlerUtilities.getObjectBrowserSelectedObjects().size() > 1) {
            List<ServerObject> serverObjects = (List<ServerObject>) IHandlerUtilities.getObjectBrowserSelectedObjects();
            ServerObject firstElement = serverObjects.get(0);
            String o1Type = firstElement.getType().toString();
            serverObjects.stream().skip(1).filter(item -> item != null && issameObjectType).forEach(item -> {
                String o2Type = item.getType().toString();
                issameObjectType = validateExportObjects(o1Type, o2Type);
            });

            if (issameObjectType) {
                appendExportTypeToFileName(firstElement, name);
            }
        } else {
            ServerObject objToExport = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
            if (objToExport != null) {
                appendExportTypeToFileName(objToExport, name);
                name.append(objToExport.getName());
            }  
        }
        issameObjectType = true;
        name.append("_" + new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        return name.toString();
    }

    private boolean validateExportObjects(String o1Type, String o2Type) {
        boolean objectType;
        if (checkForTable(o1Type) && !(checkForTable(o2Type))) {
            objectType = false;
        } else if (checkForFunc(o1Type) && !checkForFunc(o2Type)) {
            objectType = false;
        } else {
            objectType = checkNamespaceSequenceViewType(o1Type, o2Type);
        }
        return objectType;
    }

    private boolean checkNamespaceSequenceViewType(String o1Type, String o2Type) {
        boolean isNameSpaceSeqViewType;
        if ("NAMESPACE".equals(o1Type) && !("NAMESPACE".equals(o2Type))) {
            isNameSpaceSeqViewType = false;
        } else if ("VIEW_META_DATA".equals(o1Type) && !("VIEW_META_DATA".equals(o2Type))) {
            isNameSpaceSeqViewType = false;
        } else if ("SEQUENCE_METADATA_GROUP".equals(o1Type) && !("SEQUENCE_METADATA_GROUP".equals(o2Type))) {
            isNameSpaceSeqViewType = false;
        } else {
            isNameSpaceSeqViewType = true;
        }
        return isNameSpaceSeqViewType;
    }

    private void getExportType(EXPORTTYPE exportType, StringBuilder name) {
        if (exportType == EXPORTTYPE.SQL_DDL) {
            name.append("ddl_");
        } else {
            name.append("ddl_data_");
        }
    }

    private boolean checkForFunc(String o2Type) {
        return "PLSQLFUNCTION".equals(o2Type) || "CFUNCTION".equals(o2Type) || "SQLFUNCTION".equals(o2Type);
    }

    private boolean checkForTable(String o2Type) {
        return "PARTITION_TABLE".equals(o2Type) || "TABLEMETADATA".equals(o2Type);
    }

    /**
     * Sets the export type.
     *
     * @param exportOption the export option
     * @return the exporttype
     */
    private EXPORTTYPE setExportType(String exportOption) {
        ddlOrData = "batchexportddldata".equals(exportOption)
                ? MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_DATA)
                : MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL);
        return "batchexportddldata".equals(exportOption) ? EXPORTTYPE.SQL_DDL_DATA : EXPORTTYPE.SQL_DDL;
    }

    /**
     * Execute.
     *
     * @param exportOption the export option
     */
    @SuppressWarnings("unchecked")
    @Execute
    public void execute(@Optional @Named("batchexport.ddl") String exportOption) {
        List<Object> objsToExport = (List<Object>) IHandlerUtilities.getObjectBrowserSelectedObjects();
        EXPORTTYPE exportType = setExportType(exportOption);
        if (objsToExport == null) {
            return;
        }
        if (objsToExport.size() > 100) {
            String text = MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_WARNING_TEXT, ddlOrData);
            int res = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_HEADER, ddlOrData), text);

            if (res != UIConstants.OK_ID) {
                return;
            }
        }

        boolean validObjs = isExportAllowed(exportType, objsToExport);

        if (!validObjs) {
            showInvalidObjectExportError();
            return;
        }

        int res = UIConstants.OK_ID;

        boolean isTablespaceOption = IHandlerUtilities.getTablespaceSelectionOptions();

        scheduleBatchExportDDLJob(exportType, res, isTablespaceOption);
    }

    private void scheduleBatchExportDDLJob(EXPORTTYPE exportType, int res, boolean isTablespaceOption) {
        ExportZipOptionDialog dialog = null;

        String windowTitle = "";
        switch (exportType) {
            case SQL_DDL: {
                windowTitle = MessageConfigLoader.getProperty(IMessagesConstants.SQL_DDL_EXPORT_WINDOW_TITLE);
                break;
            }
            case SQL_DDL_DATA: {
                windowTitle = MessageConfigLoader.getProperty(IMessagesConstants.SQL_DDL_DATA_EXPORT_WINDOW_TITLE);
                break;
            }
            default: {
                windowTitle = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OPTION_TITLE);
                break;
            }
        }
        dialog = new ExportZipOptionDialog(Display.getDefault().getActiveShell(), getDefaultFileName(exportType), true,
                false, windowTitle);

        if (dialog.open() != UIConstants.OK_ID) {
            return;
        }

        if (!db.isShowDDLSupportByServer() && !ShowDDLViewLayerHelper.getClientSSLKeyFile(db)) {
            return;
        }

        String targetFile = dialog.getExportOption().getFilePathWithSuffixFormat();
        String tempPath = null;
        if (dialog.getExportOption().isZip()) {
            tempPath = Normalizer.normalize(System.getenv(MPPDBIDEConstants.TEMP_ENVIRONMENT_VARIABLE),
                    Normalizer.Form.NFD);
            if (!DSFilesWrapper.isExistingDirectory(tempPath)) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                MPPDBIDEConstants.LINE_SEPARATOR,
                                MessageConfigLoader.getProperty(IMessagesConstants.INVALID_TEMP_ENVIRONMENT_VARIABLE)));
                MPPDBIDELoggerUtility.error("TEMP environment varibale is not an existing directory.");
                return;
            }
        }

        BatchExportDDLWorker worker = new BatchExportDDLWorker(totalExportCount, validObjList, exportType, targetFile,
                isTablespaceOption, objTmp, dialog.getExportOption().isZip(), tempPath);

        worker.setTaskDB(db);
        resetFields();
        worker.schedule();
    }

    /**
     * Reset fields.
     */
    private void resetFields() {
        this.totalExportCount = 0;
        this.db = null;
        this.ddlOrData = null;
        this.validObjList.clear();
        this.validationError = null;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute(@Named("batchexport.ddl") String exportOption) {
        if ("batchexportddldata".equals(exportOption) && !IHandlerUtilities.getExportDataSelectionOptions()) {
            return false;
        }
        return true;
    }

    /**
     * Checks if is export allowed.
     *
     * @param exportType the export type
     * @param objsToExport the objs to export
     * @return true, if is export allowed
     */
    private boolean isExportAllowed(EXPORTTYPE exportType, List<Object> objsToExport) {
        totalExportCount = 0;
        validObjList.clear();

        Database tempdb = null;
        for (Object obj : objsToExport) {

            if (isInValidExportObject(obj)) {
                return false;
            } else if (obj instanceof ServerObject) {
                ServerObject exportObj = (ServerObject) obj;
                if (tempdb == null) {
                    tempdb = exportObj.getDatabase();
                } else if (!tempdb.equals(exportObj.getDatabase())) {
                    validationError = IMessagesConstants.BATCH_EXPORT_DDL_MULTIPLE_DB;
                    return false;
                }
                IServerObjectBatchOperations iObj = (IServerObjectBatchOperations) exportObj;
                if (!iObj.isExportAllowed(exportType)) {
                    validationError = IMessagesConstants.BATCH_EXPORT_DDL_VALID_OBJ;
                    return false;
                }

                objTmp = exportObj;
                boolean insertDone = addObject(exportObj);
                if (insertDone) {
                    totalExportCount++;
                }
            }
        }
        if (validObjList.isEmpty()) {
            validationError = IMessagesConstants.BATCH_EXPORT_DDL_NO_OBJ;
            return false;
        }
        this.db = tempdb;
        return true;
    }

    private boolean isInValidExportObject(Object obj) {
        if (obj instanceof Server || obj instanceof OLAPObjectGroup || obj instanceof OLAPObjectList
                || !(obj instanceof IServerObjectBatchOperations)) {
            validationError = IMessagesConstants.BATCH_EXPORT_DDL_VALID_OBJ;
            return true;
        }
        return false;
    }

    /**
     * Adds the object.
     *
     * @param exportObj the export obj
     * @return true, if successful
     */
    private boolean addObject(ServerObject exportObj) {
        boolean insertDone = false;
        if (exportObj instanceof Namespace) {

            Object obj = UIElement.getInstance().getActivePartObject();
            if (obj instanceof SearchWindow) {
                return false;
            }

            BatchExportDDLObjects newExpObj = new BatchExportDDLObjects();
            insertDone = newExpObj.addObject(exportObj);
            this.validObjList.put(exportObj.getName(), newExpObj);
        } else {
            Namespace ns = (Namespace) exportObj.getParent();
            if (null != ns) {
                BatchExportDDLObjects newExpObj = validObjList.get(ns.getName());
                if (newExpObj == null) {
                    newExpObj = new BatchExportDDLObjects();
                    insertDone = newExpObj.addObject(exportObj);
                    this.validObjList.put(ns.getName(), newExpObj);
                } else {
                    insertDone = newExpObj.addObject(exportObj);
                }

            }
        }
        return insertDone;
    }

    /**
     * Show invalid object export error.
     */
    private void showInvalidObjectExportError() {
        if (validationError == null) {
            return;
        }

        String err = MessageConfigLoader.getProperty(validationError, ddlOrData);

        if (IMessagesConstants.BATCH_EXPORT_DDL_VALID_OBJ.equals(validationError)) {
            String detailError;
            if (MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL).equals(ddlOrData)) {
                detailError = err + MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_DDL_VALID_OBJ_LIST);
            } else {
                detailError = err
                        + MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_DDLDATA_VALID_OBJ_LIST);

            }
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_FAIL_HEADER, ddlOrData),
                    MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_ERROR_SHORT_MSG)
                            + MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILURE_DETAILS_DESCRIPTION),
                    detailError, null);
            return;
        } else {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_FAIL_HEADER, ddlOrData), err);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BatchExportDDLWorker.
     */
    private static class BatchExportDDLWorker extends ImportExportPreUIWorker implements IDSListener {
        ArrayList<Namespace> schemasToExport;
        BatchExportDDLObjects otherObjectsToExport;
        private ExportManager exportManager;
        private BatchExportDDLFilter filter;
        private String path;
        private EXPORTTYPE exportType;
        private boolean isTablespaceOption;
        private String elapsedTime = null;
        private IExecTimer exc = new ExecTimer("BatchExportDDLWorker");
        private BufferedOutputStream bfs = null;
        private Path tempFolderPath;
        private int totalExportCount;
        private int currentExportCount;
        private BatchExportFileMerger fileMerger = null;
        private DSEventTable eventTable;
        private HashMap<String, BatchExportDDLObjects> objectsToExport;
        private Thread fileMergerThread = null;
        private String encryptedPrd;
        private boolean cancelFlag;
        private List<String> exportTempFiles;
        private boolean isZip;
        private String zipPath;
        private ExportZipData exportZipData;
        private ServerObject obj;

        /**
         * Instantiates a new batch export DDL worker.
         *
         * @param totalExportCount the total export count
         * @param objectsToExport the objects to export
         * @param exportType the export type
         * @param path the path
         * @param isTablespaceOption the is tablespace option
         * @param objTmp the obj tmp
         * @param tempPath path
         */
        public BatchExportDDLWorker(int totalExportCount, HashMap<String, BatchExportDDLObjects> objectsToExport,
                EXPORTTYPE exportType, String path, boolean isTablespaceOption, ServerObject objTmp, boolean isZip,
                String tempPath) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_JOB), MPPDBIDEConstants.CANCELABLEJOB,
                    IMessagesConstants.IMPORT_EXPORT_LOGIN_FAILED);
            this.totalExportCount = totalExportCount;
            this.currentExportCount = 0;
            this.exportManager = new ExportManager();
            this.filter = null;
            if (isZip) {
                this.zipPath = path;
                this.path = ExportZipData.getTempPathStr(path, ".sql", tempPath);
            } else {
                this.path = path;
            }
            this.exportType = exportType;
            this.isTablespaceOption = isTablespaceOption;
            this.schemasToExport = new ArrayList<Namespace>(4);
            this.otherObjectsToExport = new BatchExportDDLObjects();
            this.eventTable = new DSEventTable();
            this.objectsToExport = new HashMap<String, BatchExportDDLObjects>();
            this.objectsToExport.putAll(objectsToExport);
            this.cancelFlag = false;
            this.exportTempFiles = new ArrayList<String>(10);
            Path profileFolderPath = ConnectionProfileManagerImpl.getInstance().getDiskUtility().getProfileFolderPath();
            this.tempFolderPath = Paths.get(profileFolderPath.toString(), MPPDBIDEConstants.TEMP_FOLDER_PATH);
            this.exportZipData = new ExportZipData();
            this.isZip = isZip;
            this.obj = objTmp;
        }

        /**
         * Update progress bar label.
         *
         * @param obj the obj
         */
        private void updateProgressBarLabel(ServerObject obj) {
            String exportProp = exportType == EXPORTTYPE.SQL_DDL ? IMessagesConstants.EXPORT_DDL_PROGRESS_NAME
                    : IMessagesConstants.EXPORT_DDL_DATA_PROGRESS_NAME;

            String serverObjName = (obj.getDatabase() != null) ? obj.getDatabase().getName() : "";
            String serverName = (obj.getDatabase() != null) ? obj.getDatabase().getServerName() : "";
            String label = ProgressBarLabelFormatter.getProgressLabelForBatchExport(exportProp, this.currentExportCount,
                    this.totalExportCount, obj.getName(), serverObjName, serverName);
            this.setName(label);
            refreshProgressBar();
        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class BatchExportFileMerger.
         */
        private static class BatchExportFileMerger implements Runnable {
            List<String> files;
            final BatchExportDDLFilter filter;
            private int sleepTime;
            private volatile boolean exit;
            private boolean exceptionFlag;

            /**
             * Instantiates a new batch export file merger.
             *
             * @param filter the filter
             */
            public BatchExportFileMerger(BatchExportDDLFilter filter) {
                files = new ArrayList<String>(4);
                this.filter = filter;
                this.sleepTime = 500;
                this.exit = false;
                this.exceptionFlag = false;
            }

            /**
             * Adds the file.
             *
             * @param file the file
             */
            public void addFile(String file) {
                files.add(file);
            }

            /**
             * Adds the files.
             *
             * @param fewfiles the fewfiles
             */
            public void addFiles(List<String> fewfiles) {
                files.addAll(fewfiles);
            }

            /**
             * Delete file.
             *
             * @param file the file
             */
            private void deleteFile(String file) {
                files.remove(file);
            }

            /**
             * Checks if is file merge list empty.
             *
             * @return true, if is file merge list empty
             */
            public boolean isFileMergeListEmpty() {
                return files.isEmpty();
            }

            /**
             * Checks for exception happened.
             *
             * @return true, if successful
             */
            public boolean hasExceptionHappened() {
                return this.exceptionFlag;
            }

            /**
             * Delete all files.
             */
            public void deleteAllFiles() {
                for (String onefile : files) {
                    try {
                        Files.deleteIfExists(Paths.get(onefile));
                    } catch (IOException exception) {
                        MPPDBIDELoggerUtility.error("BatchExportDDLHandler: delete all files for batch export failed.",
                                exception);
                    }
                }
                files.clear();
            }

            @Override
            public void run() {
                while (!exit) {
                    String currentFile = null;
                    try {
                        if (files.size() > 0) {
                            currentFile = files.get(0);
                            UIElement.getInstance().validateFileSize(currentFile);
                            filter.removeComments(currentFile);
                            deleteFile(currentFile);
                        } else {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException exception) {
                                MPPDBIDELoggerUtility.error("BatchExportDDLHandler: InterruptedException occurred.",
                                        exception);
                            }
                        }
                    } catch (FileOperationException exception) {
                        this.handleException();
                        closeFilter();
                        return;
                    } catch (Exception exception) {
                        MPPDBIDELoggerUtility.error("BatchExportDDLHandler: exception occurred for batch delete.",
                                exception);
                        this.handleException();
                        closeFilter();
                        return;
                    }
                }
                closeFilter();
            }

            /**
             * Close filter.
             */
            private void closeFilter() {
                filter.closeOutputStream();
            }

            /**
             * Handle exception.
             */
            private void handleException() {
                this.exceptionFlag = true;
                this.exit = true;
                deleteAllFiles();
                return;
            }

            /**
             * Stop.
             */
            public void stop() {
                this.exit = true;
            }

            /**
             * Checks if is exited.
             *
             * @return true, if is exited
             */
            public boolean isExited() {
                return exit;
            }
        }

        /**
         * Delete temp files if exists.
         */
        private void deleteTempFilesIfExists() {
            for (String onefile : this.exportTempFiles) {
                try {
                    Files.deleteIfExists(Paths.get(onefile));
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("BatchExportDDLHandler: delete temp files if exists failed.",
                            exception);
                }
            }
            this.exportTempFiles.clear();
        }

        @SuppressWarnings("rawtypes")
        void sortObjectsToExport() {
            otherObjectsToExport.clear();
            Iterator<Entry<String, BatchExportDDLObjects>> it = this.objectsToExport.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                BatchExportDDLObjects expObjs = (BatchExportDDLObjects) pair.getValue();
                if (expObjs.getNamespaceToExport() == null) {
                    otherObjectsToExport.addObject(expObjs.getAllDebugObjectsToExport());
                    otherObjectsToExport.addObject(expObjs.getAllTablesToExport());
                    otherObjectsToExport.addObject(expObjs.getAllPartitionTablesToExport());
                    otherObjectsToExport.addObject(expObjs.getAllViewsToExport());
                    otherObjectsToExport.addObject(expObjs.getAllSequencesToExport());
                    otherObjectsToExport.addObject(expObjs.getTriggerExport());
                } else {
                    schemasToExport.add(expObjs.getNamespaceToExport());
                }
            }
        }

        /**
         * Gets the working dir.
         *
         * @return the working dir
         * @throws FileOperationException the file operation exception
         */
        private File getWorkingDir()
                throws FileOperationException, DatabaseOperationException {
            Path tempFolder = (tempFolderPath.toAbsolutePath() != null) ? tempFolderPath.toAbsolutePath().normalize()
                    : null;
            if (tempFolder != null) {
                if (!Files.exists(tempFolder)) {
                    FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(tempFolder.toString(),
                            true, null, true);
                }
                File workingDir = new File(tempFolder.toString());
                return workingDir;
            }
            return null;
        }

        /**
         * Gets the random file name.
         *
         * @return the random file name
         */
        private String getRandomFileName() {
            byte[] byteRandomArray = SecureRandomGenerator.getRandomNumber();
            BigInteger generatedRandom = new BigInteger(byteRandomArray);
            return generatedRandom.abs().toString();
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, FileOperationException,
                MPPDBIDEException, Exception {
            int sleepTime = 20;

            exc.start();

            if (getDatabase() == null) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE));
                throw new MPPDBIDEException(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE);
            }
            setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));

            sortObjectsToExport();

            this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, this);
            exportManager.setEventTable(eventTable);

            Database dbase = getDatabase();

            if (null != dbase && dbase.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
                doInnerJob(true);
            } else {
                doInnerJob(false);
            }
            while (!fileMerger.isFileMergeListEmpty() && !fileMerger.isExited() && fileMergerThread.isAlive()
                    && !this.isCancel()) {
                checkAndThrowFileException();
                Thread.sleep(sleepTime);
            }

            fileMerger.stop();
            checkAndThrowFileException();

            doCompress();

            exc.stopAndLog();

            return null;
        }

        private void doCompress() throws FileCompressException {
            if (isZip) {
                commitExportFile();
                exportZipData.doCompress(path, zipPath);
            }
        }

        /**
         * Check and throw file exception.
         *
         * @throws DatabaseOperationException the database operation exception
         */
        private void checkAndThrowFileException() throws DatabaseOperationException {
            if (fileMerger.hasExceptionHappened()) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_IO_ERROR_EXPORT));
                throw new DatabaseOperationException(IMessagesConstants.ERR_IO_ERROR_EXPORT);
            }
        }

        /**
         * Check and throw cancel exception.
         *
         * @throws DatabaseOperationException the database operation exception
         */
        private void checkAndThrowCancelException() throws DatabaseOperationException {
            if (this.cancelFlag) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST));
                throw new DatabaseOperationException(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST);
            }
        }

        /**
         * Do inner job.
         *
         * @param passwordFlag the password flag
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws DataStudioSecurityException the data studio security
         * exception
         * @throws FileOperationException the file operation exception
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private void doInnerJob(boolean passwordFlag) throws DatabaseOperationException, DatabaseCriticalException,
                DataStudioSecurityException, FileOperationException, IOException {
            bfs = new BufferedOutputStream(new FileOutputStream(new File(path)));
            filter = new BatchExportDDLFilter(bfs);
            fileMerger = new BatchExportFileMerger(filter);
            fileMergerThread = new Thread(fileMerger, "fileMerger");
            fileMergerThread.start();

            exportSchemas(passwordFlag);
            exportOtherObjects(passwordFlag);
        }

        /**
         * Export other objects.
         *
         * @param passwordFlag the password flag
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws DataStudioSecurityException the data studio security
         * exception
         * @throws FileOperationException the file operation exception
         */
        @SuppressWarnings("unchecked")
        private void exportOtherObjects(boolean passwordFlag) throws DatabaseOperationException,
                DatabaseCriticalException, DataStudioSecurityException, FileOperationException {
            // initial ArrayList size as export object types
            ArrayList<ServerObject>[] group = new ArrayList[6];

            // export object type according to potential dependent relationship
            group[0] = otherObjectsToExport.getAllSequencesToExport();
            group[1] = otherObjectsToExport.getAllTablesToExport();
            group[2] = otherObjectsToExport.getAllPartitionTablesToExport();
            group[3] = otherObjectsToExport.getAllViewsToExport();
            group[4] = otherObjectsToExport.getAllDebugObjectsToExport();
            group[5] = otherObjectsToExport.getTriggerExport();

            File workingDir = getWorkingDir();
            for (int i = 0; i < group.length; i++) {
                if (!group[i].isEmpty()) {
                    String fileName = getRandomFileName();
                    checkAndThrowCancelException();
                    List<String> tempfiles = null;
                    if (!passwordFlag) {
                        tempfiles = exportManager.exportSqlToFiles(fileName, exportType, group[i], isTablespaceOption,
                                workingDir, "");
                    } else {
                        tempfiles = exportManager.exportSqlToFiles(new ExportParameters(getEncryptedPrd(), fileName,
                                getTaskDB(), exportType, group[i], isTablespaceOption, workingDir));
                    }
                    fileMerger.addFiles(tempfiles);
                    this.exportTempFiles.addAll(tempfiles);
                    checkAndThrowFileException();
                }
            }
        }

        /**
         * Export schemas.
         *
         * @param passwordFlag the password flag
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws DataStudioSecurityException the data studio security
         * exception
         * @throws FileOperationException the file operation exception
         */
        private void exportSchemas(boolean passwordFlag) throws DatabaseOperationException, DatabaseCriticalException,
                DataStudioSecurityException, FileOperationException {
            Iterator<Namespace> schemaIt = schemasToExport.iterator();
            File workingDir = getWorkingDir();
            while (schemaIt.hasNext()) {
                checkAndThrowCancelException();
                Namespace ns = schemaIt.next();
                String fileName = getRandomFileName();
                String tempFilePath = workingDir + File.separator + fileName;
                if (null != workingDir) {
                    if (!passwordFlag) {
                        exportManager.exportSqlToFile(fileName, exportType, ns, isTablespaceOption, workingDir);
                    } else {
                        exportManager.exportSqlToFile(new ExportParameters(getEncryptedPrd(), fileName, getTaskDB(),
                                exportType, ns, isTablespaceOption, workingDir));
                    }
                }
                fileMerger.addFile(tempFilePath);
                this.exportTempFiles.add(tempFilePath);
                checkAndThrowFileException();
            }
        }

        @Override
        public void onSuccessUIAction(Object obj) {

            MPPDBIDEDialogs.clearExistingDialog();
            if (!isCancel() && !cancelFlag) {
                commitExportFile();
                String exportPath = path;
                if (isZip) {
                    exportPath = zipPath;
                }
                String ddlOrDataflag = (this.exportType == EXPORTTYPE.SQL_DDL_DATA)
                        ? MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_DATA)
                        : MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL);

                String successMessage = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_SUCCESS,
                        ddlOrDataflag, exportPath);
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_HEADER, ddlOrDataflag),
                        successMessage);

                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(successMessage));
            }
        }

        /**
         * Delete export file.
         */
        private void deleteExportFile() {
            commitExportFile();
            try {
                Files.deleteIfExists(Paths.get(path));
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLHandler: delete the exported file.", exception);
            }
        }

        /**
         * Commit export file.
         */
        private void commitExportFile() {
            try {
                if (bfs != null) {
                    bfs.close();
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLHandler: commit the exported file.", exception);
            }
        }

        /**
         * Handle error msg dialog.
         *
         * @param failMessage the fail message
         * @param exception the e
         */
        private void handleErrorMsgDialog(String failMessage, MPPDBIDEException exception) {
            if (failMessage
                    .contains(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST))) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_CANCEL_MSG));
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                        Message.getInfoFromConst(IMessagesConstants.CANCEL_EXPORT_SUCCES_CONSOLE_MESSAGE));
                return;
            }

            String ddlOrDataflag = (this.exportType == EXPORTTYPE.SQL_DDL_DATA)
                    ? MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_DATA)
                    : MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL);

            String popUpMsgStr = MessageConfigLoader
                    .getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED_WITHOUT_SERVER_MSG);

            String hintMsg = "";
            if (exception.getDBErrorMessage().contains("No matching")) {
                hintMsg = MessageConfigLoader.getProperty(IMessagesConstants.CHECK_FILE_ENCODE_SET);
            }
            MPPDBIDEDialogs.generateErrorPopup(
                    MessageConfigLoader.getProperty(IMessagesConstants.BATCH_EXPORT_DDL_ERROR_FAIL_OBJ, ddlOrDataflag),
                    Display.getDefault().getActiveShell(), exception, hintMsg, popUpMsgStr, null);

            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                    .getProperty(IMessagesConstants.BATCH_EXPORT_DDL_ERROR_FAIL_OBJ, ddlOrDataflag)));
        }

        @Override
        public void onOperationalExceptionAction(DatabaseOperationException databaseOperationException) {
            operationFailCleanUp();
        }

        @Override
        public void onCriticalExceptionAction(DatabaseCriticalException databaseCriticalException) {
            operationFailCleanUp();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException databaseCriticalException) {
            operationUIFail(databaseCriticalException);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException databaseOperationException) {
            operationUIFail(databaseOperationException);
        }

        @Override
        public void onMPPDBIDEException(MPPDBIDEException mppdbideException) {
            operationFailCleanUp();
            if (mppdbideException instanceof FileOperationException) {
                return;
            }
            super.onMPPDBIDEException(mppdbideException);
        }

        @Override
        public void onMPPDBIDEExceptionUIAction(MPPDBIDEException mppdbideException) {
            operationUIFail(mppdbideException);
            if (mppdbideException instanceof FileOperationException) {
                return;
            }
            super.onMPPDBIDEExceptionUIAction(mppdbideException);
        }

        /**
         * Operation UI fail.
         *
         * @param mppdbideException the e
         */
        private void operationUIFail(MPPDBIDEException mppdbideException) {
            MPPDBIDEDialogs.clearExistingDialog();
            if (null != mppdbideException.getDBErrorMessage()) {
                handleErrorMsgDialog(mppdbideException.getDBErrorMessage(), mppdbideException);
            } else if (mppdbideException.getCause() instanceof FileCompressException) {
                MPPDBIDEDialogs.generateDSErrorDialog(
                        MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                        mppdbideException.getCause().getMessage(), mppdbideException);
            } else {
                handleErrorMsgDialog(mppdbideException.getMessage(), mppdbideException);
            }
        }

        /**
         * Operation fail clean up.
         */
        private void operationFailCleanUp() {
            deleteTempFilesIfExists();
            deleteExportFile();
        }

        @Override
        public void onException(Exception exception) {
            operationFailCleanUp();
            super.onException(exception);
        }

        @Override
        public void finalCleanup() {
            if (this.getState() != Job.RUNNING && fileMerger.isFileMergeListEmpty()) {
                fileMerger.stop();
            }
            deleteTempFilesIfExists();
            super.finalCleanup();
        }

        @Override
        protected void canceling() {
            this.cancelFlag = true;

            super.canceling();
            exportManager.cancel();
        }

        @Override
        public void onOutOfMemoryError(OutOfMemoryError error) {
            try {
                exc.stop();
                elapsedTime = exc.getElapsedTime();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("BatchExportDDLHandler: out of memory occurred in batch export.",
                        exception);
            }
            UIElement.getInstance().outOfMemoryCatch(elapsedTime, error.getMessage());

        }

        @Override
        public void handleEvent(DSEvent event) {
            if (event instanceof DSEventWithCount) {
                DSEventWithCount actualEvent = (DSEventWithCount) event;
                currentExportCount += actualEvent.getCount();
                updateProgressBarLabel((ServerObject) actualEvent.getObject());
            }
        }

        /**
         * Gets the encrypted prd.
         *
         * @return the encrypted prd
         */
        public String getEncryptedPrd() {
            if (null != getDatabase()) {
                encryptedPrd = getDatabase().getServer().getEncrpytedProfilePrd();
            }
            return encryptedPrd;
        }

        @Override
        protected Database getDatabase() {
            return obj.getDatabase();
        }
    }
}

class BatchExportDDLObjects {
    Namespace schema = null;
    ArrayList<ServerObject> dbgObjList = new ArrayList<ServerObject>(4);
    ArrayList<ServerObject> tableList = new ArrayList<ServerObject>(4);
    ArrayList<ServerObject> pTableList = new ArrayList<ServerObject>(4);
    ArrayList<ServerObject> viewList = new ArrayList<ServerObject>(4);
    ArrayList<ServerObject> sequenceList = new ArrayList<ServerObject>(4);
    ArrayList<ServerObject> triggerList = new ArrayList<ServerObject>(4);

    /**
     * Gets the namespace to export.
     *
     * @return the namespace to export
     */
    public Namespace getNamespaceToExport() {
        return this.schema;
    }

    /**
     * Clear.
     */
    public void clear() {
        dbgObjList.clear();
        pTableList.clear();
        tableList.clear();
        viewList.clear();
        sequenceList.clear();
        triggerList.clear();
    }

    /**
     * Gets the all debug objects to export.
     *
     * @return the all debug objects to export
     */
    public ArrayList<ServerObject> getAllDebugObjectsToExport() {
        return this.dbgObjList;
    }

    /**
     * Gets the all tables to export.
     *
     * @return the all tables to export
     */
    public ArrayList<ServerObject> getAllTablesToExport() {
        return this.tableList;
    }

    /**
     * Gets the all partition tables to export.
     *
     * @return the all partition tables to export
     */
    public ArrayList<ServerObject> getAllPartitionTablesToExport() {
        return this.pTableList;
    }

    /**
     * Gets the all views to export.
     *
     * @return the all views to export
     */
    public ArrayList<ServerObject> getAllViewsToExport() {
        return this.viewList;
    }

    /**
     * Gets the all sequences to export.
     *
     * @return the all sequences to export
     */
    public ArrayList<ServerObject> getAllSequencesToExport() {
        return this.sequenceList;
    }

    /**
     * Gets the all trigger to export.
     *
     * @return the all trigger to export
     */
    public ArrayList<ServerObject> getTriggerExport() {
        return this.triggerList;
    }

    /**
     * Adds the object.
     *
     * @param obj the obj
     * @return true, if successful
     */
    public boolean addObject(ServerObject obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Namespace) {
            schema = (Namespace) obj;

            /*
             * no need to keep other objects as they will be anyway exported
             * with namespace
             */
            clear();
            return true;
        }

        if (schema != null) {
            return false; // no need to insert as namespace is already there
        }
        if (obj instanceof DebugObjects) {
            dbgObjList.add(obj);
        } else if (obj instanceof TableMetaData) {
            tableList.add(obj);
        } else if (obj instanceof PartitionTable) {
            pTableList.add(obj);
        } else if (obj instanceof ViewMetaData) {
            viewList.add(obj);
        } else if (obj instanceof SequenceMetadata) {
            sequenceList.add(obj);
        } else if (obj instanceof TriggerMetaData) {
            triggerList.add(obj);
        }
        return true;
    }

    /**
     * Adds the object.
     *
     * @param list the list
     */
    public void addObject(ArrayList<ServerObject> list) {
        if (list.isEmpty()) {
            return;
        }
        ServerObject obj = list.get(0);
        if (obj instanceof DebugObjects) {
            dbgObjList.addAll(list);
        } else if (obj instanceof TableMetaData) {
            tableList.addAll(list);
        } else if (obj instanceof PartitionTable) {
            pTableList.addAll(list);
        } else if (obj instanceof ViewMetaData) {
            viewList.addAll(list);
        } else if (obj instanceof SequenceMetadata) {
            sequenceList.addAll(list);
        } else if (obj instanceof TriggerMetaData) {
            triggerList.addAll(list);
        }
    }

}
