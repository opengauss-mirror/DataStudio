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
 * @since 3.0.0
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
