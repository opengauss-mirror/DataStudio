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

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.component.grid.GridSelectionLayerPortData;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.dialog.ExportZipOptionDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.DSDeleteFileExport;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridResultGenerateSelectedLineInsertSql.
 *
 * @since 3.0.0
 */
public class GridResultGenerateSelectedLineInsertSql extends Observable {
    private GridSelectionLayerPortData selectData;
    private ConsoleMessageWindow consoleMessageWindow;
    private IQueryExecutionSummary resultSummary;
    private IDSGridDataProvider result;
    private TerminalExecutionConnectionInfra termConnection;
    private String windowName;
    private Path path = null;
    private String sqlPath = null;
    private boolean generateCurrentSql;
    private boolean isZip;

    /**
     * Instantiates a new grid result generate selected line insert sql.
     *
     * @param termConnection the term connection
     * @param selectData the select data
     * @param consoleMessageWindow the console message window
     * @param result the result
     * @param resultSummary the result summary
     * @param windowName the window name
     * @param generateCurrentSql the generate current sql
     */
    public GridResultGenerateSelectedLineInsertSql(TerminalExecutionConnectionInfra termConnection,
            GridSelectionLayerPortData selectData, ConsoleMessageWindow consoleMessageWindow,
            IDSGridDataProvider result, IQueryExecutionSummary resultSummary, String windowName,
            boolean generateCurrentSql) {
        this.selectData = selectData;
        this.consoleMessageWindow = consoleMessageWindow;
        this.resultSummary = resultSummary;
        this.termConnection = termConnection;
        this.result = result;
        this.windowName = windowName;
        this.generateCurrentSql = generateCurrentSql;
    }

    /**
     * End of generate.
     */
    public void endOfGenerate() {
        setChanged();
        notifyObservers(true);
    }

    /**
     * Generate.
     */
    public void generate() {
        int userOption = confirmOverridingDialog();

        if (userOption == 0) {
            endOfGenerate();
            return;
        }

        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMssage = new StatusMessage("Generate Insert Sql...");

        String tempPath = null;
        if (isZip) {
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
        
        GridResultGenerateInsertSqlWorker worker = new GridResultGenerateInsertSqlWorker(termConnection, resultSummary,
                result, selectData, windowName, statMssage, consoleMessageWindow, path, sqlPath, generateCurrentSql,
                isZip, tempPath);
        StatusMessageList.getInstance().push(statMssage);
        if (bttmStatusBar != null) {
            bttmStatusBar.activateStatusbar();
        }
        worker.schedule();
    }

    private int confirmOverridingDialog() {
        int mark = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

        String defaultFileName = windowName + "_" + dateFormat.format(new Date());
        ExportZipOptionDialog exportZipOptionDialog = new ExportZipOptionDialog(Display.getDefault().getActiveShell(),
                defaultFileName, true, false,
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_SQL_EXPORT_WINDOW_TITLE, windowName));
        int returnValue = exportZipOptionDialog.open();
        if (returnValue != 0) {
            mark = 0;
            return mark;
        }

        isZip = exportZipOptionDialog.getExportOption().isZip();

        sqlPath = exportZipOptionDialog.getExportOption().getFilePathWithSuffixFormat();
        Path newPath = Paths.get(sqlPath);

        boolean fileExists = Files.exists(newPath);

        // If file already exists , confirm for overwriting the file.
        if (fileExists) {
            DSDeleteFileExport deleteFileExport = new DSDeleteFileExport();
            deleteFileExport.deleteFile(newPath, "Error generate SQL file:", "Error generate SQL file:", sqlPath);
        }
        ISetFilePermission setFilePermission = FilePermissionFactory.getFilePermissionInstance();
        try {
            // create the file with security permissions
            path = setFilePermission.createFileWithPermission(sqlPath, false, null, false);
        } catch (DatabaseOperationException e) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty("Error generate SQL file:", sqlPath)));
            mark = 0;
            return mark;
        }

        return mark;
    }

}
