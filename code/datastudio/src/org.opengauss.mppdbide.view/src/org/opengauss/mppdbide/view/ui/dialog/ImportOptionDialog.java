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

package org.opengauss.mppdbide.view.ui.dialog;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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

import org.opengauss.mppdbide.bl.serverdatacache.ImportExportOption;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.TableImporExportException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.prefernces.PreferenceWrapper;
import org.opengauss.mppdbide.view.utils.Preferencekeys;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportOptionDialog.
 *
 * @since 3.0.0
 */
public class ImportOptionDialog extends ImportExportDialog {
    private Text txtUserExpr;
    private Combo dateCombo;
    private static final String DATA_FORMATE0 = "yyyy-MM-dd HH:mm:ss";
    private static final String DATA_FORMATE1 = "yyyy-MM-dd hh:mm:ss";

    /**
     * Instantiates a new import option dialog.
     *
     * @param parent the parent
     * @param core the core
     */
    public ImportOptionDialog(Shell parent, AbstractImportExportDataCore core) {
        super(parent, core);
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sc1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite maincomp = (Composite) super.createDialogArea(sc1);

        Group importGrp = new Group(maincomp, SWT.NONE);
        importGrp.setLayout(new GridLayout(1, false));
        GridData importGrpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        importGrp.setLayoutData(importGrpGD);

        Composite importcomp = new Composite(importGrp, SWT.NONE);
        importcomp.setLayout(new GridLayout(1, false));
        GridData gData = new GridData(SWT.FILL, SWT.FILL, true, true);
        importcomp.setLayoutData(gData);

        createImportPathSelectorComposite(importcomp);
        createImportOptionsGroup(importGrp);

        sc1.setContent(maincomp);
        sc1.setExpandHorizontal(true);
        sc1.setExpandVertical(true);
        sc1.setMinSize(maincomp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        sc1.pack();
        enableDisableComponens(false);
        return maincomp;
    }

    private void createImportPathSelectorComposite(Composite importcomp) {
        Composite importtextcomp = new Composite(importcomp, SWT.NONE);
        importtextcomp.setLayout(new GridLayout(3, false));
        GridData importtextcompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        importtextcomp.setLayoutData(importtextcompGD);

        Label importDataLbl = new Label(importtextcomp, SWT.NONE);
        importDataLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_DATA_FILE));
        txtUserExpr = new Text(importtextcomp, SWT.BORDER | SWT.READ_ONLY);
        txtUserExpr.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
        txtUserExpr.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        ColumnComponent.setTxtUserExpr(txtUserExpr);
        addBrowseBtn(importtextcomp);
    }

    private void createImportOptionsGroup(Composite importcomp) {
        Group importOptionsGrp = new Group(importcomp, SWT.NONE);
        importOptionsGrp.setLayout(new GridLayout(1, false));
        GridData importOptionsGrpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        importOptionsGrp.setLayoutData(importOptionsGrpGD);

        super.grpGeneralInfoMainCompositeArea(importOptionsGrp);
    }

    private void addBrowseBtn(Composite importtextcomp) {
        Button browseBtn = new Button(importtextcomp, SWT.NONE);
        browseBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_FILE_BROWSE));
        browseBtn.addSelectionListener(addBrowsBtnSelectionListener());
    }

    private SelectionListener addBrowsBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String canonicalPath = null;
                String fileFormat = null;
                try {
                    FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                    setDialogFilterNameAndExtensions(dialog);

                    String csv = dialog.open();

                    if (null == csv || csv.length() < 1) {
                        return;
                    } else {
                        toggleOKButtons(true);
                    }

                    File file = new File(csv);
                    if (validateForFileExists(file) || validateFileSize(file)) {
                        return;
                    }
                    canonicalPath = file.getCanonicalPath();

                    // Setting fileFormat value
                    fileFormat = FilenameUtils.getExtension(canonicalPath);

                    if (validateFileFormat(fileFormat, dialog)) {
                        return;
                    }

                    importExportDataCore.getImportExportoptions().setFileName(canonicalPath);
                    txtUserExpr.setText(canonicalPath);

                    changeFileFormat(fileFormat.toUpperCase(Locale.ENGLISH));
                    changeDateComboStatus(fileFormat.toUpperCase(Locale.ENGLISH));
                } catch (IOException ioException) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_ERROR),
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                                    MPPDBIDEConstants.LINE_SEPARATOR, ioException.getMessage()));
                    MPPDBIDELoggerUtility.error(
                            MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV), ioException);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent envent) {

            }
        };
    }

    private boolean validateForFileExists(File file) {
        if (!file.exists()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_DATA_FILE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_DATA_FILE));
            return true;
        }
        return false;
    }

    private boolean validateFileSize(File file) {
        double fileLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA);
        double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);

        if (fileLimit != 0 && fileSizeInMB > fileLimit) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_HEADER),
                    MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
            return true;
        }
        return false;
    }

    private boolean validateFileFormat(String fileFormat, FileDialog dialog) {
        if (!("bin".equalsIgnoreCase(fileFormat) || "txt".equalsIgnoreCase(fileFormat)
                || "csv".equalsIgnoreCase(fileFormat) || "xlsx".equalsIgnoreCase(fileFormat)
                || "xls".equalsIgnoreCase(fileFormat))) {
            dialog.getParent().close();
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.INVALID_EXTENSION_HEADER),
                    MessageConfigLoader.getProperty(IMessagesConstants.INVALID_EXTENSION_MESSAGE));
            return true;
        }

        return false;
    }

    private void setDialogFilterNameAndExtensions(FileDialog dialog) {
        ServerObject selTable = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (selTable instanceof TableMetaData) {
            String[] filterExt = {"*.txt;*.TXT;*.bin;*.BIN;*.csv;*.CSV;*.xlsx;*.XLSX;*.xls;*.XLS", "*.txt;*.TXT;",
                "*.bin;*.BIN;", "*.csv;*.CSV", "*.xlsx;*.XLSX", "*.xls;*.XLS"};
            String[] filterNames = {" ", ".txt", ".bin", ".csv", ".xlsx", ".xls"};
            dialog.setFilterExtensions(filterExt);
            dialog.setFilterNames(filterNames);
        } else {
            String[] filterExt = {"*.xlsx;*.XLSX;*.xls;*.XLS", "*.xlsx;*.XLSX", "*.xls;*.XLS"};
            String[] filterNames = {" ", ".xlsx", ".xls"};
            dialog.setFilterExtensions(filterExt);
            dialog.setFilterNames(filterNames);
        }
    }

    /**
     * Change date combo status.
     *
     * @param fileFormat the file format
     */
    protected void changeDateComboStatus(String fileFormat) {
        if (MPPDBIDEConstants.STR_XLSX.equals(fileFormat) || MPPDBIDEConstants.STR_XLS.equals(fileFormat)) {
            enableDateCombo(true);
        } else {
            enableDateCombo(false);
        }
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    @Override
    protected String getWindowTitle() {

        return MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_OPTION_TITLE)
                + importExportDataCore.getImportExportServerObj().getDisplayName();
    }

    /**
     * Perform ok operation.
     */
    @Override
    protected void performOkOperation() {
        setimportExportData(importExportDataCore.getImportExportoptions());
        setImportExcelDate(importExportDataCore.getImportExportoptions());
        try {
            importExportDataCore.validateImportExportOptParameters();
            if (validateDateFormat()) {
                this.close();
            } else {
                enableButtons();
            }
        } catch (TableImporExportException tableImporExportException) {

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_ERROR),
                    tableImporExportException.getDBErrorMessage());
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
        return IconUtility.getIconImage(IiconPath.ICO_IMPORTTABLE, this.getClass());
    }

    /**
     * Gets the NULL label.
     *
     * @return the NULL label
     */
    @Override
    protected String getNULLLabel() {
        return MessageConfigLoader.getProperty(IMessagesConstants.OPTION_REPLACE_WITHNULL);
    }

    /**
     * Gets the import export data core.
     *
     * @return the import export data core
     */
    public AbstractImportExportDataCore getImportExportDataCore() {
        return importExportDataCore;
    }

    /**
     * Sets the import export data core.
     *
     * @param importExportDataCore the new import export data core
     */
    public void setImportExportDataCore(ImportExportDataCore importExportDataCore) {
        this.importExportDataCore = importExportDataCore;
    }

    /**
     * Gets the format selector.
     *
     * @param formatSelector the format selector
     * @return the format selector
     */
    @Override
    protected void getFormatSelector(final Combo formatSelector) {
        ServerObject selTable = (ServerObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (selTable instanceof TableMetaData) {
            formatSelector.add(MPPDBIDEConstants.STR_EXCEL_XLSX);
            formatSelector.add(MPPDBIDEConstants.STR_EXCEL_XLS);
            formatSelector.add(MPPDBIDEConstants.CSV);
            formatSelector.add(MPPDBIDEConstants.BINARY);
            formatSelector.add(MPPDBIDEConstants.STR_TEXT);

            formatSelector.select(0);
            formatSelector.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent envent) {
                    if (formatSelector.getSelectionIndex() == 3) {
                        enableDisableComponens(false);
                        enableDateCombo(false);
                    } else if (formatSelector.getSelectionIndex() == 0 || formatSelector.getSelectionIndex() == 1) {
                        enableDisableComponens(false);
                        enableDateCombo(true);
                    } else {
                        enableDisableComponens(true);
                        enableDateCombo(false);
                    }
                    validateFileFormat(formatSelector.getText());
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent envent) {

                }
            });
        }
    }

    /**
     * Enable date combo.
     *
     * @param value the value
     */
    protected void enableDateCombo(boolean value) {
        if (null != this.dateCombo) {
            if (!value) {
                dateCombo.setItem(0, "");
            } else {
                dateCombo.setItems(DATA_FORMATE0, DATA_FORMATE1);
                dateCombo.select(0);
            }
            dateCombo.setEnabled(value);
        }
    }

    /**
     * Sets the import excel date.
     *
     * @param importExportoptions the new import excel date
     *
     * @Title: setImportExcellData
     * @Description: Set import excel date
     */
    private void setImportExcelDate(ImportExportOption importExportoptions) {
        if (null != this.dateCombo) {
            importExportoptions.setDateSelector(this.dateCombo.getText());
        }
    }

    /**
     * Adds the date selection.
     *
     * @param curComposite the cur composite
     */
    @Override
    protected void addDateSelection(Composite curComposite) {
        Label dateLabel = new Label(curComposite, SWT.NONE);
        dateLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        dateLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXCEL_DATE));
        dateCombo = new Combo(curComposite, SWT.CENTER);
        dateCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        dateCombo.add(DATA_FORMATE0);
        dateCombo.add(DATA_FORMATE1);
        dateCombo.select(0);
    }

    private boolean validateDateFormat() {
        boolean isDateFormat = true;
        try {
            DateTimeFormatter.ofPattern(importExportDataCore.getImportExportoptions().getDateSelector(),
                    Locale.ENGLISH);
        } catch (IllegalArgumentException exception) {
            isDateFormat = false;
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_DATE_FORMAT),
                    exception);
        }

        if (!isDateFormat) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IMPORT_TBL_DATA),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_DATE_FORMAT));
        }
        return isDateFormat;
    }

    /**
     * conver file format
     * 
     * @return file format
     */
    private String convertFileFormat() {
        String fileFormat = null;
        if (null != txtUserExpr) {
            String filePath = FilenameUtils.getExtension(txtUserExpr.getText());
            if (filePath.equalsIgnoreCase("xlsx")) {
                fileFormat = MPPDBIDEConstants.STR_EXCEL_XLSX;
            }
            if (filePath.equalsIgnoreCase("xls")) {
                fileFormat = MPPDBIDEConstants.STR_EXCEL_XLS;
            }
            if (filePath.equalsIgnoreCase("bin")) {
                fileFormat = MPPDBIDEConstants.STR_BINARY;
            }
            if (filePath.equalsIgnoreCase("csv")) {
                fileFormat = MPPDBIDEConstants.STR_CSV;
            }
            if (filePath.equalsIgnoreCase("txt")) {
                fileFormat = MPPDBIDEConstants.STR_TEXT;
            }
        }
        return fileFormat;
    }

    private void validateFileFormat(String fileFormatSelector) {
        if (null != txtUserExpr && !"".equals(txtUserExpr.getText())) {
            String fileFormat = convertFileFormat();
            if (null == fileFormat || !fileFormat.equals(fileFormatSelector)) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IMPORT_TBL_DATA),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_DIFF_FILE_FORMAT));
                toggleOKButtons(false);
            } else {
                toggleOKButtons(true);
            }
        }
    }
}
