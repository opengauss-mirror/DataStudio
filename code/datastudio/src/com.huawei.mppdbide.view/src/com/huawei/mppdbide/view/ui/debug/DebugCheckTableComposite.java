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
import java.util.stream.Collectors;
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
 * Title: class Description: The Class LifeCycleManager.
 *
 * @since 3.0.0
 */
public class DebugCheckTableComposite extends DebugBaseTableComposite {
    /**
     * the tool bar
     */
    protected ToolBar toolBar;

    private CheckboxTableViewer checkboxTableViewer;

    /**
     * Create the composite.
     *
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
    public void initUi() {
        setLayout(new FillLayout(SWT.VERTICAL));

        SashForm sashForm = new SashForm(this, SWT.VERTICAL);
        sashForm.addControlListener(new ToolControlAdapter());

        toolBar = createToolBar(sashForm);

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

        Menu menu = createMenu(table);
        table.setMenu(menu);
        sashForm.setWeights(new int[] {1, 10});
        commonInitUi();
    }

    private ToolBar createToolBar(SashForm sashForm) {
        ToolBar tmpToolBar = new ToolBar(sashForm, SWT.FLAT | SWT.RIGHT);

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
            ToolItem toolItem = new ToolItem(tmpToolBar, SWT.NONE);
            toolItem.setText("");
            toolItem.setToolTipText(toolItemsTipText[idx]);
            toolItem.addSelectionListener(new ToolBaseSelectionAdapter(toolItemsEvent[idx], this));
            String tmpIconPath = "debug" + File.separator + toolItemsImagePath[idx];
            toolItem.setImage(IconUtility.getIconImage(tmpIconPath, this.getClass()));
        });
        return tmpToolBar;
    }

    private Menu createMenu(Table table) {
        Menu menu = new Menu(table);
        menu.addMenuListener(new TableMenuAdapter());

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
        return menu;
    }

    /**
     * description: get toolbar object
     *
     * @return ToolBar the tool bar
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
        public void menuShown(MenuEvent menuEvent) {
            Object sourceObj = menuEvent.getSource();
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
                                .getDataList()
                                .orElse(new ArrayList<IDebugSourceData>(1)).size() > 0;
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

        private List<IDebugSourceData> getSelectItems(Object source) {
            List<?> items = new ArrayList<Object>(1);
            if (!(source instanceof MenuItem)) {
                items = Arrays.asList(checkComposite.getCheckboxTableViewer().getCheckedElements());
            } else {
                MenuItem sourceItem = (MenuItem) source;
                Object needSelectItem = sourceItem.getData(TableMenuAdapter.ITEM_ENABLE_SELECT);
                if (needSelectItem != null) {
                    items = Arrays.asList(checkComposite.getTableViewer().getStructuredSelection().getFirstElement());
                } else {
                    Optional<List<?>> datas = checkComposite.getDataList();
                    if (datas.isPresent()) {
                        items =  datas.get();
                    }
                }
            }

            return items.stream().map(item -> {
                    if (item instanceof IDebugSourceData) {
                        return (IDebugSourceData) item;
                    } else {
                        return null;
                    }
                }).filter(item -> item != null).collect(Collectors.toList());
        }

        @Override
        public void widgetSelected(SelectionEvent selectEvent) {
            Object source = selectEvent.getSource();
            DebugTableEventHandler handler = checkComposite.getTableEventHandler();
            if (handler == null) {
                return;
            }
            List<IDebugSourceData> items = getSelectItems(source);
            if (items != null) {
                if (event == DebugCheckboxEvent.SELECT_ALL) {
                    checkComposite.getCheckboxTableViewer().setAllChecked(true);
                } else if (event == DebugCheckboxEvent.DE_SELECT_ALL) {
                    checkComposite.getCheckboxTableViewer().setAllChecked(false);
                } else {
                    checkComposite.getTableEventHandler().selectHandler(items, event);
                }
            }
        }
    }

    private static class ToolControlAdapter extends ControlAdapter {
        @Override
        public void controlResized(ControlEvent crlEvent) {
            if (crlEvent.getSource() instanceof SashForm) {
                SashForm sf = (SashForm) crlEvent.getSource();
                int scaleBase = (int)((double) sf.getSize().y / 25 - 0.5);
                sf.setWeights(new int[] {1, scaleBase <= 1 ? 1 : scaleBase - 1});
            }
        }
    }
}
