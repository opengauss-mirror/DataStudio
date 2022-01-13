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

package com.huawei.mppdbide.view.visualexplainplanpropertyview;

import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.DSGridComponent;
import com.huawei.mppdbide.view.component.grid.GridQueryArea;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;

/**
 * Title: VisualExplainQueryArea
 * 
 * @since 3.0.0
 */
public class VisualExplainQueryArea extends DSGridComponent {

    private VisualExplainPlanUIPresentation uiPresenter;

    /**
     * Instantiates a new visual explain query area.
     *
     * @param uiPref the ui pref
     * @param dataProvider the data provider
     * @param visualExplainPlanUIPresentation the visual explain plan UI
     * presentation
     */
    public VisualExplainQueryArea(IGridUIPreference uiPref, IDSGridDataProvider dataProvider,
            VisualExplainPlanUIPresentation visualExplainPlanUIPresentation) {
        super(uiPref, dataProvider);
        this.uiPresenter = visualExplainPlanUIPresentation;
    }

    /**
     * Creates the query area.
     *
     * @param gridComposite the grid composite
     * @return the grid query area
     */
    protected GridQueryArea createQueryArea(Composite gridComposite) {
        GridQueryArea area = new GridQueryArea((uiPresenter).getPresenter().getAllQuery());
        area.setSQLSyntax((uiPresenter).getPresenter().getDatabase() != null
                ? (uiPresenter).getPresenter().getDatabase().getSqlSyntax()
                : null);
        area.createComponent(gridComposite, false);
        return area;
    }

}
