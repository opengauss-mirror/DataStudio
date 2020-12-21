/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableValidatorRules;
import com.huawei.mppdbide.bl.serverdatacache.TypeMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.FontAndColorUtility;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeDataTypeDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ChangeDataTypeDialog extends Dialog {
    private Shell currentShell;

    private TableMetaData tableMetaData;

    /**
     * The ok button.
     */
    protected Button okButton = null;

    /**
     * The cancel button.
     */
    protected Button cancelButton = null;

    /**
     * The combo clm data schema.
     */
    protected Combo comboClmDataSchema;

    /**
     * The combo clm data type.
     */
    protected Combo comboClmDataType;

    /**
     * The spinner prev size.
     */
    protected Spinner spinnerPrevSize;

    /**
     * The spinner scale.
     */
    protected Spinner spinnerScale;

    /**
     * The lbl description of datatype.
     */
    protected Label lblDescriptionOfDatatype;

    private ColumnMetaData columnMetaData;

    /**
     * The validator.
     */
    protected TableValidatorRules validator;

    /**
     * The current datatype name.
     */
    protected String currentDatatypeName;

    /**
     * Instantiates a new change data type dialog.
     *
     * @param shell the shell
     * @param tableMetaData the table meta data
     * @param columnMetaData the column meta data
     */
    @Inject
    public ChangeDataTypeDialog(Shell shell, TableMetaData tableMetaData, ColumnMetaData columnMetaData) {
        super(shell);
        this.tableMetaData = tableMetaData;
        this.columnMetaData = columnMetaData;
        this.validator = new TableValidatorRules(tableMetaData);

    }

    /**
     * Open.
     *
     * @return the object
     */
    public Object open() {
        Display display = configureShell();

        Composite mainComposite = new Composite(currentShell, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        mainComposite.setLayoutData(mainCompositeGD);

        createDatatypeInfoGui(mainComposite);

        Composite btnComposite = new Composite(mainComposite, SWT.NONE);
        btnComposite.setLayout(new GridLayout(8, true));
        GridData btnCompositeGD = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        btnComposite.setLayoutData(btnCompositeGD);

        new Label(btnComposite, SWT.NONE).setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 6, 1));
        ;
        okButton = new Button(btnComposite, SWT.NONE);
        GridData okButtonGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        okButton.setLayoutData(okButtonGD);
        okButton.addSelectionListener(new OKBtnSelectionAdapter());
        okButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));

        cancelButton = new Button(btnComposite, SWT.NONE);
        GridData cancelButtonGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cancelButton.setLayoutData(cancelButtonGD);
        cancelButton.addSelectionListener(new CancelBtnSelectionAdapter());
        cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC));

        currentShell.open();
        boolean isDisposed = currentShell.isDisposed();
        while (!isDisposed) {
            if (display != null && !display.readAndDispatch()) {
                display.sleep();
            }
            isDisposed = currentShell.isDisposed();
        }

        return currentShell;
    }

    private Display configureShell() {
        Shell parent = getParent();
        Display display = parent.getDisplay();

        currentShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        currentShell.setLayout(new GridLayout(1, false));
        GridData currentShellGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        currentShell.setLayoutData(currentShellGD);
        currentShell.setSize(720, 235);

        /* Place the window in the centre of primary monitor */
        if (display != null) {
            Monitor primary = display.getPrimaryMonitor();

            Rectangle bounds = primary.getBounds();
            Rectangle rect = currentShell.getBounds();

            int xCordination = bounds.x + (bounds.width - rect.width) / 2;
            int yCordination = bounds.y + (bounds.height - rect.height) / 2;

            currentShell.setLocation(xCordination, yCordination);
        }

        currentShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_DATA_TYPE));
        currentShell.setImage(IconUtility.getIconImage(IiconPath.DATATYPE, getClass()));
        return display;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class OKBtnSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    protected class OKBtnSelectionAdapter extends SelectionAdapter {

        /**
         * Widget selected.
         *
         * @param event the event
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
            performOkButtonPressed();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CancelBtnSelectionAdapter.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class CancelBtnSelectionAdapter extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent event) {
            cancelPressed();
        }
    }

    /**
     * Creates the datatype info gui.
     *
     * @param compositeColumns the composite columns
     */
    public void createDatatypeInfoGui(Composite compositeColumns) {
        /**
         * STEP: 2 COLUMN CREATION
         */
        final Database db = tableMetaData.getNamespace().getDatabase();

        Label lblCurrentDataType = new Label(compositeColumns, SWT.NONE);
        GridData lblCurrentDataTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblCurrentDataTypeGD.horizontalIndent = 10;
        lblCurrentDataType.setLayoutData(lblCurrentDataTypeGD);

        Composite inputComp = new Composite(compositeColumns, SWT.NONE);
        inputComp.setLayout(new GridLayout(4, false));
        GridData inputCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        inputComp.setLayoutData(inputCompGD);

        createDatatypeSchemaComposite(inputComp, db);
        createDataTypeComposite(inputComp, db);
        createPrecisionComposite(inputComp);
        createScaleComposite(inputComp);

        createTypeDescriptorComposite(compositeColumns);
        createTypeConversionTipComposite(compositeColumns);

        comboClmDataSchema.addSelectionListener(addComboClmDataSchemaSelectionListener(db));
        comboClmDataType.addModifyListener(addComboClmDataTypeModifyListener(db));

        enableDisableSizeLenBefore(db);
        disableDataTypenCombo();

        if (null != columnMetaData.getDataType()) {
            if (columnMetaData.getDataType().getName().isEmpty()) {
                currentDatatypeName = MessageConfigLoader.getProperty(IMessagesConstants.UKNOWN_COMPLEX_DATATYPE);
            } else {
                currentDatatypeName = columnMetaData.getDisplayDatatype();
            }
            lblCurrentDataType.setText(MessageConfigLoader.getProperty(IMessagesConstants.CURRENT_DATA_TYPE,
                    columnMetaData.getName(), UIUtils.convertToDisplayDatatype(currentDatatypeName)));
            lblCurrentDataType.pack();
        }

    }

    private void createTypeDescriptorComposite(Composite compositeColumns) {
        Composite typeDescriptorComp = new Composite(compositeColumns, SWT.NONE);
        typeDescriptorComp.setLayout(new GridLayout(2, false));
        GridData typeDescriptorCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        typeDescriptorCompGD.horizontalIndent = 5;
        typeDescriptorComp.setLayoutData(typeDescriptorCompGD);

        Label lblTypeDescription = new Label(typeDescriptorComp, SWT.NONE);
        lblTypeDescription.setText(MessageConfigLoader.getProperty(IMessagesConstants.TYPE_DES));
        lblTypeDescription.pack();

        lblDescriptionOfDatatype = new Label(typeDescriptorComp, SWT.NONE);
        lblDescriptionOfDatatype.setBackground(FontAndColorUtility.getColor(SWT.COLOR_WHITE));
        GridData lblDescriptionOfDatatypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        lblDescriptionOfDatatype.setLayoutData(lblDescriptionOfDatatypeGD);
        lblDescriptionOfDatatype.setText("");
    }

    private void createTypeConversionTipComposite(Composite compositeColumns) {
        Label datatypeConversionTip = new Label(compositeColumns, SWT.NONE);
        GridData datatypeConversionTipGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        datatypeConversionTipGD.horizontalIndent = 10;
        datatypeConversionTip.setLayoutData(datatypeConversionTipGD);
        datatypeConversionTip.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_TYPE_CONVERSION_TIP));
        datatypeConversionTip.pack();
    }

    private void createScaleComposite(Composite compositeColumns) {
        Composite scaleComp = new Composite(compositeColumns, SWT.NONE);
        scaleComp.setLayout(new GridLayout(1, false));
        GridData scaleCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        scaleCompGD.verticalSpan = 2;
        scaleComp.setLayoutData(scaleCompGD);

        Label lblScale = new Label(scaleComp, SWT.NONE);
        lblScale.setText(MessageConfigLoader.getProperty(IMessagesConstants.SCALE_MSG));
        lblScale.pack();

        spinnerScale = new Spinner(scaleComp, SWT.BORDER);
        GridData spinnerScaleGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        spinnerScale.setLayoutData(spinnerScaleGD);
    }

    private void createPrecisionComposite(Composite compositeColumns) {
        Composite precisionSizeComp = new Composite(compositeColumns, SWT.NONE);
        precisionSizeComp.setLayout(new GridLayout(1, false));
        GridData precisionSizeCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        precisionSizeCompGD.verticalSpan = 2;
        precisionSizeComp.setLayoutData(precisionSizeCompGD);

        Label lblPre = new Label(precisionSizeComp, SWT.NONE);
        lblPre.setText(MessageConfigLoader.getProperty(IMessagesConstants.SIZE));
        lblPre.pack();

        spinnerPrevSize = new Spinner(precisionSizeComp, SWT.BORDER);
        GridData spinnerPrevSizeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        spinnerPrevSize.setLayoutData(spinnerPrevSizeGD);
        spinnerPrevSize.setMaximum(Integer.MAX_VALUE);
    }

    private void createDataTypeComposite(Composite compositeColumns, final Database db) {
        Composite dataTypeComp = new Composite(compositeColumns, SWT.NONE);
        dataTypeComp.setLayout(new GridLayout(1, false));
        GridData dataTypeCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        dataTypeCompGD.verticalSpan = 2;
        dataTypeComp.setLayoutData(dataTypeCompGD);

        Label lblDataType = new Label(dataTypeComp, SWT.NONE);
        lblDataType.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_TYPE));
        lblDataType.pack();

        comboClmDataType = new Combo(dataTypeComp, SWT.READ_ONLY);
        GridData comboClmDataTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        comboClmDataType.setLayoutData(comboClmDataTypeGD);
        TypeMetaData currDt = columnMetaData.getDataType();
        UIUtils.displayDatatypeList(validator.getDataTypeList(db, true), currDt, comboClmDataType);
    }

    private void createDatatypeSchemaComposite(Composite compositeColumns, final Database db) {
        Composite dataTypeSchemaComp = new Composite(compositeColumns, SWT.NONE);
        dataTypeSchemaComp.setLayout(new GridLayout(1, false));
        GridData dataTypeSchemaCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        dataTypeSchemaCompGD.verticalSpan = 2;
        dataTypeSchemaComp.setLayoutData(dataTypeSchemaCompGD);

        Label lblSchema1 = new Label(dataTypeSchemaComp, SWT.NONE);
        lblSchema1.setText(MessageConfigLoader.getProperty(IMessagesConstants.DTYPE_SCHEMA));
        lblSchema1.pack();

        comboClmDataSchema = new Combo(dataTypeSchemaComp, SWT.READ_ONLY | SWT.BORDER);
        GridData comboClmDataSchemaGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        comboClmDataSchema.setLayoutData(comboClmDataSchemaGD);

        final String consCurrNs = "DEFAULT";
        // Need to get the list of schemas available from GAUSS DB
        UIUtils.displayDatatypeSchemaList(tableMetaData.getNamespace().getDatabase(), consCurrNs, comboClmDataSchema,
                true);
    }

    private ModifyListener addComboClmDataTypeModifyListener(final Database db) {
        return new ModifyListener() {

            /**
             * Modify text.
             *
             * @param event the event
             */
            public void modifyText(ModifyEvent event) {
                // enable & disable the data type items
                getEnableDisableSizeLen(db);
            }

        };
    }

    private SelectionAdapter addComboClmDataSchemaSelectionListener(final Database db) {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Namespace dtypeNamespace = UIUtils.getNamespaceForDatatype(db, comboClmDataSchema);
                if (null != dtypeNamespace) {
                    UIUtils.displayDatatypeList(dtypeNamespace.getTypes().getList(), null, comboClmDataType);
                } else {
                    UIUtils.displayDatatypeList(validator.getDataTypeList(db, true), null, comboClmDataType);
                }
            }
        };
    }

    /**
     * Cancel pressed.
     */
    public void cancelPressed() {
    }

    /**
     * Perform ok button pressed.
     */
    public void performOkButtonPressed() {

    }

    /**
     * Set previous values for precisuion/size of column data type Added for
     * DTS2016020407889 fix.
     *
     * @param db the db
     */
    /*
     * this method is called when UI is being created
     */
    private void enableDisableSizeLenBefore(Database db) {

        Namespace selectedNS = UIUtils.getNamespaceForDatatype(db, comboClmDataSchema);
        TypeMetaData type = UIUtils.getDtypeFromCombo(selectedNS, db, comboClmDataType);

        if (null == type || null == spinnerPrevSize || null == spinnerScale) {
            lblDescriptionOfDatatype.setText("");
            return;
        }

        spinnerPrevSize.setEnabled(UIUtils.enableDisablePrecisionFieldForDatatype(type.getName()));
        spinnerScale.setEnabled(UIUtils.enableDisableScaleFieldForDatatype(type.getName()));

        /*
         * when dialog is opened the saved value should be displayed
         */

        spinnerPrevSize.setSelection(columnMetaData.getLenOrPrecision());

        spinnerScale.setSelection(columnMetaData.getScale());

        lblDescriptionOfDatatype.setText(type.getDescription() == null ? "" : type.getDescription());
    }

    /**
     * Gets the enable disable size len.
     *
     * @param db the db
     * @return the enable disable size len
     */
    /*
     * this method is used when user changes the datatype of a column
     */
    private void getEnableDisableSizeLen(Database db) {

        Namespace selectedNamespace = UIUtils.getNamespaceForDatatype(db, comboClmDataSchema);
        TypeMetaData type = UIUtils.getDtypeFromCombo(selectedNamespace, db, comboClmDataType);

        if (lblDescriptionOfDatatype != null && (null == type || null == spinnerPrevSize || null == spinnerScale)) {
            lblDescriptionOfDatatype.setText("");
            return;
        }

        /*
         * we have not mapped the data types of information_schema and
         * pg_catalog hence the check is to avoid null pointer exception
         */
        if (selectedNamespace == null || (!"information_schema".equals(selectedNamespace.getName())
                && !"pg_catalog".equals(selectedNamespace.getName()))) {
            spinnerPrevSize.setEnabled(UIUtils.enableDisablePrecisionFieldForDatatype(type.getName()));
            spinnerScale.setEnabled(UIUtils.enableDisableScaleFieldForDatatype(type.getName()));
        } else {
            spinnerPrevSize.setEnabled(false);
            spinnerScale.setEnabled(false);
        }
        /*
         * scale and precision should be reset to 0 when user changes the
         * datatype
         */
        spinnerPrevSize.setSelection(0);
        spinnerScale.setSelection(0);
        if (lblDescriptionOfDatatype != null) {
            lblDescriptionOfDatatype.setText(type.getDescription() == null ? "" : type.getDescription());
        }
    }

    /**
     * Gets the DB column.
     *
     * @return the DB column
     */
    public void getDBColumn() {
        Database db = columnMetaData.getParentDB();
        Namespace selectedNS = UIUtils.getNamespaceForDatatype(db, comboClmDataSchema);
        TypeMetaData type = UIUtils.getDtypeFromCombo(selectedNS, db, comboClmDataType);

        columnMetaData.setDataType(type);

        if (0 != comboClmDataSchema.getSelectionIndex()) {
            columnMetaData.setDataTypeScheam(comboClmDataSchema.getText());
        }

        if (spinnerPrevSize != null && spinnerScale != null) {
            columnMetaData.setPre(spinnerPrevSize.getSelection(), spinnerScale.getSelection());
        }
    }

    /**
     * Enable button.
     */
    public void enableButton() {
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Dispose.
     */
    public void dispose() {
        currentShell.dispose();
    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    public boolean isDisposed() {
        return currentShell.isDisposed();
    }

    /**
     * Disable data typen combo.
     */
    private void disableDataTypenCombo() {
        if (!(validator.enableDisable())) {
            comboClmDataSchema.setEnabled(false);
            comboClmDataSchema.select(0);
            comboClmDataType.select(0);
        }
    }

    /**
     * Disable OK button.
     */
    public void disableOKButton() {
        okButton.setEnabled(false);
    }

    /**
     * Enable cancel button.
     */
    public void enableCancelButton() {
        cancelButton.setEnabled(true);
    }

    /**
     * Close.
     */
    public void close() {
        currentShell.close();
    }

    /**
     * Cancel.
     */
    public void cancel() {
    }

}
