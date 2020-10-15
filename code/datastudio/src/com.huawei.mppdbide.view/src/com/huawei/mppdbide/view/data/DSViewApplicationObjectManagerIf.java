/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.data;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface DSViewApplicationObjectManagerIf.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public interface DSViewApplicationObjectManagerIf {

    /**
     * Gets the part service.
     *
     * @return the part service
     */
    public EPartService getPartService();

    /**
     * Sets the part service.
     *
     * @param partService the new part service
     */
    public void setPartService(EPartService partService);

    /**
     * Gets the model service.
     *
     * @return the model service
     */
    public EModelService getModelService();

    /**
     * Sets the model service.
     *
     * @param modelService the new model service
     */
    public void setModelService(EModelService modelService);

    /**
     * Gets the application.
     *
     * @return the application
     */
    public MApplication getApplication();

    /**
     * Sets the application.
     *
     * @param application the new application
     */
    public void setApplication(MApplication application);

}
