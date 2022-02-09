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

package org.opengauss.mppdbide.view.component.grid;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.table.AddColumn;
import org.opengauss.mppdbide.view.ui.table.AddConstraint;
import org.opengauss.mppdbide.view.ui.table.CreateIndexDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class AddPropertiesInfoDialog.
 *
 * @since 3.0.0
 */
public class AddPropertiesInfoDialog {

    private ServerObjectTypeForDialog dialogType;
    private ServerObject serverObject;
    private Shell shell;
    private TableMetaData tableMetaData;

    /**
     * Instantiates a new adds the properties info dialog.
     *
     * @param type the type
     * @param shell the shell
     * @param tableMetaData the table meta data
     */
    public AddPropertiesInfoDialog(ServerObjectTypeForDialog type, Shell shell, TableMetaData tableMetaData) {
        this.dialogType = type;
        this.shell = shell;
        this.tableMetaData = tableMetaData;

    }

    /**
     * Creates the dialog.
     */
    public void createDialog() {
        if (dialogType == ServerObjectTypeForDialog.COLUMNS) {
            createAddColumnDialog();
        } else if (dialogType == ServerObjectTypeForDialog.CONSTRAINTS) {
            createConstraintDialog();
        } else if (dialogType == ServerObjectTypeForDialog.INDEX) {
            createIndexDialog();
        }
    }

    private void createIndexDialog() {

        PropertiesAddIndexDialog newIndex = new PropertiesAddIndexDialog(shell, tableMetaData,
                tableMetaData.getServer());
        newIndex.open();
    }

    private void createConstraintDialog() {

        PropertiesAddConstraintDialog constraintDialog = new PropertiesAddConstraintDialog(shell, tableMetaData);
        constraintDialog.open();
    }

    private void createAddColumnDialog() {

        PropertiesAddColumnDialog dialog = new PropertiesAddColumnDialog(this.shell, this.tableMetaData);
        dialog.open();

    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public ServerObject getServerObject() {
        return this.serverObject;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertiesAddColumnDialog.
     */
    private final class PropertiesAddColumnDialog extends AddColumn {

        private ColumnMetaData column;

        /**
         * Instantiates a new properties add column dialog.
         *
         * @param shell the shell
         * @param tableMetaData the table meta data
         */
        public PropertiesAddColumnDialog(Shell shell, TableMetaData tableMetaData) {
            super(shell, tableMetaData);
        }

        @Override
        public Object open() {
            Object obj = super.open();
            return obj;

        }

        @Override
        protected void performOkPressed() {
            okButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    column = columnUI.getDBColumn(null, false, tableMetaData.getOrientation());
                    if (null != column) {
                        if (null == column.getName() || "".equals(column.getName())) {
                            lblLblerrormsg
                                    .setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_COL_NAME));
                        } else if (null == column.getDataType()) {
                            lblLblerrormsg
                                    .setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_SELECT_DATA_TYPE));
                        } else {
                            serverObject = column;
                            close();
                        }

                    } else {
                        lblLblerrormsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_COL_NAME));
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertiesAddConstraintDialog.
     */
    private class PropertiesAddConstraintDialog extends AddConstraint {

        /**
         * Instantiates a new properties add constraint dialog.
         *
         * @param shell the shell
         * @param tableMetaData the table meta data
         */
        public PropertiesAddConstraintDialog(Shell shell, TableMetaData tableMetaData) {

            super(shell, tableMetaData);
        }

        @Override
        public Object open() {

            return super.open();
        }

        @Override
        protected void performOkPressed() {
            okButton.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    ConstraintMetaData constraint = constraintUI.getConstraint(true);
                    lblErrormsg.setText("");

                    if (null != constraint) {
                        constraint.setTable(tableMetaData);
                        serverObject = constraint;

                        close();

                    }

                    else {
                        lblErrormsg
                                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PLS_ENTER_TABLE_FOR_CONS));
                    }

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });

        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertiesAddIndexDialog.
     */
    private class PropertiesAddIndexDialog extends CreateIndexDialog {

        /**
         * Instantiates a new properties add index dialog.
         *
         * @param parent the parent
         * @param tbl the tbl
         * @param server the server
         */
        public PropertiesAddIndexDialog(Shell parent, TableMetaData tbl, Server server) {
            super(parent, tbl, server);
        }

        @Override
        protected Control createContents(Composite parent) {

            return super.createContents(parent);
        }

        @Override
        protected void performOkPressed() {
            btnCreateIndex.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        setIdx(ui.getIndexMetaData());
                        close();
                        serverObject = getIdx();
                    } catch (DatabaseOperationException e1) {
                        showError(e1.getMessage());
                        btnCancel.setEnabled(true);
                        return;
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });

        }

    }
}
