/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import com.huawei.mppdbide.presentation.visualexplainplan.AbstractExplainPlanPropertyCore;
import com.huawei.mppdbide.presentation.visualexplainplan.ExplainPlanNodePropertiesCore;
import com.huawei.mppdbide.presentation.visualexplainplan.ExplainPlanOverAllPlanPropertiesCore;
import com.huawei.mppdbide.presentation.visualexplainplan.ExplainPlanPresentation;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanUIPresentation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VisualExplainPlanUIPresentation {
    private ExplainPlanPresentation presenter;
    private String operationType;
    private String tabID;
    private Object operationObject;

    /**
     * Instantiates a new visual explain plan UI presentation.
     *
     * @param presentation the presentation
     */
    public VisualExplainPlanUIPresentation(String TabID, ExplainPlanPresentation presentation) {
        this.tabID = TabID;
        this.presenter = presentation;
    }

    /**
     * Gets the explain plan window handler.
     *
     * @return the explain plan window handler
     */
    public String getExplainPlanTabId() {
        return tabID;
    }

    /**
     * Gets the presenter.
     *
     * @return the presenter
     */
    public ExplainPlanPresentation getPresenter() {
        return presenter;
    }

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Gets the operation object.
     *
     * @return the operation object
     */
    public Object getOperationObject() {
        return operationObject;
    }

    /**
     * Sets the operation object.
     *
     * @param operationObject the new operation object
     */
    public void setOperationObject(Object operationObject) {
        this.operationObject = operationObject;
    }

    /**
     * Sets the operation type.
     *
     * @param operationType the new operation type
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * Gets the suitable property presenter.
     *
     * @return the suitable property presenter
     */
    public AbstractExplainPlanPropertyCore getSuitablePropertyPresenter() {
        if (this.getOperationType().equals(VisualExplainPlanConstants.VISUAL_EXPLAIN_OPTYPE_ALLPROPERTY)) {
            return new ExplainPlanOverAllPlanPropertiesCore(this.presenter);
        } else if (this.getOperationType().equals(VisualExplainPlanConstants.VISUAL_EXPLAIN_PERNODE_DETAILS)) {
            return (ExplainPlanNodePropertiesCore) this.getOperationObject();
        }

        return null;
    }

}