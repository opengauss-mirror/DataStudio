/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;

/**
 * Title: class Description: The Class LifeCycleManager. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DebugBaseTableComposite extends Composite {
    /**
     * this is column default width
     */
    private static final int DEFAULT_COLUMN_WIDTH = 100;

    /**
     * this is table variable
     */
    protected Table table;

    /**
     * this use to indicate if column is create
     */
    protected boolean isColumnTitleCreated = false;

    /**
     * receive some table event
     */
    protected DebugTableEventHandler eventHandler;

    /**
     * the viewer
     */
    protected TableViewer tableViewer;

    /**
     * Create the composite.
     *
     * @param parent the parent
     * @param style the style
     */
    public DebugBaseTableComposite(Composite parent, int style) {
        super(parent, style);
        initUi();
        commonInitUi();
    }

    /**
     * description: init Ui, if subclass have special, override this function
     */
    protected void initUi() {
        setLayout(new FillLayout(SWT.VERTICAL));

        SashForm sashForm = new SashForm(this, SWT.VERTICAL);

        Composite composite = new Composite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        sashForm.setWeights(new int[] {10});
    }

    /**
     * description:common init ui, this after initUi, not recommand override this
     */
    protected void commonInitUi() {
        TableViewer viewer = getTableViewer();
        table = viewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        viewer.setContentProvider(new ListStructuredContentProvider());
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (eventHandler == null) {
                    return;
                }
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection select = (IStructuredSelection) event.getSelection();
                Object selectObj = select.getFirstElement();
                if (selectObj instanceof IDebugSourceData) {
                    eventHandler.selectHandler(Arrays.asList((IDebugSourceData) selectObj),
                            DebugCheckboxEvent.DOUBLE_CLICK);
                }
            }
        });
    }

    /**
     * get table viewer object
     *
     * @return TableViewer the viewer
     */
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    /**
     * description: add data
     *
     * @param data the data to add
     * @return boolean true if add success
     */
    @SuppressWarnings("unchecked")
    public boolean addData(IDebugSourceData data) {
        if (data == null || !isColumnTitleCreated) {
            return false;
        }
        Optional<List<?>> inputOptional = getDataList();
        List<Object> newInputs;
        if (!inputOptional.isPresent()) {
            newInputs = new ArrayList<Object>(1);
        } else {
            List<?> inputLists = inputOptional.get();
            if (inputLists instanceof List) {
                newInputs = (List<Object>) inputOptional.get();
            } else {
                newInputs = new ArrayList<Object>(1);
            }
        }
        data.setDataOrder(newInputs.size());
        newInputs.add(data);
        TableViewer viewer = getTableViewer();
        viewer.setInput(newInputs);
        viewer.refresh();
        return true;
    }

    /**
     * description: remove data
     *
     * @param data the data to remove, must already in tableviewer
     * @return boolean true if success
     */
    public boolean removeData(IDebugSourceData data) {
        if (data == null || !isColumnTitleCreated) {
            return false;
        }
        boolean isRemoved = false;
        Optional<List<?>> inputOptional = getDataList();
        if (inputOptional.isPresent()) {
            List<?> inputs = inputOptional.get();
            isRemoved = inputs.remove(data);
            resetOrder(inputs);
            getTableViewer().refresh();
        }
        return isRemoved;
    }

    /**
     * description: remove all data
     */
    public void removeAllData() {
        getTableViewer().setInput(null);
        getTableViewer().refresh();
    }

    /**
     * description: reset all data
     *
     * @param dataList the set data list
     */
    public void resetAllData(List<IDebugSourceData> dataList) {
        getTableViewer().setInput(dataList);
        resetOrder(dataList);
        getTableViewer().refresh();
    }

    /**
     * description: create column by data header
     *
     * @param header the column header
     */
    public void createColumns(IDebugSourceDataHeader header) {
        if (isColumnTitleCreated) {
            return;
        }
        isColumnTitleCreated = true;
        List<String> titles = header.getTitles();
        TableViewer viewer = getTableViewer();
        IntStream.iterate(0, seed -> seed + 1).limit(titles.size()).forEach(idx -> {
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            column.getColumn().setWidth(DEFAULT_COLUMN_WIDTH);
            column.getColumn().setText(titles.get(idx));
            column.setEditingSupport(new DebugEditingSupport(viewer, idx));
            column.setLabelProvider(new DebugSourceDataLableProvider(idx));
        });
        table.addControlListener(new DebugControlAdapter(table, header));
    }

    /**
     * description: add event handler
     *
     * @param handler the event handler
     */
    public void setTableHandler(DebugTableEventHandler handler) {
        this.eventHandler = handler;
    }

    /**
     * description: get event handler
     *
     * @return DebugTableEventHandler the event handler
     */
    public DebugTableEventHandler getTableEventHandler() {
        return this.eventHandler;
    }

    /**
     * description: get data list from tableviewer
     *
     * @return Optional<List<?>> the data list
     */
    public Optional<List<?>> getDataList() {
        Object inputs = getTableViewer().getInput();
        if (inputs instanceof List<?>) {
            return Optional.of((List<?>) inputs);
        }
        return Optional.empty();
    }

    /**
     * description: reset the order
     *
     * @param inputsList the list to be ordered
     */
    protected void resetOrder(List<?> inputsList) {
        if (inputsList.size() == 0) {
            return;
        }
        IntStream.range(0, inputsList.size()).forEach(idx -> {
            Object inputItem = inputsList.get(idx);
            if (inputItem instanceof IDebugSourceData) {
                ((IDebugSourceData) inputItem).setDataOrder(idx);
            }
        });
    }
}
