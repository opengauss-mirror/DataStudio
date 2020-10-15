/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.view.component.grid.GridQueryArea;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanTextPart.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VisualExplainPlanTextPart {
    private VisualExplainPlanUIPresentation uiPresenter;
    private GridQueryArea queryArea;

    /**
     * Post construct.
     *
     * @param availableComp the available comp
     * @param part the part
     * @return the composite
     */
    @PostConstruct
    public Composite postConstruct(Composite availableComp, MPart part) {
        uiPresenter = (VisualExplainPlanUIPresentation) part.getObject();
        createComponent(availableComp);
        return availableComp;
    }

    private void createComponent(Composite parent) {
        this.queryArea = new GridQueryArea(uiPresenter.getPresenter().getAllQuery());
        this.queryArea.setSQLSyntax(uiPresenter.getPresenter().getDatabase() != null
                ? uiPresenter.getPresenter().getDatabase().getSqlSyntax()
                : null);
        this.queryArea.createComponent(parent, true);
    }
}
