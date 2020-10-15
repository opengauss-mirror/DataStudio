/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.visualexplainplan;

import com.huawei.mppdbide.presentation.DummyTerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractExplainPlanPropertyCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractExplainPlanPropertyCore extends PropertyHandlerCore
        implements IAbstractExplainPlanPropertyCoreLabelFactory {
    private int explainPlanType;

    /**
     * Instantiates a new abstract explain plan property core.
     *
     * @param explainPlanType the explain plan type
     */
    public AbstractExplainPlanPropertyCore(int explainPlanType) {
        this.explainPlanType = explainPlanType;
    }

    /**
     * Gets the explain plan type.
     *
     * @return the explain plan type
     */
    public int getExplainPlanType() {
        return explainPlanType;
    }

    /**
     * Sets the explain plan type.
     *
     * @param explainPlanType the new explain plan type
     */
    public void setExplainPlanType(int explainPlanType) {
        this.explainPlanType = explainPlanType;
    }

    /**
     * Checks if is executable.
     *
     * @return true, if is executable
     */
    @Override
    public boolean isExecutable() {
        return true;
    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    @Override
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == connInfra) {
            this.connInfra = new DummyTerminalExecutionConnectionInfra();
        }

        return connInfra;
    }
}
