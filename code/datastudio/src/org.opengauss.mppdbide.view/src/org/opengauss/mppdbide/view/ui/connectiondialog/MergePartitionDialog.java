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

package org.opengauss.mppdbide.view.ui.connectiondialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.table.UIUtils;
import org.opengauss.mppdbide.view.utils.FontAndColorUtility;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UIVerifier;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class MergePartitionDialog.
 *
 * @since 3.0.0
 */
public class MergePartitionDialog extends Dialog {

    private Object object;
    private Label lebelNotice;
    private Button okButton;
    private Button cancelButton;
    private KeyChecker checker = new KeyChecker();

    /* Data caches */
    private Table table;
    private Table table1;
    private Text destinationPartName;
    private Composite newComposite;
    private Composite curComposite;
    private Combo tableSpaceCombo;
    private boolean isOkButtonEnable;
    private Button addToMergeList;
    private ArrayList<String> avlPartitionList;
    private ArrayList<String> partitiontoBMergedList;
    private Table partitionTable;

    /**
     * The merge partition table.
     */
    protected Table mergePartitionTable;

    /**
     * The partition.
     */
    final PartitionMetaData partition = IHandlerUtilities.getSelectedPartitionMetadata();

    /**
     * The part table.
     */
    final PartitionTable partTable = partition != null ? partition.getParent() : null;

    // Dialog Buttons

    /**
     * Instantiates a new merge partition dialog.
     *
     * @param parent the parent
     * @param serverObject the server object
     */
    public MergePartitionDialog(Shell parent, Object serverObject) {
        super(parent);
        this.object = serverObject;
        super.setDefaultImage(getWindowImage());
    }

    /**
     * Gets the object.
     *
     * @return the object
     */
    protected Object getObject() {
        return this.object;
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(getWindowTitle());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class KeyChecker.
     */
    private class KeyChecker implements KeyListener {

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {

            String text1 = destinationPartName.getText();
            String text2 = tableSpaceCombo.getText();
            if (!"".equals(text1) && !"".equals(text2)) {
                okButton.setEnabled(true);
            } else {
                okButton.setEnabled(false);
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
        curComposite = (Composite) super.createDialogArea(parent);
        curComposite.setLayout(new GridLayout(1, false));

        curComposite = new Composite(parent, SWT.NONE);
        newComposite = new Composite(parent, SWT.NONE);

        curComposite.setBounds(0, 78, 700, 200);
        newComposite.setBounds(0, 78, 700, 200);

        Group group = new Group(curComposite, SWT.NONE);
        group.setBounds(10, 0, 430, 157);

        createColumnListTable(group);
        createPartitionParameters();
        createDestinationPartName();
        createTablespaceCombo();
        createButtons(group);
        createLblNotice();
        return curComposite;
    }

    private void createLblNotice() {
        lebelNotice = new Label(curComposite, SWT.WRAP);
        lebelNotice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lebelNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lebelNotice.setText(' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ' + MPPDBIDEConstants.LINE_SEPARATOR + ' ');
    }

    private void createButtons(Group group) {
        addToMergeList = new Button(group, SWT.NONE);
        addToMergeList.setBounds(202, 55, 39, 25);
        addToMergeList.setText(" > ");
        buttonAddToList(addToMergeList);
        Button returnToAvlList = new Button(group, SWT.NONE);
        returnToAvlList.setBounds(202, 86, 39, 25);
        returnToAvlList.setText(" < ");
        buttonRemoveBackFromMergeList(returnToAvlList);
    }

    private void createTablespaceCombo() {
        tableSpaceCombo = new Combo(curComposite, SWT.NONE);
        tableSpaceCombo.setBounds(273, 197, 159, 23);
        tableSpaceCombo.addKeyListener(checker);
        comboDisplayValues(tableSpaceCombo);
        tableSpaceCombo.forceFocus();
    }

    private void createDestinationPartName() {
        destinationPartName = new Text(curComposite, SWT.BORDER);
        destinationPartName.setBounds(20, 199, 169, 21);
        destinationPartName.addKeyListener(checker);
        UIVerifier.verifyTextSize(destinationPartName, 63);
        final ControlDecoration deco = new ControlDecoration(destinationPartName, SWT.TOP | SWT.LEFT);
        Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());
        deco.setImage(image);
        deco.setShowOnlyOnFocus(false);
    }

    private void createPartitionParameters() {
        Label lblDestinationPartitionName = new Label(curComposite, SWT.NONE);
        lblDestinationPartitionName.setBounds(10, 173, 159, 15);
        lblDestinationPartitionName.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, curComposite));
        lblDestinationPartitionName.setText("Destination Partition Name");
        Label lblTablespace = new Label(curComposite, SWT.NONE);
        lblTablespace.setBounds(263, 173, 111, 15);
        lblTablespace.setFont(FontAndColorUtility.getFont("Arial", 9, SWT.BOLD, curComposite));
        lblTablespace.setText("Tablespace");
    }

    private void createColumnListTable(Group group) {
        table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        table.setBounds(10, 10, 169, 145);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableColumn tblclmnAvailableColumns = new TableColumn(table, SWT.NONE);
        tblclmnAvailableColumns.setWidth(282);
        tblclmnAvailableColumns.setText("Available Partitions");
        tableDisplayPartition(table);
        tablePartitionDoubleClick(table);
        tblclmnAvailableColumns.pack();

        table1 = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        table1.setBounds(263, 10, 159, 145);
        table1.setHeaderVisible(true);
        table1.setLinesVisible(true);
        TableColumn tblclmnAvailableColumns1 = new TableColumn(table1, SWT.NONE);
        tblclmnAvailableColumns1.setWidth(204);
        tblclmnAvailableColumns1.setText("Partitions to be Merged");
        tblclmnAvailableColumns1.pack();
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String cancelLabel = "    " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "    ";
        final String okLabel = "    " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "    ";
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        cancelButton = createButton(parent, UIConstants.CANCEL_ID, cancelLabel, false);
    }

    /**
     * Table partition double click.
     *
     * @param partitionTable1 the partition table 1
     */
    protected void tablePartitionDoubleClick(final Table partitionTable1) {
        partitionTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                partitionTable = partitionTable1;
                int selectedIdx = partitionTable.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = avlPartitionList.get(selectedIdx);
                    avlPartitionList.remove(selectedIdx);
                    partitionTable.remove(selectedIdx);
                    partitiontoBMergedList.add(colName);
                    tableNeedToMergePartition(table1);
                    repopulateSelectedPartitions();
                }
            }
        });
    }

    /**
     * Table need to merge partition double click.
     *
     * @param partitionTable1 the partition table 1
     */
    protected void tableNeedToMergePartitionDoubleClick(final Table partitionTable1) {
        mergePartitionTable.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent e) {
            }

            @Override
            public void mouseDown(MouseEvent e) {
            }

            @Override
            public void mouseDoubleClick(MouseEvent e) {
                mergePartitionTable = partitionTable1;
                int selectedIdx = mergePartitionTable.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = partitiontoBMergedList.get(selectedIdx);
                    partitiontoBMergedList.remove(selectedIdx);
                    mergePartitionTable.remove(selectedIdx);
                    avlPartitionList.add(colName);
                    repopulateAvailablePartitions();
                }
            }
        });
    }

    /**
     * Combo display values.
     *
     * @param tbleSpaceCombo the tble space combo
     */
    protected void comboDisplayValues(final Combo tbleSpaceCombo) {

        UIUtils.displayTablespaceList(partition.getParent().getDatabase(), tbleSpaceCombo, false,
                partition.getParent().getOrientation());

        tbleSpaceCombo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isSelected = tableSpaceCombo.getSelectionIndex() >= 0;
                setOkButtonEnabled(isSelected);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    /**
     * Table display partition.
     *
     * @param partitionTable1 the partition table 1
     */
    protected void tableDisplayPartition(final Table partitionTable1) {

        partitionTable = partitionTable1;
        avlPartitionList = new ArrayList<String>(0);
        partitiontoBMergedList = new ArrayList<String>(0);
        Iterator<PartitionMetaData> partItr = null;
        if (partTable != null) {
            partItr = partTable.getPartitions().getList().iterator();
            boolean hasNext = partItr.hasNext();
            PartitionMetaData partion = null;
            TableItem item = null;
            while (hasNext) {
                partion = partItr.next();
                item = new TableItem(partitionTable, SWT.NONE);
                item.setText(partion.getDisplayName());
                avlPartitionList.add(partion.getDisplayName());
                hasNext = partItr.hasNext();
            }
        }
    }

    /**
     * Button add to list.
     *
     * @param addToButton the add to button
     */
    protected void buttonAddToList(Button addToButton) {
        addToButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedIdx = partitionTable.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = avlPartitionList.get(selectedIdx);
                    avlPartitionList.remove(selectedIdx);
                    partitionTable.remove(selectedIdx);
                    partitiontoBMergedList.add(colName);
                    tableNeedToMergePartition(table1);
                    repopulateSelectedPartitions();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * Table need to merge partition.
     *
     * @param mergeTable the merge table
     * @return the list
     */
    protected List<String> tableNeedToMergePartition(Table mergeTable) {
        mergePartitionTable = mergeTable;

        Iterator<String> colsItr = partitiontoBMergedList.iterator();
        TableItem item = null;
        boolean hasNext = colsItr.hasNext();
        String col = null;
        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(mergeTable, SWT.NONE);
            item.setText(col);
            hasNext = colsItr.hasNext();
        }
        return partitiontoBMergedList;
    }

    /**
     * Repopulate selected partitions.
     */
    protected void repopulateSelectedPartitions() {
        mergePartitionTable.removeAll();
        TableItem item = null;
        partitiontoBMergedList.removeAll(avlPartitionList);
        Iterator<String> colsItr = this.partitiontoBMergedList.iterator();
        boolean hasNext = colsItr.hasNext();
        String col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(mergePartitionTable, SWT.NONE);
            item.setText(col);
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Repopulate available partitions.
     */
    protected void repopulateAvailablePartitions() {
        partitionTable.removeAll();
        avlPartitionList.removeAll(partitiontoBMergedList);
        TableItem item = null;
        Iterator<String> colsItr = this.avlPartitionList.iterator();
        boolean hasNext = colsItr.hasNext();
        String col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(partitionTable, SWT.NONE);
            item.setText(col);
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Button remove back from merge list.
     *
     * @param removeButton the remove button
     */
    protected void buttonRemoveBackFromMergeList(Button removeButton) {
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedIdx = mergePartitionTable.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = partitiontoBMergedList.get(selectedIdx);
                    partitiontoBMergedList.remove(selectedIdx);
                    mergePartitionTable.remove(selectedIdx);
                    avlPartitionList.add(colName);
                    repopulateAvailablePartitions();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * Gets the user input.
     *
     * @return the user input
     */
    protected String getUserInput() {
        Text text = (Text) destinationPartName;
        if (text.isDisposed()) {
            return "";
        }

        return text.getText().trim();
    }

    /**
     * Gets the combo input.
     *
     * @return the combo input
     */
    protected String getComboInput() {
        Combo text = tableSpaceCombo;
        if (text.isDisposed()) {
            return "";
        }
        return text.getText();
    }

    /**
     * Prints the message.
     *
     * @param msg the msg
     * @param isInProgressMsg the is in progress msg
     */
    public void printMessage(String msg, boolean isInProgressMsg) {
        printColourMessageLebel(msg, isInProgressMsg, false);
    }

    private void printColourMessageLebel(String msg, boolean isInProgressMsg, boolean isErrormsg) {
        if (lebelNotice.isDisposed()) {
            return;
        }
        setForeGroundColor(msg, isErrormsg);

        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }
        if (!isInProgressMsg) {
            okButton.setEnabled(true);
            cancelButton.setEnabled(true);
        }
    }

    private void setForeGroundColor(String msg, boolean isErrormsg) {
        if (isErrormsg) {
            lebelNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        } else {
            lebelNotice.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }
        lebelNotice.setText(msg);
        lebelNotice.redraw();
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        performOkOperation();
    }

    /**
     * Sets the ok button enabled.
     *
     * @param isEnabled the new ok button enabled
     */
    public void setOkButtonEnabled(boolean isEnabled) {
        this.isOkButtonEnable = isEnabled;

        if (null != okButton) {
            okButton.setEnabled(this.isOkButtonEnable);
        }
    }

    /**
     * Prints the error message.
     *
     * @param msg the msg
     * @param isInProgressMsg the is in progress msg
     */
    public void printErrorMessage(String msg, boolean isInProgressMsg) {
        printColourMessageLebel(msg, isInProgressMsg, true);
    }

    /**
     * Enable buttons.
     */
    public void enableButtons() {
        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }

        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected String getWindowTitle() {
        return "Merge Partition";
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected final Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.PARTITION_TABLE, this.getClass());
    }

    /**
     * Perform ok operation.
     */
    protected void performOkOperation() {

    }
}
