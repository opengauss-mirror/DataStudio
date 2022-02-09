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

package org.opengauss.mppdbide.view.ui.visualexplainplan;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.view.component.grid.GridQueryArea;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanTextPart.
 *
 * @since 3.0.0
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
