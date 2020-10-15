/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.visualexplainplanpropertyview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPropertyTabManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VisualExplainPlanPropertyTabManager {
    private Composite parent;

    /**
     * The tab folder 1.
     */
    protected CTabFolder tabFolder1 = null;
    private VisualExplainPlanUIPresentation uiPresenter;

    /**
     * Instantiates a new visual explain plan property tab manager.
     *
     * @param parent the parent
     */
    public VisualExplainPlanPropertyTabManager(Composite parent, VisualExplainPlanUIPresentation uiPresenter) {
        this.parent = parent;
        this.setUiPresenter(uiPresenter);
        createTabFolder();
    }

    private void createTabFolder() {
        if (this.parent.isDisposed()) {
            return;
        }
        tabFolder1 = new CTabFolder(this.parent, SWT.BORDER | SWT.NONE);
        tabFolder1.setLayout(new GridLayout());

        tabFolder1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tabFolder1.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (event.item instanceof VisualExplainPlanPropertyTab) {
                    ((VisualExplainPlanPropertyTab) event.item).handleFocus();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * Creates the result.
     *
     * @param propDetails the prop details
     */
    public void createResult(IPropertyDetail propDetails) {
        getTabFolderDefinite();

        tabFolder1.setLayoutData(new GridData(GridData.FILL_BOTH));

        for (IObjectPropertyData oneTabData : propDetails.objectproperties()) {
            Composite composite = new Composite(tabFolder1, SWT.NONE);

            VisualExplainPlanPropertyTab tab = new VisualExplainPlanPropertyTab(tabFolder1, SWT.NONE, composite,
                    oneTabData, this);
            tab.init();
            setTabProperties(tab, oneTabData);
        }

        tabFolder1.setSelection(0);
    }

    /**
     * Reset result.
     *
     * @param propDetails the prop details
     */
    public void resetResult(IPropertyDetail propDetails) {
        int index = 0;
        for (IObjectPropertyData oneTabData : propDetails.objectproperties()) {
            VisualExplainPlanPropertyTab tab = (VisualExplainPlanPropertyTab) tabFolder1.getItem(index);
            tab.resetData(oneTabData);
            index++;
        }
    }

    private CTabFolder getTabFolderDefinite() {
        if (tabFolder1 == null) {
            createTabFolder();
        }

        return tabFolder1;
    }

    private void setTabProperties(VisualExplainPlanPropertyTab tab, IObjectPropertyData oneTabData) {
        tab.setText(oneTabData.getObjectPropertyName());
        tab.setData(oneTabData.getObjectPropertyName());
        tab.setToolTipText(oneTabData.getObjectPropertyName());
    }

    public VisualExplainPlanUIPresentation getUiPresenter() {
        return uiPresenter;
    }

    public void setUiPresenter(VisualExplainPlanUIPresentation uiPresenter) {
        this.uiPresenter = uiPresenter;
    }
}