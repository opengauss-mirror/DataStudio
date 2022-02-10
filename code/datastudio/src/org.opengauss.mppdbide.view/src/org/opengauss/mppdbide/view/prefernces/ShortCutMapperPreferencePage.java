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

package org.opengauss.mppdbide.view.prefernces;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.util.TableViewerUtil;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShortCutMapperPreferencePage.
 *
 * @since 3.0.0
 */
public class ShortCutMapperPreferencePage extends PreferencePage {

    private TableViewer tableViewer;
    private Text bindingText;
    private Text commandNameLbl;
    private ShortcutKeyMapper mapper;

    private String lastKey = "";

    /**
     * The doubleclick event.
     */
    protected HandleDoubleClickEvent doubleclickEvent;

    /**
     * Instantiates a new short cut mapper preference page.
     */
    public ShortCutMapperPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.SHORTCUT_MAPPER));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        mapper = new ShortcutKeyMapper();

        Label label = new Label(parent, SWT.BOLD);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.SELECT_SHORTCUTKEY));

        Composite composite = new Composite(parent, SWT.NONE);
        GridData gdToolbarComposite = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
        gdToolbarComposite.widthHint = 800;
        gdToolbarComposite.minimumWidth = 500;

        composite.setLayoutData(gdToolbarComposite);

        GridLayout layout = new GridLayout(1, true);
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setData(new GridData(SWT.FILL, SWT.TOP, false, false));

        tableViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.getTable().setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_TBL_RESULT_TABLE_001");
        setTableProperties();
        tableViewer.setContentProvider(new ArrayContentProvider());
        setLayout();
        populateData(getColumns(), getValues());
        composite.addControlListener(new CompositeControlListener());

        addButtonsForShortCutPrefPage(composite);

        addUiForAddingNewKeyBinding(composite);

        return composite;
    }

    /**
     * Adds the ui for adding new key binding.
     *
     * @param composite the composite
     */
    private void addUiForAddingNewKeyBinding(Composite composite) {
        Composite textcomposite = new Composite(composite, SWT.BORDER);
        GridData gdTextComposite = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 2);

        gdTextComposite.widthHint = 300;
        gdTextComposite.minimumWidth = 300;
        textcomposite.setLayoutData(gdTextComposite);

        GridLayout textlayout = new GridLayout(2, false);
        textcomposite.setLayout(textlayout);
        textcomposite.setData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label nameLbl = new Label(textcomposite, SWT.NONE);
        nameLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.KEY_NAME));

        commandNameLbl = new Text(textcomposite, SWT.BORDER | SWT.FILL);
        GridData grid = new GridData(GridData.GRAB_HORIZONTAL);
        grid.widthHint = 150;
        grid.minimumWidth = 200;
        commandNameLbl.setLayoutData(grid);
        commandNameLbl.setEnabled(false);

        Label bindingLbl = new Label(textcomposite, SWT.NONE);
        bindingLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.BINDING_KEY));

        bindingText = new Text(textcomposite, SWT.BORDER | SWT.FILL);
        GridData bindtextgrid = new GridData(GridData.GRAB_HORIZONTAL);
        bindtextgrid.widthHint = 150;
        bindtextgrid.minimumWidth = 200;
        bindingText.setLayoutData(bindtextgrid);
        bindingText.setEditable(false);
        bindingText.setEnabled(false);
        bindingText.setBackground(new Color(Display.getDefault(), new RGB(255, 250, 250)));

        KeyPressedEventCapture capture = new KeyPressedEventCapture();
        bindingText.addKeyListener(capture);
    }

    /**
     * Adds the buttons for short cut pref page.
     *
     * @param composite the composite
     */
    private void addButtonsForShortCutPrefPage(Composite composite) {
        Composite buttoncomposite = new Composite(composite, SWT.NONE);
        GridData gdButtonComposite = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 2);

        buttoncomposite.setLayoutData(gdButtonComposite);

        GridLayout buttonlayout = new GridLayout(2, false);
        buttonlayout.verticalSpacing = 10;
        buttoncomposite.setLayout(buttonlayout);
        buttoncomposite.setData(new GridData(SWT.FILL, SWT.TOP, false, false));

        Button modifyButton = new Button(buttoncomposite, SWT.PUSH);
        modifyButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.MODIFY_KEY));

        Button deleteButton = new Button(buttoncomposite, SWT.PUSH);
        deleteButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNBIND_KEY));

        modifyButton.addSelectionListener(new ModifyBtnSelectionListener());

        deleteButton.addSelectionListener(new DeleteBtnSelectionListener());

        // Adding a Double-click Event for tableViewer
        doubleclickEvent = new HandleDoubleClickEvent();
        tableViewer.addDoubleClickListener(doubleclickEvent);
    }

    /**
     * The listener interface for receiving deleteBtnSelection events. The class
     * that is interested in processing a deleteBtnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addDeleteBtnSelectionListener<code> method. When the
     * deleteBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * DeleteBtnSelectionEvent
     */
    private class DeleteBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            tableViewer.getTable().getItem(tableViewer.getTable().getSelectionIndex()).setText(1, "");
            PreferenceWrapper.getInstance().setChangeDone(true);
            PreferenceWrapper.getInstance().setNeedRestart(true);
            getApplyButton().setEnabled(true);
            getDefaultsButton().setEnabled(true);
            bindingText.setText("");
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving modifyBtnSelection events. The class
     * that is interested in processing a modifyBtnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addModifyBtnSelectionListener<code> method. When the
     * modifyBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ModifyBtnSelectionEvent
     */
    private class ModifyBtnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {

            int index = tableViewer.getTable().getSelectionIndex();
            mapper.clear();
            commandNameLbl.setText(tableViewer.getTable().getItem(index).getText(0));
            bindingText.setText(tableViewer.getTable().getItem(index).getText(1));
            lastKey = bindingText.getText();
            getDefaultsButton().setEnabled(true);
            getApplyButton().setEnabled(true);
            bindingText.setEnabled(true);
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {

        }
    }

    /**
     * The listener interface for receiving compositeControl events. The class
     * that is interested in processing a compositeControl event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addCompositeControlListener<code>
     * method. When the compositeControl event occurs, that object's appropriate
     * method is invoked.
     *
     * CompositeControlEvent
     */
    private class CompositeControlListener implements ControlListener {
        @Override
        public void controlResized(ControlEvent event) {
            Table table = tableViewer.getTable();
            int columnCount = table.getColumnCount();
            if (columnCount == 0) {
                return;
            }
            Rectangle area = table.getClientArea();
            int totalAreaWdith = area.width;
            int lineWidth = table.getGridLineWidth();
            int totalGridLineWidth = (columnCount - 1) * lineWidth;
            int totalColumnWidth = 0;
            // INITIALIZATION
            for (TableColumn column : table.getColumns()) {
                totalColumnWidth = totalColumnWidth + column.getWidth();
                column.pack();
            }

            // INITIALIZATION
            int diff = totalAreaWdith - (totalColumnWidth + totalGridLineWidth);

            TableColumn lastCol = table.getColumns()[columnCount - 1];

            // check diff is valid or not. setting negetive width doesnt
            // make sense.
            lastCol.setWidth(diff + lastCol.getWidth());
            lastCol.pack();
        }

        @Override
        public void controlMoved(ControlEvent event) {

        }
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        // rewrite default & apply
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class KeyPressedEventCapture.
     */
    private final class KeyPressedEventCapture implements KeyListener {
        int clmKey = 0;

        /**
         * Instantiates a new key pressed event capture.
         */
        public KeyPressedEventCapture() {
        }

        @Override
        public void keyPressed(KeyEvent event) {

            if (event.character == SWT.DEL) {
                return;
            }
            KeyStroke actualKeyStroke = SWTKeySupport
                    .convertAcceleratorToKeyStroke(SWTKeySupport.convertEventToUnmodifiedAccelerator(event));
            TableColumn[] columns = tableViewer.getTable().getColumns();
            for (int cnt = 0; cnt < columns.length; cnt++) {
                if (columns[cnt].getText()
                        .equalsIgnoreCase(MessageConfigLoader.getProperty(IMessagesConstants.KEY_BINDING))) {
                    clmKey = cnt;
                    break;
                }
            }
            if (!("BS".equalsIgnoreCase(actualKeyStroke.toString()))) {
                bindingText.setText(actualKeyStroke.toString());
                tableViewer.getTable().getItem(tableViewer.getTable().getSelectionIndex()).setText(clmKey,
                        actualKeyStroke.toString());

            } else {
                bindingText.setText("");
                tableViewer.getTable().getItem(tableViewer.getTable().getSelectionIndex()).setText(clmKey, "");
            }
            validate(tableViewer.getTable().getSelectionIndex());
            tableViewer.getTable().setRedraw(true);

        }

        @Override
        public void keyReleased(KeyEvent event) {

            KeyStroke stroke;
            try {
                stroke = KeyStroke.getInstance(bindingText.getText());
                int keys = stroke.getNaturalKey();
                if (0 == keys) {
                    bindingText.setText("");
                    tableViewer.getTable().getItem(tableViewer.getTable().getSelectionIndex()).setText(clmKey, "");
                }
                // Keyboard Pop-up Triggers Repeated Verification of Shortcut
                // Keys
                if (!isError()) {
                    Table table = tableViewer.getTable();
                    int index = table.getSelectionIndex();
                    bindingText.setText(lastKey);
                    table.getItem(index).setText(clmKey, lastKey);
                    return;
                }
            } catch (ParseException e1) {
                MPPDBIDELoggerUtility.error("Parse Exception", e1);
            }

        }

    }

    /**
     * Validate.
     *
     * @param event the event
     */
    public void validate(int event) {
        Object obj = tableViewer.getInput();
        String[][] input = (String[][]) obj;
        if (!input[event][1].equals(tableViewer.getTable().getItem(event).getText())) {
            getApplyButton().setEnabled(true);
            getDefaultsButton().setEnabled(true);
            PreferenceWrapper.getInstance().setChangeDone(true);

        }

    }

    /**
     * Sets the table properties.
     */
    private void setTableProperties() {
        final Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
    }

    /**
     * Sets the layout.
     */
    private void setLayout() {
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        tableViewer.getControl().setLayoutData(gridData);
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    private String[] getColumns() {
        String[] columns = {" " + MessageConfigLoader.getProperty(IMessagesConstants.COMMAND_NAME) + " ",
            MessageConfigLoader.getProperty(IMessagesConstants.KEY_BINDING),
            MessageConfigLoader.getProperty(IMessagesConstants.KEY_DESCRIPTION)};
        return columns;

    }

    /**
     * Populate data.
     *
     * @param columns the columns
     * @param values the values
     */
    public void populateData(String[] columns, Object[][] values) {
        tableViewer.getTable().setRedraw(false);

        tableViewer.getTable().removeAll();
        TableViewerUtil.disposeCurrentColumns(tableViewer);

        if (null != columns) {
            TableViewerUtil.createColumns(columns, tableViewer);
        }
        tableViewer.setInput(values);

        tableViewer.getTable().setRedraw(true);

    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public String[][] getValues() {

        List<KeyBinding> list = KeyBindingWrapper.getInstance().getList();
        ArrayList<String[]> rowset = new ArrayList<String[]>();
        String[] row = null;
        for (int index = 0; index < list.size(); index++) {
            row = new String[getColumns().length];
            KeyBinding key = list.get(index);
            row[0] = key.getCommandName().trim();
            row[1] = key.getNewKey();
            row[2] = MessageConfigLoader.getProperty(key.getDescription().trim());
            rowset.add(row);
        }
        return (String[][]) rowset.toArray(new String[rowset.size()][]);

    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        List<KeyBinding> list = KeyBindingWrapper.getInstance().getList();
        TableItem[] iteams = tableViewer.getTable().getItems();
        for (TableItem item : iteams) {
            String id = item.getText(0);
            for (int jindex = 0; jindex < list.size(); jindex++) {
                KeyBinding key = list.get(jindex);
                if (id.equalsIgnoreCase(key.getCommandName())) {
                    item.setText(1, preferenceStore.getDefaultString(key.getCommand()));
                    break;
                }
            }
        }

        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        Table table = tableViewer.getTable();
        int index = table.getSelectionIndex();
        if (index == -1) {
            return;
        }
        TableItem tableItem = table.getItem(index);
        commandNameLbl.setText(tableItem.getText(0));
        bindingText.setText(tableItem.getText(1));
        MPPDBIDELoggerUtility.operationInfo("Shortcut keys in Preferences setting are set to default");
        return;
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        lastKey = bindingText.getText();
        PreferenceWrapper.getInstance().setPreferenceApply(true);
        performOk();
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {

        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {
            getTableData(preferenceStore);
        }
        if (null != getApplyButton() && !getApplyButton().isEnabled()) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
        }
        PreferenceWrapper.getInstance().setNeedRestart(true);
        return true;
    }

    /**
     * Gets the table data.
     *
     * @param preferenceStore the preference store
     * @return the table data
     */
    public void getTableData(IPreferenceStore preferenceStore) {
        List<KeyBinding> list = KeyBindingWrapper.getInstance().getList();
        if (null != tableViewer) {
            TableItem[] iteams = tableViewer.getTable().getItems();
            for (TableItem item : iteams) {
                String id = item.getText(0);
                for (int jindex = 0; jindex < list.size(); jindex++) {
                    KeyBinding key = list.get(jindex);
                    if (id.equalsIgnoreCase(key.getCommandName())) {
                        String currShortcut = preferenceStore.getString(key.getCommand());
                        if (!currShortcut.contentEquals(item.getText(1))) {
                            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                                    "Shortcut key of %s in preferences changed from %s to %s", id, currShortcut,
                                    item.getText(1)));
                        }
                        preferenceStore.setValue(key.getCommand(), item.getText(1));
                        key.setNewKey(item.getText(1));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    @Override
    public boolean performCancel() {
        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setPreferenceApply(false);
        PreferenceWrapper.getInstance().setDefaultStore(false);
        return true;
    }

    /**
     * Checks if is error.
     *
     * @return true, if is error
     */
    private boolean isError() {
        String currentText = bindingText.getText();
        String[][] keyval = getValues();
        for (String[] val : keyval) {
            // No warning is required for operations on the same menu
            if (!"".equals(currentText) && val[1].equalsIgnoreCase(currentText)
                    && !currentText.equalsIgnoreCase(lastKey)) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.WARNING, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.SHORTCUT_KEY_DUPLICATE),
                        MessageConfigLoader.getProperty(IMessagesConstants.SHORTCUT_KEY_ALREADY_EXIST) + val[0]
                                + MessageConfigLoader.getProperty(IMessagesConstants.SELECT_SHORTCUT_KEY_AGAIN));
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class HandleDoubleClickEvent.
     */
    private final class HandleDoubleClickEvent implements IDoubleClickListener {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = event.getSelection();
            if (selection instanceof IStructuredSelection) {
                Object objct = ((IStructuredSelection) selection).getFirstElement();
                if (objct instanceof String[]) {
                    Table table = tableViewer.getTable();
                    int index = table.getSelectionIndex();
                    String[] item = (String[]) objct;
                    commandNameLbl.setText(item[0]);
                    bindingText.setText(table.getItem(index).getText(1));
                    lastKey = bindingText.getText();
                    getDefaultsButton().setEnabled(true);
                    getApplyButton().setEnabled(true);
                    bindingText.setEnabled(true);
                }
            }
        }
    }
}
