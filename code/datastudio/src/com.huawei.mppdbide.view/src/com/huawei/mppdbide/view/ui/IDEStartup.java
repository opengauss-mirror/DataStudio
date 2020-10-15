/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import com.huawei.mppdbide.view.data.DSViewFactoryManager;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class IDEStartup.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
