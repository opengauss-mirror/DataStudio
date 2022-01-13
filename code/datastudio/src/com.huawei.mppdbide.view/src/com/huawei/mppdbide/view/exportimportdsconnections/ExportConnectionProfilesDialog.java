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

package com.huawei.mppdbide.view.exportimportdsconnections;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.ProfileDiskUtility;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.presentation.exportimportdsconnectionprofiles.ExportConnectionCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.files.FileValidationUtils;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportConnectionProfilesDialog.
 *
 * @since 3.0.0
 */
public class ExportConnectionProfilesDialog extends Dialog {

    private Text outputPathText;
    private Button checkButton;
    private Button selectAllBtn;
    private Button clearAllBtn;
    private Table connectionTable;
    private String[] connNames;
    private String[] connDetails;
    private List<String> userSelectedConn;
    private List<Button> chkBtnList;
    private Button okBtn;
    private Button cancelBtn;
    private ExportConnectionCore core;
    private int numberOfConnectionsLoaded;
    private Map<String, IServerConnectionInfo> connectionInfoMap;

    private boolean isConnectionSelected;
    private boolean isFileNameAvailable;
    private boolean isOutputPathAvailable;

    /**
     * Instantiates a new export connection profiles dialog.
     *
     * @param parentShell the parent shell
     * @param core the core
     */
    protected ExportConnectionProfilesDialog(Shell parentShell, ExportConnectionCore core) {
        super(parentShell);
        this.core = core;
        chkBtnList = new ArrayList<Button>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        userSelectedConn = new ArrayList<>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    /**
     * Configure shell.
     *
     * @param exportProfileShell the export profile shell
     */
    @Override
    protected void configureShell(Shell exportProfileShell) {
        exportProfileShell
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_DIALOG_HEADER));
        super.configureShell(exportProfileShell);
        exportProfileShell.setImage(IconUtility.getIconImage(IiconPath.EXPORT_CONN_PROFILES, this.getClass()));
    }

    private String getDefaultOutputPath(String path) {
        String canonicalPath = null;
        try {
            canonicalPath = new File(path).getCanonicalPath();
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("Error while getting the default output path for exporting a profile",
                    exception);
        }
        return canonicalPath;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        // composite for the table name label and table name textbox
        Composite fileInfoComposite = (Composite) super.createDialogArea(parent);
        GridLayout fileInfoLayout = new GridLayout();
        fileInfoLayout.numColumns = 3;
        fileInfoLayout.makeColumnsEqualWidth = false;

        GridData fileInfogridData = getFileInfoGridData();

        fileInfoComposite.setLayout(fileInfoLayout);
        fileInfoComposite.setLayoutData(fileInfogridData);

        createOpPathText(fileInfoComposite);

        Button browseBtn = new Button(fileInfoComposite, SWT.NONE);
        browseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_FILE_BROWSE));
        browseBtn.addSelectionListener(browseBtnSelectionListener());

        // Group to contain the tableviewer
        Group profilesGrp = getProfileGroup(parent);

        // tableviewer to shows the column names and datatypes
        Composite tableViewerComp = getTableViewerComp(profilesGrp);

        TableViewer connectionTableViewer = createConnTableViewer(tableViewerComp);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        connectionTableViewer.getControl().setLayoutData(gridData);

        formConnectionNameInfo();
        createTableItems(connNames, connDetails, connectionTable);
        connectionTable.pack();
        // composite to contain the buttons of the group to select all the check
        // box or clear all the checkbox
        addButtons(profilesGrp);
        return parent;
    }

    private GridData getFileInfoGridData() {
        GridData fileInfogridData = new GridData();
        fileInfogridData.grabExcessHorizontalSpace = true;
        fileInfogridData.verticalAlignment = GridData.FILL;
        fileInfogridData.horizontalAlignment = GridData.FILL;
        fileInfogridData.verticalIndent = 5;
        fileInfogridData.horizontalIndent = 5;
        fileInfogridData.minimumWidth = 200;

        return fileInfogridData;
    }

    private void createOpPathText(Composite fileInfoComposite) {
        Label labelOutputPath = new Label(fileInfoComposite, SWT.NONE);
        labelOutputPath.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OUTPUTPATH) + "*" + " :");

        outputPathText = new Text(fileInfoComposite, SWT.READ_ONLY | SWT.BORDER);
        GridData outputPathTextGridData = new GridData();
        outputPathTextGridData.widthHint = 450;

        outputPathText.setLayoutData(outputPathTextGridData);
        outputPathText.setEnabled(true);

        outputPathText.addVerifyListener(opPathTextverifyListener());
    }

    private String getConnectionProfileBasePath() {
        ProfileDiskUtility diskUtility;
        String defaultOutputPath;
        diskUtility = ConnectionProfileManagerImpl.getInstance().getDiskUtility();
        defaultOutputPath = diskUtility.getConnctionProfileBasePath().toString();
        return defaultOutputPath;
    }

    private Group getProfileGroup(Composite parent) {
        Group profilesGrp = new Group(parent, SWT.NONE);
        GridData profilesGrpData = new GridData();
        profilesGrpData.grabExcessHorizontalSpace = false;
        profilesGrpData.grabExcessVerticalSpace = true;
        profilesGrpData.verticalAlignment = GridData.FILL;
        profilesGrpData.horizontalAlignment = GridData.FILL;
        profilesGrpData.verticalIndent = 10;
        profilesGrpData.horizontalIndent = 5;
        profilesGrpData.minimumWidth = 300;
        profilesGrpData.minimumHeight = 300;
        profilesGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CONNECTION_PROFILE_DIALOG_GROUP));
        profilesGrp.setLayoutData(profilesGrpData);
        profilesGrp.setLayout(new GridLayout());
        return profilesGrp;
    }

    private TableViewer createConnTableViewer(Composite tableViewerComp) {
        TableViewer connectionTableViewer = new TableViewer(tableViewerComp,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        connectionTableViewer.setContentProvider(new ArrayContentProvider());
        connectionTable = connectionTableViewer.getTable();
        connectionTable.setHeaderVisible(true);
        connectionTable.setVisible(true);
        GridData colTablegridData = new GridData();
        colTablegridData.verticalAlignment = GridData.FILL;
        colTablegridData.grabExcessHorizontalSpace = true;
        colTablegridData.grabExcessVerticalSpace = true;
        colTablegridData.horizontalAlignment = GridData.FILL;

        connectionTable.setLayoutData(colTablegridData);
        // first column of the tableviewer
        TableColumn checkBoxColumn = new TableColumn(connectionTable, SWT.NONE);
        checkBoxColumn.setText("");
        checkBoxColumn.setWidth(50);

        // second column of the tableviewer
        TableColumn connectionNameCol = new TableColumn(connectionTable, SWT.NONE);
        connectionNameCol.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_NAME));
        connectionNameCol.setWidth(200);

        // third column of the tableviewer
        TableColumn connectionDetailsColumn = new TableColumn(connectionTable, SWT.NONE);
        connectionDetailsColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_DETAILS));
        connectionDetailsColumn.setWidth(330);
        return connectionTableViewer;
    }

    private Composite getTableViewerComp(Group profilesGrp) {
        Composite tableViewerComp = new Composite(profilesGrp, SWT.NONE);
        GridData tableviewerGrpData = new GridData();
        tableviewerGrpData.grabExcessHorizontalSpace = false;
        tableviewerGrpData.grabExcessVerticalSpace = true;
        tableviewerGrpData.verticalAlignment = GridData.FILL;
        tableviewerGrpData.horizontalAlignment = GridData.FILL;
        tableviewerGrpData.verticalIndent = 10;
        tableviewerGrpData.horizontalIndent = 5;
        tableviewerGrpData.minimumWidth = 300;
        tableviewerGrpData.heightHint = 200;
        tableViewerComp.setLayoutData(tableviewerGrpData);
        tableViewerComp.setLayout(new GridLayout());
        return tableViewerComp;
    }

    private void addButtons(Group profilesGrp) {
        Composite grpButtonsComp = new Composite(profilesGrp, SWT.NONE);

        GridData grpButtonsGridData = new GridData();

        grpButtonsGridData.horizontalIndent = 450;

        grpButtonsComp.setLayout(new GridLayout(2, false));
        grpButtonsComp.setLayoutData(grpButtonsGridData);

        selectAllBtn = new Button(grpButtonsComp, SWT.NONE);
        selectAllBtn
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_SELECT_ALL_BTN_LABEL));
        selectAllBtn.addSelectionListener(selectAllBtnSelectionListener());

        clearAllBtn = new Button(grpButtonsComp, SWT.NONE);
        clearAllBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_CLEAR_ALL_BTN_LABEL));
        clearAllBtn.addSelectionListener(clearAllBtnSelectionListener());
    }

    private VerifyListener opPathTextverifyListener() {
        return new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent event) {
                setOutputPathAvailable(event.text != null && !event.text.trim().isEmpty());
                enableDisableOkButton();
            }
        };
    }

    private SelectionListener browseBtnSelectionListener() {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
                dialog.setFilterPath(getDefaultOutputPath(getConnectionProfileBasePath()));
                String outputPath = dialog.open();
                if (outputPath != null) {
                    outputPathText.setText(outputPath);
                    core.setFileOutputPath(outputPath);
                    setFileNameAvailable(true);
                    enableDisableOkButton();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private SelectionListener selectAllBtnSelectionListener() {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                int size = chkBtnList.size();
                for (int i = 0; i < size; i++) {
                    chkBtnList.get(i).setSelection(true);
                    String text = connectionTable.getItem(i).getText(1);
                    if (!userSelectedConn.contains(text)) {
                        userSelectedConn.add(text);
                    }

                }
                setConnectionSelected(chkBtnList.size() > 0);
                enableDisableOkButton();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private SelectionListener clearAllBtnSelectionListener() {
        return new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                int size = chkBtnList.size();
                for (int i = 0; i < size; i++) {
                    chkBtnList.get(i).setSelection(false);
                    userSelectedConn.remove(connectionTable.getItem(i).getText(1));
                    setConnectionSelected(false);
                    enableDisableOkButton();

                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param prnt the prnt
     */
    @Override
    protected void createButtonsForButtonBar(Composite prnt) {
        String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        okBtn = createButton(prnt, IDialogConstants.OK_ID, okLbl, true);
        setConnectionSelected(false);
        enableDisableOkButton();
        cancelBtn = createButton(prnt, IDialogConstants.CANCEL_ID, cancelLbl, false);
        cancelBtn.setEnabled(true);
    }

    private String formFileLocationPath() {
        return core.getFileOutputPath();
    }

    private void formConnectionNameInfo() {
        List<IServerConnectionInfo> connectionInfos = core.getLoadedProfileList();
        numberOfConnectionsLoaded = connectionInfos.size();
        connNames = new String[numberOfConnectionsLoaded];
        connDetails = new String[numberOfConnectionsLoaded];
        connectionInfoMap = new HashMap<>();
        int counter = 0;
        for (IServerConnectionInfo info : connectionInfos) {
            connNames[counter] = info.getConectionName();
            connDetails[counter] = info.getDsUsername() + '@' + info.getServerIp() + ':' + info.getServerPort() + '/'
                    + info.getDatabaseName();
            connectionInfoMap.put(connNames[counter], info);
            counter++;
        }

    }

    /**
     * Enable disable ok button.
     */
    protected void enableDisableOkButton() {
        okBtn.setEnabled(isFileNameAvailable && isOutputPathAvailable && isConnectionSelected);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        String outputText = core.getFileOutputPath();
        Path outputPath = Paths.get(outputText);
        if (outputPath == null || outputPath.getFileName() == null) {
            return;
        }
        String fileName = outputPath.getFileName().toString();

        if (!FileValidationUtils.validateFileName(fileName)) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_INVALIDFILENAME));
        }

        if (!isFileExist()) {
            return;
        }
        core.setFileOutputPath(formFileLocationPath());
        List<Integer> indexList = new ArrayList<Integer>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        List<IServerConnectionInfo> loadedProfileList = core.getLoadedProfileList();

        userSelectedConn.stream().forEach(item -> {
            if (loadedProfileList.indexOf(connectionInfoMap.get(item)) != -1) {
                indexList.add(loadedProfileList.indexOf(connectionInfoMap.get(item)));
            }
        });
        core.setExportProfilesIndexList(indexList);

        super.okPressed();

    }

    private boolean handleOverwriteOperation() {
        try {
            Path path = Paths.get(formFileLocationPath());
            Files.delete(path);
        } catch (IOException err) {
            String message = err.getMessage();
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, message));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY), err);
            return false;
        }
        return true;

    }

    private boolean isFileExist() {
        String fileOutputPath = formFileLocationPath();
        Path path = Paths.get(fileOutputPath);
        int res = -1;
        if (Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            res = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_FILE_OVERWRITE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_NAME_EXIT, fileOutputPath));
            return IDialogConstants.OK_ID == res ? handleOverwriteOperation() : false;

        }
        return true;
    }

    /**
     * Cancel pressed.
     */
    @Override
    protected void cancelPressed() {
        connectionInfoMap.clear();
        chkBtnList.clear();
        connDetails = null;
        connNames = null;
        super.cancelPressed();
    }

    /**
     * Close.
     *
     * @return true, if successful
     */
    @Override
    public boolean close() {
        connectionInfoMap.clear();
        chkBtnList.clear();
        connDetails = null;
        connNames = null;
        return super.close();
    }

    private void createTableItems(String[] columnNames, String[] arrDatatypes, Table tbl) {
        TableItem itm = null;
        TableEditor tblEditor = null;

        for (int i = 0; i < columnNames.length; i++) {
            itm = new TableItem(tbl, SWT.NONE);
            tblEditor = new TableEditor(tbl);
            checkButton = new Button(tbl, SWT.CHECK);

            checkButton.pack();
            chkBtnList.add(checkButton);
            registerCheckBtnListener(checkButton, itm);
            tblEditor.minimumWidth = checkButton.getSize().x;
            tblEditor.horizontalAlignment = SWT.LEFT;
            tblEditor.setEditor(checkButton, itm, 0);

            itm.setText(1, columnNames[i]);
            itm.setText(2, arrDatatypes[i]);

        }

    }

    private void registerCheckBtnListener(Button checkBtn, TableItem item) {
        checkBtn.addSelectionListener(new CheckButtonListener(checkBtn, item));
    }

    /**
     * Sets the connection selected.
     *
     * @param isConnectionSelected the new connection selected
     */
    public void setConnectionSelected(boolean isConnectionSelected) {
        this.isConnectionSelected = isConnectionSelected;
    }

    /**
     * Sets the file name available.
     *
     * @param isFileNameAvailable the new file name available
     */
    public void setFileNameAvailable(boolean isFileNameAvailable) {
        this.isFileNameAvailable = isFileNameAvailable;
    }

    /**
     * Sets the output path available.
     *
     * @param isOutputPathAvailable the new output path available
     */
    public void setOutputPathAvailable(boolean isOutputPathAvailable) {
        this.isOutputPathAvailable = isOutputPathAvailable;
    }

    /**
     * The listener interface for receiving checkButton events. The class that
     * is interested in processing a checkButton event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addCheckButtonListener<code>
     * method. When the checkButton event occurs, that object's appropriate
     * method is invoked.
     *
     * CheckButtonEvent
     */
    private final class CheckButtonListener implements SelectionListener {
        private Button chkBtn;
        private TableItem tableItem;

        private CheckButtonListener(Button btn, TableItem item) {
            this.chkBtn = btn;
            this.tableItem = item;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (chkBtn.getSelection()) {
                userSelectedConn.add(tableItem.getText(1));
            } else {
                userSelectedConn.remove(tableItem.getText(1));
            }
            setConnectionSelected(userSelectedConn.size() > 0);
            enableDisableOkButton();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // Ignore. Nothing to do.
        }

    }
}
