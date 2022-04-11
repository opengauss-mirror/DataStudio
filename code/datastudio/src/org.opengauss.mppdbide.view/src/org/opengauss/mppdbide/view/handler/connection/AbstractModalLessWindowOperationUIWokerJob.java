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

package org.opengauss.mppdbide.view.handler.connection;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractModalLessWindowOperationUIWokerJob.
 *
 * @since 3.0.0
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
