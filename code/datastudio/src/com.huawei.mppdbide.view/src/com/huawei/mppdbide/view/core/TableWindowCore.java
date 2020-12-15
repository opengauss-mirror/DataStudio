/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core;

import java.util.List;

import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Title: class
 * Description: The Class TableWindowCore.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public abstract class TableWindowCore<T> {
    private TableViewer tableViewer;
    private TableViewerColumn tableInfoColumn;

    /**
     * Constructor of class TableWindowCore.
     */
    public TableWindowCore() {
    }

    /**
     * Creates the part control.
     *
     * @param parent the parent
     * @param partSrvce the part service
     */
    public void createPartControl(Composite parent, EPartService partSrvce) {
        final Table table = new Table(parent,
                SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE | SWT.HIDE_SELECTION);
        tableViewer = new TableViewer(table);
        tableViewer.setData("org.eclipse.swtbot.widget.key", "tblStackframes");
        createColumns();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        tableViewer.setContentProvider(new ArrayContentProvider());
    }

    /**
     * Create column.
     */
    protected void createColumns() {
        List<String> titles = getTitle();
        for (int i = 0; i < titles.size(); i ++) {
            tableInfoColumn = new TableViewerColumn(tableViewer, SWT.NULL, i);
            tableInfoColumn.getColumn().setWidth(200);
            tableInfoColumn.getColumn().setText(titles.get(i));
            tableInfoColumn.setLabelProvider(getProvider(i));
        }
    }

    /**
     * Abstract method for getting column title.
     *
     * @return List<String> the column title list
     */
    protected abstract List<String> getTitle();

    /**
     * Abstract method for getting column label provider.
     *
     * @param index the index of the column
     * @return ColumnLabelProvider the column label provider for column index
     */
    protected abstract ColumnLabelProvider getProvider(int index);

    /**
     * Abstract method for getting list element.
     *
     * @return List<T> the list element
     */
    protected abstract List<T> getListElement();

    /**
     * Refresh.
     */
    public void refresh() {
        List<T> listElements = getListElement();
        if (listElements.size() > 0) {
            tableViewer.setInput(listElements);
        }
        tableInfoColumn.getColumn().pack();
    }

    /**
     * Clear.
     */
    public void clear() {
        tableViewer.setInput(null);
    }
}