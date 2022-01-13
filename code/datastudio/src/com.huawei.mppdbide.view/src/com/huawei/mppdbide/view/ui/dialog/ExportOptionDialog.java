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

package com.huawei.mppdbide.view.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.utils.EnvirnmentVariableValidator;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.TableImporExportException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.FileValidationUtils;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.InitListener;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportOptionDialog.
 *
 * @since 3.0.0
 */
public class ExportOptionDialog extends ImportExportDialog {
    private Button confirmationBtn;
    private Button agreementBtn;
    private AbstractImportExportDataCore core;
    private Text outputFolder;
    private Button zip;

    /**
     * Instantiates a new export option dialog.
     *
     * @param parent the parent
     * @param core the core
     */
    public ExportOptionDialog(Shell parent, AbstractImportExportDataCore core) {
        super(parent, core);
        this.core = core;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        sc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite maincomp = (Composite) super.createDialogArea(sc1);
        sc1.setContent(maincomp);
        // Create Export File Component
        createExportFileGroupComponents(maincomp);

        if (!UIDisplayFactoryProvider.getUIDisplayStateIf().isDisclaimerReq()) {
            enableDisableComponens(false);
            sc1.setExpandHorizontal(true);
            sc1.setExpandVertical(true);
            sc1.setMinSize(maincomp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            sc1.pack();
            return maincomp;
        }

        addSecurityWarningOptionArea(maincomp);
        enableDisableComponens(false);

        sc1.setExpandHorizontal(true);
        sc1.setExpandVertical(true);
        sc1.setMinSize(maincomp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        sc1.pack();
        return maincomp;
    }

    /**
     * Creates the export file group components.
     *
     * @param maincomp the maincomp
     */
    public void createExportFileGroupComponents(Composite maincomp) {
        Group fileGroup = new Group(maincomp, SWT.NONE);
        fileGroup.setLayout(new GridLayout(4, false));
        fileGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

        addExportPathUi(fileGroup);
        addOutputFolder(fileGroup);
        addZipSelectionUi(fileGroup);

        createExportOptionsGroup(fileGroup);
    }

    private void createExportOptionsGroup(Group fileGroup) {
        Group exportOptionsGrp = new Group(fileGroup, SWT.NONE);
        exportOptionsGrp.setLayout(new GridLayout(1, false));
        GridData exportOptionsGrpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        exportOptionsGrpGD.horizontalSpan = 4;
        exportOptionsGrp.setLayoutData(exportOptionsGrpGD);

        super.grpGeneralInfoMainCompositeArea(exportOptionsGrp);
    }

    /**
     * Add export path ui
     * 
     * @param maincomp the composite
     */
    private void addExportPathUi(Group fileGroup) {
        Label exportpath = new Label(fileGroup, SWT.NONE);
        exportpath.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OUTPUTPATH) + "* :");
    }

    /**
     * setFileNametobeExporteValuesetFileNametobeExporte Add output folder
     * 
     * @param maincomp the composite
     */
    private void addOutputFolder(Group fileGroup) {
        outputFolder = new Text(fileGroup, SWT.BORDER | SWT.READ_ONLY);
        GridData outputFolderGD = new GridData(SWT.FILL, SWT.CENTER, true, false);
        outputFolderGD.widthHint = 430;
        outputFolder.setLayoutData(outputFolderGD);

        outputFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        outputFolder.addListener(SWT.MenuDetect, new InitListener());
        Button browseBtn = new Button(fileGroup, SWT.NONE);
        browseBtn.setText("...");
        browseBtn.addSelectionListener(new BrowseBtnSelectionListener());
        browseBtn.setLayoutData(new GridData(GridData.END));
    }

    private void addZipSelectionUi(Composite grpGeneralInfo) {
        zip = new Button(grpGeneralInfo, SWT.CHECK);
        zip.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_ZIP));
        zip.setSelection(false);
    }

    private void addSecurityWarningOptionArea(Composite maincomp) {
        if (enableSecurityWarningOption()) {
            Group disclamerGroup = new Group(maincomp, SWT.NONE | SWT.SCROLL_PAGE);
            disclamerGroup.setLayout(new GridLayout(2, false));
            disclamerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
            disclamerGroup.setText(MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_WARNING_OPTION));
            Label lblNotice = new Label(disclamerGroup, SWT.WRAP);

            StringBuilder notice = new StringBuilder(
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER));
            notice.append(
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER_SECONDPART2));
            GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
            gridData.horizontalSpan = 2;
            lblNotice.setText(notice.toString());
            lblNotice.setLayoutData(gridData);
            agreementBtn = new Button(disclamerGroup, SWT.CHECK);
            agreementBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_I_AGREE));
            agreementBtn.setSelection(false);
            agreementBtn.addSelectionListener(new AgreementBtnSelectionListener());
            agreementBtn.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
            ColumnComponent.setAgreementBtn(agreementBtn);
            confirmationBtn = new Button(disclamerGroup, SWT.CHECK);
            confirmationBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
            confirmationBtn.addSelectionListener(new ConfirmationBtnSelectionListener());
            confirmationBtn.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        }
    }

    /**
     * The listener interface for receiving confirmationBtnSelection events. The
     * class that is interested in processing a confirmationBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addConfirmationBtnSelectionListener<code> method. When the
     * confirmationBtnSelection event occurs, that object's appropriate method
     * is invoked.
     *
     * ConfirmationBtnSelectionEvent
     */
    private class ConfirmationBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (confirmationBtn.getSelection()) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(false);
            } else {
                UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(true);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving agreementBtnSelection events. The
     * class that is interested in processing a agreementBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addAgreementBtnSelectionListener<code> method. When the
     * agreementBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * AgreementBtnSelectionEvent
     */
    private class AgreementBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (isOkButtonToEnabled()) {
                toggleOKButtons(true);
            } else {
                toggleOKButtons(false);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * The listener interface for receiving browseBtnSelection events. The class
     * that is interested in processing a browseBtnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addBrowseBtnSelectionListener<code> method. When the
     * browseBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * BrowseBtnSelectionEvent
     */
    private class BrowseBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent arg0) {
            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
            dialog.setFileName(core.getFileName());
            String outputPathText = dialog.open();
            if (outputPathText == null) {
                return;
            }
            setFileLocation(outputPathText);
            if (isOkButtonToEnabled()) {
                enableButtons();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {

        }

    }

    /**
     * Checks if is ok button to enabled.
     *
     * @return true, if is ok button to enabled
     */
    protected boolean isOkButtonToEnabled() {
        if (null != agreementBtn) {
            return !outputFolder.getText().isEmpty() && agreementBtn.getSelection() && checkSelectedColumns();
        } else {
            return !outputFolder.getText().isEmpty() && checkSelectedColumns();
        }
    }

    private boolean validateSelectedFile() {

        String filePath = core.getFileLocation();
        if (filePath == null || filePath.length() < 1) {
            return false;
        }

        filePath = getFilePath(filePath);
        Path path = Paths
                .get(zip.getSelection() ? filePath.substring(0, filePath.lastIndexOf(MPPDBIDEConstants.DOT)) + ".zip"
                        : filePath);

        boolean filepathExist = Files.exists(path);

        // If file already exists , confirm for overwriting the file.
        if (filepathExist) {
            if (UIConstants.OK_ID != MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_FILE_OVERWRITE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_NAME_EXIT, filePath))) {
                // If user chooses not to overwrite, return from here.
                return false;
            } else {
                try {
                    // Delete the file if file exists and chooses to overwrite.
                    Files.delete(path);
                } catch (IOException err) {
                    String message = err.getMessage();
                    MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                    MPPDBIDEConstants.LINE_SEPARATOR, message));
                    MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                            err);
                    return false;
                }
            }
        }
        return setFilePermissionForFile(filePath);
    }

    private boolean setFilePermissionForFile(String filePath) {
        ISetFilePermission setFilePermission = FilePermissionFactory.getFilePermissionInstance();
        Path outputPath = null;
        try {
            if (zip.getSelection()) {
                String tempPath = Normalizer.normalize(System.getenv(MPPDBIDEConstants.TEMP_ENVIRONMENT_VARIABLE),
                        Normalizer.Form.NFD);
                if (!DSFilesWrapper.isExistingDirectory(tempPath)) {
                    MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                    MPPDBIDEConstants.LINE_SEPARATOR, MessageConfigLoader
                                            .getProperty(IMessagesConstants.INVALID_TEMP_ENVIRONMENT_VARIABLE)));
                    MPPDBIDELoggerUtility.error("TEMP environment varibale is not an existing directory.");
                    return false;
                }
                String tempFilePath = tempPath + EnvirnmentVariableValidator.validateAndGetFileSeperator() + filePath
                        .substring(filePath.lastIndexOf(EnvirnmentVariableValidator.validateAndGetFileSeperator()));
                File file = new File(tempFilePath);
                Files.deleteIfExists(file.toPath());

                core.setTempFilePath(setFilePermission.createFileWithPermission(tempFilePath, false, null, false));
                filePath = filePath.substring(0, filePath.lastIndexOf(MPPDBIDEConstants.DOT)) + ".zip";
            }
            // create the file with security permissions
            outputPath = setFilePermission.createFileWithPermission(filePath, false, null, false);
            core.setFilePath(outputPath);
        } catch (DatabaseOperationException databaseOperationException) {
            String message = databaseOperationException.getServerMessage();
            if (null == message) {
                message = databaseOperationException.getDBErrorMessage();
            } else if (databaseOperationException.getCause() instanceof IOException) {

                message = message + '-' + databaseOperationException.getDBErrorMessage();
            }

            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, message));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    databaseOperationException);
            return false;
        } catch (IOException ioException) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, ioException.getMessage()));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    ioException);
            return false;
        }
        return true;
    }

    private String getFilePath(String filePath) {
        if ("Excel(xlsx)".equalsIgnoreCase(core.getFileFormat())
                && !filePath.toLowerCase(Locale.ENGLISH).endsWith(".xlsx")) {
            filePath = filePath + ".xlsx";
        }
        if ("Excel(xls)".equalsIgnoreCase(core.getFileFormat())
                && !filePath.toLowerCase(Locale.ENGLISH).endsWith(".xls")) {
            filePath = filePath + ".xls";
        }
        if ("Text".equalsIgnoreCase(core.getFileFormat()) && !filePath.toLowerCase(Locale.ENGLISH).endsWith(".txt")) {
            filePath = filePath + ".txt";
        }
        if ("Binary".equalsIgnoreCase(core.getFileFormat()) && !filePath.toLowerCase(Locale.ENGLISH).endsWith(".bin")) {
            filePath = filePath + ".bin";
        } 
        return filePath;
    }

    private void setFileLocation(String outputpath) {
        if (outputpath != null) {
            outputFolder.setText(outputpath);
            ColumnComponent.setOutputFolder(outputFolder);
        }
        core.setFileLocation(outputFolder.getText());
    }

    /**
     * Handle shell close event.
     */
    @Override
    protected void handleShellCloseEvent() {
        if (null != confirmationBtn && !UIDisplayFactoryProvider.getUIDisplayStateIf().isDisclaimerReq()) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(true);
        }
        super.handleShellCloseEvent();
    }

    /**
     * Cancel pressed.
     */
    protected void cancelPressed() {
        if (null != confirmationBtn && !UIDisplayFactoryProvider.getUIDisplayStateIf().isDisclaimerReq()) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(true);
        }
        super.cancelPressed();
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    @Override
    protected String getWindowTitle() {
        String windowTitle = null;
        if (importExportDataCore.getImportExportServerObj() instanceof TableMetaData) {
            windowTitle = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OPTION_TITLE)
                    + importExportDataCore.getImportExportServerObj().getDisplayName();
        } else {

            windowTitle = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_EXEC_TITLE,
                    importExportDataCore.getTerminalId());
        }
        return windowTitle;
    }

    /**
     * Perform ok operation.
     */
    @Override
    protected void performOkOperation() {
        String outputText = outputFolder.getText();
        Path outputPath = Paths.get(outputText);
        if (outputPath == null || outputPath.getFileName() == null) {
            return;
        }
        String fileName = outputPath.getFileName().toString();
        if ("".equals(fileName.trim()) || !FileValidationUtils.validateFileName(fileName)) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_INVALIDFILENAME));
            enableButtons();
            return;
        }

        ImportExportOption importexportOption = importExportDataCore.getImportExportoptions();
        importexportOption.setZip(zip.getSelection());
        setimportExportData(importexportOption);

        try {
            importExportDataCore.validateImportExportOptParameters();
            if (validateSelectedFile()) {
                this.close();
            } else {
                enableButtons();
            }
        } catch (TableImporExportException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR), e.getDBErrorMessage());
            enableButtons();
        }

    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    @Override
    protected Image getWindowImage() {
        if (importExportDataCore.getImportExportServerObj() instanceof TableMetaData) {
            return IconUtility.getIconImage(IiconPath.ICO_EXPORTTABLE, this.getClass());
        } else {
            return IconUtility.getIconImage(IiconPath.ICO_EXPORT_ALL_DATA, this.getClass());
        }

    }

    /**
     * Gets the NULL label.
     *
     * @return the NULL label
     */
    @Override
    protected String getNULLLabel() {
        return MessageConfigLoader.getProperty(IMessagesConstants.OPTION_REPLACE_NULL);
    }

    private boolean enableSecurityWarningOption() {
        return UserPreference.getInstance().getEnableSecurityWarningOption();

    }

    /**
     * Sets the file name.
     *
     * @param fileName the new file name
     */
    public void setFileName(String fileName) {
        outputFolder.setText(fileName);
    }

    /**
     * Gets the format selector.
     *
     * @param formatSelector the format selector
     * @return the format selector
     */
    @Override
    protected void getFormatSelector(final Combo formatSelector) {
        formatSelector.add("Excel(xlsx)");
        formatSelector.add("Excel(xls)");
        if (core.isOLAPDB()) { 
            formatSelector.add("Text");
            formatSelector.add("Binary");
        }
        formatSelector.select(0);
        formatSelector.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (formatSelector.getSelectionIndex() == 0 || formatSelector.getSelectionIndex() == 1
                        || (core.isOLAPDB() && formatSelector.getSelectionIndex() == 3)) {
                    enableDisableComponens(false);
                } else {
                    enableDisableComponens(true);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }
}
