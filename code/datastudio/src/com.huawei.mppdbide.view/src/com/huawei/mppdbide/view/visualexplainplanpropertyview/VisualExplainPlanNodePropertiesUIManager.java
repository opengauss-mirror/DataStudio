/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.visualexplainplanpropertyview;

import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.visualexplainplan.AbstractExplainPlanPropertyCore;
import com.huawei.mppdbide.presentation.visualexplainplan.IAbstractExplainPlanPropertyCoreLabelFactory;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.objectpropertywiew.ViewObjectPropertiesResultDisplayUIManager;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPartsManager;
import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPlanUIPresentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanNodePropertiesUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
