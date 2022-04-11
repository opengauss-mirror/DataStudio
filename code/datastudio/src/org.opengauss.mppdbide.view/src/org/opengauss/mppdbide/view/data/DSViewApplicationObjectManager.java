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

package org.opengauss.mppdbide.view.data;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSViewApplicationObjectManager.
 *
 * @since 3.0.0
 */
public final class DSViewApplicationObjectManager implements DSViewApplicationObjectManagerIf {

    private static volatile DSViewApplicationObjectManager instance = null;
    private static final Object LOCK = new Object();

    private EPartService partService = null;
    private EModelService modelService = null;
    private MApplication application = null;

    /**
     * Gets the single instance of DSViewApplicationObjectManager.
     *
     * @return single instance of DSViewApplicationObjectManager
     */
    public static DSViewApplicationObjectManager getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DSViewApplicationObjectManager();
                }
            }
        }
        return instance;
    }

    private DSViewApplicationObjectManager() {

    }

    /**
     * Gets the part service.
     *
     * @return the part service
     */
    public EPartService getPartService() {
        return partService;
    }

    /**
     * Sets the part service.
     *
     * @param partService the new part service
     */
    public void setPartService(EPartService partService) {
        this.partService = partService;
    }

    /**
     * Gets the model service.
     *
     * @return the model service
     */
    public EModelService getModelService() {
        return modelService;
    }

    /**
     * Sets the model service.
     *
     * @param modelService the new model service
     */
    public void setModelService(EModelService modelService) {
        this.modelService = modelService;
    }

    /**
     * Gets the application.
     *
     * @return the application
     */
    public MApplication getApplication() {
        return application;
    }

    /**
     * Sets the application.
     *
     * @param application the new application
     */
    public void setApplication(MApplication application) {
        this.application = application;
    }

}
