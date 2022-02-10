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

package org.opengauss.mppdbide.view.ui;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import org.opengauss.mppdbide.view.data.DSViewFactoryManager;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class IDEStartup.
 *
 * @since 3.0.0
 */
public final class IDEStartup {

    private static volatile IDEStartup instance = null;
    private static final Object INSTANCELOCK = new Object();
    private boolean initDone;

    /**
     * Private constructor as this is a singleton class.
     */
    private IDEStartup() {

    }

    /**
     * Gets the single instance of IDEStartup.
     *
     * @return single instance of IDEStartup
     */
    public static IDEStartup getInstance() {
        if (null == instance) {
            synchronized (INSTANCELOCK) {
                if (null == instance) {
                    instance = new IDEStartup();
                }
            }
        }
        return instance;
    }

    /**
     * Inits the.
     *
     * @param partService the part service
     * @param modelService the model service
     * @param application the application
     */
    public void init(EPartService partService, EModelService modelService, MApplication application) {
        if (!initDone) {
            UIElement uiIns = UIElement.getInstance(partService, modelService, application);
            DSViewFactoryManager.getDSViewApplicationObjectManager().setApplication(uiIns.getApplication());
            DSViewFactoryManager.getDSViewApplicationObjectManager().setModelService(uiIns.getModelService());
            DSViewFactoryManager.getDSViewApplicationObjectManager().setPartService(uiIns.getPartService());
            initDone = true;
        }
    }

}
