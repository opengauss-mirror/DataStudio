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

package org.opengauss.mppdbide.view.visualexplainplanpropertyview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.presentation.objectproperties.IObjectPropertyData;
import org.opengauss.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import org.opengauss.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanPropertyTabManager.
 *
 * @since 3.0.0
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