/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.DSEventWithCount;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ExportManager implements IExportManager {
    
    private DSEventTable eventTable = null;
    private static final byte[] UTF8_BOM = new byte[] {(byte) 0XEF, (byte) 0xBB, (byte) 0xBF};
    private boolean isCancel = false;
    
    /**
     * Instantiates a new export manager.
     */
    public ExportManager() {
    }

    private void checkAndThrowCancelException() throws DatabaseOperationException {
        if (this.isCancel) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST);
        }
    }

    /**
     * Sets the event table.
     *
     * @param tab the new event table
     */
    public void setEventTable(DSEventTable tab) {
        this.eventTable = tab;
    }

    @Override
    public void exportSqlToFile(String path, EXPORTTYPE type, ServerObject object, boolean isTablespaceOption,
            File workingDir) throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException {
        checkAndThrowCancelException();
        exportDatabaseObjectsFromSystemTables(path, type, object, workingDir, "", isTablespaceOption);
    }
    
    private void exportDatabaseObjectsFromSystemTables(String path, EXPORTTYPE type, ServerObject object,
            File workingDir, String encryptedPwd, boolean isTablespaceOption) throws DatabaseOperationException {
        Path createdFilePath = null;
        Database db = object.getDatabase();
        try {
            Path exportFilePath = Paths
                    .get(String.format(Locale.ENGLISH, "%s%s%S", workingDir.getCanonicalPath(), File.separator, path))
                    .normalize();
            ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
            createdFilePath = withPermission.createFileWithPermission(exportFilePath.toString(), false, null, false);

            isPretendBom(createdFilePath);
            DBConnection conn = getDbConnection(db, encryptedPwd);
            exportDatabseObjects(type, object, createdFilePath, conn, isTablespaceOption);
            releaseConnection(db, conn);
        } catch (MPPDBIDEException exp) {
            if (createdFilePath != null) {
                deleteFileOnException(createdFilePath);
            }
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED_FOR_OBJ, ""), exp);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED_FOR_OBJ,
                    exp.getServerMessage());
        } catch (IOException exp) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR), exp);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR, exp);
        }
    }

    private void exportDatabseObjects(EXPORTTYPE type, ServerObject object, Path createdFilePath, DBConnection conn,
            boolean isTablespaceOption) throws DatabaseOperationException, DatabaseCriticalException, IOException,
            UnsupportedEncodingException, MPPDBIDEException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        if (object instanceof Namespace) {
            Namespace ns = (Namespace) object;
            exportNamespaceDdl(createdFilePath, conn, ns);
            exportDebugObjDdl(conn, createdFilePath, ns);
            exportTableDdl(type, conn, createdFilePath, ns, isTablespaceOption);
            exportSequenceDDLOwenedBy(type, conn, createdFilePath, ns);
            exportViewDdl(conn, createdFilePath, ns);
            exportSequenceDdl(type, conn, createdFilePath, ns);
        } else if (object instanceof TableMetaData) {
            objList.add((TableMetaData) object);
            writeTableObjectDDL(objList, createdFilePath, type, conn, isTablespaceOption);
        } else if (object instanceof ViewMetaData) {
            objList.add((ViewMetaData) object);
            writeViewObjectDDL(objList, createdFilePath, conn);
        } else if (object instanceof SequenceMetadata) {
            objList.add((SequenceMetadata) object);
            writeSequenceObjectDDL(objList, createdFilePath, type, conn);
        } else {
            return;
        }
    }
    
    private void exportSequenceDDLOwenedBy(EXPORTTYPE type, DBConnection conn, Path createdFilePath, Namespace ns)
            throws DatabaseOperationException, UnsupportedEncodingException, IOException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        objList.addAll(ns.getSequenceGroup().getSortedServerObjectList());
        String fileEncodingName = getFileEncoding();
        for (ServerObject fobj : objList) {
            SequenceMetadata seqObject = (SequenceMetadata) fobj;
            StandardOpenOption fileMode = StandardOpenOption.APPEND;
            String seqDDL = seqObject.getSequenceQwnedByDDL(conn);
            if (!seqDDL.trim().isEmpty()) {
                addSeqOwnedByHeader(createdFilePath, fileMode, seqObject, fileEncodingName);
                Files.write(createdFilePath, seqDDL.getBytes(fileEncodingName), fileMode);
            }
            if (eventTable != null) {
                eventTable
                        .sendEvent(new DSEventWithCount(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, seqObject));
            }
        }
        objList.clear();
    }

    private void exportSequenceDdl(EXPORTTYPE type, DBConnection conn, Path createdFilePath, Namespace ns)
            throws DatabaseOperationException, IOException, UnsupportedEncodingException, DatabaseCriticalException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        objList.addAll(ns.getSequenceGroup().getSortedServerObjectList());
        writeSequenceObjectDDL(objList, createdFilePath, type, conn);
        objList.clear();
    }

    private void exportViewDdl(DBConnection conn, Path createdFilePath, Namespace ns)
            throws MPPDBIDEException, IOException, UnsupportedEncodingException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        objList.addAll(ns.getViewGroup().getSortedServerObjectList());
        writeViewObjectDDL(objList, createdFilePath, conn);
        objList.clear();
    }

    private void exportTableDdl(EXPORTTYPE type, DBConnection conn, Path createdFilePath, Namespace ns,
            boolean isTablespaceOption) throws MPPDBIDEException, IOException, UnsupportedEncodingException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        objList.addAll(ns.getTables().getSortedServerObjectList());
        objList.addAll(ns.getForeignTablesGroup().getSortedServerObjectList());
        writeTableObjectDDL(objList, createdFilePath, type, conn, false);
        objList.clear();
    }

    private void exportDebugObjDdl(DBConnection conn, Path createdFilePath, Namespace ns)
            throws DatabaseOperationException, DatabaseCriticalException, IOException, UnsupportedEncodingException {
        ArrayList<ServerObject> objList = new ArrayList<ServerObject>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        objList.addAll(ns.getFunctions().getSortedServerObjectList());
        writeDebugObjectDDL(objList, createdFilePath, conn);
        objList.clear();
    }

    private void exportNamespaceDdl(Path createdFilePath, DBConnection conn, Namespace ns)
            throws DatabaseOperationException, DatabaseCriticalException, IOException, UnsupportedEncodingException {
        if (!ns.isLoaded()) {
            ns.getAllObjectsOnDemand(ns.getDatabase().getConnectionManager().getObjBrowserConn());
        }
        addNamespaceHeader(createdFilePath, ns);
        addNamespaceComments(createdFilePath, ns, conn);
        addAclDDL(createdFilePath, ns, conn, "SCHEMA");
    }

    private void addNamespaceComments(Path createdFilePath, Namespace ns, DBConnection conn)
            throws DatabaseOperationException, UnsupportedEncodingException, IOException, DatabaseCriticalException {
        String fileEncodingName = getFileEncoding();
        StandardOpenOption fileMode = StandardOpenOption.APPEND;
        String comments = ns.getNamespceComments(conn);
        if (!comments.isEmpty()) {
            String commHeader = MPPDBIDEConstants.LINE_SEPARATOR
                    + MPPDBIDEConstants.LINE_SEPARATOR + String.format(Locale.ENGLISH,
                            "-- Name: SCHEMA %s; Type: COMMENT; Schema: -; Owner: -" + "", ns.getDisplayName())
                    + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR;
            Files.write(createdFilePath, commHeader.getBytes(fileEncodingName), fileMode);
            Files.write(createdFilePath, comments.getBytes(fileEncodingName), fileMode);
        }
        String str = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR
                + String.format(Locale.ENGLISH, "SET search_path = %s ;", ns.getDisplayName())
                + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, str.getBytes(fileEncodingName), fileMode);
    }

    private void deleteFileOnException(Path createdFilePath) {
        try {
            Files.deleteIfExists(createdFilePath);
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("ExportManager: delete files on exception failed", exception);
        }
    }

    private void addDebugObjectHeader(Path createdFilePath, StandardOpenOption fileMode, DebugObjects dbgObject,
            String fileEncoding) throws IOException, UnsupportedEncodingException {
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + "-- Name: " + dbgObject.getName() + "; Type: "
                + dbgObject.getTypeLabel() + "; Schema: " + dbgObject.getNamespace().getName() + ";"
                + MPPDBIDEConstants.LINE_SEPARATOR + "--" + MPPDBIDEConstants.LINE_SEPARATOR
                + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, header.getBytes(fileEncoding), fileMode);
    }
    
    private void writeDebugObjectDDL(ArrayList<ServerObject> objList, Path createdFilePath, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException, IOException, UnsupportedEncodingException {
        DebugObjects dbgObject = null;
        StandardOpenOption fileMode = StandardOpenOption.APPEND;
        String fileEncodingName = getFileEncoding();
        for (ServerObject fobj : objList) {
            dbgObject = (DebugObjects) fobj;
            dbgObject.refreshSourceCode();

            addDebugObjectHeader(createdFilePath, fileMode, dbgObject, fileEncodingName);
            fileMode = StandardOpenOption.APPEND;
            Files.write(createdFilePath, dbgObject.getSourceCode().getCode().getBytes(fileEncodingName), fileMode);
            addAclDDL(createdFilePath, dbgObject, conn, "FUNCTION");
            if (eventTable != null) {
                eventTable
                        .sendEvent(new DSEventWithCount(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, dbgObject));
            }
        }
    }
    

    /*
     * Add BOM (Byte Order Mark) for UTF8 encoding.
     */
    private static boolean isPrependBomForUtf8(String encodingName) {
        return "UTF8".equalsIgnoreCase(encodingName) || "UTF-8".equalsIgnoreCase(encodingName);
    }

    /**
     * Prepend bom for utf 8.
     *
     * @param encodingName the encoding name
     * @param fileOutStream the file out stream
     * @throws DatabaseOperationException the database operation exception
     */
    public static void prependBomForUtf8(String encodingName, FileOutputStream fileOutStream)
            throws DatabaseOperationException {
        if (isPrependBomForUtf8(encodingName)) {
            try {
                fileOutStream.write(UTF8_BOM);
                fileOutStream.flush();
            } catch (IOException exp) {
                throw new DatabaseOperationException(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT, exp);
            }
        }
    }

    @Override
    public void exportSqlToFile(ExportParameters expParameter)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException {
        checkAndThrowCancelException();
        // Validate the type of export.
        ServerObject serverObject = expParameter.getServerObject();
        exportDatabaseObjectsFromSystemTables(expParameter.getPath(), expParameter.getExportType(), serverObject,
                expParameter.getWorkingDir(), expParameter.getPassword(), false);
    }

    /**
     * Export sql to files.
     *
     * @param expParameter the exp parameter
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws DataStudioSecurityException the data studio security exception
     */
    public List<String> exportSqlToFiles(ExportParameters expParameter)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException {
        String basepath = expParameter.getPath();

        List<String> paths = new ArrayList<String>(4);
        try {
            paths = exportSqlToFiles(basepath, expParameter.getExportType(), expParameter.getServerObjList(),
                    expParameter.isTablespaceOption(), expParameter.getWorkingDir(), expParameter.getPassword());
            return paths;
        } catch (DatabaseOperationException | DatabaseCriticalException | DataStudioSecurityException exp) {
            deleteTempFiles(paths);
            throw exp;
        }
    }

    @Override
    public void cancel() {
        this.isCancel = true;
    }

    @Override
    public List<String> exportSqlToFiles(String basepath, EXPORTTYPE type, ArrayList<ServerObject> objects,
            boolean isTablespaceOption, File workingDir, String encryptedPwd)
            throws DatabaseOperationException, DatabaseCriticalException, DataStudioSecurityException {
        checkAndThrowCancelException();
        List<String> paths = new ArrayList<String>(4);
        try {
            int count = 0;
            Path createdFilePath = createTemporaryFile(basepath, workingDir, paths, count);
            isPretendBom(createdFilePath);
            exportDDLUsingSystemTable(type, objects, encryptedPwd, createdFilePath, isTablespaceOption);
            return paths;
        } catch (DatabaseOperationException | DatabaseCriticalException exp) {
            deleteTempFiles(paths);
            throw exp;
        } catch (IOException exp) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR), exp);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR, exp);
        } catch (MPPDBIDEException exp) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR), exp);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR, exp);
        }
    }
    
    private void exportDDLUsingSystemTable(EXPORTTYPE type, ArrayList<ServerObject> objects, String encryptedPwd,
            Path createdFilePath, boolean isTablespaceOption) throws IOException, DatabaseOperationException,
            DatabaseCriticalException, UnsupportedEncodingException, MPPDBIDEException {
        Database db = objects.get(0).getDatabase();
        MPPDBIDELoggerUtility.info("Export DDL by system table");

        DBConnection conn = getDbConnection(db, encryptedPwd);
        if (objects.get(0) instanceof DebugObjects) {
            writeDebugObjectDDL(objects, createdFilePath, conn);
        }
        if (objects.get(0) instanceof TableMetaData) {
            writeTableObjectDDL(objects, createdFilePath, type, conn, isTablespaceOption);
        }
        if (objects.get(0) instanceof ViewMetaData) {
            writeViewObjectDDL(objects, createdFilePath, conn);
        }
        if (objects.get(0) instanceof SequenceMetadata) {
            writeSequenceObjectDDL(objects, createdFilePath, type, conn);
        }
        releaseConnection(db, conn);
    }

    private Path createTemporaryFile(String basepath, File workingDir, List<String> paths, int count)
            throws IOException, DatabaseOperationException {
        String path = basepath + "_" + Integer.toString(count);
        paths.add(workingDir.toString() + File.separator + path);
        Path exportFilePath = Paths
                .get(String.format(Locale.ENGLISH, "%s%s%s", workingDir.getCanonicalPath(), File.separator, path))
                .normalize();
        ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
        Path createdFilePath = withPermission.createFileWithPermission(exportFilePath.toString(), false, null, false);
        return createdFilePath;
    }

    private String getFileEncoding() {
        String fileEncoding = BLPreferenceManager.getInstance().getBLPreference().getFileEncoding();
        String fileEncodingName = fileEncoding.isEmpty() ? Charset.defaultCharset().name() : fileEncoding;
        return fileEncodingName;
    }

    private void isPretendBom(Path createdFilePath) throws IOException {
        String fileEncoding = BLPreferenceManager.getInstance().getBLPreference().getFileEncoding();
        boolean isPrependBom = isPrependBomForUtf8(fileEncoding);
        StandardOpenOption fileMode = StandardOpenOption.TRUNCATE_EXISTING;
        if (isPrependBom) {
            Files.write(createdFilePath, UTF8_BOM, fileMode);
        }
    }

    private void writeSequenceObjectDDL(ArrayList<ServerObject> objList, Path createdFilePath, EXPORTTYPE type,
            DBConnection conn)
            throws DatabaseOperationException, IOException, UnsupportedEncodingException, DatabaseCriticalException {
        String fileEncodingName = getFileEncoding();
        for (ServerObject fobj : objList) {
            SequenceMetadata seqObject = (SequenceMetadata) fobj;
            String seqQuery = seqObject.getDDL(seqObject.getDatabase(), conn);
            StandardOpenOption fileMode = StandardOpenOption.APPEND;
            addServerObjectHeader(createdFilePath, fileMode, seqObject, fileEncodingName);
            addSetSchemaQuery(createdFilePath, seqObject.getNamespace(), fileEncodingName);
            fileMode = StandardOpenOption.APPEND;
            Files.write(createdFilePath, seqQuery.getBytes(fileEncodingName), fileMode);
            addAclDDL(createdFilePath, seqObject, conn, "SEQUENCE");

            addSequenceDataDdl(createdFilePath, type, seqObject, conn);
            if (eventTable != null) {
                eventTable
                        .sendEvent(new DSEventWithCount(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, seqObject));
            }
        }
    }

    private void addSequenceDataDdl(Path createdFilePath, EXPORTTYPE type, SequenceMetadata seqObject,
            DBConnection conn) throws IOException, UnsupportedEncodingException, DatabaseOperationException {
        String sequenceDDL = "";
        String fileEncodingName = getFileEncoding();
        StandardOpenOption fileMode = StandardOpenOption.APPEND;
        if (type.toString().equals("SQL_DDL_DATA")) {
            addServerDataHeader(createdFilePath, fileMode, seqObject, fileEncodingName);
            fileMode = StandardOpenOption.APPEND;
            ExportObjectDataManager exportManager = new ExportObjectDataManager(conn, createdFilePath,
                    fileEncodingName);
            try {
                sequenceDDL = exportManager.getSequenceNextValue(seqObject);
            } catch (DatabaseCriticalException | DatabaseOperationException exception) {
                MPPDBIDELoggerUtility
                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_FETCHING_SEQ_VALUE));
                throw new DatabaseOperationException(IMessagesConstants.ERR_WHILE_FETCHING_SEQ_VALUE, exception);
            }
            Files.write(createdFilePath, sequenceDDL.getBytes(fileEncodingName), fileMode);
        }
    }

    private void writeViewObjectDDL(ArrayList<ServerObject> objList, Path createdFilePath, DBConnection conn)
            throws MPPDBIDEException, IOException, UnsupportedEncodingException {
        String fileEncodingName = getFileEncoding();
        for (ServerObject fobj : objList) {
            ViewMetaData viewObject = (ViewMetaData) fobj;
            String viewQuery = viewObject.getDDL(viewObject.getDatabase());
            StandardOpenOption fileMode = StandardOpenOption.APPEND;
            addServerObjectHeader(createdFilePath, fileMode, viewObject, fileEncodingName);
            addSetSchemaQuery(createdFilePath, viewObject.getNamespace(), fileEncodingName);
            fileMode = StandardOpenOption.APPEND;
            Files.write(createdFilePath, viewQuery.getBytes(fileEncodingName), fileMode);
            addAclDDL(createdFilePath, viewObject, conn, "TABLE");
            if (eventTable != null) {
                eventTable.sendEvent(
                        new DSEventWithCount(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, viewObject));
            }
        }
    }

    private void writeTableObjectDDL(ArrayList<ServerObject> objList, Path createdFilePath, EXPORTTYPE type,
            DBConnection conn, boolean isTablespaceOption)
            throws MPPDBIDEException, IOException, UnsupportedEncodingException {
        String fileEncodingName = getFileEncoding();
        for (ServerObject fobj : objList) {
            TableMetaData tableObject = (TableMetaData) fobj;
            if (tableObject.getNamespace().getErrorTableList().contains(tableObject.getName())) {
                continue;
            }
            tableObject.fetchDDL(tableObject.getDatabase(), conn);
            String tableQuery = tableObject.getSource();
            StandardOpenOption fileMode = StandardOpenOption.APPEND;

            addServerObjectHeader(createdFilePath, fileMode, tableObject, fileEncodingName);
            addDefaultTablespaceForTable(createdFilePath, isTablespaceOption, tableObject, fileEncodingName, conn);
            fileMode = StandardOpenOption.APPEND;
            Files.write(createdFilePath, tableQuery.getBytes(fileEncodingName), fileMode);
            addAclDDL(createdFilePath, tableObject, conn, "TABLE");
            exportTableDataDdl(createdFilePath, type, conn, tableObject);

            if (eventTable != null) {
                eventTable.sendEvent(
                        new DSEventWithCount(IDSGridUIListenable.LISTEN_BATCHEXPORT_DDL_SUCCESS, tableObject));
            }
        }
    }

    private void addDefaultTablespaceForTable(Path createdFilePath, boolean isTablespaceOption,
            TableMetaData tableObject, String fileEncodingName, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, UnsupportedEncodingException, IOException {
        if (isTablespaceOption) {
            String tablespaceName = tableObject.getTablespaceForTable(conn);
            String query = String.format(Locale.ENGLISH, "SET default_tablespace = %s;",
                    (tablespaceName == null || tablespaceName.isEmpty()) ?
                            tableObject.getDatabase().getDBDefaultTblSpc() : tablespaceName)
                    + MPPDBIDEConstants.LINE_SEPARATOR;
            Files.write(createdFilePath, query.getBytes(fileEncodingName), StandardOpenOption.APPEND);
        }
    }

    private void addSetSchemaQuery(Path createdFilePath, Namespace ns, String fileEncodingName)
            throws UnsupportedEncodingException, IOException {
        String query = String.format(Locale.ENGLISH, "%sSET search_path = %s ;%s", MPPDBIDEConstants.LINE_SEPARATOR,
                ns.getDisplayName(), MPPDBIDEConstants.LINE_SEPARATOR);
        Files.write(createdFilePath, query.getBytes(fileEncodingName), StandardOpenOption.APPEND);
    }

    private void exportTableDataDdl(Path createdFilePath, EXPORTTYPE type, DBConnection conn, TableMetaData tableObject)
            throws IOException, UnsupportedEncodingException, DatabaseCriticalException, DatabaseOperationException,
            MPPDBIDEException {
        String fileEncodingName = getFileEncoding();
        if (type.toString().equals("SQL_DDL_DATA") && !(tableObject instanceof ForeignTable)) {
            addServerDataHeader(createdFilePath, StandardOpenOption.APPEND, tableObject, fileEncodingName);
            String query = String.format(Locale.ENGLISH, "Select * from  %s ;", tableObject.getDisplayName());
            GenerateCursorExecuteUtil genrateUtil = new GenerateCursorExecuteUtil(tableObject.getDisplayName(),
                    fileEncodingName, true);
            ExportObjectDataManager exportManager = new ExportObjectDataManager(conn, createdFilePath, fileEncodingName,
                    query, genrateUtil);
            exportManager.exportTableData();
            exportManager.cleanData();
        }
    }

    private void addAclDDL(Path createdFilePath, ServerObject servObj, DBConnection conn, String objType)
            throws IOException, UnsupportedEncodingException, DatabaseCriticalException, DatabaseOperationException {
        String fileEncodingName = getFileEncoding();
        StandardOpenOption fileMode = StandardOpenOption.APPEND;
        ExportGrantRevokeQueries grantRevokeQueries = new ExportGrantRevokeQueries(
                ServerObject.getQualifiedObjectName(servObj.getName()), null, objType, "");
        long ownerId = -1;
        ownerId = getOwnerId(servObj, conn, grantRevokeQueries);
        if (ownerId != -1) {
            grantRevokeQueries.getOwnerName(ownerId, conn);
        }

        String grantQueries = grantRevokeQueries.getGrantRevokeQueries();
        if (!grantQueries.isEmpty()) {
            addServerObjectAclHeader(createdFilePath, fileMode, servObj);
            Files.write(createdFilePath, grantQueries.getBytes(fileEncodingName), fileMode);
        }
    }

    private long getOwnerId(ServerObject servObj, DBConnection conn, ExportGrantRevokeQueries grantRevokeQueries) {
        long ownerId;
        if (servObj instanceof Namespace) {
            ownerId = grantRevokeQueries.executeToGetRelAclQueryForNamespace(servObj.getDisplayName(), conn);
        } else {
            ownerId = grantRevokeQueries.executeToGetRelAclQuery(servObj.getOid(), servObj.getNamespace().getOid(),
                    conn, servObj.getName());
        }
        return ownerId;
    }

    private DBConnection getDbConnection(Database database, String encryptedPwd)
            throws DatabaseCriticalException, DatabaseOperationException {
        DBConnection connection = null;
        try {
            if (!encryptedPwd.isEmpty()) {
                database.getServer().setPrd(encryptedPwd);
            }
            connection = database.getConnectionManager().getFreeConnection();
            database.getServer().clearPrds();
        } catch (MPPDBIDEException exception) {
            String msg = exception.getServerMessage() != null ? exception.getServerMessage() : exception.getMessage();
            if (msg.contains("Connection refused")) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR),
                        exception);
                throw new DatabaseCriticalException(IMessagesConstants.DATABASE_CONNECTION_ERR, exception);
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.DATABASE_CONNECTION_ERR));
                throw new DatabaseOperationException(IMessagesConstants.DATABASE_CONNECTION_ERR);
            }
        }
        return connection;

    }

    private void releaseConnection(Database database, DBConnection dbConn) {
        if (dbConn != null && database.getConnectionManager() != null) {
            database.getConnectionManager().releaseAndDisconnection(dbConn);
        }
    }

    private void addServerObjectHeader(Path createdFilePath, StandardOpenOption fileMode, ServerObject dbgObject,
            String fileEncoding) throws IOException, UnsupportedEncodingException {
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + "-- Name: " + dbgObject.getName() + "; Type: "
                + dbgObject.getTypeLabel() + "; Schema: " + dbgObject.getNamespace().getName() + ";"
                + MPPDBIDEConstants.LINE_SEPARATOR + "--" + MPPDBIDEConstants.LINE_SEPARATOR
                + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, header.getBytes(fileEncoding), fileMode);
    }

    private void addSeqOwnedByHeader(Path createdFilePath, StandardOpenOption fileMode, ServerObject dbgObject,
            String fileEncoding) throws IOException, UnsupportedEncodingException {
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + "-- Name: " + dbgObject.getName() + "; Type: SEQUENCE OWNED BY "
                + "; Schema: " + dbgObject.getNamespace().getName() + ";" + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, header.getBytes(fileEncoding), fileMode);
    }

    private void addServerDataHeader(Path createdFilePath, StandardOpenOption fileMode, ServerObject dbgObject,
            String fileEncoding) throws IOException, UnsupportedEncodingException {
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--" + "Data for "
                + " Name: " + dbgObject.getName() + "; Type: " + dbgObject.getTypeLabel() + "; Schema: "
                + dbgObject.getNamespace().getName() + ";" + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, header.getBytes(fileEncoding), fileMode);
    }

    private void addServerObjectAclHeader(Path createdFilePath, StandardOpenOption fileMode, ServerObject dbgObject)
            throws IOException, UnsupportedEncodingException {
        String namespaceName = null;
        if (dbgObject instanceof Namespace) {
            namespaceName = dbgObject.getName();
        } else {
            namespaceName = dbgObject.getNamespace().getName();
        }
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--" + " Name: "
                + dbgObject.getName() + "; Type: ACL" + "; Schema: " + namespaceName + ";"
                + MPPDBIDEConstants.LINE_SEPARATOR + "--" + MPPDBIDEConstants.LINE_SEPARATOR
                + MPPDBIDEConstants.LINE_SEPARATOR;
        Files.write(createdFilePath, header.getBytes(getFileEncoding()), fileMode);
    }

    private void addNamespaceHeader(Path createdFilePath, Namespace namespace)
            throws IOException, UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        String header = MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + "-- Name: " + namespace.getName() + "; Type: "
                + namespace.getTypeLabel() + "; Schema:  ;" + MPPDBIDEConstants.LINE_SEPARATOR + "--"
                + MPPDBIDEConstants.LINE_SEPARATOR + MPPDBIDEConstants.LINE_SEPARATOR;

        builder.append(header);
        String query = "CREATE SCHEMA " + namespace.getDisplayName() + ";";
        builder.append(query);
        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        builder.append(MPPDBIDEConstants.LINE_SEPARATOR);
        Files.write(createdFilePath, builder.toString().getBytes(getFileEncoding()), StandardOpenOption.APPEND);
    }
                    
    private void deleteTempFiles(List<String> paths) {
        if (!paths.isEmpty()) {
            for (String onefilepath : paths) {
                deleteFileOnException(Paths.get(onefilepath));
            }
        }
    }

}