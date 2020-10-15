/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractModalLessWindowOperationUIWokerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractModalLessWindowOperationUIWokerJob extends ObjectBrowserOperationUIWorkerJob {

    /**
     * Instantiates a new abstract modal less window operation UI woker job.
     *
     * @param name the name
     * @param obj the obj
     * @param msg the msg
     * @param family the family
     */
    public AbstractModalLessWindowOperationUIWokerJob(String name, ServerObject obj, String msg, Object family) {
        super(name, obj, msg, family);

    }

    /**
     * Additional do jobhandling.
     */
    protected void additionalDoJobhandling() {
        ObjectBrowser ob = UIElement.getInstance().getObjectBrowserModel();
        if (null != ob) {
            ob.refreshObject(obj.getParent());
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        additionalDoJobhandling();
    }

}
