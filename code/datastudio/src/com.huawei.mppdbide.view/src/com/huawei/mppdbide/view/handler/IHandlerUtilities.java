/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.poi.util.BoundedInputStream;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DebugObjects;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ITableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IViewObjectGroups;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.ColumnList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ConstraintList;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultset.ConsoleDataWrapper;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.search.SearchWindow;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: interface Description: The Interface IHandlerUtilities. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface IHandlerUtilities {

    long MAX_INI_FILE_SIZE = 1048576;

    /**
     * Title: class Description: The Class FetchAllCloseConnection. Copyright
     * (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    class FetchAllCloseConnection implements IResultConfig {

        /**
         * Gets the fetch count.
         *
         * @return the fetch count
         */
        // Get all records in debug flow.
        @Override
        public int getFetchCount() {
            return -1;
        }

        /**
         * Gets the action after fetch.
         *
         * @return the action after fetch
         */
        @Override
        public ActionAfterResultFetch getActionAfterFetch() {

            return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
        }

    }

    /**
     * Gets the selected user role group.
     *
     * @return the selected user role group
     */
    static UserRoleObjectGroup getSelectedUserRoleGroup() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof UserRoleObjectGroup) {
            return (UserRoleObjectGroup) obj;
        }

        return null;
    }

    /**
     * Gets the selected user role.
     *
     * @return the selected user role
     */
    static UserRole getSelectedUserRole() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof UserRole) {
            return (UserRole) obj;
        }

        return null;
    }

    /**
     * Gets the object browser selected object.
     *
     * @return the object browser selected object
     */

    static Object getObjectBrowserSelectedObject() {
        TreeViewer viewer = null;
        Object partObject = UIElement.getInstance().getActivePartObject();
        if (partObject instanceof ObjectBrowser) {
            Object obj = ((ObjectBrowser) partObject).getSelection();
            if (obj != null && obj instanceof ISelection && obj instanceof IStructuredSelection) {
                return ((IStructuredSelection) obj).getFirstElement();
            }

            viewer = ((ObjectBrowser) partObject).getTreeViewer();
        } else if (partObject instanceof SearchWindow) {
            Object obj = ((SearchWindow) partObject).getSelection();
            if (obj != null && obj instanceof ISelection && obj instanceof IStructuredSelection) {
                return ((IStructuredSelection) obj).getFirstElement();
            }

            viewer = ((SearchWindow) partObject).getResultViewer();
        }

        if (null != viewer) {
            ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                return ((IStructuredSelection) selection).getFirstElement();
            }
        }

        return null;
    }

    /**
     * Gets the object browser selected objects.
     *
     * @return the object browser selected objects
     */
    static List<?> getObjectBrowserSelectedObjects() {
        TreeViewer viewer = null;
        Object partObject = UIElement.getInstance().getActivePartObject();
        if (partObject instanceof ObjectBrowser) {
            Object obj = ((ObjectBrowser) partObject).getSelection();
            if (obj != null && obj instanceof ISelection && obj instanceof IStructuredSelection) {
                return ((IStructuredSelection) obj).toList();
            }

            viewer = ((ObjectBrowser) partObject).getTreeViewer();
        } else if (partObject instanceof SearchWindow) {
            Object obj = ((SearchWindow) partObject).getSelection();
            if (obj != null && obj instanceof ISelection && obj instanceof IStructuredSelection) {
                return ((IStructuredSelection) obj).toList();
            }

            viewer = ((SearchWindow) partObject).getResultViewer();
        }

        if (null != viewer) {
            ISelection selection = viewer.getSelection();
            if (selection instanceof IStructuredSelection) {
                return ((IStructuredSelection) selection).toList();
            }
        }

        return null;
    }

    /**
     * Gets the selected debug object.Have to changed to menu parameters. This
     * method is very costly.
     *
     * @return the selected debug object
     */
    static IDebugObject getSelectedDebugObject() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof DebugObjects) {
            return (DebugObjects) obj;
        }
        return null;
    }

    /**
     * Gets the selected user namespace.Have to changed to menu parameters. This
     * method is very costly.
     *
     * @return the selected user namespace
     */
    static UserNamespace getSelectedUserNamespace() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof UserNamespace) {
            return (UserNamespace) obj;
        }

        return null;
    }

    /**
     * Gets the selected database. Have to changed to menu parameters. This
     * method is very costly.
     *
     * @return the selected database
     */
    static Database getSelectedDatabase() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof Database) {
            return (Database) obj;
        }

        return null;
    }

    /**
     * Gets the selected column. Have to changed to menu parameters. This method
     * is very costly.
     *
     * @return the selected column
     */
    static ColumnMetaData getSelectedColumn() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof ColumnMetaData) {
            return (ColumnMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected table.
     *
     * @return the selected table
     */
    static TableMetaData getSelectedTable() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof TableMetaData) {
            return (TableMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected I table meta data.
     *
     * @return the selected I table meta data
     */
    static ITableMetaData getSelectedITableMetaData() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof ITableMetaData) {
            return (ITableMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected tablespace.
     *
     * @return the selected tablespace
     */
    static Tablespace getSelectedTablespace() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof Tablespace) {
            return (Tablespace) obj;
        }

        return null;
    }

    /**
     * Gets the selected table group. Have to changed to menu parameters. This
     * method is very costly.
     *
     * @return the selected table group
     */
    static INamespace getSelectedTableGroup() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof TableObjectGroup) {
            return ((TableObjectGroup) obj).getNamespace();
        }
        if (obj instanceof Namespace) {
            return (Namespace) obj;
        }
        return null;
    }

    /**
     * Gets the selected partition metadata.
     *
     * @return the selected partition metadata
     */
    static PartitionMetaData getSelectedPartitionMetadata() {
        Object obj = getObjectBrowserSelectedObject();

        if (obj instanceof PartitionMetaData) {
            return (PartitionMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected table space group.
     *
     * @return the selected table space group
     */
    static Server getSelectedTableSpaceGroup() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof TablespaceObjectGroup) {
            return ((TablespaceObjectGroup) obj).getServer();
        }
        return null;
    }

    /**
     * Gets the selected column group.
     *
     * @return the selected column group
     */
    static ColumnList getSelectedColumnGroup() {
        Object obj = getObjectBrowserSelectedObject();

        if (obj instanceof ColumnList) {
            return (ColumnList) obj;
        }
        return null;
    }

    /**
     * Gets the selected constraint group.
     *
     * @return the selected constraint group
     */
    static ConstraintList getSelectedConstraintGroup() {
        Object obj = getObjectBrowserSelectedObject();

        if (obj instanceof ConstraintList) {
            return (ConstraintList) obj;
        }
        return null;
    }

    /**
     * Gets the selected server. Have to changed to menu parameters. This method
     * is very costly.
     *
     * @return the selected server
     */
    static Server getSelectedServer() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof Server) {
            return (Server) obj;
        }

        return null;
    }

    /**
     * Gets the selected DB group.
     *
     * @return the selected DB group
     */
    static DatabaseObjectGroup getSelectedDBGroup() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof DatabaseObjectGroup) {
            return (DatabaseObjectGroup) obj;
        }

        return null;
    }

    /**
     * Gets the selected constraint.
     *
     * @return the selected constraint
     */
    static ConstraintMetaData getSelectedConstraint() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof ConstraintMetaData) {
            return (ConstraintMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected index.
     *
     * @return the selected index
     */
    static IndexMetaData getSelectedIndex() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof IndexMetaData) {
            return (IndexMetaData) obj;
        }
        return null;
    }

    /**
     * Gets the selected I view object group.
     *
     * @return the selected I view object group
     */
    static IViewObjectGroups getSelectedIViewObjectGroup() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof IViewObjectGroups) {
            return (IViewObjectGroups) obj;
        }

        return null;
    }

    /**
     * Gets the selected I view object.
     *
     * @return the selected I view object
     */
    static IViewMetaData getSelectedIViewObject() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof IViewMetaData) {
            return (IViewMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected view object.
     *
     * @return the selected view object
     */
    static ViewMetaData getSelectedViewObject() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof ViewMetaData) {
            return (ViewMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the selected view column object.
     *
     * @return the selected view column object
     */
    static ViewColumnMetaData getSelectedViewColumnObject() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof ViewColumnMetaData) {
            return (ViewColumnMetaData) obj;
        }

        return null;
    }

    /**
     * Exception message add reason.
     *
     * @param message the message
     * @param exception the e
     * @return the string
     */
    static String exceptionMessageAddReason(String message, MPPDBIDEException exception) {
        StringBuilder builder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        builder.append(message);
        if (null != exception.getServerMessage()) {

            builder.append(" : ").append(exception.getServerMessage());
        }
        return builder.toString();
    }

    /**
     * Refresh object browser tree.
     */
    static void refreshObjectBrowserTree() {
        if (UIElement.getInstance().isObjectBrowserPartOpen()) {
            ObjectBrowser browser = UIElement.getInstance().getObjectBrowserModel();
            if (null != browser) {
                browser.getTreeViewer().refresh();
            }
        }
    }

    /**
     * Handle get src code exception.
     *
     * @param sqlObject the sql object
     */
    static void handleGetSrcCodeException(IDebugObject sqlObject) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DISPLAY_SOURCE_CODE_FAILED),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OCCURED_WHILE_DISPLAYING_CODE)
                        + MessageConfigLoader.getProperty(IMessagesConstants.MSG_HINT_GETSOURCE));

        MPPDBIDELoggerUtility.info("GUI: ObjectBrowser: Display source code failed.");
        if (null != sqlObject) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                    .getProperty(IMessagesConstants.FETCHING_SOURCE_CODE_FAILED, sqlObject.getDisplayName(false))));
        }
    }

    /**
     * Checks if is function executable.
     *
     * @param isDebug the is debug
     * @param params the params
     * @return true, if is function executable
     */
    static boolean isFunctionExecutable(boolean isDebug, ArrayList<ObjectParameter> params) {
        String operation = isDebug ? "debug" : "execute";
        ObjectParameter param = null;
        int length = params.size();
        boolean bool = false;
        String dataType = null;
        for (int i = 0; i < length; i++) {
            param = params.get(i);
            bool = param.getIsSupportedDatatype();
            if (!bool) {
                dataType = param.getDataType();
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.HANDLER_UTILITIES_CANNOT, operation),
                        MessageConfigLoader.getProperty(IMessagesConstants.HANDLER_UTILITIES_CANNOT_PERFORM, operation,
                                dataType));

                return false;
            }
        }

        return true;
    }

    /**
     * Pritn and refresh.
     *
     * @param obj the obj
     */
    static void pritnAndRefresh(Object obj) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(obj);
        }
    }

    /**
     * Gets the new PLSQL object template.Create Template PLSQL Function
     *
     * @param objectRetType the object ret type
     * @param namespace the namespace
     * @return the new PLSQL object template
     */
    static String getNewPLSQLObjectTemplate(String objectRetType, INamespace namespace) {
        StringBuilder code = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        code.append("CREATE [OR REPLACE] FUNCTION ");
        if (null != namespace) {
            code.append(namespace.getQualifiedObjectName()).append('.');

        }

        code.append("function_name ([ parameter datatype[,parameter datatype] ])");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("\tRETURNS " + objectRetType);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("\tLANGUAGE PLPGSQL");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("AS");
        code.append("  $$");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("DECLARE");
        getPlSqlObjTemplateOne(code);
        code.append("END;$$");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.ESCAPE_FORWARDSLASH);

        return code.toString();
    }

    /**
     * Gets the pl sql obj template one.
     *
     * @param code the code
     * @return the pl sql obj template one
     */
    static void getPlSqlObjTemplateOne(StringBuilder code) {
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("\t/*declaration_section*/");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("BEGIN");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("\t/*executable_section*/");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
    }

    /**
     * Gets the pl sql object template end.
     *
     * @param code the code
     * @return the pl sql object template end
     */
    static void getPlSqlObjectTemplateEnd(StringBuilder code) {
        getPlSqlObjTemplateOne(code);
        code.append("END;");
    }

    /**
     * Gets the new PLSQL procedure template. Create Template PLSQL Procedure -
     * Added for AR.SR.IREQ02147423.001.001
     *
     * @param namespace the namespace
     * @return the new PLSQL procedure template
     */
    static String getNewPLSQLProcedureTemplate(INamespace namespace) {
        StringBuilder code = getPlSqlProcedureTemplateOne(namespace);
        code.append("DECLARE");
        getPlSqlObjectTemplateEnd(code);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.ESCAPE_FORWARDSLASH);

        return code.toString();
    }

    /**
     * Gets the pl sql procedure template one.
     *
     * @param namespace the namespace
     * @return the pl sql procedure template one
     */
    static StringBuilder getPlSqlProcedureTemplateOne(INamespace namespace) {
        StringBuilder code = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        code.append("CREATE [OR REPLACE] PROCEDURE ");
        if (null != namespace) {
            code.append(namespace.getQualifiedObjectName()).append('.');

        }

        code.append("procedure_name ([ parameter [IN|OUT|INOUT] datatype[,parameter [IN|OUT|INOUT] datatype] ])");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append("IS");
        code.append("  |");
        code.append("  AS");
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        code.append(MPPDBIDEConstants.LINE_SEPARATOR);
        return code;
    }

    /**
     * Sets the locale.
     *
     * @param selectionIndex the selection index
     * @return true, if successful
     */
    static boolean setLocale(int selectionIndex) {
        URL url = null;
        BufferedReader bufferReader = null;

        Writer writer = null;
        PrintWriter printWriter = null;
        FileInputStream filePathInputStream = null;
        InputStreamReader inputStream = null;
        BoundedInputStream boundStream = null;
        File file = null;
        FileOutputStream fileOutput = null;

        try {
            url = HandlerUtilities.class.getProtectionDomain().getCodeSource().getLocation();
            String filePath = getFilePathForReleasePackage(url);
            file = new File(filePath);
            long fileSize = Files.size(file.toPath());
            if (fileSize > MAX_INI_FILE_SIZE) {
                displayErrorMessage();
                return false;
            }
            filePathInputStream = new FileInputStream(filePath);
            boundStream = new BoundedInputStream(filePathInputStream, MAX_INI_FILE_SIZE);
            inputStream = new InputStreamReader(boundStream, "UTF-8");
            bufferReader = new BufferedReader(inputStream, 2048);
            String content = getDsIniFileContent(selectionIndex, bufferReader);
            StringTokenizer bufferString = new StringTokenizer(content, MPPDBIDEConstants.LINE_SEPARATOR);

            // Check
            if (!Files.isRegularFile(file.toPath())) {
                MPPDBIDELoggerUtility.error("Not a regular file");
                return false;
            }
            fileOutput = new FileOutputStream(file);
            writer = new OutputStreamWriter(fileOutput, "UTF-8");
            printWriter = new PrintWriter(writer);
            writeToDsIniFile(printWriter, bufferString);

            return true;
        } catch (UnsupportedEncodingException exception) {
            displayErrorMessage();
            return false;
        } catch (FileNotFoundException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_CONFIG_ERR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_CONFIG_ERR_MES));

            return false;
        } catch (IOException exe) {

            displayErrorMessage();

            return false;
        } finally {
            cleanUpResources(bufferReader, writer, printWriter, filePathInputStream, inputStream, fileOutput,
                    boundStream);
        }

    }

    /**
     * Display error message.
     */
    static void displayErrorMessage() {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_RD_WD_ERROR));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getErrorFromConst(IMessagesConstants.LOCALE_CHANGE_RD_WD_ERROR));
    }

    /**
     * Clean up resources.
     *
     * @param bufferReader the buffer reader
     * @param writer the writer
     * @param printWriter the print writer
     * @param filePathInputStream the file path input stream
     * @param inputStream the input stream
     * @param fileOutput the file output
     * @param boundedInputStream the bounded input stream
     */
    static void cleanUpResources(BufferedReader bufferReader, Writer writer, PrintWriter printWriter,
            FileInputStream filePathInputStream, InputStreamReader inputStream, FileOutputStream fileOutput,
            BoundedInputStream boundedInputStream) {
        try {
            closePrintWriter(printWriter);
            closeBoundedInputStream(boundedInputStream);
            closeBufferReader(bufferReader);
            closeFilePathInputStream(filePathInputStream);
            closeWriter(writer);
            closeInputStreamReader(inputStream);
            closeFileOutput(fileOutput);
        } catch (IOException e) {
            displayErrorMessage();
        } catch (Exception e) {
            displayErrorMessage();
        }
    }

    /**
     * Write to ds ini file.
     *
     * @param printWriter the print writer
     * @param bufferString the buffer string
     */
    static void writeToDsIniFile(PrintWriter printWriter, StringTokenizer bufferString) {
        String contentData;
        boolean writeBufferString;
        writeBufferString = bufferString.hasMoreTokens();
        while (writeBufferString) {
            contentData = bufferString.nextToken();
            printWriter.write(contentData);
            printWriter.write(MPPDBIDEConstants.LINE_SEPARATOR);
            writeBufferString = bufferString.hasMoreTokens();
        }
    }

    /**
     * Gets the ds ini file content.
     *
     * @param selectionIndex the selection index
     * @param bufferReader the buffer reader
     * @return the ds ini file content
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static String getDsIniFileContent(int selectionIndex, BufferedReader bufferReader) throws IOException {
        String fileContent;
        fileContent = bufferReader.readLine();
        StringBuffer buffer = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        while (fileContent != null) {
            if (!(fileContent.startsWith("-Duser"))) {
                buffer.append(fileContent);
                buffer.append(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            fileContent = bufferReader.readLine();
        }

        addParameterOnLanSelection(selectionIndex, buffer);

        String content = buffer.toString();
        return content;
    }

    /**
     * Gets the file path for release package.
     *
     * @param url the url
     * @return the file path for release package
     */
    static String getFilePathForReleasePackage(URL url) {
        String filePath = null;
        // Code used to get the file from the release package

        StringBuilder builder = new StringBuilder(url.getFile());
        builder.deleteCharAt(0);
        builder.delete(builder.lastIndexOf("/"), builder.length());
        builder.delete(builder.lastIndexOf("/"), builder.length());
        builder.append(MessageConfigLoader.getProperty(IMessagesConstants.LOCALE_CHANGE_CONFIG_FILE));
        filePath = builder.toString();
        return filePath;
    }

    /**
     * Adds the parameter on lan selection.
     *
     * @param selectionIndex the selection index
     * @param buffer the buffer
     */
    static void addParameterOnLanSelection(int selectionIndex, StringBuffer buffer) {
        switch (selectionIndex) {
            case 0: {
                buffer.append("-Duser.language=en");
                buffer.append(MPPDBIDEConstants.LINE_SEPARATOR);
                buffer.append("-Duser.country=IN");
                break;
            }
            case 1: {
                buffer.append("-Duser.language=zh");
                buffer.append(MPPDBIDEConstants.LINE_SEPARATOR);
                buffer.append("-Duser.country=CN");
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Close file output.
     *
     * @param fileOutput the file output
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeFileOutput(FileOutputStream fileOutput) throws IOException {
        if (null != fileOutput) {
            fileOutput.close();
        }
    }

    /**
     * Close input stream.
     *
     * @param inputStream the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeInputStreamReader(InputStreamReader inputStreamReader) throws IOException {
        if (null != inputStreamReader) {
            inputStreamReader.close();
        }
    }

    /**
     * Close input stream.
     *
     * @param inputStream the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeInputStream(InputStream inputStream) throws IOException {
        if (null != inputStream) {
            inputStream.close();
        }
    }

    /**
     * Close writer.
     *
     * @param writer the writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeWriter(Writer writer) throws IOException {
        if (null != writer) {
            writer.close();
        }
    }

    /**
     * Close file path input stream.
     *
     * @param filePathInputStream the file path input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeFilePathInputStream(FileInputStream filePathInputStream) throws IOException {
        if (null != filePathInputStream) {
            filePathInputStream.close();
        }
    }

    /**
     * Close bounded input stream.
     *
     * @param boundedInputStream the bounded input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeBoundedInputStream(BoundedInputStream boundedInputStream) throws IOException {
        if (null != boundedInputStream) {
            boundedInputStream.close();
        }
    }

    /**
     * Close buffer reader.
     *
     * @param bufferReader the buffer reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    static void closeBufferReader(BufferedReader bufferReader) throws IOException {
        if (null != bufferReader) {
            bufferReader.close();
        }
    }

    /**
     * Close print writer.
     *
     * @param printWriter the print writer
     */
    static void closePrintWriter(PrintWriter printWriter) {
        if (null != printWriter) {
            printWriter.close();
        }
    }

    /**
     * Gets the active DB.
     *
     * @param server the server
     * @return the active DB
     */
    static boolean getActiveDB(Server server) {
        return server != null && server.isAleastOneDbConnected();
    }

    /**
     * Gets the selected partition table group.
     *
     * @return the selected partition table group
     */
    static INamespace getSelectedPartitionTableGroup() {
        Object obj = getObjectBrowserSelectedObject();

        if (obj instanceof TableObjectGroup) {
            return ((TableObjectGroup) obj).getNamespace();
        }
        if (obj instanceof Namespace) {
            return (Namespace) obj;
        }

        return null;
    }

    /**
     * Gets the selected sequence object.
     *
     * @return the selected sequence object
     */
    static SequenceMetadata getSelectedSequenceObject() {
        Object obj = getObjectBrowserSelectedObject();
        if (obj instanceof SequenceMetadata) {
            return (SequenceMetadata) obj;
        }

        return null;
    }

    /**
     * Gets the tablespace selection options.
     *
     * @return the tablespace selection options
     */
    static boolean getTablespaceSelectionOptions() {

        return PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS);
    }

    /**
     * Gets the export data selection options.
     *
     * @return the export data selection options
     */
    static boolean getExportDataSelectionOptions() {

        return PreferenceWrapper.getInstance().getPreferenceStore()
                .getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS);
    }

    /**
     * set the enable state of Tool Item.
     *
     * @param toolItem the ToolItem
     * @param value the boolean value
     */
    static void setToolItemEnabled(ToolItem toolItem, boolean value) {
        if (toolItem != null && !toolItem.isDisposed() && toolItem.isEnabled()) {
            toolItem.setEnabled(value);
        }
    }

    /**
     * set the enable state of Menu Item.
     *
     * @param menuItem the MenuItem
     * @param value the boolean value
     */
    static void setMenuItemEnabled(MenuItem menuItem, boolean value) {
        if (menuItem != null && !menuItem.isDisposed() && menuItem.isEnabled()) {
            menuItem.setEnabled(value);
        }
    }

    /**
     * return true is if item enabled and export flag is false.
     *
     * @param toolItem the ToolItem
     * @param value the boolean value
     * @return boolean value
     */
    static boolean disableToolBarIfEnabled(ToolItem toolItem, boolean value) {
        if (toolItem != null && !toolItem.isDisposed() && toolItem.isEnabled() && !value) {
            toolItem.setEnabled(value);
            return true;
        }
        return false;
    }

    /**
     * Checks if is search option enable.
     *
     * @return true, if is search option enable
     */
    static boolean isSearchOptionEnable() {

        Iterator<Server> servers = DBConnProfCache.getInstance().getServers().iterator();
        boolean serverHasNext = servers.hasNext();
        Server server = null;
        while (serverHasNext) {
            server = servers.next();
            if (server.isAleastOneDbConnected()) {
                return true;
            }
            serverHasNext = servers.hasNext();
        }
        return false;

    }

    /**
     * Cleanup all jobs in DB.
     *
     * @param database the database
     */
    static void cleanupAllJobsInDB(Database database) {
        final IJobManager jm = Job.getJobManager();
        Job[] allJobs = jm.find(MPPDBIDEConstants.CANCELABLEJOB);
        UIWorkerJob uiWorkJob = null;
        for (final Job job : allJobs) {
            if (job instanceof UIWorkerJob) {
                uiWorkJob = (UIWorkerJob) job;
                if (uiWorkJob.getTaskDB() != null && uiWorkJob.getTaskDB().equals(database)) {
                    job.cancel();
                }
            }
        }
    }

    /**
     * Checks if is selected table forign partition.
     *
     * @return true, if is selected table forign partition
     */
    static boolean isSelectedTableForignPartition() {
        Object obj = getObjectBrowserSelectedObject();

        if (null != obj && obj instanceof ForeignPartitionTable) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is DDL operations supported.
     *
     * @param database the database
     * @return true, if is DDL operations supported
     */
    static boolean isDDLOperationsSupported(Database database) {
        return true;
    }

    /**
     * Gets the selected synonym group.
     *
     * @return synonym object group
     * @Author: c00550043
     * @Date: Mar 6, 2020
     * @Title: getSelectedSynonymGroup
     * @Description: get the selected synonym group
     * @return synonym object group
     */
    static SynonymObjectGroup getSelectedSynonymGroup() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof SynonymObjectGroup) {
            return (SynonymObjectGroup) obj;
        }

        return null;
    }
}
