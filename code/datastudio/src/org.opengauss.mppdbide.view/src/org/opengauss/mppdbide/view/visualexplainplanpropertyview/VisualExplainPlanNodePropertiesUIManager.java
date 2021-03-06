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

import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import org.opengauss.mppdbide.presentation.visualexplainplan.AbstractExplainPlanPropertyCore;
import org.opengauss.mppdbide.presentation.visualexplainplan.IAbstractExplainPlanPropertyCoreLabelFactory;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import org.opengauss.mppdbide.view.ui.visualexplainplan.VisualExplainPartsManager;
import org.opengauss.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanNodePropertiesUIManager.
 *
 * @since 3.0.0
 */
public class VisualExplainPlanNodePropertiesUIManager extends ViewObjectPropertiesResultDisplayUIManager {
    private VisualExplainPlanUIPresentation uiPresenter;
    private VisualExplainPlanPropertyTabManager tabManager;

    /**
     * Instantiates a new visual explain plan node properties UI manager.
     *
     * @param core the core
     * @param explainUIPresenter the explain UI presenter
     */
    public VisualExplainPlanNodePropertiesUIManager(AbstractExplainPlanPropertyCore core,
            VisualExplainPlanUIPresentation explainUIPresenter) {
        super(core);
        this.uiPresenter = explainUIPresenter;
    }

    /**
     * Creates the result new.
     *
     * @param result the result
     */
    @Override
    protected void createResultNew(Object result) {
        setResultData((IPropertyDetail) result);
        IWindowDetail windowDetails = getPropertyCore().getWindowDetails();

        AbstractExplainPlanPropertyCore explainPlanProperty = (AbstractExplainPlanPropertyCore) getPropertyCore();
        if (IAbstractExplainPlanPropertyCoreLabelFactory.VISUAL_EXPLAIN_OVERLLPROPERTIES_STACK == explainPlanProperty
                .getExplainPlanType()) {
            VisualExplainPartsManager.getInstance().createOverAllPlanInfoPart(uiPresenter.getExplainPlanTabId(), this);

        } else if (IAbstractExplainPlanPropertyCoreLabelFactory.VISUAL_EXPLAIN_PERNODEPROPERTIES_STACK == explainPlanProperty
                .getExplainPlanType()) {
            VisualExplainPartsManager.getInstance().createPerNodeInfoPart(uiPresenter.getExplainPlanTabId(),
                    windowDetails, this);
        }
    }

    /**
     * Handle pre execution UI display setup.
     *
     * @param terminalExecutionConnectionInfra the terminal execution connection
     * infra
     * @param isActivateStatusBar the is activate status bar
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void handlePreExecutionUIDisplaySetup(TerminalExecutionConnectionInfra terminalExecutionConnectionInfra,
            boolean isActivateStatusBar) throws MPPDBIDEException {

    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    @Override
    public void showResult(Composite parentComposite) {
        if (null == tabManager) {
            tabManager = new VisualExplainPlanPropertyTabManager(parentComposite, uiPresenter);
        }

        tabManager.createResult(getPropDetails());

    }

}
