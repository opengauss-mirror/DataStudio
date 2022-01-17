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
 * @since 3.0.0
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
