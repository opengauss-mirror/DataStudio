/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.command.EditCellCommand;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter;
import com.huawei.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.component.grid.core.DSNewTextCellEditor;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.uidisplay.UIDisplayUtil;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class ParameterInputDialog.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ParameterInputDialog extends ExecDialog {
    private static final String PARAMETER_NAME_LABEL = "parameterNameLabel";
    private static final String PARAMETER_TYPE_LABEL = "parameterTypeLabel";

    /**
     * The Constant PARAMETER_VALUE_LABEL.
     */
    public static final String PARAMETER_VALUE_LABEL = "parameterValueLabel";
    private static final String EXEC = "EXEC ";
    private static final String SELECT = "SELECT ";

    private IDebugObject debugObject;
    private Server server;
    private long debugObjectOid;

    private NatTable parameterInputNatTable;
    private Button okBtn;
    private Button cancelBtn;
    private Button clearBtn;

    private ArrayList<DefaultParameter> valueList = new ArrayList<>();

    /**
     * Instantiates a new parameter input dialog.
     *
     * @param parentShell the parent shell
     * @param isDebug the is debug
     */
    protected ParameterInputDialog(Shell parentShell) {
        super(parentShell);
        this.setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Shell shell = getShell();
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE));

        Composite container = new Composite(parent, SWT.BORDER);
        container.setLayout(new GridLayout(1, false));
        GridData containerGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        container.setLayoutData(containerGD);

        DefaultGridLayer gridLayer = getGridLayer(valueList);
        // automation need select first record of Value column
        gridLayer.getBodyLayer().getSelectionLayer().setSelectedCell(2, 0);
        parameterInputNatTable = new NatTable(container, gridLayer, false);
        parameterInputNatTable.setLayout(new GridLayout(1, false));
        GridData parameterInputNatTableGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        parameterInputNatTableGD.widthHint = 565;
        parameterInputNatTableGD.heightHint = 220;
        parameterInputNatTable.setLayoutData(parameterInputNatTableGD);
        parameterInputNatTable.addConfiguration(new DefaultNatTableStyleConfiguration());
        parameterInputNatTable.addConfiguration(new ParameterInputRegistryConfiguration());
        parameterInputNatTable.configure();
        super.buttonBar = createButtonBar(shell);
        // automation need focus
        parameterInputNatTable.setFocus();
        return container;
    }

    /**
     * Default parameter values.
     */
    public void defaultParameterValues() {
        List<ObjectParameter> parameters = this.debugObject.getTemplateParameters();
        HashMap<Long, ArrayList<DefaultParameter>> valueMap = server.getDefaulParametertMap();

        // no cache for parameter value
        if (valueMap == null || valueMap.get(this.debugObjectOid) == null) {
            parameters.stream().forEach(param -> valueList.add(
                    new DefaultParameter(param.getName(), param.getDataType(), param.getValue(), param.getType())));
        } else {
            ArrayList<DefaultParameter> oldValueList = valueMap.get(this.debugObjectOid);
            ArrayList<DefaultParameter> cloneValueList = new ArrayList<DefaultParameter>(oldValueList.size());
            oldValueList.stream()
                    .forEach(oldValue -> cloneValueList.add(
                            new DefaultParameter(oldValue.getDefaultParameterName(), oldValue.getDefaultParameterType(),
                                    oldValue.getDefaultParameterValue(), oldValue.getDefaultParameterMode())));
            valueList = cloneValueList;
        }
    }

    /**
     * Creates the button bar.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(4, false));
        GridData compositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite.setLayoutData(compositeGD);
        composite.setFont(parent.getFont());

        // Add buttons to the button bar.
        createButtonsForButtonBar(composite);
        return composite;
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        String tab = "     ";
        String okLabel = tab + MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK) + tab;
        String claerLabel = tab + MessageConfigLoader.getProperty(IMessagesConstants.BTN_CLEAR) + tab;
        String cancelLabel = tab + MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL) + tab;

        Label outModeParamHintLabel = new Label(parent, SWT.NONE);
        GridData outModeParamHintLabelGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        outModeParamHintLabel.setLayoutData(outModeParamHintLabelGD);
        outModeParamHintLabel.setText(this.debugObject.getUsagehint());
        outModeParamHintLabel.setFont(JFaceResources.getTextFont());

        clearBtn = createButton(parent, UIConstants.CLEAR_ID, claerLabel, 310);
        okBtn = createButton(parent, UIConstants.OK_ID, okLabel, 400);
        cancelBtn = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, 490);

    }

    /**
     * Creates the button.
     *
     * @param parent the parent
     * @param id the id
     * @param label the label
     * @param space the space
     * @return the button
     */
    protected Button createButton(Composite parent, int id, String label, int space) {
        Button button = new Button(parent, SWT.PUSH);
        GridData buttonGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        button.setLayoutData(buttonGD);
        button.setText(label);
        button.setFont(JFaceResources.getDialogFont());
        button.setData(Integer.valueOf(id));
        setButtonLayoutData(button);

        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                buttonPressed(((Integer) event.widget.getData()).intValue());
            }
        });

        return button;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {

        if (UIConstants.OK_ID == buttonId) {
            okBtn.setEnabled(false);
            cancelBtn.setEnabled(false);
            clearBtn.setEnabled(false);

            server.setDefaulParametertMap(this.debugObjectOid, valueList);

            this.executePressed();

            close();
        } else if (UIConstants.CANCEL_ID == buttonId) {
            this.cancelPressedValue();
        } else if (UIConstants.CLEAR_ID == buttonId) {
            clearPressed();

        }
    }

    /**
     * Sets the debug object.
     *
     * @param debugObj the debug obj
     * @return true, if successful
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public boolean setDebugObject(IDebugObject debugObj) throws DatabaseOperationException, DatabaseCriticalException {
        this.debugObject = debugObj;
        this.debugObjectOid = debugObj.getOid();
        this.server = debugObject.getDatabase().getServer();

        return super.setDebugObject(debugObj);
    }

    /**
     * Execute pressed.
     */
    @Override
    public void executePressed() {
        Database db = this.debugObject.getDatabase();

        String executionStatement = "";
        try {
            executionStatement = getProcedureExecutionStatement();
        } catch (DatabaseOperationException exception) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                    .getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD, exception.getMessage())));
            MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTE_DEBUGE),
                    MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD,
                            exception.getServerMessage()),
                    exception);
            UIDisplayUtil.getDebugConsole()
                    .logError(MessageConfigLoader.getProperty(IMessagesConstants.OPERATION_CANNOT_BE_PERFOREMD)
                            + exception.getMessage());
            return;
        }

        SQLTerminal terminal = null;
        // Create new terminal to execute the debug object if it is not current
        // terminal
        if (!debugObject.getCurrentTerminal()) {
            terminal = UIElement.getInstance().createNewTerminal(db);

        } else {
            terminal = UIElement.getInstance().getVisibleTerminal();
        }

        if (terminal == null) {
            return;
        }

        ArrayList<DefaultParameter> inOutParaValueList = new ArrayList<DefaultParameter>();
        ArrayList<DefaultParameter> outParaValueList = this.debugObject.getOutParameters();

        inOutParaValueList.addAll(valueList);
        if (outParaValueList != null) {
            inOutParaValueList.addAll(outParaValueList);
        }
        debugObject.setIsEditTerminalInputValues(true);
        terminal.setDebugObject(debugObject);
        terminal.setInputDailogValueTerminal(inOutParaValueList);
        terminal.getTerminalCore().getDocument().set(executionStatement);
        if (valueList != null && !valueList.isEmpty()) {
            terminal.enableEditInputValueButton(true);
        }

        terminal.getTerminalCore().setEditable(false);
        terminal.handleExecution();
    }

    /**
     * Clear pressed.
     */
    private void clearPressed() {
        valueList.stream().forEach(param -> param.setDefaultParameterValue(""));
        parameterInputNatTable.refresh();
    }

    /**
     * Gets the procedure execution statement.
     *
     * @return the procedure execution statement
     * @throws DatabaseOperationException the database operation exception
     */
    private String getProcedureExecutionStatement() throws DatabaseOperationException {
        String sqlString = "";
        sqlString = generateExecutionTemplate(debugObject.getNamespace(), debugObject.getName(),
                parameterInputNatTable == null ? null : server.getDefaulParametertMap().get(this.debugObjectOid),
                debugObject.getType());
        return sqlString;
    }

    /**
     * Cancel pressed value.
     */
    protected void cancelPressedValue() {
        this.getShell().close();
    }

    /**
     * Generate execution template.
     *
     * @param nameSpace the name space
     * @param dbgObjName the dbg obj name
     * @param params the params
     * @param type the type
     * @return the string
     * @throws DatabaseOperationException the database operation exception
     */
    public String generateExecutionTemplate(INamespace nameSpace, String dbgObjName, ArrayList<DefaultParameter> params,
            OBJECTTYPE type) throws DatabaseOperationException {
        StringBuilder template = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        template.append(SELECT);

        if (nameSpace != null) {
            template.append(nameSpace.getQualifiedObjectName()).append(".");
        }

        template.append(ServerObject.getQualifiedObjectName(dbgObjName));

        template.append(MPPDBIDEConstants.LINE_SEPARATOR);
        template.append(MPPDBIDEConstants.LEFT_PARENTHESIS);
        template.append(MPPDBIDEConstants.LINE_SEPARATOR);
        createTemplateForParams(params, template);

        template.append(MPPDBIDEConstants.LINE_SEPARATOR);
        template.append(MPPDBIDEConstants.RIGHT_PARENTHESIS);
        template.append(MPPDBIDEConstants.LINE_SEPARATOR);

        return template.toString();
    }

    /**
     * Creates the template for params.
     *
     * @param params the params
     * @param template the template
     * @throws DatabaseOperationException the database operation exception
     */
    private void createTemplateForParams(ArrayList<DefaultParameter> params, StringBuilder template)
            throws DatabaseOperationException {
        if (null != params) {
            boolean isFirstParam = true;

            String argType = null;
            String argName = null;
            String argValue = null;

            DefaultParameter param = null;
            int paramSize = params.size();

            for (int cnt = 0; cnt < paramSize; cnt++) {
                param = params.get(cnt);

                if (PARAMETERTYPE.IN.equals(param.getDefaultParameterMode())
                        || PARAMETERTYPE.INOUT.equals(param.getDefaultParameterMode())) {
                    if (!isFirstParam) {
                        template.append(MPPDBIDEConstants.LINE_SEPARATOR);
                    }

                    argType = param.getDefaultParameterType();
                    argName = param.getDefaultParameterName();
                    argValue = param.getDefaultParameterValue() == null ? "" : param.getDefaultParameterValue();

                    if (null != argType) {
                        if ("refcursor".equals(argType)) {
                            MPPDBIDELoggerUtility.error(MessageConfigLoader
                                    .getProperty(IMessagesConstants.ERR_BL_REFCUR_EXECUTION_TEMPLATE_FAILURE));
                            throw new DatabaseOperationException(
                                    IMessagesConstants.ERR_BL_REFCUR_EXECUTION_TEMPLATE_FAILURE);
                        } else {
                            template.append(MPPDBIDEConstants.TAB);
                            template.append(argValue);
                            if (cnt < paramSize - 1) {
                                template.append(MPPDBIDEConstants.COMMA_SEPARATE);
                            }
                            template.append(MPPDBIDEConstants.TAB);
                            template.append(MPPDBIDEConstants.SEPARATOR);
                            template.append(MPPDBIDEConstants.SEPARATOR);
                            template.append(argName);
                            template.append(MPPDBIDEConstants.SPACE_CHAR);
                            template.append(argType);
                        }
                    }

                    isFirstParam = false;
                }
            }
        }
    }

    /**
     * Register editors.
     *
     * @param configRegistry the config registry
     */
    private void registerEditors(IConfigRegistry configRegistry) {
        EditorText editorText = new EditorText();
        editorText.registerColumnThreeTextEditor(configRegistry);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ParameterInputRegistryConfiguration.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class ParameterInputRegistryConfiguration extends AbstractRegistryConfiguration {

        @Override
        public void configureRegistry(IConfigRegistry configRegistry) {

            Style cellStyle = new Style();
            cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle);
            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                    IEditableRule.ALWAYS_EDITABLE, DisplayMode.EDIT, PARAMETER_VALUE_LABEL);

            registerEditors(configRegistry);
        }

        @Override
        public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
            parameterInputNatTable.unregisterCommandHandler(EditCellCommand.class);
            parameterInputNatTable.registerCommandHandler(new TableCellCommand());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EditorText.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class EditorText {

        /**
         * Register column three text editor.
         *
         * @param configRegistry the config registry
         */
        private void registerColumnThreeTextEditor(IConfigRegistry configRegistry) {
            // configure the multi line text editor for parameter value column
            configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,

                    new DSNewTextCellEditor(false), DisplayMode.EDIT, PARAMETER_VALUE_LABEL);
        }
    }

    /**
     * Gets the grid layer.
     *
     * @param valueList the value list
     * @return the grid layer
     */
    private DefaultGridLayer getGridLayer(ArrayList<DefaultParameter> valueList) {
        final String parameterName = "defaultParameterName";
        final String parameterType = "defaultParameterType";
        final String parameterValue = "defaultParameterValue";

        Map<String, String> propertyToLabels = new HashMap<String, String>();
        propertyToLabels.put(parameterName,
                MessageConfigLoader.getProperty(IMessagesConstants.PARAMETER_INPUT_TABLE_PARAM_NAME_COLUMN));
        propertyToLabels.put(parameterType,
                MessageConfigLoader.getProperty(IMessagesConstants.PARAMETER_INPUT_TABLE_PARAM_TYPE_COLUMN));
        propertyToLabels.put(parameterValue,
                MessageConfigLoader.getProperty(IMessagesConstants.PARAMETER_INPUT_TABLE_PARAM_VALUE_COLUMN));

        String[] propertyNames = new String[] {parameterName, parameterType, parameterValue};

        DefaultGridLayer gridLayer = new DefaultGridLayer(valueList, propertyNames, propertyToLabels);
        DataLayer bodyDataLayer = (DataLayer) gridLayer.getBodyDataLayer();
        // When too many parameters, ensure that won't appear around the slider
        int cloumnWidth = 0;
        // Number of list lines
        int rows = 9;
        if (valueList.size() > rows) {
            cloumnWidth = 323;
        } else {
            cloumnWidth = 340;
        }
        bodyDataLayer.setColumnWidthByPosition(2, cloumnWidth, true);

        final ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyDataLayer);
        bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);
        registerColumnLabels(columnLabelAccumulator);

        gridLayer.registerCommandHandler(new CopyDataCommandHandler(gridLayer.getBodyLayer().getSelectionLayer()) {
            @Override
            public boolean doCommand(CopyDataToClipboardCommand cmd) {
                ILayerCell[][] assembleCopiedDataStruct = assembleCopiedDataStructure();
                if (null == assembleCopiedDataStruct) {
                    return true;
                } else {
                    internalDoCommand(cmd, assembleCopiedDataStruct);
                }
                return true;
            }
        });

        return gridLayer;
    }

    /**
     * Register column labels.
     *
     * @param columnLabelAccumulator the column label accumulator
     */
    private void registerColumnLabels(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
        columnLabelAccumulator.registerColumnOverrides(0, PARAMETER_NAME_LABEL);
        columnLabelAccumulator.registerColumnOverrides(1, PARAMETER_TYPE_LABEL);
        columnLabelAccumulator.registerColumnOverrides(2, PARAMETER_VALUE_LABEL);
    }
}
