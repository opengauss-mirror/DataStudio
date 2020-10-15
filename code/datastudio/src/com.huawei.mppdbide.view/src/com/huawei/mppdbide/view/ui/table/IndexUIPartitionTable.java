/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.table;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexUIPartitionTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
