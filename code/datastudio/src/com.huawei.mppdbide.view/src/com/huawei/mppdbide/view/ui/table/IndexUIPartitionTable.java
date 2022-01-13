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

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexUIPartitionTable.
 *
 * @since 3.0.0
 */
public class IndexUIPartitionTable extends IndexUI {

    /**
     * Instantiates a new index UI partition table.
     *
     * @param tbl the tbl
     * @param server the server
     */
    public IndexUIPartitionTable(TableMetaData tbl, Server server) {
        super(tbl, server);

    }

    /**
     * Creates the where clause.
     *
     * @param comp the comp
     */
    @Override
    protected void createWhereClause(Composite comp) {

        super.createWhereClause(comp);
        if (lblPartialIndexWhere != null) {
            lblPartialIndexWhere.setEnabled(false);
        }
        txtWhereExpr.setEnabled(false);
    }

    /**
     * Handle ORC selection.
     *
     * @param compositeIndices the composite indices
     */
    public void handleORCSelection(Composite compositeIndices) {
        Control[] cmpIndices = compositeIndices.getChildren();

        for (Control child : cmpIndices) {
            child.setEnabled(false);
        }

    }

    /**
     * Creates the index type method
     *
     * @param comp the comp
     */
    protected void createIndexType(Composite comp) {
        Composite indexTypeComposite = new Composite(comp, SWT.NONE);
        indexTypeComposite.setLayout(new GridLayout(1, false));
        GridData indexTypeCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        indexTypeComposite.setLayoutData(indexTypeCompositeGD);

        Label idxTypeMethod = new Label(indexTypeComposite, SWT.NONE);
        idxTypeMethod.setText(MessageConfigLoader.getProperty(IMessagesConstants.INDEX_UI_TYPE));
        idxTypeMethod.pack();

        cmbIndexType = new Combo(indexTypeComposite, SWT.READ_ONLY);
        cmbIndexType.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_COMBO_INDEXUI_ACCESS_METHOD_001");
        GridData cmbIndexTypeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        cmbIndexType.setLayoutData(cmbIndexTypeGD);
        updateIndexTypeObject();
    }

    /**
     * Handle row selection.
     *
     * @param compositeIndices1 the composite indices 1
     */
    public void handleRowSelection(Composite compositeIndices1) {
        Control[] cmpIndices = compositeIndices1.getChildren();

        txtUserExpr.setEnabled(true);

        for (Control child : cmpIndices) {
            child.setEnabled(true);
        }
        txtWhereExpr.setEnabled(false);

    }

    /**
     * Handle column selection.
     *
     * @param compositeIndices2 the composite indices 2
     */
    public void handleColumnSelection(Composite compositeIndices2) {
        Control[] cmpIndices = compositeIndices2.getChildren();
        for (Control child : cmpIndices) {
            child.setEnabled(true);
        }
        fillFactor.setEnabled(false);
        if (null != btnUniqueIndex) {
            btnUniqueIndex.setEnabled(false);
        }
        txtUserExpr.setEnabled(false);
        txtWhereExpr.setEnabled(false);
    }

}
