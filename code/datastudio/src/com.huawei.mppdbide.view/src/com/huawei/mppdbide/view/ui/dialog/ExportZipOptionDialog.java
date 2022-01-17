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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ExportOption;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.DSDeleteFileExport;
import com.huawei.mppdbide.view.utils.InitListener;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: ExportZipOptionDialog
 * 
 * @since 3.0.0
 */
public class ExportZipOptionDialog extends Dialog {
    private Combo formatSelector;
    private Button zip;
    private Button confirmationBtn;
    private Button agreementBtn;
    private Button oKBtn;
    private Text outputFolder;
    private String defaultFileName;
    private ExportOption exportOption;
    private boolean isSqlFile;
    private boolean isExecPlan;
    private String windowTitle;

    /**
     * Instantiates a new export zip option dialog.
     *
     * @param parentShell the parent shell
     * @param defaultFileName the default file name
     * @param isSqlFile the is sql file
     * @param windowTitle the window title
     */
    public ExportZipOptionDialog(Shell parentShell, String defaultFileName, boolean isSqlFile, boolean isExecPlan,
            String windowTitle) {
        super(parentShell);

        String newFileName = CustomStringUtility.sanitizeExportFileName(defaultFileName);
        // When filename contains only these special characters <>?*:\/|", then
        // export filename will contain a double underscores
        if (newFileName.contains("__")) {
            newFileName = newFileName.replace("__", "_");
        }

        this.defaultFileName = newFileName;
        this.isSqlFile = isSqlFile;
        this.isExecPlan = isExecPlan;
        this.windowTitle = windowTitle;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        oKBtn = createButton(parent, IDialogConstants.OK_ID, MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK),
                true);
        oKBtn.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        oKBtn.setEnabled(false);

        Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID,
                MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL), false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
    }

    private void oKBtenable() {
        if (oKBtn != null) {
            if ((agreementBtn != null && !agreementBtn.getSelection()) || outputFolder.getText().length() <= 0) {
                oKBtn.setEnabled(false);
            } else {
                oKBtn.setEnabled(true);
            }
        }
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        parent.getShell().setText(windowTitle);
        parent.getShell().setImage(IconUtility.getIconImage(IiconPath.ICO_EXPORT_ALL_DATA, this.getClass()));

        Composite mainComposite = (Composite) super.createDialogArea(parent);
        mainComposite.setLayout(new GridLayout(1, true));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gridData.widthHint = 650;
        gridData.horizontalIndent = 0;
        gridData.verticalIndent = 0;
        mainComposite.setLayoutData(gridData);

        Composite grpGeneralInfo = new Composite(mainComposite, SWT.NONE);
        grpGeneralInfo.setLayout(new GridLayout(3, false));
        grpGeneralInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label formatLabel = new Label(grpGeneralInfo, SWT.NONE);
        formatLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_FORMAT));

        formatSelector = new Combo(grpGeneralInfo, SWT.READ_ONLY);
        getFormatSelector(formatSelector);

        addFileText(mainComposite);

        if (UIDisplayFactoryProvider.getUIDisplayStateIf().isDisclaimerReq()
                && UserPreference.getInstance().getEnableSecurityWarningOption()) {
            addSecurityWarningOptionArea(mainComposite);
        }
        return mainComposite;

    }

    /**
     * Gets the format selector.
     *
     * @param formatSelector the format selector
     * @return the format selector
     */
    protected void getFormatSelector(Combo formatSelector) {
        if (isSqlFile) {
            formatSelector.add(".sql");
        } else {
            if (!isExecPlan) {
                formatSelector.add("Excel(xlsx)");
                formatSelector.add("Excel(xls)");
            } else {
                formatSelector.add("Excel(xlsx)");
                formatSelector.add("Excel(xls)");
                formatSelector.add("Text");
            }
        }
        formatSelector.select(0);

    }

    private void addZipSelectionUi(Composite grpGeneralInfo) {
        zip = new Button(grpGeneralInfo, SWT.CHECK);
        zip.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_ZIP));
        zip.setSelection(false);
    }

    private void addSecurityWarningOptionArea(Composite maincomp) {
        Group disclamerGroup = new Group(maincomp, SWT.NONE | SWT.SCROLL_PAGE);
        disclamerGroup.setLayout(new GridLayout(2, false));
        GridData disclamerGroupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        disclamerGroup.setLayoutData(disclamerGroupGD);

        disclamerGroup.setText(MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_WARNING_OPTION));

        Label lblNotice = new Label(disclamerGroup, SWT.WRAP);
        GridData lblNoticeGD = new GridData(SWT.FILL, SWT.CENTER, true, true);
        lblNoticeGD.horizontalSpan = 2;
        lblNotice.setLayoutData(lblNoticeGD);

        StringBuilder notice = new StringBuilder(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER));
        notice.append(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_DS_NO_DATA_ENCRYPT_DISCLAIMER_SECONDPART2));
        lblNotice.setText(notice.toString());

        agreementBtn = new Button(disclamerGroup, SWT.CHECK);
        agreementBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_I_AGREE));
        agreementBtn.setSelection(false);
        agreementBtn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                oKBtenable();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        confirmationBtn = new Button(disclamerGroup, SWT.CHECK);
        confirmationBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.MSG_DO_NOT_SHOW_AGAIN));
    }

    private void addFileText(Composite maincomp) {
        Group fileGroup = new Group(maincomp, SWT.NONE);
        fileGroup.setLayout(new GridLayout(4, false));
        GridData fileGroupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        fileGroup.setLayoutData(fileGroupGD);

        Label exportpath = new Label(fileGroup, SWT.NONE);
        exportpath.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OUTPUTPATH) + "* :");

        outputFolder = new Text(fileGroup, SWT.BORDER | SWT.READ_ONLY);
        GridData outputFolderGD = new GridData(SWT.FILL, SWT.CENTER, true, true);
        outputFolder.setLayoutData(outputFolderGD);

        outputFolder.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        outputFolder.addListener(SWT.MenuDetect, new InitListener());
        outputFolder.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                oKBtenable();
            }
        });

        outputFolder.addListener(SWT.MenuDetect, new InitListener());
        Button browseBtn = new Button(fileGroup, SWT.NONE);
        browseBtn.setText("...");
        browseBtn.addSelectionListener(new BrowseBtnSelectionListener());

        addZipSelectionUi(fileGroup);
    }

    private class BrowseBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent arg0) {
            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
            dialog.setFileName(defaultFileName);
            String outputPathText = dialog.open();
            if (outputPathText == null) {
                return;
            }
            setFileLocation(outputPathText);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent arg0) {
        }

    }

    private void setFileLocation(String outputpath) {
        if (outputpath != null) {
            outputFolder.setText(outputpath);
        }
    }

    private void securityDisclaimer() {
        if (null == confirmationBtn) {
            return;
        }
        UIDisplayFactoryProvider.getUIDisplayStateIf().setDisclaimerReq(!confirmationBtn.getSelection());
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        securityDisclaimer();
        if (!setExportOption()) {
            return;
        }
        if (zip.getSelection()) {
            String tempPath = Normalizer.normalize(System.getenv(MPPDBIDEConstants.TEMP_ENVIRONMENT_VARIABLE),
                    Normalizer.Form.NFD);
            if (!DSFilesWrapper.isExistingDirectory(tempPath)) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_ZIP_EXPORT_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_ZIP_EXPORT_DAILOG_DESC,
                                MPPDBIDEConstants.LINE_SEPARATOR,
                                MessageConfigLoader.getProperty(IMessagesConstants.INVALID_TEMP_ENVIRONMENT_VARIABLE)));
                MPPDBIDELoggerUtility.error("TEMP environment varibale is not an existing directory.");
                return;
            }
        }
        Path newPath = null;
        String targetFile = getExportOption().getFilePathWithSuffixFormat();
        newPath = Paths.get(targetFile);
        // If file already exists , confirm for overwriting the file.
        if (Files.exists(newPath)) {
            if (UIConstants.OK_ID != MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_FILE_OVERWRITE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_NAME_EXIT, targetFile))) {
                // If user chooses not to overwrite, return from here.
                return;
            } else {
                // Delete the file if file exists and chooses to overwrite.
                DSDeleteFileExport deleteFileExport = new DSDeleteFileExport();
                boolean deleteFile = deleteFileExport.deleteFile(newPath, IMessagesConstants.ERR_WHILE_EXPORTING,
                        IMessagesConstants.ERR_UI_EXPORT_QUERY, targetFile);
                if (!deleteFile) {
                    return;
                }
            }
        }

        setReturnCode(IDialogConstants.OK_ID);
        this.close();
    }

    /**
     * Gets the export option.
     *
     * @return the export option
     */
    public ExportOption getExportOption() {
        return exportOption;
    }

    /**
     * Sets the export option.
     *
     * @return true, if successful
     */
    public boolean setExportOption() {
        if (formatSelector == null || zip == null || outputFolder == null) {
            return false;
        }
        String format = formatSelector.getText();
        boolean isZip = zip.getSelection();
        String outputText = outputFolder.getText();
        Path outputPath = Paths.get(outputText);
        if (outputPath == null || outputPath.getParent() == null || outputPath.getFileName() == null) {
            return false;
        }
        String foldeName = outputPath.getParent().toString();
        String fileName = outputPath.getFileName().toString();
        this.exportOption = new ExportOption(format, isZip, foldeName, fileName);
        return true;
    }
}