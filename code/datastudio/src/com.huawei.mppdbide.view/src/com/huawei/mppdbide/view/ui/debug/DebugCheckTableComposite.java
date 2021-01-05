/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.huawei.mppdbide.view.utils.icon.IconUtility;

import org.eclipse.jface.viewers.TableViewer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * Title: class Description: The Class LifeCycleManager. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DebugCheckTableComposite extends DebugBaseTableComposite {
    protected ToolBar toolBar;
    private CheckboxTableViewer checkboxTableViewer;
    /**
     * Create the composite.
     * @param parent the parent
     * @param style the style
     */
    public DebugCheckTableComposite(Composite parent, int style) {
        super(parent, style);
    }


    /**
     * description: this is check table special create ui
     */
    @Override
    protected void initUi() {
        setLayout(new FillLayout(SWT.VERTICAL));
        
        SashForm sashForm = new SashForm(this, SWT.VERTICAL);
        sashForm.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                if (e.getSource() instanceof SashForm) {
                    SashForm sf = (SashForm) e.getSource();
                    int scaleBase = (int)(new Double(sf.getSize().y) / 25 - 0.5);
                    sf.setWeights(new int[] {1, scaleBase <= 1? 1: scaleBase - 1});
                }
            }
        });
        
        toolBar = new ToolBar(sashForm, SWT.FLAT | SWT.RIGHT);
        
        String[] toolItemsTipText = new String[] {"Enable", "Disable", "Remove", "RemoveAll"};
        DebugCheckboxEvent[] toolItemsEvent = new DebugCheckboxEvent[] {
            DebugCheckboxEvent.ENABLE,
            DebugCheckboxEvent.DISABLE,
            DebugCheckboxEvent.DELETE,
            DebugCheckboxEvent.DELETE_ALL};
        String[] toolItemsImagePath = new String[] {
            "icon_breakpoint-enabled.png",
            "icon_breakpoint-disabled.png",
            "icon_breakpoint-delete.png",
            "icon_breakpoint-delete_all.png",
        };
        IntStream.range(0, toolItemsTipText.length).forEach(idx -> {
            ToolItem toolItem = new ToolItem(toolBar, SWT.NONE);
            toolItem.setText("");
            toolItem.setToolTipText(toolItemsTipText[idx]);
            toolItem.addSelectionListener(new ToolBaseSelectionAdapter(toolItemsEvent[idx], this));
            String tmpIconPath = "debug" + File.separator + toolItemsImagePath[idx];
            toolItem.setImage(IconUtility.getIconImage(tmpIconPath, this.getClass()));
        });
        Composite composite = new Composite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        checkboxTableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = checkboxTableViewer.getTable();
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                TableItem item = table.getItem(new Point(e.x, e.y));
                if (item == null) {
                    table.deselectAll();
                }
            }
        });
        checkboxTableViewer.setAllGrayed(true);
        checkboxTableViewer.setAllChecked(true);
        
        Menu menu = new Menu(table);
        menu.addMenuListener(new TableMenuAdapter());
        table.setMenu(menu);
        
        MenuItem mntmEnable = new MenuItem(menu, SWT.NONE);
        mntmEnable.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.ENABLE, this));
        mntmEnable.setText("Enable");
        mntmEnable.setData(TableMenuAdapter.ITEM_ENABLE_SELECT, 1);
        
        MenuItem mntmDisable = new MenuItem(menu, SWT.NONE);
        mntmDisable.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.DISABLE, this));
        mntmDisable.setText("Disable");
        mntmDisable.setData(TableMenuAdapter.ITEM_ENABLE_SELECT, 1);
        
        MenuItem mntmRemove = new MenuItem(menu, SWT.NONE);
        mntmRemove.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.DELETE, this));
        mntmRemove.setText("Remove");
        mntmRemove.setData(TableMenuAdapter.ITEM_ENABLE_SELECT, 1);
        
        MenuItem mntmRemoveAll = new MenuItem(menu, SWT.NONE);
        mntmRemoveAll.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.DELETE_ALL, this));
        mntmRemoveAll.setText("RemoveAll");
        
        MenuItem mntmSelectAll = new MenuItem(menu, SWT.NONE);
        mntmSelectAll.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.SELECT_ALL, this));
        mntmSelectAll.setText("SelectAll");

        MenuItem mntmDeSelectAll = new MenuItem(menu, SWT.NONE);
        mntmDeSelectAll.addSelectionListener(new ToolBaseSelectionAdapter(DebugCheckboxEvent.DE_SELECT_ALL, this));
        mntmDeSelectAll.setText("DeSelectAll");
        sashForm.setWeights(new int[] {1, 10});
    }

    /**
     * description: get toolbar object
     * 
     * @return
     */
    public ToolBar getToolBar() {
        return toolBar;
    }

    /**
     * get checkboxtableviewer
     *
     * @return TableViewer the viewer
     */
    @Override
    public TableViewer getTableViewer() {
        return getCheckboxTableViewer();
    }

    /**
     * description: get checkbox viewer
     * 
     * @return CheckBoxTableViewer the viewer
     */
    public CheckboxTableViewer getCheckboxTableViewer() {
        return checkboxTableViewer;
    }

    private class TableMenuAdapter extends MenuAdapter {
        /**
         * the event of enable select item if item need show
         */
        public static final String ITEM_ENABLE_SELECT = "need_select_item";

        @Override
        public void menuShown(MenuEvent e) {
            Object sourceObj = e.getSource();
            if (sourceObj instanceof Menu) {
                Menu menu = (Menu) sourceObj;
                Arrays.asList(menu.getItems()).stream().forEach(item -> {
                    boolean enable = true;
                    Object objValue = item.getData(ITEM_ENABLE_SELECT);
                    if (objValue != null) {
                        Object objSelect = getTableViewer().getStructuredSelection().getFirstElement();
                        if (objSelect == null) {
                            enable = false;
                        }
                    } else {
                        enable = DebugCheckTableComposite.this
                                .getDataList().orElse(new ArrayList<IDebugSourceData>(1)).size() > 0;
                    }
                    item.setEnabled(enable);
                });
            }
        } 
    }

    private static class ToolBaseSelectionAdapter extends SelectionAdapter {
        private DebugCheckboxEvent event;
        private DebugCheckTableComposite checkComposite;
        public ToolBaseSelectionAdapter(DebugCheckboxEvent event, DebugCheckTableComposite checkComposite) {
            this.event = event;
            this.checkComposite = checkComposite;
        }

        private Object[] getSelectItems(Object source) {
            if (!(source instanceof MenuItem)) {
                return checkComposite.getCheckboxTableViewer().getCheckedElements();
            }

            MenuItem sourceItem = (MenuItem) source;
            Object needSelectItem = sourceItem.getData(TableMenuAdapter.ITEM_ENABLE_SELECT);
            if (needSelectItem != null) {
                return new Object[] {checkComposite.getTableViewer().getStructuredSelection().getFirstElement()};
            }
            Optional<List<?>> datas = checkComposite.getDataList();
            if (datas.isPresent()) {
                return datas.get().toArray();
            }
            return null;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            DebugTableEventHandler handler = checkComposite.getTableEventHandler();
            if (handler == null) {
                return;
            }
            Object[] items = getSelectItems(source);
            if (items != null) {
                if (event == DebugCheckboxEvent.SELECT_ALL) {
                    checkComposite.getCheckboxTableViewer().setAllChecked(true);
                } else if (event == DebugCheckboxEvent.DE_SELECT_ALL) {
                    checkComposite.getCheckboxTableViewer().setAllChecked(false);
                } else {
                    checkComposite.getTableEventHandler().selectHandler(Arrays.asList(items), event);
                }
            }
        }
    }
}
