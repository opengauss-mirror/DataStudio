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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.graphics.Image;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionCommonDialog.
 *
 * @since 3.0.0
 */
public class PartitionCommonDialog extends Dialog {

    /**
     * The col table.
     */
    protected Table colTable;

    /**
     * Instantiates a new partition common dialog.
     *
     * @param parentShell the parent shell
     */
    protected PartitionCommonDialog(Shell parentShell) {
        super(parentShell);
        setDefaultImage(getWindowImage());
    }

    private Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.PARTITION_TABLE, this.getClass());
    }

    /**
     * Creates the partition UI.
     *
     * @param parent the parent
     */
    public void createPartitionUI(Composite parent) {
        Composite tableViewerComp = new Composite(parent, SWT.NONE);
        GridData grpData = new GridData();
        grpData.verticalAlignment = GridData.FILL;
        grpData.horizontalIndent = 5;
        grpData.verticalIndent = 10;
        grpData.minimumWidth = 180;
        grpData.heightHint = 120;
        grpData.grabExcessHorizontalSpace = false;
        grpData.grabExcessVerticalSpace = true;
        grpData.horizontalAlignment = GridData.FILL;

        tableViewerComp.setLayoutData(grpData);
        tableViewerComp.setLayout(new GridLayout());

        TableViewer viewer = new TableViewer(tableViewerComp,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        viewer.setContentProvider(new ArrayContentProvider());
        colTable = viewer.getTable();
        colTable.setVisible(true);
        colTable.setHeaderVisible(true);
        colTable.setSize(100, 130);
        GridData colTablegridData = new GridData();
        colTablegridData.verticalAlignment = GridData.FILL;
        colTablegridData.grabExcessHorizontalSpace = false;
        colTablegridData.grabExcessVerticalSpace = true;
        colTablegridData.horizontalAlignment = GridData.FILL;
        colTable.setLayoutData(colTablegridData);

        TableColumn colNameColumn = new TableColumn(colTable, SWT.BORDER);
        colNameColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_COLUMN));
        colNameColumn.setWidth(120);

        TableColumn textBoxColumn = new TableColumn(colTable, SWT.BORDER);
        textBoxColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PARTITION_VALUE));
        textBoxColumn.setWidth(150);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
    }

}
