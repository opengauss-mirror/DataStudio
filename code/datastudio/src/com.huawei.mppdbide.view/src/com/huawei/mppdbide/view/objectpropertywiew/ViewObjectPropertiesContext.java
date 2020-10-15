/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.objectpropertywiew;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.AbstractPropertiesContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectPropertiesContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewObjectPropertiesContext extends AbstractPropertiesContext {
    private ViewObjectPropertiesResultDisplayUIManager uiManager;
    private ObjectPropertyResultConfig resultConfig;

    /**
     * Instantiates a new view object properties context.
     *
     * @param core2 the core 2
     * @param uiManager2 the ui manager 2
     */
    public ViewObjectPropertiesContext(PropertyHandlerCore core2,
            ViewObjectPropertiesResultDisplayUIManager uiManager2) {
        super(core2);
        this.uiManager = uiManager2;
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return "Object Property";
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        if (null == this.resultConfig) {
            this.resultConfig = new ObjectPropertyResultConfig();
        }

        return this.resultConfig;
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return this.uiManager;
    }

    /**
     * Handle execution exception.
     *
     * @param e the e
     */
    @Override
    public void handleExecutionException(Exception e) {
        MPPDBIDELoggerUtility.none("ViewObjectPropertiesContext: handle execution exception.");
    }

    /**
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }
}
