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
import java.util.Arrays;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
            toolItem.setData(ToolBaseSelectionAdapter.EVENT_NAME, toolItemsEvent[idx]);
            toolItem.addSelectionListener(new ToolBaseSelectionAdapter(toolItem, this));
            String tmpIconPath = "debug" + File.separator + toolItemsImagePath[idx];
            toolItem.setImage(IconUtility.getIconImage(tmpIconPath, this.getClass()));
        });

        Composite composite = new Composite(sashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        
        checkboxTableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
        checkboxTableViewer.setAllGrayed(true);
        checkboxTableViewer.setAllChecked(true);
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

    private static class ToolBaseSelectionAdapter extends SelectionAdapter {
        public static final String EVENT_NAME = "event_name";
        private ToolItem toolItem;
        private DebugCheckTableComposite checkComposite;
        public ToolBaseSelectionAdapter(ToolItem toolItem, DebugCheckTableComposite checkComposite) {
            this.toolItem = toolItem;
            this.checkComposite = checkComposite;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            DebugTableEventHandler handler = checkComposite.getTableEventHandler();
            if (handler == null) {
                return;
            }
            if (toolItem.getData(EVENT_NAME) instanceof DebugCheckboxEvent) {
                DebugCheckboxEvent event = (DebugCheckboxEvent) toolItem.getData(EVENT_NAME);
                Object[] items = checkComposite.getCheckboxTableViewer().getCheckedElements();
                if (items != null && items.length > 0) {
                    checkComposite.getTableEventHandler().selectHandler(Arrays.asList(items), event);
                }
            }
        }
    }
}
