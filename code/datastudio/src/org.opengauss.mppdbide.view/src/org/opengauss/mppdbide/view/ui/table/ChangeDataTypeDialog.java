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

package org.opengauss.mppdbide.view.ui.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableValidatorRules;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.FontAndColorUtility;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeDataTypeDialog.
 *
 * @since 3.0.0
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

    private Group grpValues;

    private Table tblValues;
    private Button editValues;
    
    private StyledText txtUserValue;

    private ArrayList<String> setOrEnumValues;
    private HashSet<String> setOrEnumList;

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
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);

        createDatatypeInfoGui(mainComposite);

        Composite btnComposite = new Composite(mainComposite, SWT.BOTTOM);
        btnComposite.setLayout(new GridLayout(8, true));
        GridData btnCompositeGD = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        btnCompositeGD.heightHint = 40;
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
        currentShell.setSize(720, 345);

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
        GridData lblCurrentDataTypeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        lblCurrentDataTypeGD.horizontalIndent = 10;
        lblCurrentDataTypeGD.heightHint = 20;
        lblCurrentDataType.setLayoutData(lblCurrentDataTypeGD);

        Composite inputComp = new Composite(compositeColumns, SWT.NONE);
        inputComp.setLayout(new GridLayout(4, false));
        GridData inputCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        inputComp.setLayoutData(inputCompGD);
        inputCompGD.heightHint = 100;

        createDatatypeSchemaComposite(inputComp, db);
        createDataTypeComposite(inputComp, db);
        createPrecisionComposite(inputComp);
        createScaleComposite(inputComp);

        createTypeDescriptorComposite(compositeColumns);
        createTypeConversionTipComposite(compositeColumns);
        addDisplayEditValuesButton(compositeColumns);
        createColumnOrExpr(compositeColumns);

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
        GridData typeDescriptorCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        typeDescriptorCompGD.horizontalIndent = 5;
        typeDescriptorComp.setLayoutData(typeDescriptorCompGD);
        typeDescriptorCompGD.heightHint = 40;

        Label lblTypeDescription = new Label(typeDescriptorComp, SWT.NONE);
        lblTypeDescription.setText(MessageConfigLoader.getProperty(IMessagesConstants.TYPE_DES));
        lblTypeDescription.pack();

        lblDescriptionOfDatatype = new Label(typeDescriptorComp, SWT.NONE);
        lblDescriptionOfDatatype.setBackground(FontAndColorUtility.getColor(SWT.COLOR_WHITE));
        GridData lblDescriptionOfDatatypeGD = new GridData(SWT.FILL, SWT.NONE, true, false);
        lblDescriptionOfDatatypeGD.heightHint = 40;
        lblDescriptionOfDatatype.setLayoutData(lblDescriptionOfDatatypeGD);
        lblDescriptionOfDatatype.setText("");
    }

    private void createTypeConversionTipComposite(Composite compositeColumns) {
        Label datatypeConversionTip = new Label(compositeColumns, SWT.NONE);
        GridData datatypeConversionTipGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        datatypeConversionTipGD.horizontalIndent = 10;
        datatypeConversionTipGD.heightHint = 30;
        datatypeConversionTip.setLayoutData(datatypeConversionTipGD);
        datatypeConversionTip.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_TYPE_CONVERSION_TIP));
        datatypeConversionTip.pack();
    }

    private void createScaleComposite(Composite compositeColumns) {
        Composite scaleComp = new Composite(compositeColumns, SWT.NONE);
        scaleComp.setLayout(new GridLayout(1, false));
        GridData scaleCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        scaleCompGD.heightHint = 30;
        scaleCompGD.verticalSpan = 2;
        scaleComp.setLayoutData(scaleCompGD);

        Label lblScale = new Label(scaleComp, SWT.NONE);
        lblScale.setText(MessageConfigLoader.getProperty(IMessagesConstants.SCALE_MSG));
        lblScale.pack();

        spinnerScale = new Spinner(scaleComp, SWT.BORDER);
        GridData spinnerScaleGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        spinnerScale.setLayoutData(spinnerScaleGD);
    }

    private void createPrecisionComposite(Composite compositeColumns) {
        Composite precisionSizeComp = new Composite(compositeColumns, SWT.NONE);
        precisionSizeComp.setLayout(new GridLayout(1, false));
        GridData precisionSizeCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        precisionSizeCompGD.heightHint = 30;
        precisionSizeCompGD.verticalSpan = 2;
        precisionSizeComp.setLayoutData(precisionSizeCompGD);

        Label lblPre = new Label(precisionSizeComp, SWT.NONE);
        lblPre.setText(MessageConfigLoader.getProperty(IMessagesConstants.SIZE));
        lblPre.pack();

        spinnerPrevSize = new Spinner(precisionSizeComp, SWT.BORDER);
        GridData spinnerPrevSizeGD = new GridData(SWT.FILL, SWT.FILL, true, false);
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

        spinnerPrevSize.setEnabled(UIUtils.enableDisablePrecisionFieldForDatatype(type.getName(), db.getDolphinTypes()));
        spinnerScale.setEnabled(UIUtils.enableDisableScaleFieldForDatatype(type.getName(), db.getDolphinTypes()));

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
            spinnerPrevSize.setEnabled(UIUtils.enableDisablePrecisionFieldForDatatype(type.getName(), db.getDolphinTypes()));
            spinnerScale.setEnabled(UIUtils.enableDisableScaleFieldForDatatype(type.getName(), db.getDolphinTypes()));
            if ("set".equals(type.getName()) || "enum".equals(type.getName())) {
                editValues.setVisible(true);
            } else {
                grpValues.setVisible(false);
                editValues.setVisible(false);
            }
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

        if ("set".equals(type.getName()) || "enum".equals(type.getName())) {
            columnMetaData.setEnumOrSetValues(new ArrayList<String>(this.setOrEnumValues));
            columnMetaData.setEnumOrSetList(new HashSet<String>(this.setOrEnumList));
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
     * Disable data type combo.
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

    /**
     * Creates the column or expr.
     *
     * @param comp the comp
     */
    private void createColumnOrExpr(Composite comp) {
        grpValues = new Group(comp, SWT.FILL);
        grpValues.setLayout(new GridLayout(5, false));
        GridData groupGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        groupGD.heightHint = 10;
        groupGD.horizontalSpan = 4;
        grpValues.setLayoutData(groupGD);

        createUserDefComposite();
        createAddRemoveValueComposite();
        createValueTable(grpValues);
        createUpDownValueComposite();
        createFinishComposite();
        grpValues.setVisible(false);
    }

    private void createUserDefComposite() {
        Composite userValueComp = new Composite(this.grpValues, SWT.FILL);
        userValueComp.setLayout(new GridLayout(1, false));
        GridData avalColsOrUserExpCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        userValueComp.setLayoutData(avalColsOrUserExpCompGD);

        Composite userExprComposite = new Composite(userValueComp, SWT.FILL);
        userExprComposite.setLayout(new GridLayout(1, false));
        GridData userExprCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        userExprComposite.setLayoutData(userExprCompositeGD);
        userExprComposite.setSize(50, 30);

        Label lblUserDefinedValue = new Label(userExprComposite, SWT.FILL);
        lblUserDefinedValue.setText(MessageConfigLoader.getProperty(IMessagesConstants.SET_ENUM_UI_USER_VALUE));
        lblUserDefinedValue.pack();

        txtUserValue = new StyledText(userExprComposite, SWT.BORDER);
        txtUserValue.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TXT_INDEXUI_USEREXPR_001");
        GridData txtUserExprGD = new GridData(SWT.FILL, SWT.NONE, true, true);
        txtUserExprGD.heightHint = 30;
        txtUserValue.setLayoutData(txtUserExprGD);

        setOrEnumList = new HashSet<String>();
        setOrEnumValues = new ArrayList<String>();
    }

    private void createAddRemoveValueComposite() {
        Composite addRemoveValueComposite = new Composite(this.grpValues, SWT.NONE);
        addRemoveValueComposite.setLayout(new GridLayout(1, false));
        GridData addRemoveIndexCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        addRemoveIndexCompositeGD.verticalAlignment = SWT.CENTER;
        addRemoveValueComposite.setLayoutData(addRemoveIndexCompositeGD);

        createAddToValue(addRemoveValueComposite);
        createRemoveFromIndex(addRemoveValueComposite);
    }
    /**
     * Creates the add to index.Add/Remove/MoveUp/MoveDown Buttons
     *
     * @param comp the comp
     */
    private void createAddToValue(Composite comp) {
        Button addToIndex = new Button(comp, SWT.ARROW | SWT.RIGHT);
        GridData addToIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        addToIndexGD.heightHint = 20;
        addToIndex.setLayoutData(addToIndexGD);
        addToIndex.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_ADD_TO_INDEX_001");
        addToIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_ADD_TO));
        addToIndex.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String userExpr = txtUserValue.getText().trim();
                if (!"".equals(userExpr)) {
                    if (!setOrEnumList.contains(userExpr)) {
                        setOrEnumList.add(userExpr);
                        setOrEnumValues.add(userExpr);
                        repopulateValueCols();
                        txtUserValue.setText("");
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }
    
    public void repopulateValueCols() {
        TableItem item = null;
        tblValues.removeAll();
        Iterator<String> valuesItr = this.setOrEnumValues.iterator();
        boolean hasNext = valuesItr.hasNext();
        String value = null;

        while (hasNext) {
            value = valuesItr.next();
            item = new TableItem(tblValues, SWT.NONE);
            item.setText(value);
            hasNext = valuesItr.hasNext();
        }
    }


    private void createRemoveFromIndex(Composite comp) {
        Button removeFromIndex = new Button(comp, SWT.ARROW | SWT.LEFT);
        GridData removeFromIndexGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        removeFromIndexGD.heightHint = 20;
        removeFromIndex.setLayoutData(removeFromIndexGD);
        removeFromIndex.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_REMOVE_FROM_INDEX_001");
        removeFromIndex.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_REMOVE));
        removeFromIndex.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblValues.getSelectionIndex();
                if (selectedIdx > -1) {
                    String expr = setOrEnumValues.get(selectedIdx);
                    setOrEnumValues.remove(selectedIdx);
                    tblValues.remove(selectedIdx);
                    setOrEnumList.remove(expr);
                    txtUserValue.setText(expr);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent eevent) {
                // Nothing to do.
            }
        });
    }

    private void createValueTable(Composite comp) {
        tblValues = new Table(comp, SWT.BORDER | SWT.FULL_SELECTION);
        tblValues.setLayout(new GridLayout(1, false));
        GridData tblIndexColsGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        tblIndexColsGD.horizontalIndent = 5;
        tblValues.setLayoutData(tblIndexColsGD);
        tblValues.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_INDEXUI_INDEX_COLS_001");
        tblValues.setLinesVisible(true);
        tblValues.setHeaderVisible(true);

        ControlDecoration decofk = new ControlDecoration(tblValues, SWT.TOP | SWT.LEFT);

        // use an existing image
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

        // set description and image
        decofk.setDescriptionText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MSG));
        decofk.setImage(image);

        TableColumn tblclmnIndexColumns = new TableColumn(tblValues, SWT.FILL);
        tblclmnIndexColumns.setWidth(185);
        tblclmnIndexColumns.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_COL_INDEXUI_INDEX_COLS_001");
        tblclmnIndexColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.SET_ENUM_UI_USER_VALUE));

    }

    private void createFinishComposite() {
        Composite finishValueComposite = new Composite(this.grpValues, SWT.NONE);
        finishValueComposite.setLayout(new GridLayout(1, false));
        GridData finishValueCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        finishValueCompositeGD.verticalAlignment = SWT.CENTER;
        finishValueComposite.setLayoutData(finishValueCompositeGD);

        Button finish = new Button(finishValueComposite, SWT.NONE);
        GridData finishGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        finishGD.heightHint = 20;
        finish.setLayoutData(finishGD);
        finish.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_UP_INDEX_001");
        finish.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_FINISH_BTN));
        finish.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                grpValues.setVisible(false);
                grpValues.setSize(0,0);
                editValues.setVisible(true);
                currentShell.setSize(720, 345);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }

    private void createUpDownValueComposite() {
        Composite upDownValueComposite = new Composite(this.grpValues, SWT.NONE);
        upDownValueComposite.setLayout(new GridLayout(1, false));
        GridData upDownIndexCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        upDownIndexCompositeGD.verticalAlignment = SWT.CENTER;
        upDownValueComposite.setLayoutData(upDownIndexCompositeGD);

        createValueMoveUp(upDownValueComposite);
        createValueMoveDown(upDownValueComposite);
    }

    private void createValueMoveUp(Composite comp) {
        Button moveUp = new Button(comp, SWT.ARROW | SWT.UP);
        GridData moveUpGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        moveUpGD.heightHint = 20;
        moveUp.setLayoutData(moveUpGD);
        moveUp.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_UP_INDEX_001");
        moveUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MOVE_UP));
        moveUp.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblValues.getSelectionIndex();
                if (selectedIdx > 0) {
                    String value = setOrEnumValues.get(selectedIdx);
                    setOrEnumValues.remove(selectedIdx);
                    setOrEnumValues.add(selectedIdx - 1, value);
                    repopulateValueCols();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }

    private void createValueMoveDown(Composite comp) {
        Button moveDown = new Button(comp, SWT.ARROW | SWT.DOWN);
        GridData moveDownGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        moveDownGD.heightHint = 20;
        moveDown.setLayoutData(moveDownGD);
        moveDown.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_DOWN_INDEX_001");
        moveDown.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_MOVE_DOWN));
        moveDown.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selectedIdx = tblValues.getSelectionIndex();
                if (selectedIdx > -1 && selectedIdx < (tblValues.getItemCount() - 1)) {
                    String value = setOrEnumValues.get(selectedIdx);
                    setOrEnumValues.remove(selectedIdx);
                    setOrEnumValues.add(selectedIdx + 1, value);
                    repopulateValueCols();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do

            }
        });
    }

    /**
     * 
     * @param compositeColumns
     */
    private void addDisplayEditValuesButton(Composite compositeColumns) {
        Composite editValuesComposite = new Composite(compositeColumns, SWT.FILL);
        editValuesComposite.setLayout(new GridLayout(1, false));
        editValues = new Button(editValuesComposite, SWT.NONE);
        GridData editValuesCompositeGD = new GridData(SWT.NONE, SWT.NONE, true, false);
        editValuesCompositeGD.horizontalAlignment = SWT.RIGHT;
        editValuesCompositeGD.heightHint = 20;
        editValuesCompositeGD.widthHint = 100;
        editValues.setLayoutData(editValuesCompositeGD);
        editValues.setSize(20, 100);
        editValues.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BTN_INDEXUI_MOVE_UP_INDEX_001");
        editValues.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_SETTING_VALUE));
        editValues.setVisible(false);
        editValues.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                grpValues.setSize(720, 200);
                grpValues.setVisible(true);
                editValues.setVisible(false);
                currentShell.setSize(720, 525);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {
                // Nothing to do
            }
        });
    }
}
