/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 30-Dec-2019]
 * @since 30-Dec-2019
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
